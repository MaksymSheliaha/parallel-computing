package com.example.task;

public record Result(int count, Integer max) {
    @Override
    public String toString() {
        return "Result{" +
                "count=" + count +
                ", max=" + max +
                '}';
    }
}
