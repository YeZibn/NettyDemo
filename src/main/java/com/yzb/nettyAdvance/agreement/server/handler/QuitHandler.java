package com.yzb.nettyAdvance.agreement.server.handler;

import com.yzb.nettyAdvance.agreement.server.session.SessionFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuitHandler extends ChannelInboundHandlerAdapter {


    // 当链接断的时候，会调用此方法
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.info("链接 {} 断开了", ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.debug("链接 {} 异常断开", ctx.channel());
    }


}
