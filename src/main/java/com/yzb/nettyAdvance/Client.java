package com.yzb.nettyAdvance;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class Client {
        public static void main(String[] args) throws InterruptedException {
            new Bootstrap()
                    .group(new NioEventLoopGroup())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    for(int i = 0; i < 10; i++)
                                    {
                                        ByteBuf buffer = ctx.alloc().buffer(16);
                                        buffer.writeBytes(new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16});
                                        ctx.writeAndFlush(buffer);
                                    }
                                }
                            });
                        }
                    })
                    .connect(new InetSocketAddress("localhost", 8888));
        }
}
