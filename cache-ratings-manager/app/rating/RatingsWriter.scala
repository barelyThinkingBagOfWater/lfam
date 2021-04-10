package rating

import _root_.akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem}
import akka.actor.Status.Failure
import eventstore.akka.Settings
import eventstore.akka.tcp.ConnectionActor
import eventstore.core._
import eventstore.core.util.uuid.randomUuid
import play.api.libs.json.Json
import rating.RatingObject._


class RatingsWriter(settings: Settings) extends Actor with ActorLogging {

  val EVENT_TYPE = "rating"

  val connection: ActorRef = ActorSystem().actorOf(ConnectionActor.props(settings))

  override def receive: Receive = {
    case rating: Rating => saveRating(rating)
    case Failure(e: EsException) => log.error(e.toString)
    case WriteEventsCompleted(range, position) => log.debug("range: {}, position: {}", range, position)
    case x => log.error("RatingsWriter just received an unknown message: {}", x)
  }


  def saveRating(rating: Rating): Unit = {
    val streamName = "ratings-" + rating.movieId.replace("\"", "")

    val event = EventData(
      EVENT_TYPE,
      eventId = randomUuid,
      data = Content(Json.toJson(rating).toString())
    )

    log.debug("Sending now rating:{} to stream:{} on Thread:{}",
      rating, streamName, Thread.currentThread().getName)

    connection ! WriteEvents(
      EventStream.Id(streamName),
      List(event),
      expectedVersion = ExpectedVersion.Any,
      requireMaster = false
    )

    sender() ! "thanks"
  }
}
