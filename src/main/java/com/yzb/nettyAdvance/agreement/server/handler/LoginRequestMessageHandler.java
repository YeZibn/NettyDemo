package com.yzb.nettyAdvance.agreement.server.handler;

import com.yzb.nettyAdvance.agreement.message.LoginRequestMessage;
import com.yzb.nettyAdvance.agreement.message.LoginResponseMessage;
import com.yzb.nettyAdvance.agreement.server.service.UserServiceFactory;
import com.yzb.nettyAdvance.agreement.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LoginRequestMessage loginRequestMessage) throws Exception {
        String username = loginRequestMessage.getUsername();
        String password = loginRequestMessage.getPassword();
        boolean login = UserServiceFactory.getUserService().login(username, password);
        if (login) {
            SessionFactory.getSession().bind(channelHandlerContext.channel(), username);
            channelHandlerContext.writeAndFlush(new LoginResponseMessage(true, "登录成功"));
        } else {
            channelHandlerContext.writeAndFlush(new LoginResponseMessage(false, "用户名或密码错误"));
        }
    }
}
