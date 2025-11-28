package com.yzb.nio.multiThread;

import com.sun.security.ntlm.Server;

import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;

public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0, null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new java.net.InetSocketAddress(8070));
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
                }
            }
        }
    }

    class Worker implements Runnable{
        private Thread thread;
        private Selector worker;
        private String name;

        public Worker(String name)
        {
            this.name = name;
        }

        public void register() throws IOException {
            worker = Selector.open();
            thread = new Thread(this, name);
            thread.start();
        }

        @Override
        public void run() {
            while(true)
            {
                try {
                    worker.select();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
