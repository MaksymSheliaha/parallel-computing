package com.example.task.custom;

import java.util.ArrayList;
import java.util.List;

public class Collector {
    private final List<CustomFuture> results = new ArrayList<>();

    public synchronized void addResult(CustomFuture result) {
        results.add(result);
    }

    public synchronized List<CustomFuture> getResults(){
        return results;
    }
}
