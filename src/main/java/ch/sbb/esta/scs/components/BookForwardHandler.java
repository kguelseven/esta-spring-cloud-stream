package ch.sbb.esta.scs.components;

import ch.sbb.esta.scs.book.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class BookForwardHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BookForwardHandler.class);

    /**
     * @param bookMessage a book message
     * @return the forwarding message
     */
    public Message<Book> processBook(final Message<Book> bookMessage) {
        LOG.info("STEP 2a: Got book message: {} - {}", bookMessage.getHeaders(), bookMessage.getPayload());

        // Book enrichement, whatever
        final Message<Book> forwardMessage = MessageBuilder
                .withPayload(bookMessage.getPayload())
                .build();
        LOG.info("STEP 3: Forwarding book message: {} - {}", forwardMessage.getHeaders(), forwardMessage.getPayload());

        return forwardMessage;
    }

}
