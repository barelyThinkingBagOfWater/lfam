package rating

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.kenshoo.play.metrics.Metrics
import com.newmotion.akka.rabbitmq._
import play.api.libs.json.Json

import java.util.UUID
import java.util.concurrent.TimeUnit
import scala.concurrent.duration
import scala.concurrent.duration._

class RatingAddedMessageConsumer(configuration: play.api.Configuration,
                                 ratingsWriter: ActorRef,
                                 metrics: Metrics) extends Actor with ActorLogging{

  val RATING_ADDED_QUEUE_NAME: String = "rating.added.queue.cache-ratings-manager." + UUID.randomUUID
  val RATING_ADDED_EXCHANGE_NAME: String = "rating.added.exchange"


  val factory: ConnectionFactory = new ConnectionFactory() {
    setHost(configuration.underlying.getString("rabbitmq.host"))
    setPort(configuration.underlying.getInt("rabbitmq.port"))
    setUsername(configuration.underlying.getString("rabbitmq.user"))
    setPassword(configuration.underlying.getString("rabbitmq.password"))
  }

  val connectionActor: ActorRef = ActorSystem().actorOf(ConnectionActor.props(factory,
    reconnectionDelay = 10.seconds), "ratingAddedConnection")

  val repositoryTimeout: FiniteDuration = new duration.FiniteDuration(configuration.underlying.getString("manager.repository.timeout.seconds").toInt, TimeUnit.SECONDS)
  implicit val timeout: Timeout = new Timeout(repositoryTimeout)

  def setupSubscriber(channel: Channel, self: ActorRef) : Unit =  {
    channel.queueDeclare(RATING_ADDED_QUEUE_NAME,false, false, true, null)
    channel.queueBind(RATING_ADDED_QUEUE_NAME, RATING_ADDED_EXCHANGE_NAME, "")

    val consumer = new DefaultConsumer(channel) {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: BasicProperties, body: Array[Byte]): Unit = {

        val correlationId = properties.getCorrelationId
        val rating: Rating = extractRatingFromMessage(body)

        log.info("Received message to add rating:{} to movieId:{} with correlationId:{}", rating.rating, rating.movieId, correlationId) //MDC is harder to use with play framework, log there the correlationId if required
        ratingsWriter ? rating
        metrics.defaultRegistry.counter("ratings_added_total").inc()
      }
    }
    channel.basicConsume(RATING_ADDED_QUEUE_NAME, false, consumer)
  }

  connectionActor.createChannel(ChannelActor.props(setupSubscriber), Some("ratingAddedSubscriber"))


  def extractRatingFromMessage(body: Array[Byte]): Rating =  {
    val message = new String(body, "UTF-8")

    val rating: String = (Json.parse(message) \\ "rating").head.toString().replace("\"", "")
    val movieId: String = (Json.parse(message) \\ "movieId").head.toString().replace("\"", "")

    Rating(null, movieId, rating, (System.currentTimeMillis / 1000).toString)
  }

  override def receive: Receive = {
    case x => log.error("RatingAddedConsumer just received a message: {}", x)
  }
}
