package com.fan.rpc.common.extension;

public class FRpcInternalLoadingStrategy implements LoadingStrategy {
    @Override
    public String directory() {
        return "META-INF/frpc/internal/";
    }

    @Override
    public int getPriority() {
        return MAX_PRIORITY;
    }
}
