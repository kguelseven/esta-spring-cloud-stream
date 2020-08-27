package io.opentracing.contrib.solace.spring;

import io.opentracing.References;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.contrib.spring.integration.messaging.MessageTextMap;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.Format.Builtin;
import io.opentracing.tag.Tags;
import org.springframework.messaging.Message;

public final class SolaceTracingMessageUtils {

    public static final String OPERATION_NAME_SEND = "solace-send";
    public static final String OPERATION_NAME_RECEIVE = "solace-receive";
    public static final String COMPONENT_NAME = "spring-cloud-stream-solace";

    private SolaceTracingMessageUtils() {
    }

    /**
     * Build following span and finish it. Should be used by consumers/listeners
     *
     * @param message Solace message
     * @param tracer Tracer
     * @return child span context
     */
    public static SpanContext buildAndFinishChildSpan(Message<?> message, Tracer tracer) {
        if (message == null) {
            return null;
        }
        Span child = buildFollowingSpan(message, tracer);
        child.finish();
        return child.context();
    }

    /**
     * It is used by consumers only
     */
    public static Span buildFollowingSpan(Message<?> message, Tracer tracer) {
        SpanContext context = extract(message, tracer);

        final SpanBuilder spanBuilder = tracer
                .buildSpan(OPERATION_NAME_RECEIVE)
                .ignoreActiveSpan()
                .withTag(Tags.COMPONENT.getKey(), COMPONENT_NAME)
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CONSUMER);

        // if context is null this is a no-op
        spanBuilder.addReference(References.FOLLOWS_FROM, context);

        Span span = spanBuilder.start();

        SolaceSpanDecorator.onResponse(message, span);

        return span;
    }

    /**
     * Extract span context from JMS message properties or active span
     *
     * @param message Solace message
     * @param tracer Tracer
     * @return extracted span context
     */
    public static SpanContext extract(final Message<?> message, final Tracer tracer) {
        final SpanContext spanContext =
                tracer.extract(Format.Builtin.TEXT_MAP, new MessageTextMap(message));
        if (spanContext != null) {
            return spanContext;
        }

        final Span span = tracer.activeSpan();
        if (span != null) {
            return span.context();
        }
        return null;
    }

    /**
     * Inject span context to Solace message properties
     *
     * @param span span
     * @param message the message
     */
    public static Message<?> inject(final Span span, Message<?> message, final Tracer tracer) {
        final SolaceMessageTextMap textMap = new SolaceMessageTextMap(message);
        tracer.inject(span.context(), Builtin.TEXT_MAP, textMap);
        return textMap.getMessage();
    }

    /**
     * Build span and inject. Should be used by producers.
     *
     * @param message Solace message
     * @return span
     */
    public static Span buildSpan(/*Destination destination,*/ final Message message,
            final Tracer tracer) {
        final SpanBuilder spanBuilder = tracer.buildSpan(SolaceTracingMessageUtils.OPERATION_NAME_SEND)
                .ignoreActiveSpan()
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_PRODUCER);

        final SpanContext parent = extract(message, tracer);

        if (parent != null) {
            spanBuilder.asChildOf(parent);
        }

        final Span span = spanBuilder.start();

        // SolaceSpanDecorator.onRequest(destination, span);

        return span;
    }
}

