package com.example.task.benchmark;

import com.example.task.Solution;
import com.example.task.accumulators.Accumulator;
import com.example.task.accumulators.AtomicAccumulator;
import com.example.task.accumulators.BlockingAccumulator;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
@State(Scope.Benchmark)
public class BenchmarkSolution {

    @Param({"1000", "5000"})
    private int size;

    @State(Scope.Benchmark)
    public static class ParallelParams {

        @Param({"4", "8", "16"})
        public int threadNum;

        @Param({"atomic", "blocking"})
        public String accumulatorType;

        public Accumulator accumulator;

        @Setup(Level.Trial)
        public void setup() {
            System.out.println("in parallel setup");
            accumulator = switch (accumulatorType) {
                case "atomic" -> new AtomicAccumulator();
                case "blocking" -> new BlockingAccumulator();
                default -> throw new IllegalStateException("Unknown type: " + accumulatorType);
            };
        }
    }

    private int[][] matrix;

    private final Solution solution = new Solution();

    @Setup(Level.Trial)
    public void setup() {
        System.out.println("in setup");
        matrix = solution.getMatrix(size);
    }

    @Benchmark
    public void testSequential() {
        solution.executeSequentially(matrix);
    }

    @Benchmark
    public void testParallel(ParallelParams p) {
        solution.executeParallel(matrix, new AtomicAccumulator(), p.threadNum);
    }
}
