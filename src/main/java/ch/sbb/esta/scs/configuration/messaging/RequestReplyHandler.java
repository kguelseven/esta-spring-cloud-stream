package ch.sbb.esta.scs.configuration.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solacesystems.jcsmp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.function.context.FunctionRegistry;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class RequestReplyHandler<T> implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(RequestReplyHandler.class);

    private final Class<T> payloadClass;
    private final Topic topic;
    private final CountDownLatch latch;
    private AtomicReference<BytesXMLMessage> response;
    private AtomicReference<JCSMPException> errorResponse;
    private XMLMessageConsumer consumer;
    private JCSMPSession session;
    private final FunctionRegistry functionRegistry;
    private final ObjectMapper objectMapper;

    public RequestReplyHandler(final Class<T> payloadClass, final String topicName, final JCSMPSession session, final FunctionRegistry functionRegistry, final ObjectMapper objectMapper) {
        this.payloadClass = payloadClass;
        this.topic = JCSMPFactory.onlyInstance().createTopic(topicName);
        this.session = session;
        this.functionRegistry = functionRegistry;
        this.objectMapper = objectMapper;
        latch = new CountDownLatch(1);
    }

    @Override
    public void run() {
        try {
            session.addSubscription(topic);
            LOG.info("addSubscription {} - {}", topic.toString(), topic.isTemporary());

            response = new AtomicReference<>();
            errorResponse = new AtomicReference<>();


            consumer = session.getMessageConsumer(new XMLMessageListener() {

                @Override
                public void onReceive(final BytesXMLMessage msg) {
                    response.set(msg);
                    latch.countDown();  // unblock main thread
                }

                @Override
                public void onException(JCSMPException e) {
                    errorResponse.set(e);
                    latch.countDown();  // unblock main thread
                }
            });
            consumer.start();
        } catch (JCSMPException e) {
            e.printStackTrace();
        }
    }

    public T getReply() {
        try {
            latch.await(20, TimeUnit.SECONDS); // block here until message received, and latch will flip
        } catch (InterruptedException e) {
           LOG.info("I was awoken while waiting");
        }
        try {
            session.removeSubscription(topic);
            consumer.stopSync();
        } catch (JCSMPException e) {
            e.printStackTrace();
        }

        if (errorResponse.get() != null) {
            throw new RuntimeException(errorResponse.get());
        }
        //Set<String> names = functionRegistry.getNames(MessageConverter.class);


        final BytesXMLMessage bytesXMLMessage = response.get();
        final String dump = bytesXMLMessage.dump();
        LOG.info("Got {}", dump);

        final byte[] payload = new byte[bytesXMLMessage.getAttachmentContentLength()];
        bytesXMLMessage.readAttachmentBytes(payload);
        final String jsonString = new String(payload);

        try {
            return objectMapper.readValue(jsonString, payloadClass);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
