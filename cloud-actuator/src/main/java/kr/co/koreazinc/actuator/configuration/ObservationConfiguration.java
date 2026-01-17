package kr.co.koreazinc.actuator.configuration;

import org.springframework.boot.actuate.autoconfigure.observation.ObservationRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import net.ttddyy.observation.tracing.ConnectionTracingObservationHandler;
import net.ttddyy.observation.tracing.HikariJdbcObservationFilter;
import net.ttddyy.observation.tracing.QueryTracingObservationHandler;
import net.ttddyy.observation.tracing.ResultSetTracingObservationHandler;

@Configuration(proxyBeanMethods = false)
public class ObservationConfiguration {

    @Bean
    public ObservationRegistryCustomizer<ObservationRegistry> customObservationRegistry(Tracer tracer) {
        return registry -> {
            registry.observationConfig().observationFilter(new HikariJdbcObservationFilter());
            registry.observationConfig().observationHandler(new ConnectionTracingObservationHandler(tracer));
            registry.observationConfig().observationHandler(new QueryTracingObservationHandler(tracer));
            registry.observationConfig().observationHandler(new ResultSetTracingObservationHandler(tracer));
        };
    }
}