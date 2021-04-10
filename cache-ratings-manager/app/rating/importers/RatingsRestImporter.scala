package rating.importers

import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}
import rating.RatingsImporter

import javax.inject.{Inject, Singleton}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{Duration, SECONDS}

@Singleton
class RatingsRestImporter @Inject()(val wsClient: WSClient,
                                    val configuration: play.api.Configuration) extends RatingsImporter {

  val host: String = configuration.underlying.getString("manager.ratings.host")
  val keycloakHost: String = configuration.underlying.getString("keycloak.host")
  val keycloakSecret: String = configuration.underlying.getString("keycloak.secret ")
  val ratingsServiceUrl: String = host + "/ratings"
  val importerTimeout: String = configuration.underlying.getString("manager.import.timeout.seconds")
  val log: Logger = Logger(this.getClass)

  implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
    EntityStreamingSupport.json()
      .withParallelMarshalling(Runtime.getRuntime.availableProcessors(), unordered = true)


  override def importAllRatings(): Future[WSResponse] = {
    //No support for token refresh for now
    val tokenHeader: String = "Bearer ".concat(getToken()).replace("\"", "")

    wsClient.url(ratingsServiceUrl)
      .withRequestTimeout(Duration.Inf) //importing 1M ratings can take a lot of time
      .addHttpHeaders("Accept" -> "application/json", "Authorization" -> tokenHeader)
      .withMethod("GET")
      .stream()
  }


  def getToken(): String = {
    log.info("Fetching token to access movies-manager service")

    val response: WSResponse = Await.result(wsClient
      .url(keycloakHost + "/auth/realms/services-realm/protocol/openid-connect/token")
      .withMethod("POST")
      .addHttpHeaders("Content-type" -> "application/x-www-form-urlencoded")
      .post(Map(
        "client_id" -> "movies-manager",
        "client_secret" -> keycloakSecret,
        "grant_type" -> "client_credentials")),
      Duration.Inf)

    (Json.parse(response.body) \ "access_token").get.toString()
  }
}
