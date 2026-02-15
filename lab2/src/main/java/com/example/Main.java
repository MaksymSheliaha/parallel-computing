package com.example;


import com.example.task.Solution;
import com.example.task.benchmark.CustomBenchmark;

public class Main {
    public static void main(String[] args) {

//        Solution solution=new Solution();
//        var matrix=solution.getMatrix(1000);
//        Accumulator atomic = new AtomicAccumulator();
//        Accumulator blocking = new BlockingAccumulator();
//
//        var res1=solution.executeSequentially(matrix);
//        var res2=solution.executeParallel(matrix, atomic, 8);
//        var res3=solution.executeParallel(matrix, blocking, 8);
//        var res4=solution.executeParallelOptimized(matrix, atomic, 8);
//        var res5=solution.executeParallelOptimized(matrix, blocking, 8);
//
//
//        System.out.printf("Result of sequential execution: %s\n", res1);
//        System.out.printf("Result of parallel execution with atomic accumulator: %s\n", res2);
//        System.out.printf("Result of parallel execution with blocking accumulator: %s\n", res3);
//        System.out.printf("Result of optimized parallel execution with atomic accumulator: %s\n", res4);
//        System.out.printf("Result of optimized parallel execution with blocking accumulator: %s\n", res5);


        CustomBenchmark benchmark = new CustomBenchmark(new Solution());
        benchmark.run();
    }
}
