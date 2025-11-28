package com.yzb.nio.multiThread;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class TestClient {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new java.net.InetSocketAddress("localhost", 8070));
        sc.write(Charset.defaultCharset().encode("hello server"));
        System.in.read();

    }
}
