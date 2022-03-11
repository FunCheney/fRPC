package com.fan.rpc.remoting;

import com.fan.rpc.common.extension.ExtensionLoader;

public class Transporters {

    public static Transporter getTransporter() {
        return ExtensionLoader.getExtensionLoader(Transporter.class).getAdaptiveExtension();
    }
}
