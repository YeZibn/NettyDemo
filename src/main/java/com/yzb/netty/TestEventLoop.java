package com.yzb.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TestEventLoop {
    public static void main(String[] args) {
        // 创建事件循环组，创建的线程数量可以指定，默认创建的线程数量是CPU核数*2
        EventLoopGroup group = new NioEventLoopGroup();
        // 获取下一个事件循环
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());

        // 执行普通任务
        group.next().submit(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            log.debug("普通任务执行完成");
        });

        // 执行定时任务
        group.next().scheduleAtFixedRate(()->{
            log.debug("定时任务执行完成");
        }, 0 ,1 , TimeUnit.SECONDS);
    }
}
