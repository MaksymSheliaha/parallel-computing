package com.example;



import com.example.task.CustomBenchmark;
import com.example.task.Solution;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
//        Solution solution = new Solution();
//        var matrixA = solution.generateMatrix(10000);
//        var matrixB = solution.generateMatrix(10000);
//        int k = 5;
//        System.out.println("Matrix A:");
//        prettyPrint(matrixA);
//        System.out.println("Matrix B:");
//        prettyPrint(matrixB);

//        var result = solution.executeSequentially(matrixA, matrixB, k);

//        System.out.println(" ------------------ \n Matrix result:");
//
//        prettyPrint(result);
//
//        System.out.println("Matrix A:");
//        prettyPrint(matrixA);
//        System.out.println("Matrix B:");
//        prettyPrint(matrixB);


//        System.out.println(" ------------------ \n Matrix result:");


//        var parallelResult = solution.executeParallel(matrixA, matrixB, k, 64);
//        System.out.println(Arrays.deepToString(parallelResult));
//
//
//        CustomBenchmark benchmark = new CustomBenchmark(solution);
//
//        benchmark.run();


        org.openjdk.jmh.Main.main(args);
    }

    public static void prettyPrint(int[][] matrix){
        for (int[] row:matrix){
            System.out.println(Arrays.toString(row));
        }
    }
}
