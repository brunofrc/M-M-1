package com.ms;

public class Event {

    double time;
    String type;

    public Event() {
    }

    public Event(double time, String type) {
        this.time = time;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
