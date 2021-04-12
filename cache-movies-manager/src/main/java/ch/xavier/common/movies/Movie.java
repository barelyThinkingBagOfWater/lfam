package ch.xavier.common.movies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString //for logging during imports

//Useful? specific to micronaut
//@Introspected
//@CompileStatic

public class Movie {

    private Long movieId;
    private String title;
    private String genres;
    private Set<String> tags;

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
