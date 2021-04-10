package ch.xavier.common.ratings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ratings")
@Getter
@AllArgsConstructor
public class Rating {

    private final String rating;
    private final String userId;
    private final String movieId;
    private final String timestamp;
}