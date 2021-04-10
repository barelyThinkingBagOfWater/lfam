package ch.xavier.common.ratings.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class RatingAddedMessage {

    //no userId yet
    private final String rating;
    private final String movieId;
}
