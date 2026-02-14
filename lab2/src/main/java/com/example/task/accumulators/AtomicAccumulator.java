package com.example.task.accumulators;

import com.example.task.Result;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicAccumulator implements Accumulator{

    private final AtomicInteger atomicCounter;
    private final AtomicReference<Integer> atomicMax;

    public AtomicAccumulator(){
        this.atomicCounter = new AtomicInteger(0);
        this.atomicMax = new AtomicReference<>();
    }

    @Override
    public void increaseCounter() {
        int prev;
        do {
            prev = atomicCounter.get();
        } while (!atomicCounter.compareAndSet(prev, ++prev));
    }

    @Override
    public void trySetMax(int value) {
        boolean out;
        Integer prevMax;
        do {
            out = true;
            prevMax = atomicMax.get();
            if(prevMax==null || prevMax < value){
                out = atomicMax.compareAndSet(prevMax, value);
            }
        } while (!out);
    }

    @Override
    public Result getResult() {
        return new Result(atomicCounter.getAndSet(0), atomicMax.getAndSet(null));
    }
}
