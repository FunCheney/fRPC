package com.fan.rpc.remoting.dispatcher;

import com.fan.rpc.common.URL;
import com.fan.rpc.remoting.FChannel;
import com.fan.rpc.remoting.FChannelHandler;
import com.fan.rpc.remoting.transport.ChannelHandlerDelegate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WrappedChannelHandler implements ChannelHandlerDelegate {

    protected final FChannelHandler handler;

    private final URL URL;


    public WrappedChannelHandler(FChannelHandler handler, URL URL) {
        this.handler = handler;
        this.URL = URL;
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

    public URL getUrl() {
        return URL;
    }

    @Override
    public FChannelHandler getHandler() {
        if (handler instanceof ChannelHandlerDelegate) {
            return ((ChannelHandlerDelegate) handler).getHandler();
        } else {
            return handler;
        }
    }
}
