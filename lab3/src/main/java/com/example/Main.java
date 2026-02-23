package com.example;

import com.example.task.ThreadPool;
import com.example.task.custom.CustomFuture;
import com.example.task.generator.Task;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello world");
        var pool = new ThreadPool(5, 5);
        pool.start();

        System.out.println("\nPool started\n");

        CustomFuture[] results = new CustomFuture[10];
        for(int i = 0; i<10; i++){
            var future = pool.execute(new Task(i));
            results[i]=future;
            //Thread.sleep(100);
        }

//        for(int i = 0; i<10; i++){
//            if(results[i]!=null) System.out.println(results[i].get());
//        }
        Thread.sleep(1000);
        pool.close();
    }
}
