package com.fan.rpc.remoting;

import com.fan.rpc.common.URL;
import com.fan.rpc.common.extension.ann.Adaptive;
import com.fan.rpc.common.extension.ann.SPI;
import com.fan.rpc.remoting.dispatcher.all.AllDispatcher;

@SPI(AllDispatcher.NAME)
public interface Dispatcher {

    @Adaptive
    FChannelHandler dispatch(FChannelHandler handler, URL url);
}
