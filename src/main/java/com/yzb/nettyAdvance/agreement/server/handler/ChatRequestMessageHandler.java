package com.yzb.nettyAdvance.agreement.server.handler;

import com.yzb.nettyAdvance.agreement.message.ChatRequestMessage;
import com.yzb.nettyAdvance.agreement.message.ChatResponseMessage;
import com.yzb.nettyAdvance.agreement.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ChatRequestMessage chatRequestMessage) throws Exception {
        String to = chatRequestMessage.getTo();
        Channel channel = SessionFactory.getSession().getChannel(to);
        log.debug("to: {}, channel: {}", to, channel);

        // 在线
        if(channel != null)
        {
            log.debug("用户在线");
            channel.writeAndFlush(new ChatResponseMessage(chatRequestMessage.getFrom(), chatRequestMessage.getContent()));
        }
        else
        {
            log.debug("用户不在线");
            channelHandlerContext.writeAndFlush(new ChatResponseMessage(false, "对方用户不存在或者不在线"));
        }
    }
}
