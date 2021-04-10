package ch.xavier.movies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString //for logging during imports
@Entity
@Table(name="movies")
public class Movie {

    @Id
    private Long movieId;
    private String title;
    private String genres;
    @ManyToMany
    private Set<String> tags;
}
