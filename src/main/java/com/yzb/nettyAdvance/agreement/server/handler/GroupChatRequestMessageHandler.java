package com.yzb.nettyAdvance.agreement.server.handler;

import com.yzb.nettyAdvance.agreement.message.GroupChatRequestMessage;
import com.yzb.nettyAdvance.agreement.message.GroupChatResponseMessage;
import com.yzb.nettyAdvance.agreement.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GroupChatRequestMessage groupChatRequestMessage) throws Exception {
        List<Channel> membersChannel = GroupSessionFactory.getGroupSession()
                .getMembersChannel(groupChatRequestMessage.getGroupName());

        for(Channel channel : membersChannel)
        {
            channel.writeAndFlush(new GroupChatResponseMessage(groupChatRequestMessage.getFrom(), groupChatRequestMessage.getContent()));
        }
    }
}
