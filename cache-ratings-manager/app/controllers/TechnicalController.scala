package controllers

import javax.inject._
import play.api.mvc._
import rating.RatingsCacheManager


@Singleton
class TechnicalController @Inject() (val controllerComponents: ControllerComponents,
                                    val cacheManager: RatingsCacheManager) extends BaseController {

  def isCacheReady: Action[AnyContent] = Action {
    if (cacheManager.isCacheReady) Ok("Cache is ready") else ServiceUnavailable("Cache not yet ready")
  }

  def refreshCache: Action[AnyContent] = Action {
    cacheManager.importAllRatings()
    Ok("Cache is being now filled")
  }

  def checkMetrics: Action[AnyContent] = Action {
    Ok(cacheManager.fetchMetricsForPrometheus())
  }
}
