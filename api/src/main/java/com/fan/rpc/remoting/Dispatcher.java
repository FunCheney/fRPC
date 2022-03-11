package com.fan.rpc.remoting;

import com.fan.rpc.common.FURL;
import com.fan.rpc.common.extension.ann.Adaptive;
import com.fan.rpc.common.extension.ann.SPI;
import com.fan.rpc.remoting.dispatcher.all.AllDispatcher;

@SPI(AllDispatcher.NAME)
public interface Dispatcher {

    @Adaptive({Constants.DISPATCHER_KEY, "dispather", "channel.handler"})
    FChannelHandler dispatch(FChannelHandler handler, FURL url);
}