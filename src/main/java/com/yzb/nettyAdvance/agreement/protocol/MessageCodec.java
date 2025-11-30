package com.yzb.nettyAdvance.agreement.protocol;

import com.yzb.nettyAdvance.agreement.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        // 魔数
        byteBuf.writeBytes(new byte[]{'y','z','z','b'});
        // 版本号
        byteBuf.writeByte(1);
        // 序列化方式 0 jdk 1 json
        byteBuf.writeByte(0);
        // 指令类型
        byteBuf.writeByte(message.getMessageType());
        // 请求序号
        byteBuf.writeInt(message.getSequenceId());
        // 对其填充
        byteBuf.writeByte(0xff);
        // 获取内容的字节数组
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(message);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        // 长度
        byteBuf.writeInt(byteArray.length);
        // 内容
        byteBuf.writeBytes(byteArray);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magicNumber = byteBuf.readInt();
        byte version = byteBuf.readByte();
        byte serializerType = byteBuf.readByte();
        byte messageType = byteBuf.readByte();
        int sequenceId = byteBuf.readInt();
        byte padding = byteBuf.readByte();
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes, 0, length);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message msg = (Message) objectInputStream.readObject();

        log.debug("{},{},{},{},{},{}", magicNumber, version, serializerType, messageType, sequenceId, length);
        log.debug("{}", msg);

        list.add(msg);
    }
}
