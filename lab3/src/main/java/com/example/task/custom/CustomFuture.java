package com.example.task.custom;

import java.util.concurrent.atomic.AtomicBoolean;

public class CustomFuture {

    private final AtomicBoolean isReady;
    private String payload;

    private final long creationTime;
    private long startWorkingTime;

    public CustomFuture(){
        this.isReady = new AtomicBoolean(false);
        this.creationTime = System.nanoTime();
    }

    public synchronized void set(String payload){
        isReady.set(true);
        this.payload = payload;
        notifyAll();
    }

    public synchronized String tryGet(){
        if(isReady()){
            return payload;
        }

        return null;
    }

    public synchronized String get() throws InterruptedException {
        while (!isReady()){
            wait();
        }
        return payload;
    }

    public boolean isReady(){
        return isReady.get();
    }

    public void markStarted(){
        startWorkingTime=System.nanoTime();
    }

    public long getWaitNanoTime(){
        return startWorkingTime-creationTime;
    }
}
