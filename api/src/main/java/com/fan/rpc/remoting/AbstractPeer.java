package com.fan.rpc.remoting;

import com.fan.rpc.remoting.FChannel;
import com.fan.rpc.remoting.FChannelHandler;

public abstract class AbstractPeer implements FChannelHandler {

    @Override
    public void received(FChannel ch, Object msg) {

    }
}
