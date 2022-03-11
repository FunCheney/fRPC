package com.fan.rpc.spi.frpc.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DemoInvokerHandler implements InvocationHandler {
    // 目标对象，也就是被代理的对象
    private Object target;

    public DemoInvokerHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("输出日志。。。。。。。开始");
        Object invoke = method.invoke(target, args);
        System.out.println("输出日志。。。。。。。结束");
        return invoke;
    }

    public Object getProxy() {
        // 创建代理对象
        return Proxy.newProxyInstance(Thread.currentThread()
                        .getContextClassLoader(),
                target.getClass().getInterfaces(), this);
    }
}
