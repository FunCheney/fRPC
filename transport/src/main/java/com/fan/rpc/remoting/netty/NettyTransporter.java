package com.fan.rpc.remoting.netty;

import com.fan.rpc.common.FURL;
import com.fan.rpc.remoting.FChannelHandler;
import com.fan.rpc.remoting.RemotingServer;
import com.fan.rpc.remoting.Transporter;

public class NettyTransporter implements Transporter {
    @Override
    public RemotingServer bind(FURL url, FChannelHandler handler) {

        return new NettyServer(url, handler);
    }
}
