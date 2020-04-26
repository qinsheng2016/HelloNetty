package com.qinsheng.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class ClientChannelInitializer extends ChannelInitializer {
    @Override
    protected void initChannel(Channel channel) throws Exception {
        System.out.println(channel);
    }
}
