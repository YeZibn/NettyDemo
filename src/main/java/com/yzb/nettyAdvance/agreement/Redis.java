package com.yzb.nettyAdvance.agreement;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class Redis {
    public static void main(String[] args) throws InterruptedException {
        final byte[] LINE = {13,10};
        NioEventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                    nioSocketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelActive(io.netty.channel.ChannelHandlerContext ctx) throws Exception {
                            ByteBuf buffer = ctx.alloc().buffer();
                            buffer.writeBytes("*3".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("$3".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("set".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("$5".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("hello".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("$5".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("world".getBytes());
                            buffer.writeBytes(LINE);
                            ctx.channel().writeAndFlush(buffer);
                        }

                        @Override
                        public void channelRead(io.netty.channel.ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf byteBuf = (ByteBuf) msg;
                            System.out.println(byteBuf.toString(io.netty.util.CharsetUtil.UTF_8));
                        }
                    });
                }
            });
            bootstrap.connect("localhost", 6379).sync().channel().closeFuture().sync();
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
