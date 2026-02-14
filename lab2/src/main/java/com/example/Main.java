package com.example;




import com.example.task.Solution;



import com.example.task.benchmark.CustomBenchmark;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

//        Solution solution=new Solution();
//        var matrix=solution.getMatrix(1000);
//        Accumulator atomic = new AtomicAccumulator();
//        Accumulator blocking = new BlockingAccumulator();
//
//        var res1=solution.executeSequentially(matrix);
//        var res2=solution.executeParallel(matrix, atomic, 4);
//        var res3=solution.executeParallel(matrix, blocking, 4);
//        var res4=solution.executeParallelOptimized(matrix, blocking, 4);
//
//        res1=solution.executeSequentially(matrix);
//        res2=solution.executeParallel(matrix, atomic, 4);
//        res3=solution.executeParallel(matrix, blocking, 4);
//        res4=solution.executeParallelOptimized(matrix, blocking, 4);
//
//        System.out.println(res1);
//        System.out.println(res2);
//        System.out.println(res3);
//        System.out.println(res4);


        CustomBenchmark benchmark = new CustomBenchmark(new Solution());
        benchmark.run();
    }
}
