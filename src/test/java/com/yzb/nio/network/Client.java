package com.yzb.nio.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {

    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8070));
        System.out.println("connected");
        sc.write(Charset.defaultCharset().encode("hello\nnio\n"));
        System.in.read();
    }
}
