package com.example.task;

import com.example.task.accumulators.Accumulator;

import java.util.Random;

public class Solution {

    private static int[][] matrix;

    public int[][] getMatrix(int size){
        if(matrix!=null && matrix.length==size) return matrix;

        Random random = new Random();
        matrix = new int[size][size];
        for(int[] row: matrix){
            for(int i = 0; i<row.length;i++){
                row[i] = random.nextInt();
            }
        }
        System.out.println("New matrix has been generated");
        return matrix;
    }

    public Result executeSequentially(int[][] matrix) {
        int counter = 0;
        Integer max = null;
        for(int[] row: matrix){
            for(int el: row){
                if(el>10){
                    counter++;
                    if(max==null || max <el){
                        max = el;
                    }
                }
            }
        }
        return new Result(counter, max);
    }

    public Result executeParallel(int[][] matrix, Accumulator accumulator, int threadNum){
        int rows = matrix.length;
        if(threadNum > rows) {
            threadNum = rows;
        }

        Thread[] threads = new Thread[threadNum];
        for(int i = 0; i<threadNum; i++) {
            int startRow = (i * rows) / threadNum;
            int endRow = ((i + 1) * rows) / threadNum;
            Thread thread = createThread(matrix, accumulator, startRow, endRow);
            thread.start();
            threads[i] = thread;
        }

        for(Thread thread: threads){
            try {
                thread.join();
            } catch (InterruptedException e){
                throw new RuntimeException(e);
            }
        }

        return accumulator.getResult();
    }

    private Thread createThread(int[][] matrix, Accumulator accumulator,  final int firstRow, final int lastRow){
        return new Thread(()->{
            for(int i = firstRow; i<lastRow; i++){
                for(int el: matrix[i]){
                    if(el>10){
                        accumulator.increaseCounter();
                        accumulator.trySetMax(el);
                    }
                }
            }
        });
    }
}
