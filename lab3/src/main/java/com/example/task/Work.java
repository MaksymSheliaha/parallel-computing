package com.example.task;

import com.example.task.custom.CustomFuture;
import com.example.task.generator.Task;


public record Work(Task task, CustomFuture future) implements Runnable {
    @Override
    public void run() {
        var result = task.call();
        future.set(result);
    }
}
