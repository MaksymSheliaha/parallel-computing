package com.example.task.generator;

import java.util.Random;
import java.util.concurrent.Callable;

public record Task(int id, int origin, int bound) implements Callable<String> {

    @Override
    public String call() {
        Random rand = new Random();
        int timeout = rand.nextInt(origin, bound);
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
