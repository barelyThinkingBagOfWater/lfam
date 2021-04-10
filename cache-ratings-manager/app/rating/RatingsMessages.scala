package rating

case class GetRatingsForMovieMessage(movieId: String)

case class GetGlobalRatingsCountMessage()

case class GetRatingsOfUserForMovieIdMessage(userId: String)

