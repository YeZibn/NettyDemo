package com.yzb.nio;

import java.nio.ByteBuffer;

import static com.yzb.nio.ByteBufferUtil.debugAll;

public class TestByteBufferRead {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{0x61, 0x62, 0x63, 0x64});
        buffer.flip();

        //从头开始读
        System.out.println(buffer.get(new byte[4]));
        debugAll(buffer);
        buffer.rewind();
        System.out.println((char)buffer.get());
        debugAll(buffer);
    }
}
