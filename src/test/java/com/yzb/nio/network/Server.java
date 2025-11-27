package com.yzb.nio.network;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import static com.yzb.nio.ByteBufferUtil.debugAll;

@Slf4j
public class Server {

    public static void split(ByteBuffer source)
    {
        source.flip();
        for(int i=0;i<source.limit();i++)
        {
            if(source.get(i) == '\n')
            {
                int length = i+1 - source.position();
                ByteBuffer target = ByteBuffer.allocate(length);
                for(int j=0;j<length;j++)
                {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        source.compact();
    }


    public static void main(String[] args) throws IOException {
        // 创建selector，监听多个通道
        Selector selector = Selector.open();

        // 创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);// 非阻塞

        // 建立 selector 和 channel 关系，简单来说就是注册
        // SelectionKey 了解事件和哪个channel发生了事件
        SelectionKey sscKey = ssc.register(selector, 0, null);

        // key 只关注 accept 事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("sscKey: {}", sscKey);

        // 绑定监听接口
        ssc.bind(new InetSocketAddress(8070));

        while (true) {
            // select方法 阻塞直到有事件发生
            selector.select();

            // 处理事件
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 处理完毕，移除key
                iterator.remove();
                log.debug("key: {}", key);

                // 区分事件类型
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    // 非阻塞读取
                    sc.configureBlocking(false);
                    // 注册
                    //附件可以传递对象，在读取时使用
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    // 关注读事件
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("sc: {}", sc);
                } else if (key.isReadable()) {
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        // 消息边界问题
                        // Http 1.1 通过 Content-Length 来判断消息体的长度

                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer);
                        if(read == -1)
                        {
                            key.cancel();
                            log.debug("客户端断开连接 {}", channel);
                        }
                        else
                        {
                            split(buffer);
                            if(buffer.position() == buffer.limit())
                            {
                                // 扩容
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                // 替换附件
                                key.attach(newBuffer);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        // 发生异常，取消key
                        key.cancel();
                    }
                }
            }
        }
    }
}
