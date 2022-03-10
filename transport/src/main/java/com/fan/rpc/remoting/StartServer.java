package com.fan.rpc.remoting;

import com.fan.rpc.remoting.netty.NettyServer;

public class StartServer {
    public static void main(String[] args) {
        try {
            StartServer server = new StartServer();
            server.start();

        } catch (Throwable e) {
        }
    }

    public void start() throws Throwable {
        new NettyServer().doStart();
    }

}
