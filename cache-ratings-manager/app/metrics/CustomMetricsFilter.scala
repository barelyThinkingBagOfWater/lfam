package metrics

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.kenshoo.play.metrics.{Metrics, MetricsFilter, MetricsFilterImpl, MetricsImpl}
import javax.inject.Inject
import play.api.http.Status
import play.api.inject.Module
import play.api.{Configuration, Environment}

import scala.concurrent.ExecutionContext

class CustomMetricsFilter @Inject()(metrics: Metrics)
                                        (implicit val executionContext: ExecutionContext,
                                         implicit val materializer: Materializer = Materializer(ActorSystem())) extends MetricsFilterImpl(metrics) {

  override def labelPrefix: String = "cache-ratings-manager-"
}


class CustomMetricsModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = {
    Seq(
      bind[MetricsFilter].to[CustomMetricsFilter].eagerly(),
      bind[Metrics].to[MetricsImpl].eagerly()
    )
  }
}
