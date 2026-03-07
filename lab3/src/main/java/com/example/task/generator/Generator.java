package com.example.task.generator;

import com.example.task.ThreadPool;
import com.example.task.custom.Collector;
import com.example.task.custom.MyThread;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Generator extends MyThread {

    public static final AtomicBoolean stopped = new AtomicBoolean(false);
    private static final AtomicInteger counter = new AtomicInteger(0);
    private final Random rand = new Random();

    private final ThreadPool pool;
    private final Collector collector;

    public Generator(ThreadPool pool, Collector collector){
        this.pool=pool;
        this.collector=collector;
        super();
    }

    @Override
    public void run(){
        while(!stopped.get()){
            try {
                collector.addResult(pool.execute(new Task(counter.getAndIncrement(), 500, 1000)));
                Thread.sleep(rand.nextInt(300, 1100));
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }


}
