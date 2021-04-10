package ch.xavier;

import ch.xavier.common.EntitiesImporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

@Service
@Slf4j
public class ReadinessService {

    private final List<EntitiesImporter> importers;
    private final AtomicInteger numberOfRepositoriesReady;

    private final static int SERVICE_NOT_AVAILABLE_HTTP_STATUS_CODE = 503;

    @Autowired
    public ReadinessService(final List<EntitiesImporter> importers) {
        this.importers = importers;
        numberOfRepositoriesReady = new AtomicInteger(0);
    }


    public void notifyOneRepositoryReady() {
        numberOfRepositoriesReady.incrementAndGet();
    }

    public Mono<ServerResponse> areRepositoriesReady(ServerRequest request) {
        return importers.size() == numberOfRepositoriesReady.get() ?
                ok().build() :
                status(SERVICE_NOT_AVAILABLE_HTTP_STATUS_CODE).build();
    }
}
