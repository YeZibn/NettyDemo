package com.yzb.nio;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

import static com.yzb.nio.ByteBufferUtil.debugAll;

public class TestByteBufferString {
    public static void main(String[] args) {
        //字符串转为ByteBuffer
        ByteBuffer buffer1 = ByteBuffer.allocate(16);
        buffer1.put("abcde".getBytes());
        debugAll(buffer1);

        //CharSet
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("hello");
        debugAll(buffer2);

        //wrap
        ByteBuffer buffer3 = ByteBuffer.wrap("nihao".getBytes());
        debugAll(buffer3);

        //toString
        buffer1.flip();
        String str = StandardCharsets.UTF_8.decode(buffer1).toString();
        System.out.println(str);
    }

}
