package com.yzb.nio.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

public class WriteServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        ssc.bind(new java.net.InetSocketAddress(8070));

        while(true)
        {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while(iterator.hasNext())
            {
                SelectionKey key = iterator.next();
                iterator.remove();

                if(key.isAcceptable())
                {
                    System.out.println("accept event");
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, 0, null);

                    StringBuilder sb = new StringBuilder();
                    // 构造大量数据, 发送数据
                    for(int i=0;i<5000000;i++)
                    {
                        sb.append("a");
                    }
                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());

                    // 返回值代表实际写入的字节数
                    int write = sc.write(buffer);
                    System.out.println("实际写入字节数: " + write);

                    //判断是否有剩余内容
                    if(buffer.hasRemaining())
                    {
                        // 关注可写事件
                        scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
                        //把未写完的数据挂到key上
                        scKey.attach(buffer);
                    }
                } else if(key.isWritable())
                {
                    Object attachment = key.attachment();
                    SocketChannel channel = (SocketChannel) key.channel();
                    //继续写入剩余数据，不需要担心写不完的问题，因为只要缓冲区还有剩余数据就会触发可写事件
                    int write = channel.write((ByteBuffer) attachment);
                    System.out.println("继续写入字节数: " + write);
                    //判断是否写完
                    if(!((ByteBuffer) attachment).hasRemaining())
                    {
                        key.attach(null);
                        //写完了，取消写事件的关注
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                        System.out.println("数据写完");
                    }
                }
            }
        }

    }
}
