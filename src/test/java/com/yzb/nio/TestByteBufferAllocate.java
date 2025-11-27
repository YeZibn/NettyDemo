package com.yzb.nio;

import java.nio.ByteBuffer;
public class TestByteBufferAllocate {
    public static void main(String[] args) {
        /*
        class java.nio.HeapByteBuffer  堆内存 读写效率较低，受GC影响
        class java.nio.DirectByteBuffer 直接内存 读写效率高，不受GC影响，分配效率不高
         */
        System.out.println(ByteBuffer.allocate(16).getClass());
        System.out.println(ByteBuffer.allocateDirect(16).getClass());
    }
}
