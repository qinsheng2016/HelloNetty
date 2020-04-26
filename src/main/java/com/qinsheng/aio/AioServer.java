package com.qinsheng.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AioServer {

    public static void main(String[] args) throws Exception{
        final AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open()
                .bind(new InetSocketAddress(8888));

        serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel channel, Object attachment) {
                serverSocketChannel.accept(null, this);
                try {
                    System.out.println(channel.getRemoteAddress());
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    channel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            attachment.flip();
                            System.out.println(new String(attachment.array(), 0, result));
                            channel.write(ByteBuffer.wrap("HelloClient".getBytes()));
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });

        // 手动阻塞
//        while (true) {
//            Thread.sleep(1000);
//        }
    }

}
