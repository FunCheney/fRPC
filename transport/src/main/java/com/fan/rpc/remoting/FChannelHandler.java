package com.fan.rpc.remoting;


import com.fan.rpc.remoting.netty.NettyChannel;

public interface FChannelHandler {


    void received(NettyChannel channel, Object message);
}
