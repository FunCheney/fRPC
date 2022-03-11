package com.fan.rpc.remoting.transport;

import com.fan.rpc.remoting.FChannel;
import com.fan.rpc.remoting.FChannelHandler;

public abstract class AbstractChannelHandler implements FChannelHandler {

    protected FChannelHandler handler;

    protected AbstractChannelHandler(FChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    public void received(FChannel channel, Object message) {
        handler.received(channel, message);
    }
}
