package com.example.puiwalam2.asg2;

/**
 * Created by Eva Hung on 15/11/2016.
 */
public class TravelActivity {
    int id;
    String startDate;
    String endDate;
    float expense;
    String location_name;
    String address;
    String activity_type;
    int travelId;

    public TravelActivity() {

    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public float getExpense() {
        return expense;
    }

    public void setExpense(float expense) {
        this.expense = expense;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }

    public TravelActivity(int id, String startDate, String endDate, float expense, String location_name, String address, String activity_type, int travelId) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.expense = expense;
        this.location_name = location_name;
        this.address = address;
        this.activity_type = activity_type;
        this.travelId=travelId;
    }

    public int getTravelId() {
        return travelId;
    }
}
