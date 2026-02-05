package com.example;



import com.example.task.Solution;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();
        var matrixA = solution.generateMatrix(100);
        var matrixB = solution.generateMatrix(100);
        int k = 1;

        System.out.println(Arrays.deepToString(matrixA));
        System.out.println(Arrays.deepToString(matrixB));

        var result = solution.executeSequentially(matrixA, matrixB, k);
        System.out.println(Arrays.deepToString(result));

        var parallelResult = solution.executeParallel(matrixA, matrixB, k, 64);
        System.out.println(Arrays.deepToString(parallelResult));



        org.openjdk.jmh.Main.main(args);
    }
}
