package com.qinsheng.bio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class BioServer {

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket();
        ss.bind(new InetSocketAddress("127.0.0.1", 8888));  // 绑定在8888端口中
        while (true) {
            Socket s = ss.accept(); // 接收一个新的客户端连接

            new Thread(() -> {  // 新启动一个线程去执行
                handle(s);
            }).start();
        }
    }

    static void handle(Socket s) {
        try {
            byte[] bytes = new byte[1024];
            int len = s.getInputStream().read(bytes);
            System.out.println(new String(bytes, 0, len));

            s.getOutputStream().write(bytes, 0, len);
            s.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
