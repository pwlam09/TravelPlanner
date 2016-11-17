package com.example.puiwalam2.asg2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Eva Hung on 16/11/2016.
 */
public class AddEditActivity extends android.app.Activity {
    TravelActivity ta=new TravelActivity();
    //final int travel_id=1;
    private int travel_id;
    private int action_type;
    private final int ADD_ACTIVITY=0;
    private final int EDIT_ACTIVITY=1;
    private int activity_id;
    private int working_position;
    private String[] tagList;
    private String[] activityList;
    private String tag_item;
    private String activity_item;
    private ExpenseListAdapter adapter;
    private ArrayList<Expense> data;
    private ArrayList<String> share_data;
    private ListView lv;
    private EditText et_add_name;
    private EditText et_add_price;
    private Spinner spinner_tag;
    private Spinner spinner_activity;
    private ArrayAdapter<CharSequence> spinner_tag_adapter;
    private ArrayAdapter<CharSequence> spinner_activity_adapter;

    private Button bt_start;
    private Button bt_end;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_activity);

        tagList=getResources().getStringArray(R.array.tag_array);
        activityList=getResources().getStringArray(R.array.activity_array);

        spinner_activity = (Spinner) findViewById(R.id.spinner_activity);
        spinner_activity_adapter = ArrayAdapter.createFromResource(this,
                R.array.activity_array, android.R.layout.simple_spinner_item);
        spinner_activity_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_activity.setAdapter(spinner_activity_adapter);
        spinner_activity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                // TODO Auto-generated method stub
                activity_item = activityList[position];
                ta.setActivity_type(activity_item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        bt_start=(Button) findViewById(R.id.bt_start);
        bt_end=(Button) findViewById(R.id.bt_end);

        data = new ArrayList<Expense>();

        Intent i=getIntent();
        travel_id=i.getExtras().getInt("TravelID");
        if (i.getExtras()!=null) {
            action_type = i.getExtras().getInt("ACTION_TYPE");
            if (action_type==EDIT_ACTIVITY) {
                activity_id = i.getExtras().getInt("ActivityID");
                //read database
                DbHelper dphelper;
                dphelper = new DbHelper(this);
                SQLiteDatabase db = dphelper.getReadableDatabase();

                String[] activity_projection={TravelActivityEntry._ID,TravelActivityEntry.COL_NAME_ACTIVITY_ACTIVITY_TYPE,
                        TravelActivityEntry.COL_NAME_ACTIVITY_SDATE,TravelActivityEntry.COL_NAME_ACTIVITY_EDATE,
                        TravelActivityEntry.COL_NAME_ACTIVITY_LOCATION_NAME, TravelActivityEntry.COL_NAME_ACTIVITY_ADDRESS};

                Cursor activity_c=db.query(TravelActivityEntry.TBL_NAME,activity_projection,
                        null,null,null,null,null);

                if (activity_c.moveToFirst()){
                    do{
                        if (activity_c.getInt(0) == activity_id) {
                            ta.setId(activity_c.getInt(0));
                            ta.setActivity_type(activity_c.getString(1));
                            ta.setStartDate(activity_c.getString(2));
                            ta.setEndDate(activity_c.getString(3));
                            ta.setLocation_name(activity_c.getString(4));
                            ta.setAddress(activity_c.getString(5));
                        }
                    }while (activity_c.moveToNext());
                }

                //Toast t = Toast.makeText(this, ta.getId() + " " + ta.getActivity_type() + " " + ta.getStartDate()+ " " + ta.getEndDate() + " " + ta.getLocation_name() + " " + ta.getAddress(), Toast.LENGTH_LONG);
                //t.show();

                activity_item = ta.getActivity_type();
                //translsate start date and end date to date and put the string to the button text
                bt_start.setText(ta.getStartDate());
                bt_end.setText(ta.getEndDate());
                //translate
                String[] start = ta.getStartDate().split(" ");
                String[] date, time;
                String deter;
                date = start[0].split("-");
                time=start[1].split(":");
                deter=start[2];
                start_minute=Integer.parseInt(time[1]);
                start_hour=Integer.parseInt(time[0]);
                if (deter.equals("PM") || deter.equals("pm")){
                    start_hour+=12;
                }
                final Calendar tmp = Calendar.getInstance();
                tmp.set(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]));
                startD=tmp.getTime();

                String[] end = ta.getEndDate().split(" ");
                date = end[0].split("-");
                time=end[1].split(":");
                deter=end[2];
                end_minute=Integer.parseInt(time[1]);
                end_hour=Integer.parseInt(time[0]);
                if (deter.equals("PM") || deter.equals("pm")){
                    end_hour+=12;
                }
                tmp.set(Integer.parseInt(date[2])+1, Integer.parseInt(date[1]), Integer.parseInt(date[0]));
                maxEndD=tmp.getTime();

                EditText et_loaction=(EditText) findViewById(R.id.et_location);
                EditText et_address=(EditText) findViewById(R.id.et_address);
                et_loaction.setText(ta.getLocation_name());
                et_address.setText(ta.getAddress());

                String[] expense_projection={ExpenseEntry.COL_NAME_ACTIVITY_ID, ExpenseEntry._ID,
                        ExpenseEntry.COL_NAME_EXPENSE_NAME, ExpenseEntry.COL_NAME_EXPENSE_TAG,
                        ExpenseEntry.COL_NAME_EXPENSE};

                Cursor expense_c=db.query(ExpenseEntry.TBL_NAME,expense_projection,
                        null,null,null,null,null);

                if (expense_c.moveToFirst()){
                    do{
                        if (expense_c.getInt(0) == activity_id) {
                            Expense e=new Expense();
                            e.setId(expense_c.getInt(1));
                            e.setName(expense_c.getString(2));
                            e.setTag(expense_c.getString(3));
                            e.setExpense(expense_c.getFloat(4));
                            data.add(e);
                        }
                    }while (expense_c.moveToNext());
                }
            }
        }

        //DatabaseTest.Test(this);
        //setting expense adapter
        adapter = new ExpenseListAdapter(this, data);
        lv = (ListView) findViewById(R.id.lv_expense);
        lv.setAdapter(adapter);
        this.registerForContextMenu(lv);
        resetListViewHeight(lv);

    }

    public void cancelAction(View v){
        finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_expense_context_menu, menu);
    }

    @Override public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.expense_edit:
                EditExpense(info.position);
                return true;
            case R.id.expense_del:
                DelExpense(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void resetListViewHeight(ListView lv){
        int height = 0;
        for (int i = 0; i< adapter.getCount();i++) {
            View item = adapter.getView(i, null, lv);
            item.measure(0, 0);
            height = height+ item.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = lv.getLayoutParams();
        params.height = height + (lv.getDividerHeight() * (adapter.getCount() - 1));
        lv.setLayoutParams(params);
    }

    public void DelExpense(int position) {
        data.remove(position);
        adapter.notifyDataSetChanged();
    }

    private boolean validExpense(String expense){
        if (expense.equals("")){
            Toast t= Toast.makeText(getBaseContext(), "Expense should be a non-empty number.", Toast.LENGTH_LONG);
            t.show();
            return false;
        }
        return true;
    }

    private boolean validName(String name){
        if (name.length()>40){
            Toast t= Toast.makeText(getBaseContext(), "The name need to smaller or equal to 40 length.", Toast.LENGTH_LONG);
            t.show();
            return false;
        }
        return true;
    }

    public void EditExpense(int position) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Expense e = new Expense();
                e.setName(et_add_name.getText().toString());
                e.setTag(tag_item);
                if (validExpense(et_add_price.getText().toString()) && validName(et_add_name.getText().toString()))
                {
                    e.setExpense(Float.parseFloat(et_add_price.getText().toString()));
                    data.set(working_position, e);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        Expense cur=data.get(position);
        working_position=position;
        AlertDialog ad= adb.create();
        View view = LayoutInflater.from(this).inflate(R.layout.expense_add,null);
        et_add_name= (EditText) view.findViewById(R.id.et_add_name);
        et_add_price= (EditText) view.findViewById(R.id.et_add_price);
        spinner_tag = (Spinner) view.findViewById(R.id.spinner_tag);
        spinner_tag_adapter = ArrayAdapter.createFromResource(this,
                R.array.tag_array, android.R.layout.simple_spinner_item);
        spinner_tag_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_tag.setAdapter(spinner_tag_adapter);

        //set back info
        et_add_name.setText(cur.getName());
        et_add_price.setText(String.valueOf(cur.getExpense()));
        tag_item = cur.getTag();

        spinner_tag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                // TODO Auto-generated method stub
                tag_item = tagList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
        ad.setView(view);
        ad.show();
    }

    public void AddExpense(View v) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Expense e = new Expense();
                e.setName(et_add_name.getText().toString());
                e.setTag(tag_item);
                if (validExpense(et_add_price.getText().toString()) && validName(et_add_name.getText().toString()))
                {
                    e.setExpense(Float.parseFloat(et_add_price.getText().toString()));
                    data.add(e);
                    adapter.notifyDataSetChanged();
                    resetListViewHeight(lv);
                }
            }
        });
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog ad= adb.create();
        View view = LayoutInflater.from(this).inflate(R.layout.expense_add,null);
        et_add_name= (EditText) view.findViewById(R.id.et_add_name);
        et_add_price= (EditText) view.findViewById(R.id.et_add_price);
        spinner_tag = (Spinner) view.findViewById(R.id.spinner_tag);
        spinner_tag_adapter = ArrayAdapter.createFromResource(this,
                R.array.tag_array, android.R.layout.simple_spinner_item);
        spinner_tag_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_tag.setAdapter(spinner_tag_adapter);
        spinner_tag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                // TODO Auto-generated method stub
                tag_item = tagList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
        ad.setView(view);
        ad.show();
    }

    static String start_time;
    static String end_time;
    static boolean set_start=false;
    final Calendar c = Calendar.getInstance();

    public void setStartTime(View v){
        set_start=true;
        showStartDateDialog();
    }

    public void setEndTime(View v){
        set_start=false;
        showEndDateDialog();
    }

    Date maxEndD;
    Date startD;

    public void showStartDateDialog(){
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {
                        Calendar tmp = Calendar.getInstance();
                        tmp.set(Calendar.DATE, day);
                        tmp.set(Calendar.MONTH, month);
                        tmp.set(Calendar.YEAR, year);
                        bt_start.setText(day + "-" + (month + 1) + "-"
                                + year);
                        startD=tmp.getTime();
                        showTimeDialog();
                    }
                },year, month, day);
        if (maxEndD!=null){
            dpd.getDatePicker().setMaxDate(maxEndD.getTime());
        }
        dpd.show();
    }

    public void showEndDateDialog(){
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {
                        Calendar tmp = Calendar.getInstance();
                        tmp.set(Calendar.DATE, day+1);
                        tmp.set(Calendar.MONTH, month);
                        tmp.set(Calendar.YEAR, year);
                        bt_end.setText(day + "-" + (month + 1) + "-"
                                + year);
                        maxEndD=tmp.getTime();
                        showTimeDialog();
                    }
                },year, month, day);
        if (startD!=null){
            dpd.getDatePicker().setMinDate(startD.getTime());
        }
        dpd.show();
    }

    int start_hour=-1, start_minute=-1;
    int end_hour=25, end_minute=25;

    public void showTimeDialog(){
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog tpd = new TimePickerDialog(this,
                new android.app.TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        String time;
                        if (hour-12 >= 0){
                            if (hour != 12)
                                hour = hour - 12;
                            time = "pm";
                            if (hour == 12 && minute==0)
                                time = "noon";
                        }else
                            time="am";
                        if (set_start==true){
                            if (hour>end_hour || hour==end_hour && minute > end_minute){
                                Toast t=Toast.makeText(AddEditActivity.this, "The Start Time need to be smaller than the End Time.", Toast.LENGTH_LONG);
                                t.show();
                            }else {
                                bt_start.setText(bt_start.getText().toString() + " " + hour + ":" + minute + " " + time);
                                start_hour=hour;
                                start_minute=minute;
                                ta.setStartDate(bt_start.getText().toString());
                            }
                            //bt_start.setText(bt_start.getText().toString()+" "+hour+":"+minute);
                        }else {
                            if (hour<start_hour || hour==start_hour && minute < start_minute){
                                Toast t=Toast.makeText(AddEditActivity.this, "The End Time need to be greater than the Start Time.", Toast.LENGTH_LONG);
                                t.show();
                            }else {
                                bt_end.setText(bt_end.getText().toString() + " " + hour + ":" + minute + " " + time);
                                end_hour=hour;
                                end_minute=minute;
                                ta.setEndDate(bt_start.getText().toString());
                                //bt_end.setText(bt_end.getText().toString()+" "+hour+":"+minute);
                            }
                        }
                    }
                }, hour, minute, false);
        tpd.show();
    }

    private ArrayList<String> validate(){
        ArrayList<String> invalid=new ArrayList<String>();
        if (ta.getStartDate() == null || ta.getStartDate().isEmpty()){//or .equals ""?
            invalid.add("start_date");
            bt_start.setTextColor(Color.RED);
            bt_start.setText("Please enter Start Date.");
        }
        if (ta.getEndDate() == null || ta.getEndDate().isEmpty()){
            invalid.add("end_date");
            bt_end.setTextColor(Color.RED);
            bt_end.setText("Please enter End Date.");
        }
        if (ta.getLocation_name() == null || ta.getLocation_name().isEmpty()){
            invalid.add("location");
            EditText et_location= (EditText) findViewById(R.id.et_location);
            et_location.setHintTextColor(Color.RED);
        }
        if (ta.getAddress() == null || ta.getAddress().isEmpty()){
            invalid.add("address");
            EditText et_address= (EditText) findViewById(R.id.et_address);
            et_address.setHintTextColor(Color.RED);
        }
        return invalid;
    }

    public void addEditActivity(View v){
        EditText et_location= (EditText) findViewById(R.id.et_location);
        EditText et_address= (EditText) findViewById(R.id.et_address);
        ta.setLocation_name(et_location.getText().toString());
        ta.setAddress(et_address.getText().toString());

        if (action_type==EDIT_ACTIVITY){
            if (validate().size()==0) {
                DbHelper dphelper;
                dphelper = new DbHelper(this);
                SQLiteDatabase db = dphelper.getWritableDatabase();

                calTotalExpense();

                //update activity
                ContentValues activity_value = new ContentValues();
                activity_value.put(TravelActivityEntry.COL_NAME_ACTIVITY_ACTIVITY_TYPE, ta.getActivity_type());
                activity_value.put(TravelActivityEntry.COL_NAME_ACTIVITY_SDATE, ta.getStartDate());
                activity_value.put(TravelActivityEntry.COL_NAME_ACTIVITY_EDATE, ta.getEndDate());
                activity_value.put(TravelActivityEntry.COL_NAME_ACTIVITY_EXPENSE, ta.getExpense());
                activity_value.put(TravelActivityEntry.COL_NAME_ACTIVITY_LOCATION_NAME, ta.getLocation_name());
                activity_value.put(TravelActivityEntry.COL_NAME_ACTIVITY_ADDRESS, ta.getAddress().replaceAll("\\s+",""));
                activity_value.put(TravelActivityEntry.COL_NAME_TRAVEL_ID, travel_id);
                db.update(TravelActivityEntry.TBL_NAME, activity_value, "_ID="+activity_id,null);

                //update expense
                //compare to get  no longer exist expense
                db = dphelper.getReadableDatabase();
                String[] projection={ExpenseEntry.COL_NAME_ACTIVITY_ID, ExpenseEntry._ID};
                Cursor c=db.query(ExpenseEntry.TBL_NAME,projection,
                        null,null,null,null,null);
                ArrayList<Expense> db_data=new ArrayList<Expense>();
                if (c.moveToFirst()){
                    do{
                        if (c.getInt(0) == activity_id){
                            Expense e=new Expense();
                            e.setId(c.getInt(1));
                            db_data.add(e);
                        }
                    }while (c.moveToNext());
                }

                db = dphelper.getWritableDatabase();
                for (int i=0;i<data.size();i++) {
                    ContentValues expense_value = new ContentValues();
                    expense_value.put(ExpenseEntry.COL_NAME_EXPENSE_NAME, data.get(i).getName());
                    expense_value.put(ExpenseEntry.COL_NAME_EXPENSE_TAG, data.get(i).getTag());
                    expense_value.put(ExpenseEntry.COL_NAME_EXPENSE, data.get(i).getExpense());
                    expense_value.put(ExpenseEntry.COL_NAME_ACTIVITY_ID, activity_id);
                    if (data.get(i).getId()!=0) {
                        db.update(ExpenseEntry.TBL_NAME, expense_value, "_ID=" + activity_id, null);
                        for (int index = 0; index < db_data.size(); index++) {
                            if (data.get(i).getId() == db_data.get(index).getId())
                                db_data.remove((Integer)index);
                        }
                    }
                    else{
                        long newRowId2;
                        newRowId2 = db.insert(ExpenseEntry.TBL_NAME, null, expense_value);
                    }
                }

                for (int i=0;i<db_data.size();i++) {
                    db.delete(ExpenseEntry.TBL_NAME,"_ID=" +db_data.get(i).getId(),null);
                }

                Toast t= Toast.makeText(this, "Activity Edited", Toast.LENGTH_LONG);
                t.show();
                finish();

            }else{
                Toast t= Toast.makeText(this, "Please ensure all value except the expense are typed in.", Toast.LENGTH_LONG);
                t.show();
            }
        }else if (action_type==ADD_ACTIVITY){
            if (validate().size()==0) {
                DbHelper dphelper;
                dphelper = new DbHelper(this);
                SQLiteDatabase db = dphelper.getWritableDatabase();

                calTotalExpense();

                //put activity
                ContentValues activity_value = new ContentValues();
                activity_value.put(TravelActivityEntry.COL_NAME_ACTIVITY_ACTIVITY_TYPE, ta.getActivity_type());
                activity_value.put(TravelActivityEntry.COL_NAME_ACTIVITY_SDATE, ta.getStartDate());
                activity_value.put(TravelActivityEntry.COL_NAME_ACTIVITY_EDATE, ta.getEndDate());
                activity_value.put(TravelActivityEntry.COL_NAME_ACTIVITY_EXPENSE, ta.getExpense());
                activity_value.put(TravelActivityEntry.COL_NAME_ACTIVITY_LOCATION_NAME, ta.getLocation_name());
                activity_value.put(TravelActivityEntry.COL_NAME_ACTIVITY_ADDRESS, ta.getAddress());
                activity_value.put(TravelActivityEntry.COL_NAME_TRAVEL_ID, travel_id);
                long newRowId;
                newRowId = db.insert(TravelActivityEntry.TBL_NAME, null, activity_value);

                //put expense
                for (int i=0;i<data.size();i++) {
                    ContentValues expense_value = new ContentValues();
                    expense_value.put(ExpenseEntry.COL_NAME_EXPENSE_NAME, data.get(i).getName());
                    expense_value.put(ExpenseEntry.COL_NAME_EXPENSE_TAG, data.get(i).getTag());
                    expense_value.put(ExpenseEntry.COL_NAME_EXPENSE, data.get(i).getExpense());
                    expense_value.put(ExpenseEntry.COL_NAME_ACTIVITY_ID, activity_id);
                    long newRowId2;
                    newRowId2 = db.insert(ExpenseEntry.TBL_NAME, null, expense_value);
                }

                Toast t= Toast.makeText(this, "Activity Added", Toast.LENGTH_LONG);
                t.show();
                finish();

            }else{
                Toast t= Toast.makeText(this, "Please ensure all value except the expense are typed in.", Toast.LENGTH_LONG);
                t.show();
            }
        }

    }

    public void calTotalExpense(){
        float totalExpense=0;
        for (int i=0;i<data.size();i++) {
            totalExpense+=data.get(i).getExpense();
        }
        ta.setExpense(totalExpense);
    }

}
