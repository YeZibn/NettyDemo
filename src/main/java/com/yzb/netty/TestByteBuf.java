package com.yzb.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class TestByteBuf {
    public static void main(String[] args) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        System.out.println(byteBuf);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 300; i++) {
            sb.append(i);
        }
        byteBuf.writeBytes(sb.toString().getBytes());
        System.out.println(byteBuf);
    }
}
