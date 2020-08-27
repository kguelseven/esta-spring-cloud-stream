package io.opentracing.contrib.solace.spring;

import io.opentracing.contrib.spring.integration.messaging.MessageTextMap;
import io.opentracing.propagation.TextMap;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

public class SolaceMessageTextMap<T> implements TextMap {

    private final MessageTextMap<T> delegate;

    public SolaceMessageTextMap(final Message<T> message) {
        final Message<T> filteredMessage = filteredMessage(message);
        delegate = new MessageTextMap<>(filteredMessage);
    }

    @Override
    public Iterator<Entry<String, String>> iterator() {
        return delegate.iterator();
    }

    @Override
    public void put(final String key, String value) {
        delegate.put(key, value);
    }

    public Message<T> getMessage() {
        return delegate.getMessage();
    }

    private Message<T> filteredMessage(final Message<T> message) {
        return MessageBuilder.withPayload(message.getPayload())
                .copyHeaders(filteredHeaders(message.getHeaders()))
                .build();
    }

    private Map<String, Object> filteredHeaders(final MessageHeaders headers) {
        return headers.entrySet().stream()
                .filter(entry -> isValid(entry.getValue()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    private boolean isValid(final Object value) {
        return value instanceof Serializable;
    }
}
