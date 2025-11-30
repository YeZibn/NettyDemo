package com.yzb.nettyAdvance.agreement.client;


import com.yzb.nettyAdvance.agreement.message.*;
import com.yzb.nettyAdvance.agreement.protocol.MessageCodec;
import com.yzb.nettyAdvance.agreement.protocol.MessageCodecSharable;
import com.yzb.nettyAdvance.agreement.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {


    public static void main(String[] args) {

        final NioEventLoopGroup group = new NioEventLoopGroup();
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        CountDownLatch WAIT_LOGIN = new CountDownLatch(1);
        AtomicBoolean LOGIN = new AtomicBoolean(false);
        try {
            Bootstrap bs = new Bootstrap();
            bs.channel(NioSocketChannel.class);
            bs.group(group);
            bs.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast("clientHandler", new ChannelInboundHandlerAdapter(){

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            // 登录功能，输入用户名和密码
                            new Thread(()->{
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("请输入用户名：");
                                String username = scanner.nextLine();
                                System.out.println("请输入密码：");
                                String password = scanner.nextLine();
                                // 构造消息对象
                                LoginRequestMessage loginRequestMessage = new LoginRequestMessage(username, password);
                                // 发送登录请求
                                ctx.writeAndFlush(loginRequestMessage);

                                try {
                                    WAIT_LOGIN.await();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                // 如果登陆失败
                                if(!LOGIN.get())
                                {
                                    ctx.channel().close();
                                    return;
                                }
                                // 如果登陆成功，进入功能菜单
                                while(true)
                                {
                                    System.out.println("============ 功能菜单 ============");
                                    System.out.println("send [username] [content]");
                                    System.out.println("gsend [group name] [content]");
                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                    System.out.println("gmembers [group name]");
                                    System.out.println("gjoin [group name]");
                                    System.out.println("gquit [group name]");
                                    System.out.println("quit");
                                    System.out.println("==================================");
                                    String command = scanner.nextLine();
                                    String[] split = command.split(" ");
                                    switch (split[0])
                                    {
                                        case "send":
                                            ctx.writeAndFlush(new ChatRequestMessage(username, split[1], split[2]));
                                            break;
                                        case "gsend":
                                            ctx.writeAndFlush(new GroupChatRequestMessage(username, split[1], split[2]));
                                            break;
                                        case "gcreate":
                                            HashSet<String> members = new HashSet<>(Arrays.asList(split[2].split(",")));
                                            ctx.writeAndFlush(new GroupCreateRequestMessage(split[1], members));
                                            break;
                                        case "gmembers":
                                            ctx.writeAndFlush(new GroupMembersRequestMessage(split[1]));
                                            break;
                                        case "gjoin":
                                            ctx.writeAndFlush(new GroupJoinRequestMessage(username, split[1]));
                                            break;
                                        case "gquit":
                                            ctx.writeAndFlush(new GroupQuitRequestMessage(username, split[1]));
                                            break;
                                        case "quit":
                                            ctx.channel().close();
                                            return;
                                        default:
                                            System.out.println("输入错误");
                                            break;
                                    }
                                }
                            }).start();
                        }

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.debug("{}", msg);
                            if (msg instanceof LoginResponseMessage) {
                                LoginResponseMessage loginResponseMessage = (LoginResponseMessage) msg;
                                if (loginResponseMessage.isSuccess()) {
                                    LOGIN.set(true);
                                }
                            }
                            WAIT_LOGIN.countDown();
                        }
                    });
                }
            });
            Channel channel = bs.connect("localhost", 8888).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Client error", e);
        } finally {

            group.shutdownGracefully();

        }

    }


}
