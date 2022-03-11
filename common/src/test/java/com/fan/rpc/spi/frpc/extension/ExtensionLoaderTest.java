package com.fan.rpc.spi.frpc.extension;

import com.fan.rpc.common.extension.ExtensionFactory;
import com.fan.rpc.common.extension.ExtensionLoader;
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
    public void test_getExtensionLoader() throws Exception {
        ExtensionLoader<ExtensionFactory> factory = getExtensionLoader(ExtensionFactory.class);
        ExtensionFactory adaptiveExtension = factory.getAdaptiveExtension();
        ExtensionFactory defaultExtension = factory.getDefaultExtension();

        ExtensionLoader<MySPISimple> extensionLoader = getExtensionLoader(MySPISimple.class);
    }

    @Test
    public void test_getExtension() throws Exception {
        // 这里根据名称(name)加载
        assertTrue(getExtensionLoader(MySPISimple.class).getExtension("impl1") instanceof Simple1);

    }

    @Test
    public void testGetLoadingStrategies() {
        List<LoadingStrategy> strategies = getLoadingStrategies();
        // todo 待完善
        for (LoadingStrategy s : strategies) {
            System.out.println(s);
        }

    }

    @Test
    public void testServiceLoader() {
        // 这里全量加载
        ServiceLoader<LoadingStrategy> load = ServiceLoader.load(LoadingStrategy.class);
        // todo 待完善
        for (LoadingStrategy next : load) {
            System.out.println(next);
        }


    }
}
