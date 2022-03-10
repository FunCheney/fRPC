package com.fan.rpc.spi.frpc.exention;

import com.fan.rpc.spi.frpc.exention.spi.MySPISimple;
import com.fan.rpc.spi.frpc.exention.spi.Simple1;
import org.junit.jupiter.api.Test;

import static com.fan.rpc.common.extension.ExtensionLoader.getExtensionLoader;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ExtensionLoaderTest {

    @Test
    public void test_getExtension() throws Exception {
        Simple1 impl1 = (Simple1)getExtensionLoader(MySPISimple.class).getExtension("impl1");
        assertNull(impl1);
    }
}
