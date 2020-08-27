package ch.sbb.esta.scs.components;

import ch.sbb.esta.scs.book.Book;
import ch.sbb.esta.scs.configuration.messaging.BookRequest;
import ch.sbb.esta.scs.configuration.messaging.RequestReplyHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solacesystems.jcsmp.JCSMPSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.function.context.FunctionRegistry;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

@Component
public class BookRequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BookRequestHandler.class);

    private static final String REPLY_TO_HEADER_NAME = "replyTo";

    private final EmitterProcessor<BookRequest> bookRequestProcessor = EmitterProcessor.create();
    private final JCSMPSession session;
    private final FunctionRegistry functionRegistry;
    private final ObjectMapper objectMapper;
    private final String replyDestination;
    private final ExecutorService executorService;

    public BookRequestHandler(final JCSMPSession session, final FunctionRegistry functionRegistry, final ObjectMapper objectMapper,
                              @Value("${spring.cloud.stream.solace.default.prefix}") final String prefix,
                              @Value("${spring.cloud.stream.bindings.bookRequestReplyV1-out-0.destination}") final String replyDestination) {
        this.session = session;
        this.functionRegistry = functionRegistry;
        this.objectMapper = objectMapper;
        this.replyDestination = prefix + replyDestination;
        executorService = Executors.newSingleThreadExecutor();
    }

    public Book requestBookWithId(final Long bookId) {
        LOG.info("Step 5: Requesting book with id {}", bookId);

        final String requestId = UUID.randomUUID().toString();
        final BookRequest bookRequest = BookRequest.builder()
                .bookId(bookId)
                .requestId(requestId)
                .build();

        final RequestReplyHandler<Book> requestReplyHandler = new RequestReplyHandler<>(Book.class, createEndpointName(requestId), session, functionRegistry, objectMapper);
        executorService.submit(requestReplyHandler);

        bookRequestProcessor.onNext(bookRequest);

        final Book reply = requestReplyHandler.getReply();

        LOG.info("STEP 8: received book {}", reply);

        return reply;
    }

    private String createEndpointName(final String replyId) {
        return replyDestination + "/" + replyId;
    }

    public Flux<BookRequest> supplyBookRequestProcessor() {
        return bookRequestProcessor.onErrorContinue(RuntimeException.class, errorHandler());
    }

    private BiConsumer<Throwable, Object> errorHandler() {
        return (throwable, message) -> LOG.warn("Could not publish message: " + message, throwable);
    }


}
