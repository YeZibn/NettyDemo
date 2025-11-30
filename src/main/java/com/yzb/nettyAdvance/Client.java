package com.yzb.nettyAdvance;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class Client {
        private static void send(){
            // 短连接解决粘包问题，但是无法避免半包问题
            NioEventLoopGroup  group = new NioEventLoopGroup();
            try {
                ChannelFuture future = new Bootstrap()
                        .group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        ByteBuf buffer = ctx.alloc().buffer();
                                        for (int i = 0; i < 10; i++) {
                                            buffer.writeBytes(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
                                        }
                                        ctx.writeAndFlush(buffer);
//                                      ctx.channel().close();
                                    }
                                });
                            }
                        })
                        .connect(new InetSocketAddress("localhost", 8888));
                Channel channel = future.sync().channel();
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                // 优雅关闭
                group.shutdownGracefully();
            }
        }

        public static void main(String[] args) throws InterruptedException {
            send();
            System.out.println("over");
        }
}
