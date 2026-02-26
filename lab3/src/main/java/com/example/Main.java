package com.example;

import com.example.task.ThreadPool;
import com.example.task.custom.Collector;
import com.example.task.custom.CustomFuture;
import com.example.task.custom.MyThread;
import com.example.task.generator.Generator;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Collector collector = new Collector();


        ThreadPool pool = new ThreadPool(5, 20);
        Generator generator1 = new Generator(pool, collector);
        Generator generator2 = new Generator(pool, collector);
        pool.start();
        generator1.start();
        generator2.start();
        Thread.sleep(10);

        pool.stop();

        Thread.sleep(2);

        pool.resume();

        Thread.sleep(5);

        Generator.stopped.set(true);
        generator1.join();
        generator2.join();

        pool.close();
        showStatistic(collector.getResults());


    }

    private static void showStatistic(List<CustomFuture> customFutures){
        int declined = 0;
        int done = 0;
        int notFinished = 0;

        List<String> finishedResults = new ArrayList<>();

        for(var future: customFutures){
            if(future==null){
                declined++;
            } else if (future.isReady()) {
                done++;
                finishedResults.add(future.tryGet());
            } else {
                notFinished++;
            }
        }
        System.out.println();
        System.out.println("#############################################################");
        System.out.println();
        System.out.printf("Thread created: %d\n", MyThread.getCounter());
        System.out.printf("Task denied: %d\n", declined);
        System.out.printf("Task notFinished: %d\n", notFinished);
        System.out.printf("Task done: %d\n", done);

        System.out.println("Received results: ");
        finishedResults.forEach(System.out::println);

    }
}
