package com.yzb.nettyAdvance.agreement.server;


import com.yzb.nettyAdvance.agreement.message.LoginRequestMessage;
import com.yzb.nettyAdvance.agreement.message.LoginResponseMessage;
import com.yzb.nettyAdvance.agreement.protocol.MessageCodecSharable;
import com.yzb.nettyAdvance.agreement.protocol.ProtocolFrameDecoder;
import com.yzb.nettyAdvance.agreement.server.service.UserServiceFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 聊天--服务端
 */
@Slf4j
public class ChatServer {



    public static void main(String[] args) throws InterruptedException {

        final NioEventLoopGroup boss = new NioEventLoopGroup();
        final NioEventLoopGroup worker = new NioEventLoopGroup();

        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);

        try {
            final ServerBootstrap bs = new ServerBootstrap();
            bs.channel(NioServerSocketChannel.class);
            bs.group(boss, worker);
            bs.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<LoginRequestMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, LoginRequestMessage loginRequestMessage) throws Exception {
                            String username = loginRequestMessage.getUsername();
                            String password = loginRequestMessage.getPassword();
                            boolean login = UserServiceFactory.getUserService().login(username, password);
                            if(login)
                            {
                                channelHandlerContext.writeAndFlush(new LoginResponseMessage(true, "登录成功"));
                            }else {
                                channelHandlerContext.writeAndFlush(new LoginResponseMessage(false, "用户名或密码错误"));
                            }
                        }
                    });
                }
            });

            ChannelFuture channelFuture = bs.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {

            log.error("server error", e);

        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }


}
