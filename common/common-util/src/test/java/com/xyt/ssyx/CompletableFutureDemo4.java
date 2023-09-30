package com.xyt.ssyx;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//串行化
public class CompletableFutureDemo4 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        //1 任务1 返回结果1024
        CompletableFuture<Integer> futureA = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程"+Thread.currentThread().getName());
            int value = 1024;
            System.out.println("任务一："+value);
            return value;
        }, executorService);
        //2 任务2 获取任务一的返回结果
        CompletableFuture<Object> futureB = futureA.thenApplyAsync((res) -> {
            System.out.println("当前线程"+Thread.currentThread().getName());
            System.out.println("任务二："+res+1024);
            return res+1024;
        }, executorService);
        //3 任务3 往下执行
        futureA.thenRunAsync(()->{
            System.out.println("当前线程"+Thread.currentThread().getName());
            System.out.println("任务三");
        },executorService);
    }
}
