package com.example.task.benchmark;

import com.example.task.Result;
import com.example.task.Solution;
import com.example.task.accumulators.Accumulator;
import com.example.task.accumulators.AtomicAccumulator;
import com.example.task.accumulators.BlockingAccumulator;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CustomBenchmark {

    private int[][] matrix;
    private final Solution solution;
    private final int[] sizes = {1000, 5000, 10000};
    private final int[] threadNum = {2, 4, 8, 16};
    private final String[] accumulatorType = {"atomic", "blocking"};

    public CustomBenchmark(Solution solution){
        this.solution = solution;
    }

    private void setup(int size){
        matrix = solution.getMatrix(size);
    }

    public void run(){
        for(int size: sizes){

            setup(size);

            double time = bench(()->solution.executeSequentially(matrix));
            System.out.printf("Sequential avg time for size=%d is %.2f ms %n", size, time/1_000_000);

            for(String type:accumulatorType){
                for (int threads : threadNum) {

                    time = bench(() -> {
                        Accumulator accumulator = switch (type){
                            case "atomic" -> new AtomicAccumulator();
                            default -> new BlockingAccumulator();
                        };
                        return solution.executeParallel(matrix, accumulator, threads);
                    });
                    System.out.printf("Parallel avg time for size=%d accumulatorType=\"%s\" threadNum=%d is %.2f ms %n", size, type,  threads, time / 1_000_000);
                }
            }

            for(String type:accumulatorType){
                for (int threads : threadNum) {
                    time = bench(() -> {
                        Accumulator accumulator = switch (type) {
                            case "atomic" -> new AtomicAccumulator();
                            default -> new BlockingAccumulator();
                        };
                        return solution.executeParallelOptimized(matrix, accumulator, threads);
                    });
                    System.out.printf("Optimized parallel avg time for size=%d accumulatorType=\"%s\" threadNum=%d is %.2f ms %n", size, type, threads, time / 1_000_000);
                }
            }
        }

    }

    private double bench(Supplier<Result> supplier){
        Blackhole blackhole = new Blackhole("Today's password is swordfish. I understand instantiating Blackholes directly is dangerous.");
        long start, end;

        int warmups = 3;
        int iter = 5;

        for(int i = 0; i<warmups;i++){
            blackhole.consume(supplier.get());
        }

        List<Long> times = new ArrayList<>();
        for(int i = 0; i<iter;i++){
            start = System.nanoTime();
            blackhole.consume(supplier.get());
            end = System.nanoTime();
            long time = end-start;
            times.add(time);
        }

        return times.stream().map(Long::doubleValue).reduce(0., Double::sum)/iter;

    }
}
