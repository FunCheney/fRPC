package com.fan.rpc.remoting;


import com.fan.rpc.common.extension.ann.SPI;

@SPI
public interface FChannelHandler {

    void received(FChannel channel, Object message);
}
