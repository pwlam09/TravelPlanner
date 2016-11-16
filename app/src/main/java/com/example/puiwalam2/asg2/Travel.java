package com.example.puiwalam2.asg2;

/**
 * Created by Eva Hung on 15/11/2016.
 */
public class Travel {
    int travelID;
    String travel_name;
    float budget;

    public Travel(int travelID, String travel_name, float budget) {
        this.travelID = travelID;
        this.travel_name = travel_name;
        this.budget = budget;
    }

    public int getTravelID() {
        return travelID;
    }

    public void setTravelID(int travelID) {
        this.travelID = travelID;
    }

    public String getTravel_name() {
        return travel_name;
    }

    public void setTravel_name(String travel_name) {
        this.travel_name = travel_name;
    }

    public float getBudget() {
        return budget;
    }

    public void setBudget(float budget) {
        this.budget = budget;
    }
}
