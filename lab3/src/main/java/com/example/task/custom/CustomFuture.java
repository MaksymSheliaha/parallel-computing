package com.example.task.custom;

import java.util.concurrent.atomic.AtomicBoolean;

public class CustomFuture {

    private final AtomicBoolean isReady;
    private String payload;

    public CustomFuture(){
        this.isReady = new AtomicBoolean(false);
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
}
