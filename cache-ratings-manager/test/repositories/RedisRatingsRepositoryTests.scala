package repositories

import rating.Rating
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class RedisRatingsRepositoryTests extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "Redis repository" should {

    "correctly saves a rating and fetches it" in {
      val rating = new Rating("userId", "movieId", "rating", "timestamp")
      //TODO: tests...
    }
  }
}
