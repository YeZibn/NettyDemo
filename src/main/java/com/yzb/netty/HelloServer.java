package com.yzb.netty;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class HelloServer {
    public static void main(String[] args) {
        // 1.启动器，负责组装netty组件，启动服务器
        new ServerBootstrap()
                // bossEventLoopGroup，处理连接请求，boss不处理业务
                // workerEventLoopGroup，处理业务，worker不处理连接请求
                .group(new NioEventLoopGroup())
                // 选择服务器的实现模式
                .channel(NioServerSocketChannel.class)
                // 决定了 worker 能执行哪些任务
                .childHandler(
                        // 创建一个通道初始化对象，添加业务处理逻辑
                        new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new StringDecoder());
                        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    System.out.println(msg);
                                }
                        });
                    }
                })
                .bind(8888);

    }
}
