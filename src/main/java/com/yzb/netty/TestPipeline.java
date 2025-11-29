package com.yzb.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestPipeline {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        // 获得管道
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast("handler1", new ChannelInboundHandlerAdapter() {
                             @Override
                             public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                 log.debug("handler1");
                                 super.channelRead(ctx, msg);
                             }
                         }
                        );
                        pipeline.addLast("handler2", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("handler2");
                                super.channelRead(ctx, msg);
                            }
                        });
                        pipeline.addLast("handler3", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("handler3");
                                nioSocketChannel.write(ctx.alloc().buffer().writeBytes("hello".getBytes()));
                            }
                        });
                        pipeline.addLast("handler4", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext channelHandlerContext, Object o, ChannelPromise channelPromise) throws Exception {
                                log.debug("handler4");
                                super.write(channelHandlerContext, o, channelPromise);
                            }
                        });
                        pipeline.addLast("handler5", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext channelHandlerContext, Object o, ChannelPromise channelPromise) throws Exception {
                                log.debug("handler5");
                                super.write(channelHandlerContext, o, channelPromise);
                            }
                        });
                    }
                })
                .bind(8888);
    }
}
