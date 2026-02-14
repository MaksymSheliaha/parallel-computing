package com.example.task.accumulators;

import com.example.task.Result;

public class BlockingAccumulator implements Accumulator {

    private int counter;
    private Integer max;
    private final Object maxMonitor;

    public BlockingAccumulator(){
        this.counter=0;
        this.max=null;
        this.maxMonitor = new Object();
    }


    @Override
    public synchronized void increaseCounter() {
        counter++;
    }

    @Override
    public void trySetMax(int value) {
        synchronized (maxMonitor){
            if(max==null || max<value){
                max = value;
            }
        }
    }

    @Override
    public Result getResult() {
        int count = counter;
        Integer foundMax = max;
        this.counter = 0;
        this.max = null;

        return new Result(count, foundMax);
    }

}
