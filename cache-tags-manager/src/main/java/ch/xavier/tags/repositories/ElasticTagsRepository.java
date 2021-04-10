package ch.xavier.tags.repositories;

import ch.xavier.common.tags.Tag;
import ch.xavier.tags.TagsRepository;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Repository
@ConditionalOnBean(ElasticRestClient.class)
public class ElasticTagsRepository implements TagsRepository, AutoCloseable {

    private final RestHighLevelClient client;

    @Autowired
    public ElasticTagsRepository(RestHighLevelClient client) {
        this.client = client;

        createDefaultIndexIfAbsent();
    }


    private void createDefaultIndexIfAbsent() {
        if (!hasDefaultIndex()) {
            log.info("No indice named {} present, creating one", ElasticRestClient.DEFAULT_INDEX_NAME);

            createIndex(ElasticRestClient.DEFAULT_INDEX_NAME).block();
        }
    }

    private boolean hasDefaultIndex() {
        GetIndexRequest request = new GetIndexRequest(ElasticRestClient.DEFAULT_INDEX_NAME);
        request.humanReadable(true);

        try {
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            ElasticTagsRepository.log.error("Problem when checking if index:{} exists", ElasticRestClient.DEFAULT_INDEX_NAME);
        }
        return false;
    }

    public Mono<AcknowledgedResponse> removeIndex(String indexName) {
        return Mono.create(sink -> {
            DeleteIndexRequest request = new DeleteIndexRequest(indexName);
            request.indicesOptions(IndicesOptions.lenientExpandOpen()); //when no index is found

            client.indices().deleteAsync(request, RequestOptions.DEFAULT, new ActionListener<AcknowledgedResponse>() {

                @Override
                public void onResponse(AcknowledgedResponse acknowledgedResponse) {
                    sink.success(acknowledgedResponse);
                }

                @Override
                public void onFailure(Exception e) {
                    log.error("Failure when removing index:{}", indexName);
                    sink.error(e);
                }
            });
        });
    }

    public Mono<CreateIndexResponse> createIndex(String indexName) {
        return Mono.create(sink -> {
            CreateIndexRequest request = new CreateIndexRequest(indexName);

            client.indices().createAsync(request, RequestOptions.DEFAULT, new ActionListener<CreateIndexResponse>() {
                @Override
                public void onResponse(CreateIndexResponse createIndexResponse) {
                    sink.success(createIndexResponse);
                }

                @Override
                public void onFailure(Exception e) {
                    log.error("Failure when creating index:{}", indexName);
                    sink.error(e);
                }
            });
        });
    }

    @Override
    public Mono<IndexResponse> save(Tag tag) {
        return Mono.create(sink -> {
            IndexRequest request = new IndexRequest(ElasticRestClient.DEFAULT_INDEX_NAME).id(tag.getTagName());

            request.source(new Gson().toJson(new Tag(tag.getTagName(), tag.getMovieIds())), XContentType.JSON);

            client.indexAsync(request, RequestOptions.DEFAULT, new ActionListener<>() {
                @Override
                public void onResponse(IndexResponse indexResponse) {
                    sink.success(indexResponse);
                }

                @Override
                public void onFailure(Exception e) {
                    log.error("There was a problem when saving tag:{}", tag.getTagName(), e);
                    sink.error(e);
                }
            });
        });
    }

    @Override
    public Mono<Tag> find(String tagName) {
        return Mono.create(sink -> {
            GetRequest getRequest = new GetRequest(
                    ElasticRestClient.DEFAULT_INDEX_NAME,
                    tagName.toLowerCase());

            client.getAsync(getRequest, RequestOptions.DEFAULT, new ActionListener<>() {
                @Override
                public void onResponse(GetResponse getResponse) {
                    sink.success(getResponse.getSourceAsString());
                }

                @Override
                public void onFailure(Exception e) {
                    if (e.getClass().equals(ElasticsearchStatusException.class)) {
                        if (RestStatus.NOT_FOUND.equals(((ElasticsearchStatusException) e).status())) {
                            Tag newTag = new Tag(tagName);
                            save(newTag).subscribe();
                            sink.success(newTag);
                        }
                    } else {
                        sink.error(e);
                    }
                }
            });
        }).map(getResponse -> new Gson().fromJson((String) getResponse, Tag.class));
    }

    @Override
    public Flux<Tag> fuzzilyFind(String tagName) {
        Mono<SearchResponse> responseMono = Mono.create(sink -> {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.fuzzyQuery("tagName", tagName).fuzziness(Fuzziness.TWO));

            SearchRequest searchRequest = new SearchRequest(ElasticRestClient.DEFAULT_INDEX_NAME);
            searchRequest.source(searchSourceBuilder);

            client.searchAsync(searchRequest, RequestOptions.DEFAULT, new ActionListener<SearchResponse>() {
                @Override
                public void onResponse(SearchResponse searchResponse) {
                    sink.success(searchResponse);
                }

                @Override
                public void onFailure(Exception e) {
                    log.error("Encountered exception when searching for {}", tagName, e);
                    sink.error(e);
                }
            });
        });

