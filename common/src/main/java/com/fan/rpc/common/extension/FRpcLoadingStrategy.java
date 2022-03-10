package com.fan.rpc.common.extension;

public class FRpcLoadingStrategy implements LoadingStrategy{

    @Override
    public String directory() {
        return "META-INF/frpc/";
    }

    @Override
    public boolean overridden() {
        return true;
    }

}
