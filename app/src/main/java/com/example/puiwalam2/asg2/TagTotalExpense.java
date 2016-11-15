package com.example.puiwalam2.asg2;

/**
 * Created by Winnie on 15/11/2016.
 */
public class TagTotalExpense {
    private String tag;
    private float expense;
    public TagTotalExpense (String t,float e){
        tag=t;
        expense=e;
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
