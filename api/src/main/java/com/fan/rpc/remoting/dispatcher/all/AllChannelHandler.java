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
package com.fan.rpc.remoting.dispatcher.all;


import com.fan.rpc.common.FURL;
import com.fan.rpc.common.URL;
import com.fan.rpc.remoting.FChannel;
import com.fan.rpc.remoting.FChannelHandler;
import com.fan.rpc.remoting.dispatcher.ChannelEventRunnable;
import com.fan.rpc.remoting.dispatcher.WrappedChannelHandler;

import java.util.concurrent.ExecutorService;

public class AllChannelHandler extends WrappedChannelHandler {


    public AllChannelHandler(FChannelHandler handler, FURL url) {
        super(handler, url);
    }

    @Override
    public void received(FChannel channel, Object message) {

        ExecutorService executor = getPreferredExecutorService(message);
        try {
            executor.execute(new ChannelEventRunnable(channel, handler, ChannelEventRunnable.ChannelState.RECEIVED, message));
        } catch (Throwable t) {

        }
    }
}