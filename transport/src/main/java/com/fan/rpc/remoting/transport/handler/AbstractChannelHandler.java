package com.fan.rpc.remoting.transport.handler;

import com.fan.rpc.remoting.FChannelHandler;
import com.fan.rpc.remoting.netty.NettyChannel;
import io.netty.channel.Channel;

public abstract class AbstractChannelHandler implements FChannelHandler {

    protected FChannelHandler handler;

    protected AbstractChannelHandler(FChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    public void received(NettyChannel channel, Object message) {
        handler.received(channel, message);
    }
}
