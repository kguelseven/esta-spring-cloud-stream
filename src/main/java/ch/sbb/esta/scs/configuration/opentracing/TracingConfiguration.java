package ch.sbb.esta.scs.configuration.opentracing;

import io.jaegertracing.internal.JaegerTracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfiguration {

    @Bean
    public static JaegerTracer jaegerTracer() {
        final io.jaegertracing.Configuration.SamplerConfiguration samplerConfig = io.jaegertracing.Configuration.SamplerConfiguration.fromEnv().withType("const").withParam(1);
        final io.jaegertracing.Configuration.ReporterConfiguration reporterConfig = io.jaegertracing.Configuration.ReporterConfiguration.fromEnv().withLogSpans(true);
        final io.jaegertracing.Configuration config = new io.jaegertracing.Configuration("esta-spring-cloud-stream-solace").withSampler(samplerConfig).withReporter(reporterConfig);
        return config.getTracer();
    }
}
