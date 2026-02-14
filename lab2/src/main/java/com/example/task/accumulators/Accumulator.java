package com.example.task.accumulators;

import com.example.task.Result;

public interface Accumulator {
    void increaseCounter();
    void addToCounter(int value);
    void trySetMax(int value);
    Result getResult();
}
