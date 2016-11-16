package com.example.puiwalam2.asg2;

/**
 * Created by Eva Hung on 15/11/2016.
 */
public class Expense {
    String name;
    String tag;
    float expense;
    int id;

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public float getExpense() {
        return expense;
    }

    public void setExpense(float expense) {
        this.expense = expense;
    }

}
