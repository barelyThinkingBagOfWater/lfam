package rating

import play.api.libs.ws.WSResponse

import scala.concurrent.Future

trait RatingsImporter {

  def importAllRatings(): Future[WSResponse]
}
