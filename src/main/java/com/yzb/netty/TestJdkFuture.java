package com.yzb.netty;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class TestJdkFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
       //
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Future<Integer> future = executorService.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                log.info("子线程开始执行");
                Thread.sleep(2000);
                return 50;
            }
        });

        // 主线程通过future获得结果
        log.info("主线程执行");
        Integer integer = future.get();
        log.info("子线程执行结果：{}", integer);
    }
}
