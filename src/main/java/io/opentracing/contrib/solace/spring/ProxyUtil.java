package io.opentracing.contrib.solace.spring;

import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

public final class ProxyUtil {

    private ProxyUtil() {
    }

    public static <T> T createProxy(T proxiedInstance, final Object aspect) {
        final AspectJProxyFactory factory = new AspectJProxyFactory(proxiedInstance);
        factory.addAspect(aspect);
        return (T) factory.getProxy();
    }

}
