package com.fan.rpc.remoting.exchange;

import com.fan.rpc.remoting.FChannelHandler;
import com.fan.rpc.remoting.transport.AbstractChannelHandler;

public class HeartbeatHandler extends AbstractChannelHandler {

    public HeartbeatHandler(FChannelHandler handler) {
        super(handler);
    }
}
