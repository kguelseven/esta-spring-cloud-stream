package io.opentracing.contrib.solace.spring;

import com.solace.spring.cloud.stream.binder.SolaceMessageChannelBinder;
import com.solace.spring.cloud.stream.binder.config.SolaceMessageChannelBinderConfiguration;
import com.solace.spring.cloud.stream.binder.properties.SolaceConsumerProperties;
import com.solace.spring.cloud.stream.binder.properties.SolaceExtendedBindingProperties;
import com.solace.spring.cloud.stream.binder.properties.SolaceProducerProperties;
import com.solace.spring.cloud.stream.binder.provisioning.SolaceQueueProvisioner;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.SpringJCSMPFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.integration.core.MessageProducer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import javax.annotation.PostConstruct;

@Configuration
@EnableConfigurationProperties({SolaceExtendedBindingProperties.class})
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {SolaceMessageChannelBinderConfiguration.class})})
public class SolaceMessageChannelBinderTracingConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(SolaceMessageChannelBinderTracingConfiguration.class);

    //    @Value("${io.opentracing.contrib.solace.spring.traceInLog:false}")
    //    private boolean traceInLog;

    private final SpringJCSMPFactory springJCSMPFactory;
    private final SolaceExtendedBindingProperties solaceExtendedBindingProperties;

    private JCSMPSession jcsmpSession;

    public SolaceMessageChannelBinderTracingConfiguration(
            final SpringJCSMPFactory springJCSMPFactory,
            final SolaceExtendedBindingProperties solaceExtendedBindingProperties) {
        this.springJCSMPFactory = springJCSMPFactory;
        this.solaceExtendedBindingProperties = solaceExtendedBindingProperties;
    }

    @PostConstruct
    private void initSession() throws JCSMPException {
        jcsmpSession = springJCSMPFactory.createSession();
        LOG.info(String.format("Connecting JCSMP session %s", jcsmpSession.getSessionName()));
        jcsmpSession.connect();
    }

    @Bean
    JCSMPSession jcsmpSession() {
        return jcsmpSession;
    }

    @Bean
    SolaceMessageChannelBinder solaceMessageChannelBinder(
            final SolaceOutboundMessageTracingAspect outputMessageTracingAspect,
            final SolaceInboundMessageTracingAspect inboundMessageTracingAspect) {
        final SolaceMessageChannelBinder binder = new SolaceMessageChannelBinder(jcsmpSession, provisioningProvider()) {
            @Override
            protected MessageHandler createProducerMessageHandler(ProducerDestination destination, ExtendedProducerProperties<SolaceProducerProperties> producerProperties,
                                                                  MessageChannel errorChannel) {
                final MessageHandler messageHandler = super.createProducerMessageHandler(destination, producerProperties, errorChannel);
                return ProxyUtil.createProxy(messageHandler, outputMessageTracingAspect);
            }

            @Override
            protected MessageProducer createConsumerEndpoint(ConsumerDestination destination, String group, ExtendedConsumerProperties<SolaceConsumerProperties> properties) {
                final MessageProducer messageProducer = super.createConsumerEndpoint(destination, group, properties);
                return ProxyUtil.createProxy(messageProducer, inboundMessageTracingAspect);
            }
        };
        binder.setExtendedBindingProperties(solaceExtendedBindingProperties);
        return binder;
    }

    @Bean
    SolaceQueueProvisioner provisioningProvider() {
        return new SolaceQueueProvisioner(jcsmpSession);
    }
}