package com.yzb.nio.multiThread;

import com.sun.security.ntlm.Server;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.yzb.nio.ByteBufferUtil.debugAll;

public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();

        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0, null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new java.net.InetSocketAddress(8070));
        //创建固定数量的Worker线程
        Worker[] workers = new Worker[2];
        for(int i=0;i<workers.length;i++)
        {
            workers[i] = new Worker("worker-" + i);
        }
        AtomicInteger index = new AtomicInteger();
        while(true)
        {
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();

            while(iterator.hasNext())
            {
                SelectionKey key = iterator.next();
                iterator.remove();

                if(key.isAcceptable())
                {
                    ServerSocketChannel channel =(ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    System.out.println("accept event");
                    // round robin 选择一个worker
                    int i = index.getAndIncrement() % workers.length;
                    workers[i].register(sc);
                }
            }
        }
    }

    static class Worker implements Runnable{
        private Thread thread;
        private Selector worker;
        private String name;
        private volatile boolean start = false;
        public ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        public Worker(String name)
        {
            this.name = name;
        }

        public void register(SocketChannel sc) throws IOException {
            if(!start)
            {
                worker = Selector.open();
                thread = new Thread(this, name);
                thread.start();
                start = true;
            }
            //添加任务到队列，但不立刻执行
            queue.add(() -> {
                try {
                    sc.register(worker, SelectionKey.OP_READ, null);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            worker.wakeup();
        }

        @Override
        public void run() {
            while(true)
            {
                try {
                    worker.select();
                    Runnable task = queue.poll();
                    if(task != null)
                    {
                        task.run();
                    }
                    Iterator<SelectionKey> iterator = worker.selectedKeys().iterator();
                    while(iterator.hasNext())
                    {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if(key.isReadable())
                        {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel channel = (SocketChannel) key.channel();
                            System.out.println("read event");
                            channel.read(buffer);
                            buffer.flip();
                            debugAll( buffer);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
