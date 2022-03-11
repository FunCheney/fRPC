package com.fan.rpc.spi.frpc.extension;

import com.fan.rpc.common.extension.LoadingStrategy;
import com.fan.rpc.spi.frpc.extension.spi.MySPISimple;
import com.fan.rpc.spi.frpc.extension.spi.Simple1;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ServiceLoader;

import static com.fan.rpc.common.extension.ExtensionLoader.getExtensionLoader;
import static com.fan.rpc.common.extension.ExtensionLoader.getLoadingStrategies;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExtensionLoaderTest {

    @Test
    public void test_getExtension() throws Exception {
        assertTrue(getExtensionLoader(MySPISimple.class).getExtension("impl1") instanceof Simple1);

    }

    @Test
    public void testGetLoadingStrategies() {
        List<LoadingStrategy> strategies = getLoadingStrategies();
        for (LoadingStrategy s: strategies) {
            System.out.println(s);
        }

    }

    @Test
    public void testServiceLoader(){

        ServiceLoader<LoadingStrategy> load = ServiceLoader.load(LoadingStrategy.class);
        for (LoadingStrategy next : load) {
            System.out.println(next);
        }


    }
}
