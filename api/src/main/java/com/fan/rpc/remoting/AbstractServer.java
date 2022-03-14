package com.fan.rpc.remoting;

import com.fan.rpc.common.URL;

import java.net.InetSocketAddress;

public abstract class AbstractServer extends AbstractPeer implements RemotingServer {

    private InetSocketAddress bindAddress;


    public AbstractServer(URL url, FChannelHandler handler) {

        try {
            doStart();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public abstract void doStart() throws Throwable;

    protected abstract void doClose() throws Throwable;

    public InetSocketAddress getBindAddress() {

        return new InetSocketAddress(9999);
    }
}
