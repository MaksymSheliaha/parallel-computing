package com.example;

import com.example.task.MyThread;
import com.example.task.ThreadPool;

import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello world");
        var pool = new ThreadPool(5, 10);
        pool.start();

        System.out.println("\nPool started\n");
        Thread.sleep(10000);

        pool.stop();

        System.out.println("\nPool stopped\n");
        Thread.sleep(10000);

        pool.resume();

        System.out.println("\nPool resumed\n");
        Thread.sleep(10000);


        pool.stop();

        System.out.println("\nPool stopped\n");
        Thread.sleep(10000);

        pool.resume();
        System.out.println("\nPool stopped\n");

        Thread.sleep(10000);
//        pool.stop();
        pool.close();
        System.out.println("Pool closed");

    }
}
