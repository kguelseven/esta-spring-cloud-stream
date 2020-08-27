package io.opentracing.contrib.solace.spring;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;

@Aspect
@Configuration
public class SolaceInboundMessageTracingAspect {

    private final Tracer tracer;

    public SolaceInboundMessageTracingAspect(final Tracer tracer) {
        this.tracer = tracer;
    }

    /**
     * Before handle Message we have to set the Tracing properties.
     */
    @Around(
            value = "execution(* org.springframework.integration.core.MessageProducer.setOutputChannel(..)) && args(messageChannel)",
            argNames = "proceedingJoinPoint,messageChannel")
    public void traceSolaceHandleMessage(final ProceedingJoinPoint proceedingJoinPoint, final MessageChannel messageChannel) throws Throwable {
        final MessageChannel replacedMessageChannel = createTracingMessageChannel(messageChannel);
        proceedReplacingMessageChannel(proceedingJoinPoint, replacedMessageChannel, 0);
    }

    private MessageChannel createTracingMessageChannel(final MessageChannel messageChannel) {
        return (message, timeout) -> {
            final Span span = SolaceTracingMessageUtils.buildFollowingSpan(message, tracer);
            if (/**this.traceInLog &&*/span != null) {
                MDC.put("spanId", span.context().toSpanId());
                MDC.put("traceId", span.context().toTraceId());
            }
            try (final Scope ignored = tracer.activateSpan(span)) {
                return messageChannel.send(message, timeout);
            } finally {
                span.finish();
                // if (this.traceInLog) {
                MDC.remove("spanId");
                MDC.remove("traceId");
                //}
            }
        };
    }

    private Object proceedReplacingMessageChannel(final ProceedingJoinPoint proceedingJoinPoint,
            final MessageChannel replacedMessageChannel, final int messageArgumentIndex) throws Throwable {
        final Object[] args = proceedingJoinPoint.getArgs();
        args[messageArgumentIndex] = replacedMessageChannel;
        return proceedingJoinPoint.proceed(args);
    }

}
