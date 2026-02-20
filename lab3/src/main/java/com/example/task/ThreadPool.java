package com.example.task;


import java.io.Closeable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool implements Closeable {

    private static final int DEFAULT_THREAD_NUM = 6;
    private static final int DEFAULT_QUEUE_SIZE = 20;

    private final Thread[] threads;
    private final Queue queue;
    private final AtomicBoolean active;
    private volatile boolean closed;


    public ThreadPool() throws InterruptedException {
        this(DEFAULT_THREAD_NUM, DEFAULT_QUEUE_SIZE);
    }

    public ThreadPool(int threadNum, int queueSize) {
        threads = new Thread[threadNum];
        queue = new Queue(queueSize);
        active = new AtomicBoolean(false);
        closed = false;
        for (int i = 0; i < threadNum; i++) {
            threads[i] = new MyThread(new Worker());
        }
    }

    public void start(){
        if(closed) throw new IllegalStateException();
        if(!active.get()){
            active.set(true);
            Arrays.stream(threads).forEach(Thread::start);
        }
    }

    public void stop(){
        if(closed) throw new IllegalStateException();
        active.set(false);
    }

    public void resume(){
        if(closed) throw new IllegalStateException();
        if(!active.get()){
            active.set(true);
            synchronized (active){
                active.notifyAll();
            }
        }
    }

    @Override
    public void close(){
        if(closed) throw new IllegalStateException();
        closed = true;
        resume();
    }


    private final class Worker implements Runnable{

        @Override
        public void run() {
            while(!closed){
                System.out.println("Thread "+Thread.currentThread().threadId()+" do sth");
                queue.pull().run();
                trySleep();
            }
        }

        private void trySleep(){
            synchronized (active){
                if(!active.get()){
                    System.out.println("Thread "+Thread.currentThread().threadId()+" stopped");
                    try {
                        active.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Thread "+Thread.currentThread().threadId()+" wake up");
                }
            }
        }
    }
}
