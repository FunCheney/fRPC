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
package com.fun.remote.transport.netty;

import com.fan.rpc.common.URL;
import com.fan.rpc.common.utils.NetUtils;
import com.fan.rpc.remoting.ChannelHandlerAdapter;
import com.fan.rpc.remoting.Constants;
import com.fan.rpc.remoting.RemotingServer;
import com.fan.rpc.remoting.netty.NettyTransporter;
import org.junit.jupiter.api.Test;


public class NettyTransporterTest {
    @Test
    public void shouldAbleToBindNetty4() throws Exception {
        int port = NetUtils.getAvailablePort();
        URL url = new URL("http", "localhost", port,
                new String[]{Constants.BIND_PORT_KEY, String.valueOf(port)});
        RemotingServer server = new NettyTransporter().bind(url, new ChannelHandlerAdapter());
    }

    @Test
    public void shouldConnectToNetty4Server() throws Exception {
//        final CountDownLatch lock = new CountDownLatch(1);
//
//        int port = NetUtils.getAvailablePort();
//        URL url = new URL("http", "localhost", port,
//                new String[]{Constants.BIND_PORT_KEY, String.valueOf(port)});
//
//        new NettyTransporter().bind(url, new ChannelHandlerAdapter() {
//
//            @Override
//            public void connected(Channel channel) {
//                lock.countDown();
//            }
//        });
//        new NettyTransporter().connect(url, new ChannelHandlerAdapter() {
//            @Override
//            public void sent(Channel channel, Object message) throws RemotingException {
//                channel.send(message);
//                channel.close();
//            }
//        });

//        lock.await();
    }
}
