package com.example;



import com.example.task.CustomBenchmark;
import com.example.task.Solution;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(args);
    }

    public static void prettyPrint(int[][] matrix){
        for (int[] row:matrix){
            System.out.println(Arrays.toString(row));
        }
    }
}
