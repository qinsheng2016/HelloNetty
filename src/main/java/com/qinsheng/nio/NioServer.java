package com.qinsheng.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open(); // 开门迎客，一个大管家来回巡逻
        ssc.socket().bind(new InetSocketAddress("127.0.0.1", 8888));
        ssc.configureBlocking(false);

        System.out.println("Server started, listening on :" + ssc.getLocalAddress());
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT); // 大管家注册到这个ssc，并且只关注请求连接

        while (true) {
            selector.select();  // 阻塞方法，所有的通道里有大客家关心的事件发生了，就会返回
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()) {
                SelectionKey selectionKey = it.next();
                it.remove();
                handle(selectionKey);
            }
        }
    }

    private static void handle(SelectionKey selectionKey) {
        if(selectionKey.isAcceptable()) {
            try {
                ServerSocketChannel ssc = (ServerSocketChannel) selectionKey.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(selectionKey.selector(), SelectionKey.OP_READ);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
        } else if (selectionKey.isReadable()) {
            SocketChannel sc = null;
            try {
                sc = (SocketChannel)selectionKey.channel();
                // 只有一个指针，读指针也是它，写指针也是它
                ByteBuffer buffer = ByteBuffer.allocate(512);
                buffer.clear();
                int len = sc.read(buffer);

                if(len != -1 ){
                    System.out.println(new String(buffer.array(), 0, len));
                }

                ByteBuffer byteBufferToWrite = ByteBuffer.wrap("HelloClient".getBytes());
                sc.write(byteBufferToWrite);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(sc != null) {
                    try {
                        sc.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

}
