package rating

import akka.actor.{ActorInitializationException, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.pattern.ask
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import akka.util.Timeout
import com.google.inject.Inject
import com.kenshoo.play.metrics.Metrics
import eventstore.akka.{HttpSettings, Settings}
import eventstore.core.UserCredentials
import play.api.Logger
import play.api.libs.json.Json
import rating.RatingObject.{ratingFormat, ratingsReads}
import rating.importers.RatingsRestImporter

import java.io.StringWriter
import java.net.{InetSocketAddress, NoRouteToHostException}
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import scala.concurrent._
import scala.concurrent.duration.Duration

@Singleton
class RatingsCacheManager @Inject()(val ratingsImporter: RatingsRestImporter,
                                    implicit val executionContext: ExecutionContext,
                                    val configuration: play.api.Configuration,
                                    metrics: Metrics) {

  val repositoryTimeout: String = configuration.underlying.getString("manager.repository.timeout.seconds")
  val importTimeout: String = configuration.underlying.getString("manager.import.timeout.seconds")
  val eventStoreHost: String = configuration.underlying.getString("eventstore.host")
  val importHost: String = configuration.underlying.getString("manager.ratings.host")
  val clusterNodes: String = configuration.underlying.getString("eventstore.cluster.nodes")
  val eventStoreDefaultPort = 1113
  val log: Logger = Logger(this.getClass)

  val settings: Settings = createSettingsFromConfiguration

  val numberOfAvailableCores: Int = Runtime.getRuntime.availableProcessors()
  implicit val timeout: Timeout = new Timeout(settings.connectionTimeout)
  var isCacheReady: Boolean = false
  val getRatingsCountMessage = 1

  var ratingsReader: ActorRef = _
  var ratingsWriter: ActorRef = _

  try {
    initializeActors

    val ratingsCount: Int = Await.result(ratingsReader ? GetGlobalRatingsCountMessage, settings.connectionTimeout).asInstanceOf[Int]

    if (ratingsCount == 0) {
      log.info("The cache is empty, importing ratings.")
      Await.result(importAllRatings(), Duration.Inf)
    } else {
      log.info("Cache already filled with " + ratingsCount + " ratings, no import is required")
      metrics.defaultRegistry.counter("ratings_imported_total").inc(ratingsCount)
      isCacheReady = true
    }
  } catch {
    case e: ActorInitializationException =>
      log.error("EventStore DB wasn't reachable with host:" + eventStoreHost + ", closing cache")
      System.exit(1)
    case e: NoRouteToHostException =>
      log.error("No route to Eventstore DB with host:" + eventStoreHost + ", closing cache")
      System.exit(1)
    case e: TimeoutException =>
      log.error("Timeout reached during import from url:" + importHost + "/ratings, closing cache")
      System.exit(1)
  }

  private def initializeActors = {
    ratingsWriter = ActorSystem().actorOf(Props(new RatingsWriter(settings)), "ratingsWriter")
    ratingsReader = ActorSystem().actorOf(Props(new RatingsReader(settings)), "ratingsReader")
    ActorSystem().actorOf(Props(new RatingAddedMessageConsumer(configuration, ratingsWriter, metrics)))
  }

  def importAllRatings(): Future[Unit] = {
    implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
      EntityStreamingSupport.json().withParallelMarshalling(numberOfAvailableCores, unordered = true)

    implicit val materializer: Materializer = Materializer(ActorSystem())

    ratingsImporter.importAllRatings()
      .map(response => response.bodyAsSource)
      .map(source => source
        .via(jsonStreamingSupport.framingDecoder)
        .mapAsync(numberOfAvailableCores)(bytes => Unmarshal(bytes).to[Rating])
        .mapAsync(numberOfAvailableCores)(rating => {
          ratingsWriter ? rating
        })
        .mapAsync(numberOfAvailableCores)(_ => Future {
          metrics.defaultRegistry.counter("ratings_imported_total").inc()
        })
        .runWith(Sink.last)
        .onComplete(response => {
          log.info("Import of ratings is done")
          isCacheReady = true
        }))
  }


  def getAverageRatingForMovie(movieId: String): Any = {
    log.info("Getting average of ratings for movieId:" + movieId)
    metrics.defaultRegistry.counter("ratings_searched_total").inc()
    
    Await.result(ratingsReader ? GetRatingsForMovieMessage(movieId), settings.connectionTimeout)
  }

  def getRatingsOfUserForMovie(movieId: String, userId: String): Any = {
    val result: String = Await.result(ratingsReader ? GetRatingsOfUserForMovieIdMessage(userId), settings.connectionTimeout).toString

    if (result.isEmpty) {
      log.info("No rating was found for user:" + userId + " and movieId:" + movieId)
      ""
    } else {
      log.info("Rating found for user:" + userId + " and movieId:" + movieId)

      Json.parse(result).as[List[Rating]]
        .filter(rating => rating.movieId.equals(movieId))
    }
  }

  //TODO: Use a metrics actor and do it properly.
  def fetchMetricsForPrometheus(): String = {
    val stringWriter = new StringWriter()

    //JVM metrics
    metrics.defaultRegistry.getGauges.forEach { case (key, value) =>
      if (!key.contains("jvm.attribute")) {
        stringWriter.append(key.replace(".", "_").replace("-", "_").replace("'", "")
          + "{application=\"cache-ratings-manager\",} "
          + value.getValue.toString.replace("[]", "0") + "\n")
      }
    }

    //ch.qos.logback metrics
    metrics.defaultRegistry.getMeters.forEach { case (key, value) =>
      stringWriter.append(key.replace(".", "_") + "_events_per_second{application=\"cache-ratings-manager\",} " + value.getCount + "\n")
    }

    //business metrics
    metrics.defaultRegistry.getCounters.forEach { case (key, value) =>
      stringWriter.append(key.replace(".", "_") + "{application=\"cache-ratings-manager\",} " + value.getCount + "\n")
    }

    stringWriter.toString
  }

  private def createSettingsFromConfiguration: Settings = {
    if (clusterNodes.isEmpty) {
      log.info("No cluster configuration was detected, connecting to single node on host:" + eventStoreHost)
      createSettingsFromConfigurationForSingleNode
    } else {
      log.info("Cluster configuration detected but cluster connection doesn't work, " +
        "using the first cluster node as single host:" + clusterNodes.split(",")(0))
      log.info("cluster nodes:" + clusterNodes)
      //Always get a "The server-side HTTP version is not supported",
      //updating the EventStore version could help but breaks the cluster, fix this one day
      createSettingsFromConfigurationForCluster
    }
  }

  private def createSettingsFromConfigurationForSingleNode: Settings = {
    Settings(
      address = new InetSocketAddress(eventStoreHost, eventStoreDefaultPort),
      defaultCredentials = Some(UserCredentials(configuration.underlying.getString("eventstore.user"),
        configuration.underlying.getString("eventstore.password"))),
      http = new HttpSettings(Uri("http://" + eventStoreHost + ":2113")), //required for ProjectionClient
      //Repository timeout
      connectionTimeout = new duration.FiniteDuration(repositoryTimeout.toInt, TimeUnit.SECONDS),
      //Import Timeout
      operationTimeout = new duration.FiniteDuration(importTimeout.toInt, TimeUnit.SECONDS)
    )
  }

  private def createSettingsFromConfigurationForCluster: Settings = {
    Settings(
      address = new InetSocketAddress(clusterNodes.split(",")(0), eventStoreDefaultPort),
      defaultCredentials = Some(UserCredentials(configuration.underlying.getString("eventstore.user"),
        configuration.underlying.getString("eventstore.password"))),
      http = new HttpSettings(Uri("http://" + clusterNodes.split(",")(0) + ":2113")), //required for ProjectionClient...
      //Repository timeout
      connectionTimeout = new duration.FiniteDuration(repositoryTimeout.toInt, TimeUnit.SECONDS),
      //Import Timeout
      operationTimeout = new duration.FiniteDuration(importTimeout.toInt, TimeUnit.SECONDS),
      //      cluster = Some(new ClusterSettings(new GossipSeedsOrDns.GossipSeeds(gossipSeeds =
      //        clusterNodes.split(",").map(uri => new InetSocketAddress(uri, eventStoreDefaultPort)).toList)))
    )
  }
}
