package ch.sbb.esta.scs.messaging;


import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import static ch.sbb.esta.scs.messaging.MessagingUtil.SEND_TO_DESTINATION;

/**
 * Support for replies to request.
 */
@Component
public class RequestReplySupport {


    public <T> Message<T> createResponseMessage(final T payload, final String requestId, final String replyToDestination) {
        return MessageBuilder
                .withPayload(payload)
                .setHeader(SEND_TO_DESTINATION, replyToDestination + "/" + requestId)
                .build();
    }

}