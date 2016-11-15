package com.example.puiwalam2.asg2;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Eva Hung on 15/11/2016.
 */
public class DatabaseTest {
    public static void Test(Context context){
        DbHelper dphelper;
        dphelper = new DbHelper(context);
        SQLiteDatabase db = dphelper.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(TravelEntry.COL_NAME_TRAVEL_NAME, "HK");
        values.put(TravelEntry.COL_NAME_TRAVEL_SDATE, "10-10-2016");
        values.put(TravelEntry.COL_NAME_TRAVEL_EDATE, "15-10-2016");
        values.put(TravelEntry.COL_NAME_TRAVEL_BUDGET, 10000);
        long newRowId;
        newRowId=db.insert(TravelEntry.TBL_NAME,null, values);

        ContentValues values2=new ContentValues();
        values2.put(ActivityEntry.COL_NAME_ACTIVITY_SDATE, "10-10-2016 10:00am");
        values2.put(ActivityEntry.COL_NAME_ACTIVITY_EDATE, "10-10-2016 12:00pm");
        values2.put(ActivityEntry.COL_NAME_ACTIVITY_EXPENSE, 1000);
        values2.put(ActivityEntry.COL_NAME_ACTIVITY_LOCATION_NAME, "Park");
        values2.put(ActivityEntry.COL_NAME_ACTIVITY_ADDRESS, "Somewhere in Hong Kong");
        values2.put(ActivityEntry.COL_NAME_ACTIVITY_IMAGE_PATH, "./location.jpg");
        values2.put(ActivityEntry.COL_NAME_ACTIVITY_ACTIVITY_TYPE, "SiteSeeing");
        values2.put(ActivityEntry.COL_NAME_TRAVEL_ID, newRowId);
        long newRowId2;
        newRowId2=db.insert(ActivityEntry.TBL_NAME,null, values2);

        ContentValues values3=new ContentValues();
        values3.put(ExpenseEntry.COL_NAME_EXPENSE_NAME, "Lunch");
        values3.put(ExpenseEntry.COL_NAME_EXPENSE_TAG, "Meal");
        values3.put(ExpenseEntry.COL_NAME_EXPENSE, 300);
        values3.put(ExpenseEntry.COL_NAME_ACTIVITY_ID, newRowId2);
        long newRowId3;
        newRowId3=db.insert(ExpenseEntry.TBL_NAME,null, values3);
    }
}
