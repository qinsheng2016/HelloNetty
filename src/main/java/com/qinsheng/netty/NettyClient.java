package com.qinsheng.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/*
 * Netty 中所有的方法都是异步的
 *
 *
*/
public class NettyClient {

    public static void main(String[] args) throws Exception {
        // 线程池
        // event 事件，网络上的IO事件，能不能连上，能不能写，能不能读
        // 客户端一般设置1个线程就行，默认CPU核数的两倍
        EventLoopGroup group = new NioEventLoopGroup(1);
        // 辅助启动类
        Bootstrap b = new Bootstrap();
        try {
            ChannelFuture channelFuture = b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelInitializer())    // channel 初始化的时候调用的，在连接后才会调用的handler
                    .connect("127.0.0.1", 8888) // connect 是个异步方法
                    .sync();    // 确认连接成功才能往下执行

            System.out.println(channelFuture.isSuccess());

            // 因为connect是异步方法，如果不加sync方法，需要增加一个监听器来确认有没有连上
            /* channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(!channelFuture.isSuccess()) {
                        System.out.println("not connected");
                    } else {
                        System.out.println("connected");
                    }
                }
            });
            // 监听器也需要等sync完成，再判断有没有连上
            channelFuture.sync();
            System.out.println("..."); */
        }  finally {
            group.shutdownGracefully();
        }
    }

}
