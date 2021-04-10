package controllers

import javax.inject._
import play.api.mvc._
import rating.RatingsCacheManager

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class RatingsController @Inject() (val controllerComponents: ControllerComponents,
                                    val cacheManager: RatingsCacheManager) extends BaseController {

  def getRatingForMovie(movieId: String): Action[AnyContent] = Action {
    Ok(cacheManager.getAverageRatingForMovie(movieId).toString)
  }

  def getRatingsOfUserForMovie(movieId: String, userId: String): Action[AnyContent] = Action {
    Ok(cacheManager.getRatingsOfUserForMovie(movieId, userId).toString)
  }
}
