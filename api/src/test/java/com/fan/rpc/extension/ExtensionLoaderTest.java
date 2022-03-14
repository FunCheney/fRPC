package com.fan.rpc.extension;

import com.fan.rpc.common.extension.ExtensionFactory;
import com.fan.rpc.common.extension.ExtensionLoader;
import com.fan.rpc.remoting.Dispatcher;
import com.fan.rpc.remoting.dispatcher.all.AllDispatcher;
import org.junit.jupiter.api.Test;

import static com.fan.rpc.common.extension.ExtensionLoader.getExtensionLoader;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExtensionLoaderTest {


    @Test
    public void test_getExtension() throws Exception {
        ExtensionLoader<ExtensionFactory> factory = getExtensionLoader(ExtensionFactory.class);
        ExtensionFactory adaptiveExtension = factory.getAdaptiveExtension();
        ExtensionFactory defaultExtension = factory.getDefaultExtension();

        ExtensionFactory all = factory.getExtension("all");
        ExtensionLoader<AllDispatcher> extensionLoader1 = getExtensionLoader(AllDispatcher.class);
        // 这里根据名称(name)加载
        ExtensionLoader<Dispatcher> extensionLoader = getExtensionLoader(Dispatcher.class);
        System.out.println(extensionLoader);
        assertTrue(getExtensionLoader(Dispatcher.class).getExtension("all") instanceof AllDispatcher);


    }

}
