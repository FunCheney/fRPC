package com.fan.rpc.remoting;


import com.fan.rpc.common.URL;
import com.fan.rpc.common.extension.ann.Adaptive;
import com.fan.rpc.common.extension.ann.SPI;


@SPI("netty")
public interface Transporter {
    @Adaptive
    RemotingServer bind(URL url, FChannelHandler handler);

}
