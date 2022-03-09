package com.fan.rpc.remoting.transport.dispatcher;

import com.fan.rpc.remoting.FChannelHandler;
import com.fan.rpc.remoting.netty.NettyChannel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WrappedChannelHandler implements FChannelHandler {

    protected final FChannelHandler handler;


    public WrappedChannelHandler(FChannelHandler handler) {
        this.handler = handler;
    }
    @Override
    public void received(NettyChannel channel, Object message) {
        handler.received(channel, message);
    }


    public ExecutorService getPreferredExecutorService(Object msg) {
        return getSharedExecutorService();
    }


    public ExecutorService getSharedExecutorService() {
        // 获取一个线程池
        ExecutorService executor = new ThreadPoolExecutor(4, 8, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        return executor;
    }

}
