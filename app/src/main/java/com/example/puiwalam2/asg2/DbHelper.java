package com.example.puiwalam2.asg2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Eva Hung on 15/11/2016.
 */
public class DbHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "travelData.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String ACTIVITY_SQL_CREATE_ENTRIES = "CREATE TABLE "
            + TravelActivityEntry.TBL_NAME + "(" + TravelActivityEntry._ID + " INTEGER PRIMARY KEY,"
            + TravelActivityEntry.COL_NAME_ACTIVITY_SDATE + TEXT_TYPE + COMMA_SEP
            + TravelActivityEntry.COL_NAME_ACTIVITY_EDATE + TEXT_TYPE + COMMA_SEP
            + TravelActivityEntry.COL_NAME_ACTIVITY_EXPENSE + " FLOAT" + COMMA_SEP
            + TravelActivityEntry.COL_NAME_ACTIVITY_LOCATION_NAME + TEXT_TYPE + COMMA_SEP
            + TravelActivityEntry.COL_NAME_ACTIVITY_ADDRESS + TEXT_TYPE + COMMA_SEP
            //+ TravelActivityEntry.COL_NAME_ACTIVITY_IMAGE_PATH + TEXT_TYPE + COMMA_SEP
            + TravelActivityEntry.COL_NAME_ACTIVITY_ACTIVITY_TYPE + TEXT_TYPE + COMMA_SEP
            + TravelActivityEntry.COL_NAME_TRAVEL_ID + " INTEGER);";

    private static final String TRAVEL_SQL_CREATE_ENTRIES = "CREATE TABLE "
            + TravelEntry.TBL_NAME + "(" + TravelEntry._ID + " INTEGER PRIMARY KEY,"
            + TravelEntry.COL_NAME_TRAVEL_NAME + TEXT_TYPE + COMMA_SEP
            + TravelEntry.COL_NAME_TRAVEL_SDATE + TEXT_TYPE + COMMA_SEP
            + TravelEntry.COL_NAME_TRAVEL_EDATE + TEXT_TYPE + COMMA_SEP
            + TravelEntry.COL_NAME_TRAVEL_BUDGET + " FLOAT);";

    private static final String EXPENSE_SQL_CREATE_ENTRIES = "CREATE TABLE "
            + ExpenseEntry.TBL_NAME + "(" + ExpenseEntry._ID + " INTEGER PRIMARY KEY,"
            + ExpenseEntry.COL_NAME_EXPENSE_NAME + TEXT_TYPE + COMMA_SEP
            + ExpenseEntry.COL_NAME_EXPENSE_TAG + TEXT_TYPE + COMMA_SEP
            + ExpenseEntry.COL_NAME_EXPENSE + " FLOAT" + COMMA_SEP
            + ExpenseEntry.COL_NAME_ACTIVITY_ID + " INTEGER);";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ACTIVITY_SQL_CREATE_ENTRIES);
        sqLiteDatabase.execSQL(TRAVEL_SQL_CREATE_ENTRIES);
        sqLiteDatabase.execSQL(EXPENSE_SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TravelActivityEntry.TBL_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TravelEntry.TBL_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ExpenseEntry.TBL_NAME);

        onCreate(sqLiteDatabase);
    }

    public TravelActivity getTravelActivity() {
        TravelActivity ta = new TravelActivity();
        String selectQuery = "SELECT  * FROM "+TravelActivityEntry.TBL_NAME;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ta.setId(cursor.getInt(0));
                ta.setStartDate(cursor.getString(1));
                ta.setEndDate(cursor.getString(2));
                ta.setExpense(cursor.getFloat(3));
                ta.setLocation_name(cursor.getString(4));
                ta.setAddress(cursor.getString(5));
                ta.setActivity_type(cursor.getString(6));
            } while (cursor.moveToNext());
        }
            database.close();
        cursor.close();
            return ta;
    }

    public ArrayList<Expense> getExpense(int aid){
        ArrayList<Expense> expenses=new ArrayList<Expense>();
        String selectQuery = "SELECT  * FROM "+ExpenseEntry.TBL_NAME+" WHERE activity_id ="+String.valueOf(aid);
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Expense e = new Expense();
                e.setName(cursor.getString(1));
                e.setTag(cursor.getString(2));
                e.setExpense(cursor.getFloat(3));
                expenses.add(e);
            } while (cursor.moveToNext());
        }
        database.close();
        cursor.close();
        return expenses;
    }

}
