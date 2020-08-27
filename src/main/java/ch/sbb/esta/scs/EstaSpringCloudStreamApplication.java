package ch.sbb.esta.scs;

import io.opentracing.contrib.spring.cloud.solace.SolaceAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(SolaceAutoConfiguration.class)
public class EstaSpringCloudStreamApplication {

    public static void main(String[] args) {
        SpringApplication.run(EstaSpringCloudStreamApplication.class, args);
    }

}
