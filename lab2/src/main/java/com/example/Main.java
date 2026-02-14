package com.example;



import com.example.task.Result;
import com.example.task.Solution;
import com.example.task.accumulators.AtomicAccumulator;
import com.example.task.accumulators.BlockingAccumulator;
import com.example.task.benchmark.CustomBenchmark;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        CustomBenchmark benchmark = new CustomBenchmark(new Solution());
        benchmark.run();
    }

    public static void prettyPrint(int[][] matrix){
        for (int[] row:matrix){
            System.out.println(Arrays.toString(row));
        }
    }
}
