package com.example.task.accumulators;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicAccumulator implements Accumulator{

    private final AtomicInteger atomicCounter;
    private final AtomicInteger atomicMax;

    public AtomicAccumulator(){
        this.atomicCounter = new AtomicInteger(0);
        this.atomicMax = new AtomicInteger(Integer.MIN_VALUE);
    }

    @Override
    public void increaseCounter() {
        boolean out;
        do {
            int prev = atomicCounter.get();
            out = atomicCounter.compareAndSet(prev, ++prev);
        } while (!out);
    }

    @Override
    public int getCount() {
        return atomicCounter.get();
    }

    @Override
    public void trySetMax(int value) {
        boolean out = true;
        do {
            int prevMax = atomicMax.get();
            if(prevMax < value){
                out = atomicMax.compareAndSet(prevMax, value);
            }
        } while (!out);
    }

    @Override
    public Integer getMax() {
        return atomicMax.get();
    }
}
