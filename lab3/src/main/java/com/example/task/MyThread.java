package com.example.task;

import java.util.concurrent.atomic.AtomicInteger;

public class MyThread extends Thread {

    private static final AtomicInteger counter = new AtomicInteger();

    public MyThread(Runnable task) {
        counter.incrementAndGet();
        super(task);
    }

    public static int getCounter(){
        return counter.get();
    }
}
