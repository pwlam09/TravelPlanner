package com.example.puiwalam2.asg2;

/**
 * Created by puiwalam2 on 11/7/2016.
 */
public class Trip {
    private String name;
    private int date;
    private int time;
    private double spending;

    public Trip(String name, int date, int time, double spending) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.spending = spending;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getSpending() {
        return spending;
    }

    public void setSpending(double spending) {
        this.spending = spending;
    }

    @Override
    public String toString() {
        return name;
    }
}
