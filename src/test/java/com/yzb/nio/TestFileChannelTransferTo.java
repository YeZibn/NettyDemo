package com.yzb.nio;

import java.io.*;
import java.nio.channels.FileChannel;

public class TestFileChannelTransferTo {
    public static void main(String[] args) {
        try(
                FileChannel from = new FileInputStream("src/main/resources/from.txt").getChannel();
                FileChannel to = new FileOutputStream("src/main/resources/to.txt").getChannel();
                ){
            // transferTo 方法可以直接将数据从一个通道传输到另一个通道，底层会利用操作系统的零拷贝机制，提高效率
            from.transferTo(0, from.size(), to);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
