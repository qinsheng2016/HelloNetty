package com.qinsheng.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

public class Client {

    private Channel channel = null;

    public void connect() {
        EventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap b = new Bootstrap();

        try {
            ChannelFuture f = b.group(group).channel(NioSocketChannel.class)
                    .handler(new ClientChannelInitializer()).connect("localhost", 8988);
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(!channelFuture.isSuccess()) {
                        System.out.println("not connected");
                    } else {
                        System.out.println("connected");
                        // 连接成功后，channel初始化
                        channel = channelFuture.channel();
                    }
                }
            });

            f.sync();
            // wait until close
            f.channel().closeFuture().sync();
            System.out.println("已经退出");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public void send(String message) {
        // 向服务端发送数据
        ByteBuf buf = Unpooled.copiedBuffer(message.getBytes());
        channel.writeAndFlush(buf);
    }

    public static void main(String[] args) {
        Client c = new Client();
        c.connect();
    }

    public void closeConnect() {
        this.send("_Bye_");
    }

}

class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
//                .addLast(new TankMsgEncoder())
                .addLast(new ClientHandler());
    }
}

class ClientHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        ctx.writeAndFlush(new TankMsg(5, 8));
        // channel 创建成功后， 向服务端发送第一条数据
        ByteBuf buf = Unpooled.copiedBuffer("Hello".getBytes());
        ctx.writeAndFlush(buf);

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;
        try {
            buf = (ByteBuf)msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            String messageAccepted = new String(bytes);
            ClientFrame.INSTANCE.updateText(messageAccepted);
        } finally {
            if (buf != null) {
                ReferenceCountUtil.release(buf);
            }
        }
    }
}