package com.fan.rpc.remoting.dispatcher;

import com.fan.rpc.common.FURL;
import com.fan.rpc.remoting.FChannel;
import com.fan.rpc.remoting.FChannelHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WrappedChannelHandler implements FChannelHandler {

    protected final FChannelHandler handler;

    private final FURL furl;


    public WrappedChannelHandler(FChannelHandler handler, FURL furl) {
        this.handler = handler;
        this.furl = furl;
    }

    @Override
    public void received(FChannel channel, Object message) {
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

    public FURL getUrl() {
        return furl;
    }

}
