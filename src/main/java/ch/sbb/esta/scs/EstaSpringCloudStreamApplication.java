package ch.sbb.esta.scs;

import io.opentracing.contrib.spring.integration.messaging.OpenTracingChannelInterceptorAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = OpenTracingChannelInterceptorAutoConfiguration.class)
public class EstaSpringCloudStreamApplication {

    public static void main(String[] args) {
        SpringApplication.run(EstaSpringCloudStreamApplication.class, args);
    }

}
