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
        List<Generator> generators = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            generators.add(new Generator(pool, collector));
        }

        pool.start();
        System.out.print("\n\nPool started\n\n");
        generators.forEach(Generator::start);

        Thread.sleep(120000);

        Generator.stopped.set(true);
        generators.forEach(Generator::joinUnsafe);

        pool.close();
        System.out.print("\n\nPool Closed\n\n");

        showStatistic(collector.getResults(), pool);
    }

    private static void showStatistic(List<CustomFuture> customFutures, ThreadPool pool){
        int declined = 0;
        int done = 0;
        int notFinished = 0;
        int notTaken = 0;

        List<String> finishedResults = new ArrayList<>();
        List<Long> taskWaitTimes = new ArrayList<>();

        for(var future: customFutures){
            if(future==null){
                declined++;
            } else{
                switch (future.getStatus()){
                    case DONE -> {
                        finishedResults.add(future.tryGet());
                        taskWaitTimes.add(future.getWaitNanoTime());
                        done++;
                    }
                    case CREATED -> notTaken++;
                    case INPROGRESS -> notFinished++;
                }
            }
        }
        System.out.println();
        System.out.println("#############################################################");
        System.out.println();
        System.out.printf("Thread created: %d\n", MyThread.getCounter());
        System.out.printf("Task denied: %d\n", declined);
        System.out.printf("Task notFinished: %d\n", notFinished);
        System.out.printf("Task notTaken: %d\n", notTaken);
        System.out.printf("Task done: %d\n", done);
        System.out.println("Avg queue full time(ms): "+pool.getQueueFullTimes().stream().mapToLong(e->e).average().orElse(0)/1_000_000);
        System.out.println("Avg queue empty time(ms): "+pool.getQueueEmptyTimes().stream().mapToLong(e->e).average().orElse(0)/1_000_000);
        System.out.println("Total queue full time(ms): "+pool.getQueueFullTimes().stream().mapToLong(e->e).sum()/1_000_000);
        System.out.println("Total queue empty time(ms): "+pool.getQueueEmptyTimes().stream().mapToLong(e->e).sum()/1_000_000);
        System.out.println("Avg task in queue time(ms): "+taskWaitTimes.stream().mapToLong(e->e).average().orElse(0)/1_000_000);

        System.out.println("Received results: ");
        finishedResults.forEach(System.out::println);

    }
}
