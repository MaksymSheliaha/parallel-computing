package com.example.task.accumulators;

import com.example.task.Result;

public class BlockingAccumulator implements Accumulator {

    private Counter counter;
    private MaxStorer max;

    public BlockingAccumulator(){
        this.counter=new Counter();
        this.max=new MaxStorer();
    }


    @Override
    public void increaseCounter() {
        counter.incrementCounter();
    }

    @Override
    public synchronized void addToCounter(int value) {
        counter.addToCounter(value);
    }

    @Override
    public void trySetMax(int value) {
        max.trySetMax(value);
    }

    @Override
    public Result getResult() {
        Result result = new Result(counter.getCounter(), max.getMax());
        counter = new Counter();
        max = new MaxStorer();
        return result;
    }

    private static class Counter{
        private int counter = 0;

        public synchronized void incrementCounter(){
            counter++;
        }

        public synchronized void addToCounter(int value) {
            counter+=value;
        }

        public int getCounter(){
            return counter;
        }
    }

    private static class MaxStorer{
        private Integer max = null;

        public synchronized void trySetMax(int value) {
            if(max==null || max<value){
                max = value;
            }
        }

        public Integer getMax(){
            return max;
        }
    }

}
