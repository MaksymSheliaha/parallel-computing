package com.example.task.accumulators;

public interface Accumulator {
    void increaseCounter();
    int getCount();
    void trySetMax(int value);
    Integer getMax();
}
