package com.fan.rpc.spi.frpc.proxy;

import com.fan.rpc.spi.frpc.proxy.cglib.CglibProxy;
import com.fan.rpc.spi.frpc.proxy.cglib.CglibTest;
import com.fan.rpc.spi.frpc.proxy.jdk.DemoInvokerHandler;
import com.fan.rpc.spi.frpc.proxy.jdk.RealSubject;
import com.fan.rpc.spi.frpc.proxy.jdk.Subject;
import org.junit.jupiter.api.Test;

public class ProxyTest {
    @Test
    public void jdkProxyTest() {
        RealSubject realSubject = new RealSubject();
        DemoInvokerHandler demoInvokerHandler = new DemoInvokerHandler(realSubject);
        Subject proxy = (Subject) demoInvokerHandler.getProxy();
        proxy.operation();
    }

    @Test
    public void cglibProxyTest() {
        CglibProxy cglibProxy = new CglibProxy();
        CglibTest proxy = (CglibTest) cglibProxy.getProxy(CglibTest.class);
        proxy.sayCglib();
    }
}
