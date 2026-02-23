package com.example.task;

public class Queue {

    private final Work[] buffer;
    private int head = 0;
    private int tail = 0;
    private int count = 0;

    public Queue(int size){
        if (size<1){
            throw new IllegalArgumentException();
        }
        buffer = new Work[size];
    }

    public Work pull(){
        if(count == 0){
            throw new IllegalStateException("Queue is empty");
        }

        Work work = buffer[head];
        head = (head+1)%buffer.length;
        count--;
        return work;
    }

    public boolean push(Work task){
        if(count== buffer.length){
            throw new IllegalStateException("Queue is full");
        }

        buffer[tail] = task;
        tail = (tail+1)% buffer.length;
        count++;
        return true;
    }

    public boolean isEmpty(){
        return count==0;
    }

    public boolean isFull(){
        return count== buffer.length;
    }
}
