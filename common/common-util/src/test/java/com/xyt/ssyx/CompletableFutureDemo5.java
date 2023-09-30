package com.xyt.ssyx;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//组合
public class CompletableFutureDemo5 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        //1 任务1 返回结果1024
        CompletableFuture<Integer> futureA = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程" + Thread.currentThread().getName() +"begin....");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int value = 1024;
            System.out.println("任务一：" + value);
            System.out.println("当前线程" + Thread.currentThread().getName() +"end....");
            return value;
        }, executorService);
        //1 任务1 返回结果1024
        CompletableFuture<Integer> futureB = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程" + Thread.currentThread().getName() +"begin....");
            int value = 200;
            System.out.println("任务二：" + value);
            System.out.println("当前线程" + Thread.currentThread().getName() +"end....");
            return value;
        }, executorService);
        CompletableFuture<Void> all = CompletableFuture.allOf(futureA, futureB);
        all.get();
        all.join();
        System.out.println("over.........");
    }
}
