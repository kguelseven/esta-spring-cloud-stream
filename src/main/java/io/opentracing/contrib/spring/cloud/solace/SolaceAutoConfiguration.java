package io.opentracing.contrib.spring.cloud.solace;

import com.solace.spring.cloud.stream.binder.SolaceMessageChannelBinder;
import com.solacesystems.jcsmp.XMLMessage;
import io.opentracing.Tracer;
import io.opentracing.contrib.solace.spring.SolaceMessageChannelBinderTracingConfiguration;
import io.opentracing.contrib.spring.tracer.configuration.TracerAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Mike Rothenbühler
 */
@Configuration
@ConditionalOnClass({XMLMessage.class, SolaceMessageChannelBinder.class})
@ConditionalOnBean(Tracer.class)
@AutoConfigureAfter(TracerAutoConfiguration.class)
@ConditionalOnProperty(name = "opentracing.spring.cloud.solace.enabled", havingValue = "true", matchIfMissing = true)
@Import(SolaceMessageChannelBinderTracingConfiguration.class)
public class SolaceAutoConfiguration {

}