        Function<SearchHit[], List<Tag>> extractTagsFromResponseFunction =
                searchResponse -> Arrays.stream(searchResponse)
                        .map(tag -> new Gson().fromJson(tag.getSourceAsString(), Tag.class))
                        .collect(Collectors.toList());

        return responseMono
                .map(searchResponse -> searchResponse.getHits().getHits())
                .map(extractTagsFromResponseFunction)
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<Long> count() {
        return Mono.create(sink -> {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            //Any cheaper way to count the docs?
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());

            SearchRequest searchRequest = new SearchRequest(ElasticRestClient.DEFAULT_INDEX_NAME);
            searchRequest.source(searchSourceBuilder);

            client.searchAsync(searchRequest, RequestOptions.DEFAULT, new ActionListener<>() {
                @Override
                public void onResponse(SearchResponse searchResponse) {
                    sink.success(searchResponse);
                }

                @Override
                public void onFailure(Exception e) {
                    log.error("There was a problem when counting tags", e);
                    sink.error(e);
                }
            });
        }).map(searchResponse -> ((SearchResponse) searchResponse).getHits().getTotalHits().value);
    }

    @Override
    public Mono<BulkByScrollResponse> empty() {
        return Mono.create(sink -> {
            DeleteByQueryRequest deleteRequest = new DeleteByQueryRequest(ElasticRestClient.DEFAULT_INDEX_NAME);
            deleteRequest.setQuery(QueryBuilders.matchAllQuery());

            client.deleteByQueryAsync(deleteRequest, RequestOptions.DEFAULT, new ActionListener<>() {
                @Override
                public void onResponse(BulkByScrollResponse bulkByScrollResponse) {
                    sink.success(bulkByScrollResponse);
                }

                @Override
                public void onFailure(Exception e) {
                    log.error("There was a problem when deleting all the tags", e);
                    sink.error(e);
                }
            });
        });
    }

    @Override
    public Mono<UpdateResponse> addTagToMovie(String tagName, Long movieId) {
        return getVersionedTagAndCreateItIfMissing(tagName)
                .flatMap(tag -> updateWithVersion(tag.withNewMovieId(movieId)));
    }

    private Mono<VersionedTag> getVersionedTagAndCreateItIfMissing(String tagName) {
        return Mono.create(sink -> {
            GetRequest getRequest = new GetRequest(ElasticRestClient.DEFAULT_INDEX_NAME, tagName.toLowerCase());

            client.getAsync(getRequest, RequestOptions.DEFAULT, new ActionListener<>() {
                @Override
                public void onResponse(GetResponse getResponse) {
                    Tag tag = new Gson().fromJson(getResponse.getSourceAsString(), Tag.class);

                    sink.success(new VersionedTag(tag, getResponse.getSeqNo(), getResponse.getPrimaryTerm()));
                }

                @Override
                public void onFailure(Exception e) {
                    if (e.getClass().equals(ElasticsearchStatusException.class)) {
                        if (RestStatus.NOT_FOUND.equals(((ElasticsearchStatusException) e).status())) {
                            log.debug("Tag [{}] not found, creating it in cache", tagName);

                            VersionedTag newTag = new VersionedTag(tagName);
                            save(newTag).subscribe();

                            sink.success(newTag);
                        }
                    } else {
                        sink.error(e);
                    }
                }
            });
        });
    }

    private Mono<UpdateResponse> updateWithVersion(VersionedTag versionedTag) {
        return Mono.create(sink -> {
            UpdateRequest updateRequest = new UpdateRequest(ElasticRestClient.DEFAULT_INDEX_NAME, versionedTag.getTagName());

            if (!VersionedTag.DEFAULT_PRIMARY_TERM.equals(versionedTag.getPrimaryTerm())) {
                updateRequest.setIfPrimaryTerm(versionedTag.getPrimaryTerm());
                updateRequest.setIfSeqNo(versionedTag.getSeqNumber());
            }

            updateRequest.doc(new Gson().toJson(versionedTag.getTag()), XContentType.JSON);

            client.updateAsync(updateRequest, RequestOptions.DEFAULT, new ActionListener<UpdateResponse>() {
                @Override
                public void onResponse(UpdateResponse updateResponse) {
                    sink.success(updateResponse);
                }

                @Override
                public void onFailure(Exception e) {
                    sink.error(e);
                }
            });
        });
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}