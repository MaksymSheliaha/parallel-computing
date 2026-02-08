package com.example.task;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
@State(Scope.Benchmark)
public class BenchmarkSolution {

    @Param({"1000", "5000", "10000"})
    private int size;

    @State(Scope.Thread)
    public static class ParallelParams {
        @Param({"1", "2", "4", "8", "16", "32","64", "128", "256"})
        public int threadNum;
    }

    private int[][] matrixA;
    private int[][] matrixB;
    private final int k = 5;

    private Solution solution;

    @Setup
    public void setup() {
        solution = new Solution();
        matrixA = solution.generateMatrix(size);
        matrixB = solution.generateMatrix(size);
    }

    @Benchmark
    public void testSequential() {
        solution.executeSequentially(matrixA, matrixB, k);
    }

    @Benchmark
    public void testParallel(ParallelParams p) {
        solution.executeParallel(matrixA, matrixB, k, p.threadNum);
    }
}
