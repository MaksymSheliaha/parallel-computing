package com.example.task;


import com.example.task.custom.CustomFuture;
import com.example.task.custom.MyThread;
import com.example.task.generator.Task;

import java.io.Closeable;
import java.util.Arrays;

public class ThreadPool implements Closeable {

    private static final int DEFAULT_THREAD_NUM = 6;
    private static final int DEFAULT_QUEUE_SIZE = 20;

    private final Thread[] threads;
    private final Queue queue;

    private final Object stoppedMonitor = new Object();
    private volatile boolean stopped = false;
    private boolean started = false;
    private volatile boolean closed = false;


    public ThreadPool() throws InterruptedException {
        this(DEFAULT_THREAD_NUM, DEFAULT_QUEUE_SIZE);
    }

    public ThreadPool(int threadNum, int queueSize) {
        threads = new Thread[threadNum];
        queue = new Queue(queueSize);
        for (int i = 0; i < threadNum; i++) {
            threads[i] = new MyThread(new Worker());
        }
    }

    public void start(){
        if(started || closed) throw new IllegalStateException();
        started = true;
        Arrays.stream(threads).forEach(Thread::start);
    }

    public void stop(){
        if(closed) throw new IllegalStateException();
        stopped = true;
        synchronized (queue){
            queue.notifyAll();
        }
    }

    public void resume(){
        if(closed) throw new IllegalStateException();
        if(stopped){
            synchronized (stoppedMonitor){
                stopped = false;
                stoppedMonitor.notifyAll();
            }
        }
    }

    @Override
    public void close(){
        if(closed || !started) throw new IllegalStateException();
        closed = true;
        stopped = false;

        synchronized (queue){
            queue.notifyAll();
        }
        synchronized (stoppedMonitor){
            stoppedMonitor.notifyAll();
        }
    }

    public CustomFuture execute(Task task){
        CustomFuture result;

        synchronized (queue){
            if(queue.isFull()){
                result = null;
                System.out.println("pool decline task #"+task.id());
            } else {
                result = new CustomFuture();
                queue.push(new Work(task, result));
                queue.notify();
                System.out.println("pool except task #"+task.id());
            }
        }

        return result;
    }


    private final class Worker implements Runnable{

        @Override
        public void run() {
            while(!closed){
                if(stopped){
                    synchronized (stoppedMonitor){
                        try {
                            stoppedMonitor.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    continue;
                }

                Work work;
                synchronized (queue){
                    while (queue.isEmpty() && !closed){
                        try{
                            queue.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if(closed) return;
                    work = queue.pull();
                }

                if(work!=null) {
                    try{
                        work.run();
                        System.out.println("pool finished task #"+work.task().id());
                    } catch (RuntimeException e) {
                        System.err.println("Task failed: " + work.task().id());
                    }
                }
            }
        }
    }
}
