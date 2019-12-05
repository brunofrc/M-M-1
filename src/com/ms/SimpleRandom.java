package com.ms;

public class SimpleRandom {
    int max;
    int last;

    public SimpleRandom(int max){
        this.max = max;
        last = (int) (System.currentTimeMillis() % max);
    }

    public int nextInt(){
        last = (last * 32719 + 3) % 32749;
        return last % max;
    }
}
