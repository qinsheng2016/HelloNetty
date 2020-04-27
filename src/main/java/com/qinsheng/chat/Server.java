package com.qinsheng.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

public class Server {

    // 所有的客户端
    public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public void serverStart() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);

        try {
            ServerBootstrap b = new ServerBootstrap();
            ChannelFuture f = b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pl = socketChannel.pipeline();  // 责任链的处理方式
//                            pl.addLast(new TankMsgDecoder())
                                    pl.addLast(new ServerChildHandler());
                        }
                    }).bind(8988).sync();

            ServerFrame.INSTANCE.updateServerMsg("Server started");
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}

class ServerChildHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Server.clients.add(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf buf = null;
        try {
            buf = (ByteBuf)msg;

            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            String s = new String(bytes);

            if(s.equals("_bye_")) {
                System.out.println("客户端要求退出");
                Server.clients.remove(ctx.channel());
                ctx.close();
            } else {
                ServerFrame.INSTANCE.updateServerMsg(s);
                ServerFrame.INSTANCE.updateClientMsg(s);

                Server.clients.writeAndFlush(msg);
            }
        } finally {
//            if(buf != null) {
//                ReferenceCountUtil.release(buf);
//            }
        }


//        System.out.println("Channel Read");
//        try {
//            TankMsg tm = (TankMsg)msg;
//            System.out.println(tm);
//        } finally {
//            ReferenceCountUtil.release(msg);
//        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 删除出现异常的客户端
        Server.clients.remove(ctx.channel());
        ctx.close();
    }
}
