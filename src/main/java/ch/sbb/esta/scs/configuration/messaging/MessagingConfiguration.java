package ch.sbb.esta.scs.configuration.messaging;

import ch.sbb.esta.scs.book.Book;
import ch.sbb.esta.scs.components.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
public class MessagingConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(MessagingConfiguration.class);

    @Bean
    public Supplier<Flux<Book>> bookSupplierV1(final BookPublisher bookPublisher) {
        return bookPublisher::supplyBookEventProcessor;
    }

    @Bean
    public Function<Message<Book>, Message<Book>> bookFunctionV1(final BookForwardHandler bookForwardHandler) {
        return bookForwardHandler::processBook;
    }

    @Bean
    public Consumer<Book> bookConsumerV1(final BookCache bookCache) {
        return bookCache::saveBook;
    }

    @Bean
    public Function<Message<BookRequest>, Message<Book>> bookRequestReplyV1(final BookReplyHandler bookReplyHandler) {
        return bookReplyHandler::findBookById;
    }

    @Bean
    public Supplier<Flux<BookRequest>> bookRequestSupplierV1(final BookRequestHandler bookRequestHandler) {
        return bookRequestHandler::supplyBookRequestProcessor;
    }

    @Bean
    public Consumer<String> bookRawJsonConsumerV1() {
        return (json) -> LOG.info("STEP 2a: bookRawJsonConsumerV1 got: {}", json);
    }

    @Bean
    public Consumer<byte[]> bookRawXmlConsumerV1() {
        return (xml) -> LOG.info("STEP 4b: bookRawXmlConsumerV1 got: {}", new String(xml));
    }


}
