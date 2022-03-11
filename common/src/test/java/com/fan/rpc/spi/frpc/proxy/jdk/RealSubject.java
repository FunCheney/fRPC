package com.fan.rpc.spi.frpc.proxy.jdk;

public class RealSubject implements Subject {
    @Override
    public void operation() {
        System.out.println("我执行了。。。。。");
    }

}
