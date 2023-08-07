package com.springbook.learningtest.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class UppercaseHandler implements InvocationHandler {
    Hello target;

    public UppercaseHandler(Hello target) {
        this.target = target;
    }

    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable {
        String ret = (String)method.invoke(target, args);
        return  ret.toUpperCase();
    }

    Hello proxiedHello  = (Hello) Proxy.newProxyInstance(
            getClass().getClassLoader(),
            new Class[] {Hello.class},
            new UppercaseHandler(new HelloTarget()));
}
