package rating

import play.api.libs.json._
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class Rating(userId: String, movieId: String, rating: String, timestamp: String) {}

object RatingObject extends DefaultJsonProtocol {
  implicit val ratingsWrites: Writes[Rating] = (rating: Rating) => Json.obj(
    "userId" -> rating.userId,
    "movieId" -> rating.movieId,
    "rating" -> rating.rating,
    "timestamp" -> rating.timestamp
  )

  implicit val ratingsReads: Reads[Rating] = (json: JsValue) => {
    JsSuccess(new Rating(
      (json \ "userId").as[String],
      (json \ "movieId").as[String],
      (json \ "rating").as[String],
      (json \ "timestamp").as[String],
    ))
  }

  implicit val ratingFormat: RootJsonFormat[Rating] = jsonFormat4(Rating.apply)
}