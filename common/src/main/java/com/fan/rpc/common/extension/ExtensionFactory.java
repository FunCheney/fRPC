package com.fan.rpc.common.extension;

import com.fan.rpc.common.extension.ann.SPI;

@SPI
public interface ExtensionFactory {

    <T> T getExtension(Class<T> type, String name);
}
