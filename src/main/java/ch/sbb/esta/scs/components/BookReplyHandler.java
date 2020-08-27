package ch.sbb.esta.scs.components;

import ch.sbb.esta.scs.book.Book;
import ch.sbb.esta.scs.configuration.messaging.BookRequest;
import ch.sbb.esta.scs.messaging.RequestReplySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class BookReplyHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BookReplyHandler.class);

    //   private static final String REPLY_TO_HEADER_NAME = "replyTo";

    private final BookCache bookCache;
    private final RequestReplySupport requestReplySupport;
    private String replyToDestination;

    public BookReplyHandler(final BookCache bookCache, final RequestReplySupport requestReplySupport,
                            @Value("${spring.cloud.stream.bindings.bookRequestReplyV1-out-0.destination}") final String replyToDestination) {
        this.bookCache = bookCache;
        this.requestReplySupport = requestReplySupport;
        this.replyToDestination = replyToDestination;
    }

    public Message<Book> findBookById(final Message<BookRequest> bookRequestMessage) {
        LOG.info("STEP 6: Got: {}", bookRequestMessage);
        final BookRequest bookRequest = bookRequestMessage.getPayload();
        final Book book = bookCache.findBookById(bookRequest.getBookId());


        final Message<Book> respond = requestReplySupport.createResponseMessage(book, bookRequest.getRequestId(), replyToDestination);
        LOG.info("STEP 7: Respondig with {}", respond);

        return respond;
    }

}
