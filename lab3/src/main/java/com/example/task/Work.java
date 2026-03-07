package com.example.task;

import com.example.task.custom.CustomFuture;

import java.util.concurrent.Callable;


public record Work(Callable<String> task, CustomFuture future) implements Runnable {
    @Override
    public void run() {
        future.markStarted();
        try {
            String result = task.call();
            future.set(result);

        } catch (Exception e) {
            future.error();
        }
    }
}
