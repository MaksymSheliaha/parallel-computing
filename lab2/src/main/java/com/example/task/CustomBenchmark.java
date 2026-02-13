package com.example.task;

import java.util.ArrayList;
import java.util.List;

public class CustomBenchmark {

    private int[][] matrixA;
    private int[][] matrixB;
    private final int k = 5;
    private final Solution solution;
    private final int[] sizes = {12000};
    private final int[] threadNum = {1, 2, 4, 8, 16, 32, 64, 128, 256};

    public CustomBenchmark(Solution solution){
        this.solution = solution;
    }

    private void setup(int size){
        matrixA = solution.generateMatrix(size);
        matrixB = solution.generateMatrix(size);
    }

    public void run(){
        for(int size: sizes){
            setup(size);
            double time = bench(()->solution.executeSequentially(matrixA, matrixB, k));
            System.out.printf("Sequential avg time for size=%d is %.2f ms %n", size, time/1000);
            for(int threads:threadNum){
                time = bench(()->solution.executeParallel(matrixA, matrixB, k, threads));
                System.out.printf("Parallel avg time for size=%d threadNum=%d is %.2f ms %n", size, threads, time/1000);
            }
        }

    }

    private double bench(Runnable runnable){
        long start, end;

        int warmups = 3;
        int iter = 5;
        int[][] result;

        for(int i = 0; i<warmups;i++){
            runnable.run();
        }

        List<Long> times = new ArrayList<>();
        for(int i = 0; i<iter;i++){
            start = System.nanoTime();
            result = solution.executeSequentially(matrixA, matrixB, k);
            end = System.nanoTime();
            long time = end-start;
            times.add(time);
        }

        return times.stream().map(Long::doubleValue).reduce(0., Double::sum)/iter;

    }
}
