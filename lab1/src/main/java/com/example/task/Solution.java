package com.example.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Task: C = A + k*B
public class Solution {

    public int[][] generateMatrix(int size){
        Random random = new Random();
        var matrix = new int[size][size];

        for(int[] row: matrix){
            for(int i = 0; i<row.length;i++){
                row[i] = random.nextInt(100);
            }
        }
        return matrix;
    }

    public int[][] executeSequentially(int[][] a, int[][] b, int k) {
        if(a==null || b==null || a.length!=b.length || a.length<1) throw new IllegalArgumentException();
        int rows = a.length;
        int cols = a[0].length;

        int[][] result = new int[rows][cols];

        for(int i = 0; i<rows; i++){
            for(int j = 0; j<cols; j++){
                result[i][j] = a[i][j] + k*b[i][j];
            }
        }

        return result;
    }

    public int[][] executeParallel(int[][] a, int[][] b, int k, int threadNum){
        if(a==null || b==null || a.length!=b.length || a.length<1) throw new IllegalArgumentException();
        if(threadNum < 1) throw new IllegalArgumentException();

        List<Thread> threads = new ArrayList<>();
        int rows = a.length;
        int cols = a[0].length;
        if(threadNum > rows) {
            threadNum = rows;
        }

        int[][] result = new int[rows][cols];
        for(int i = 0; i<threadNum; i++) {
            int startRow = (i * rows) / threadNum;
            int endRow = ((i + 1) * rows) / threadNum;
            Thread thread = createThread(a, b, k, result, startRow, endRow);
            thread.start();
            threads.add(thread);
        }

        threads.forEach(t-> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        return result;
    }

    private Thread createThread(int[][] a, int[][] b, int k, int[][] buffer,  final int firstRow, final int lastRow){
        return new Thread(()->{
            int cols = buffer[firstRow].length;
            for(int i = firstRow; i<lastRow; i++){
                for(int j = 0; j<cols; j++){
                    buffer[i][j] = a[i][j] + k*b[i][j];
                }
            }
        });
    }

}
