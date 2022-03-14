package com.fan.rpc.remoting.exchange;

import com.fan.rpc.remoting.FChannel;
import com.fan.rpc.remoting.FChannelHandler;
import com.fan.rpc.remoting.transport.AbstractChannelHandler;

public class HeartbeatHandler extends AbstractChannelHandler {


    public static final String KEY_READ_TIMESTAMP = "READ_TIMESTAMP";

    public static final String KEY_WRITE_TIMESTAMP = "WRITE_TIMESTAMP";

    public HeartbeatHandler(FChannelHandler handler) {
        super(handler);
    }


    @Override
    public void received(FChannel channel, Object message) {

        handler.received(channel, message);
    }

}
