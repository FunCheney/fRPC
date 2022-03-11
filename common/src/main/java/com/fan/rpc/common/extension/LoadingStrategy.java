package com.fan.rpc.common.extension;

import com.fan.rpc.common.extension.lang.Prioritized;

public interface LoadingStrategy extends Prioritized {

    String directory();

    default boolean preferExtensionClassLoader() {
        return false;
    }

    default String[] excludedPackages() {
        return null;
    }

    default boolean overridden() {
        return false;
    }
}
