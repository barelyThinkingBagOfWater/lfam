package ch.xavier.common.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micronaut.configuration.metrics.aggregator.MeterRegistryConfigurer;

import javax.inject.Singleton;

@Singleton
public class MetricsConfigurer implements MeterRegistryConfigurer {

    @Override
    public void configure(MeterRegistry meterRegistry) {
        meterRegistry.config().commonTags("application", "cache-movies-manager");
    }

    @Override
    public boolean supports(MeterRegistry meterRegistry) {
        return true;
    }

    @Override
    public Class getType() {
        return SimpleMeterRegistry.class;
    }
}
