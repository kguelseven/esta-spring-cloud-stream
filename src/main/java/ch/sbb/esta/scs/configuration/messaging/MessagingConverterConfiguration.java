package ch.sbb.esta.scs.configuration.messaging;

import ch.sbb.esta.scs.converter.BookXmlConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;

@Configuration
public class MessagingConverterConfiguration {

    @Bean
    public MessageConverter transferMessageConverter() {
        return new BookXmlConverter();
    }

}
