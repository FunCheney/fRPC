package com.fan.rpc.remoting.transport;

import com.fan.rpc.remoting.FChannel;
import com.fan.rpc.remoting.FChannelHandler;
import com.fan.rpc.remoting.netty.NettyChannel;

public abstract class AbstractPeer implements FChannelHandler {

    @Override
    public void received(FChannel ch, Object msg){

    }
}
