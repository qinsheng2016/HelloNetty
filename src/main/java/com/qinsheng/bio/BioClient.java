package com.qinsheng.bio;

import java.io.IOException;
import java.net.Socket;

public class BioClient {

    public static void main(String[] args) throws IOException {
        Socket s = new Socket("127.0.0.1", 8888);
        s.getOutputStream().write("HelloServer".getBytes());
        s.getOutputStream().flush();    // 半双工，写的时候不能读，读的时候不能写
        // s.getOutputStream().close(); // 把outputStream关掉，也不能读了...

        System.out.println("write over, waiting for message back...");
        byte[] bytes = new byte[1024];
        int len = s.getInputStream().read(bytes);
        System.out.println(new String(bytes, 0, len));
        s.close();
    }

}
