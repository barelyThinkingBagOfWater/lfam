package ch.xavier.common.movies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

//you shouldn't have Mongo specific annotations here, you would have a dummy dto here usable by artifacts without any
//mongo dependencies. Then you can extend it in this artifact and add the annotations.
@Document(collection = "movies")
@AllArgsConstructor
@Getter
@ToString //for logging during imports
public class Movie {

    @Id
    private final Long movieId;
    private final String title;
    private final String genres;
    private final Set<String> tags;

    public Movie withNewTag(String tag) {
        if (getTags().contains(tag)) {
            return this;
        } else {
            HashSet<String> tags = new HashSet<>(getTags());
            tags.add(tag);

            return new Movie(getMovieId(), getTitle(), getGenres(), tags);
        }
    }
}
