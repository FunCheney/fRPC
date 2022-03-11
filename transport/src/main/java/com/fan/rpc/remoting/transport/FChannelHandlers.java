/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fan.rpc.remoting.transport;


import com.fan.rpc.common.FURL;
import com.fan.rpc.common.extension.ExtensionLoader;
import com.fan.rpc.remoting.Dispatcher;
import com.fan.rpc.remoting.FChannelHandler;
import com.fan.rpc.remoting.exchange.HeartbeatHandler;
import com.fan.rpc.remoting.transport.handler.MultiMessageHandler;

public class FChannelHandlers {

    private static FChannelHandlers INSTANCE = new FChannelHandlers();

    protected FChannelHandlers() {
    }

    public static FChannelHandler wrap(FChannelHandler handler, FURL url) {
        // 单例，包装 handler
        return FChannelHandlers.getInstance().wrapInternal(handler, url);
    }

    protected static FChannelHandlers getInstance() {
        return INSTANCE;
    }

    static void setTestingChannelHandlers(FChannelHandlers instance) {
        INSTANCE = instance;
    }

    protected FChannelHandler wrapInternal(FChannelHandler handler, FURL url) {
        // 返回 MultiMessageHandler, 这里通过 new HeartbeatHandler() 创建 HeartbeatHandler 作为构造参数传入
        // ExtensionLoader.getExtensionLoader(Dispatcher.class).getAdaptiveExtension().dispatch(handler, url)
        // 该方法返回 AllChannelHandler 作为new HeartbeatHandler() 的参数
        return new MultiMessageHandler(new HeartbeatHandler(ExtensionLoader.getExtensionLoader(Dispatcher.class)
                .getAdaptiveExtension().dispatch(handler, url)));
    }
}
