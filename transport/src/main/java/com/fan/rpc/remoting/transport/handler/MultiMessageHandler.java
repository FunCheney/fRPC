package com.fan.rpc.remoting.transport.handler;

import com.fan.rpc.remoting.FChannel;
import com.fan.rpc.remoting.FChannelHandler;
import com.fan.rpc.remoting.netty.NettyChannel;

public class MultiMessageHandler extends AbstractChannelHandler {


    protected MultiMessageHandler(FChannelHandler handler) {
        super(handler);
    }

    @Override
    public void received(FChannel channel, Object message) {

        handler.received(channel, message);
    }
}
