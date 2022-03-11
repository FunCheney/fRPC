package com.fan.rpc.common;

import java.io.Serializable;

public class FURL implements Serializable {

    public FURL(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private String host;
    private int port;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
