package com.yzb.nettyAdvance.agreement.protocol;

import com.yzb.nettyAdvance.agreement.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestMessageCodec {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LoggingHandler(),
                new LengthFieldBasedFrameDecoder(
                        1024,
                        12,
                        4,
                        0,
                        0),
                new MessageCodec());

        // encode
        LoginRequestMessage message = new LoginRequestMessage("yzb", "12345");
        channel.writeOutbound(message);

        //decode
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, byteBuf);

        channel.writeInbound(byteBuf);
    }
}
