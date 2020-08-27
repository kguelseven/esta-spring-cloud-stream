package io.opentracing.contrib.solace.spring;

import io.opentracing.Span;
import io.opentracing.Tracer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

@Aspect
@Configuration
public class SolaceOutboundMessageTracingAspect {

    private final Tracer tracer;

    public SolaceOutboundMessageTracingAspect(Tracer tracer) {
        this.tracer = tracer;
    }

    /**
     * Before handle Message we have to set the Tracing properties.
     */
    @Around(
            //  value = "execution(* com.solace.spring.cloud.stream.binder.outbound.JCSMPOutboundMessageHandler.handleMessage(..)) && args(message)",
            value = "execution(* org.springframework.messaging.MessageHandler.handleMessage(..)) && args(message)",
            argNames = "pjp,message")
    public Object traceSolaceHandleMessage(final ProceedingJoinPoint pjp, Message<?> message) throws Throwable {
        final Span span = SolaceTracingMessageUtils.buildSpan(/**message.getDestination(),*/message, this.tracer);

        final Message traceMessage = SolaceTracingMessageUtils.inject(span, message, tracer);
        try {
            return proceedReplacingMessage(pjp, traceMessage, 0);
        } finally {
            span.finish();
        }
    }

    private Object proceedReplacingMessage(final ProceedingJoinPoint proceedingJoinPoint,
            final Message convertedMessage, int messageArgumentIndex) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        args[messageArgumentIndex] = convertedMessage;
        return proceedingJoinPoint.proceed(args);
    }

}
