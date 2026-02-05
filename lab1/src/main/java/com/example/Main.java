package com.example;

import com.example.task.Solution;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();
        var matrixA = solution.generateMatrix(10);
        var matrixB = solution.generateMatrix(10);
        int k = 1;

        System.out.println(Arrays.deepToString(matrixA));
        System.out.println(Arrays.deepToString(matrixB));

        var result = solution.executeSequentially(matrixA, matrixB, k);
        System.out.println(Arrays.deepToString(result));

        var parallelResult = solution.executeParallel(matrixA, matrixB, k, 10);
        System.out.println(Arrays.deepToString(parallelResult));



        //org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void init() {
        // Do nothing
        int i = 10;
        int j = 2005;
        int c = i+j;
    }
}
