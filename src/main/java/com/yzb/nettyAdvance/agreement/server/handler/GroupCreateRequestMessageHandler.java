package com.yzb.nettyAdvance.agreement.server.handler;

import com.yzb.nettyAdvance.agreement.message.GroupCreateRequestMessage;
import com.yzb.nettyAdvance.agreement.message.GroupCreateResponseMessage;
import com.yzb.nettyAdvance.agreement.server.session.Group;
import com.yzb.nettyAdvance.agreement.server.session.GroupSession;
import com.yzb.nettyAdvance.agreement.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GroupCreateRequestMessage groupCreateRequestMessage) throws Exception {
        String groupName = groupCreateRequestMessage.getGroupName();
        Set<String> members = groupCreateRequestMessage.getMembers();
        // 群管理器
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        if (group == null) {
            // 发送群消息
            List<Channel> membersChannel = groupSession.getMembersChannel(groupName);
            membersChannel.forEach(channel -> {
                channel.writeAndFlush(new GroupCreateResponseMessage(true, "您已被拉入"+groupName+"群"));
            });

            channelHandlerContext.writeAndFlush(new GroupCreateResponseMessage(true, groupName+"群创建成功"));
        } else {
            channelHandlerContext.writeAndFlush(new GroupCreateResponseMessage(false, groupName+"群已经存在"));
        }
    }
}
