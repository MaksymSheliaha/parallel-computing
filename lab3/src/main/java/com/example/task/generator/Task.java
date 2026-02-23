package com.example.task.generator;

import java.util.Random;
import java.util.concurrent.Callable;

public record Task(int id) implements Callable<String> {

    @Override
    public String call() {
        Random rand = new Random();
        int timeout = rand.nextInt(500, 1000);
        int a = rand.nextInt(100);
        int b = rand.nextInt(100);
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "Task " + id + " done with result " + a + b;
    }
}
