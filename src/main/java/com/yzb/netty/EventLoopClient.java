package com.yzb.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

@Slf4j
public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException, IOException {

        NioEventLoopGroup group = new NioEventLoopGroup();
        // 启动客户端
        ChannelFuture channelFuture = new Bootstrap()
                // 添加EventLoopGroup 处理IO读写事件
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                // 异步非阻塞，真正发起连接的是nio线程而不是main线程
                .connect(new InetSocketAddress("localhost", 8888));

        // 使用sync方法同步处理结果
        // 连接建立后，main线程会阻塞在这里等待连接建立完成，如果没有sync会直接往下执行导致channel没有连接到服务器
        Channel channel = channelFuture.sync()
                .channel();

        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String msg = scanner.nextLine();
                if("q".equals(msg)) {
                    channel.close();// 异步操作，并不是在这个线程关闭的，而是在nio线程关闭的
                    break;
                }else
                {
                    channel.writeAndFlush(msg);
                }
            }
        }).start();

        // 同步方式，等待关闭连接
        ChannelFuture closeFuture = channel.closeFuture();
        System.out.println("waiting close...");
        // 等待关闭连接完成，阻塞当前线程
        closeFuture.sync();
        // 关闭连接后，释放NIO线程组资源
        group.shutdownGracefully();
        System.out.println("close done");

//        // 使用addListener方法异步处理结果
//        channelFuture.addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                Channel channel = channelFuture.channel();
//            }
//        });

//        // 使用addListener方法异步处理关闭连接的结果
//        channel.closeFuture().addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                log.debug("连接已关闭");
//            }
//        });

        System.out.println(channel);

    }
}
