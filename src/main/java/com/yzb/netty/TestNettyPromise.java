package com.yzb.netty;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

@Slf4j
public class TestNettyPromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        EventLoop eventLoop = new NioEventLoopGroup().next();

        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);

        new Thread(()->{
            log.debug("子线程开始执行");
            try {
                Thread.sleep(2000);
                // 设置结果，通知等待结果的线程
                promise.setSuccess(80);
            } catch (InterruptedException e) {
                e.printStackTrace();
                // 设置异常，通知等待结果的线程
                promise.setFailure(e);
            }
        }).start();

        // 接受结果
        log.info("等待结果...");
        log.debug("结果是：{}", promise.get());
    }
}
