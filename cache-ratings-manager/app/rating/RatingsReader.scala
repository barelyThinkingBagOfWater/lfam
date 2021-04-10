package rating

import _root_.akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.actor.Status.Failure
import eventstore.akka.ProjectionsClient.ProjectionMode
import eventstore.akka.{ProjectionDetails, ProjectionsClient, Settings}
import eventstore.core._
import play.api.libs.json._

import scala.concurrent.{Await, Future}


class RatingsReader(settings: Settings) extends Actor with ActorLogging {
  val projectionClient = new ProjectionsClient(settings, ActorSystem())
  val ratingsByMovieProjectionName = "$average_ratings_by_movie"
  val ratingsGlobalCountProjectionName = "$global_ratings_count"
  val createUsersStreamsProjectionName = "$create-users-streams-from-ratings"
  val getRatingsOfUserProjectionName = "$ratings_by_user";

  override def preStart(): Unit = {
    createAndStartProjectionsIfNeeded()
  }

  def createAndStartProjectionsIfNeeded(): Unit = {
    val projectionDetailsFuture: Future[Option[ProjectionDetails]] = projectionClient.fetchProjectionDetails(ratingsByMovieProjectionName)
    val projectionDetails = Await.result(projectionDetailsFuture, settings.connectionTimeout)

    if (projectionDetails.isEmpty) {
      projectionClient.startProjection("$by_category")

      //To get all information from the admin interface like the recently created streams, optional
      projectionClient.startProjection("$streams")
      projectionClient.startProjection("$by_event_type")
      projectionClient.startProjection("$stream_by_category")

      projectionClient.createProjection(ratingsByMovieProjectionName, getAverageRatingsForMovieIdProjectionJSCode, ProjectionMode.Continuous, allowEmit = true)
      projectionClient.createProjection(ratingsGlobalCountProjectionName, countAllRatingsProjectionJSCode, ProjectionMode.Continuous, allowEmit = true)
      projectionClient.createProjection(createUsersStreamsProjectionName, createRatingsByUserStreamsProjectionJSCode, ProjectionMode.Continuous, allowEmit = true)
      projectionClient.createProjection(getRatingsOfUserProjectionName, getRatingsOfUserProjectionJSCode, ProjectionMode.Continuous, allowEmit = true)
    }
  }

  override def receive: Receive = {
    case getRatingsMessage: GetRatingsForMovieMessage => getRatingsForMovie(getRatingsMessage.movieId)
    case getRatingsCountMessage: GetGlobalRatingsCountMessage.type => countRatings()
    case getRatingsOfUserForMovieMessage: GetRatingsOfUserForMovieIdMessage => getRatingsOfUser(getRatingsOfUserForMovieMessage.userId)

    case Failure(e: EsException) => log.error(e.toString)
    case x => log.error("RatingsReader just received an unknown message: {}", x)
  }


  def getRatingsForMovie(movieId: String): Unit = {
    val partitionName = "ratings-" + movieId

    val projectionResultFuture: Future[Option[String]] = projectionClient.fetchProjectionResult(ratingsByMovieProjectionName, partition = Some(partitionName))

    sender() ! Await.result(projectionResultFuture, settings.connectionTimeout).getOrElse("")
  }

  def countRatings(): Unit = {
    val countRatingsProjectionResult: String =
      Await.result(projectionClient.fetchProjectionResult(ratingsGlobalCountProjectionName), settings.connectionTimeout).getOrElse("")

    if (countRatingsProjectionResult.isEmpty) {
      sender() ! 0
    } else {
      sender() ! (Json.parse(countRatingsProjectionResult) \ "count").get.toString().toInt
    }
  }

  def getRatingsOfUser(userId: String): Unit = {
    val partitionName = "user-" + userId

    val ratingsByUserProjectionResult: String = Await.result(projectionClient.fetchProjectionResult(getRatingsOfUserProjectionName, partition = Some(partitionName)),
      settings.connectionTimeout).getOrElse("")

    if (ratingsByUserProjectionResult.isEmpty) {
      sender() ! ""
    } else {
      sender() ! (Json.parse(ratingsByUserProjectionResult) \ "ratingsOfUser").get.toString()
    }
  }


  val getAverageRatingsForMovieIdProjectionJSCode: String = "fromCategory(\"ratings\")\n  .foreachStream()\n\t.when({\n\t\t\t$init: function() {\n\t\t\t\treturn {\n\t\t\t\t\tcount: 0,\n\t\t\t\t\tratings: 0,\n\t\t\t\t\taverageRating: 0\n\t\t\t\t}\n\t\t\t},\n\t\t\t\"rating\": function(state, event) {\n\t\t\t\tstate.count += 1;\n\t\t\t\tvar body = JSON.parse(event.bodyRaw);\n\t\t\t\tstate.ratings += parseFloat(body.rating);\n\t\t\t\tstate.averageRating = (state.ratings / state.count);\n\t}\n\t});"
  val countAllRatingsProjectionJSCode: String = "fromCategory(\"ratings\")\n  .when({\n    $init: function(){\n        return {\n            count: 0\n        };\n    },\n    $any: function(s,e){\n        s.count += 1;\n    }\n}).outputState();"
  val createRatingsByUserStreamsProjectionJSCode: String = "fromCategory(\"ratings\")\n  .foreachStream()\n\t.when({\n\t\t\t$init: function() {\n\t\t\t    return {\n\t\t\t\t\tcount: 0\n\t\t\t\t}\n\t\t\t},\n\t\t\t\"rating\": function(state, event) {\n\t\t\t\tvar body = JSON.parse(event.bodyRaw);\n\n        \t\temit(\"user-\" + body.userId, \"rating_by_user\", body);\n\t}\n\t});"
  val getRatingsOfUserProjectionJSCode: String = "  fromCategory(\"user\")\n  .foreachStream()\n\t.when({\n\t\t$init: function() {\n\t\t\treturn {\n\t\t\t\tratingsOfUser: []\n\t\t\t}\n\t\t},\n\t\t\"rating_by_user\": function(state, event) {\n\t\t\tvar body = JSON.parse(event.bodyRaw);\n\n\t\t\tstate.ratingsOfUser.push(body);\n\t\t}\n\t}).outputState();"
}