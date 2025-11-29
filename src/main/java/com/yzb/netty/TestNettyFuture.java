package com.yzb.netty;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

@Slf4j
public class TestNettyFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        EventLoop eventLoop = group.next();

        Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.info("子线程开始执行");
                Thread.sleep(2000);
                return 100;
            }
        });

//        // 主线程通过future获得结果，同步等待子线程执行完成
//        log.info("主线程执行");
//        Integer integer = future.get();
//        log.info("子线程执行结果：{}", integer);

        // 异步方式获取结果
        future.addListener(f -> {
            Integer result = null;
            try {
                result = (Integer) f.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            log.info("异步获取子线程执行结果：{}", result);
        });
    }
}
