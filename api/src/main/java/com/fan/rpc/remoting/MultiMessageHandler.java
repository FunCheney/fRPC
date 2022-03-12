package com.fan.rpc.remoting;

import com.fan.rpc.remoting.FChannel;
import com.fan.rpc.remoting.FChannelHandler;
import com.fan.rpc.remoting.transport.AbstractChannelHandler;

public class MultiMessageHandler extends AbstractChannelHandler {


    public MultiMessageHandler(FChannelHandler handler) {
        super(handler);
    }

    @Override
    public void received(FChannel channel, Object message) {

        handler.received(channel, message);
    }
}
