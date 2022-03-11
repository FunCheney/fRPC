package com.fan.rpc.remoting;


import com.fan.rpc.common.FURL;
import com.fan.rpc.common.extension.ann.Adaptive;
import com.fan.rpc.common.extension.ann.SPI;


@SPI("netty")
public interface Transporter {
    @Adaptive
    RemotingServer bind(FURL url, FChannelHandler handler);

}
