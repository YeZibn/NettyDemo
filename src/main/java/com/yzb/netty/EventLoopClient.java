package com.yzb.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {

        System.out.println("Hello Netty Client");

        // 启动客户端
        Channel channel = new Bootstrap()
                // 添加EventLoopGroup 处理IO读写事件
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect("localhost", 8888)
                .sync()
                .channel();
        System.out.println(channel);

    }
}
