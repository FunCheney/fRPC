package com.fan.rpc.remoting.transport;

import com.fan.rpc.remoting.RemotingServer;

import java.net.InetSocketAddress;

public abstract class AbstractServer extends AbstractPeer implements RemotingServer {

    private InetSocketAddress bindAddress;

    protected abstract void doConnect() throws Throwable;

    protected abstract void doClose() throws Throwable;

    public InetSocketAddress getBindAddress() {
        return bindAddress;
    }
}