package com.example.puiwalam2.asg2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static android.provider.BaseColumns._ID;


public class MainActivity extends Activity {

    private ActivityListAdaptor activityAdapter;
    private ArrayList<Travel> plans;
    private ArrayList<TravelActivity> activities;
    private DbHelper dbHelper;
    private LayoutInflater layoutInflater;
    private View edit_budget_dialog;
    private View edit_plan_dialog;

    private MainActivity selfRef;
    private final int ADD_ACTIVITY=0;
    private final int EDIT_ACTIVITY=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this.deleteDatabase("travelData.db"); //delete database
        //DatabaseTest.Test(this);  //generate db test data
        selfRef=this;
        dbHelper=new DbHelper(this);
        plans = getAllPlans();
        activities = getAllActivities();
        layoutInflater=LayoutInflater.from(this);

        //generate 1st entry if no database
        if (plans.isEmpty()) {
            TextView tvPlan_title=(TextView)findViewById(R.id.plan_title);
            TextView tvBudgetValue=(TextView)findViewById(R.id.budget_value);

            String initial_name=tvPlan_title.getText().toString();
            float initial_budget=Float.parseFloat(tvBudgetValue.getText().toString());
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String date = sdf.format(new Date());

            ContentValues cv = new ContentValues();
            cv.put(TravelEntry.COL_NAME_TRAVEL_NAME, initial_name);
            cv.put(TravelEntry.COL_NAME_TRAVEL_SDATE, date);
            cv.put(TravelEntry.COL_NAME_TRAVEL_EDATE, date);
            cv.put(TravelEntry.COL_NAME_TRAVEL_BUDGET, initial_budget);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.insert(TravelEntry.TBL_NAME, null, cv);  //add 1 plan to db
        }

        TextView tvPlan_title=(TextView)findViewById(R.id.plan_title);
        tvPlan_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_plan_dialog = layoutInflater.inflate(R.layout.edit_plan_dialog, null);
                AlertDialog.Builder tempAlertDialog = new AlertDialog.Builder(MainActivity.this);
                tempAlertDialog.setTitle("Edit Plan Name");
                tempAlertDialog.setView(edit_plan_dialog);
                tempAlertDialog.setPositiveButton(R.string.confirm_edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editText = (EditText) edit_plan_dialog.findViewById(R.id.edit_plan);
                        if (!editText.getText().toString().isEmpty()) {
                            TextView tvPlan_title = (TextView) findViewById(R.id.plan_title);
                            tvPlan_title.setText(editText.getText().toString());
                            ContentValues cv = new ContentValues();
                            cv.put(TravelEntry.COL_NAME_TRAVEL_NAME, tvPlan_title.getText().toString());
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            db.update(TravelEntry.TBL_NAME, cv, _ID + "=" + plans.get(0).getTravelID(), null);  //update the 1st travel entry
                        }
                    }
                });
                tempAlertDialog.show();
            }
        });

        Button btnEditBudget = (Button) findViewById(R.id.edit_budget);
        btnEditBudget.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_budget_dialog = layoutInflater.inflate(R.layout.edit_budget_dialog, null);
                AlertDialog.Builder tempAlertDialog = new AlertDialog.Builder(MainActivity.this);
                tempAlertDialog.setTitle(R.string.edit_budget);
                tempAlertDialog.setView(edit_budget_dialog);
                tempAlertDialog.setPositiveButton(R.string.confirm_edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editText = (EditText) edit_budget_dialog.findViewById(R.id.edit_budget_value);
                        if (!editText.getText().toString().isEmpty()) {
                            TextView tvBudget = (TextView) findViewById(R.id.budget_value);
                            tvBudget.setText(editText.getText().toString());
                            calculateAndSetExpenseAndBudget();
                            //update budget in TravelEntry
                            ContentValues cv = new ContentValues();
                            cv.put(TravelEntry.COL_NAME_TRAVEL_BUDGET, Float.parseFloat((tvBudget.getText().toString())));
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            db.update(TravelEntry.TBL_NAME, cv, _ID + "=" + plans.get(0).getTravelID(), null);  //update the 1st travel entry
                        }
                    }
                });
                tempAlertDialog.show();
            }
        });

        TextView tvPlanTitle = (TextView) findViewById(R.id.plan_title);
        tvPlanTitle.setText(String.valueOf(plans.get(0).getTravel_name())); //only use 1st travel entry

        TextView tvBudget = (TextView) findViewById(R.id.budget_value);
        tvBudget.setText(String.valueOf(plans.get(0).getBudget())); //only use 1st travel entry

        activityAdapter = new ActivityListAdaptor(this, getAllActivities());
        updateAllData();
        ListView lv = (ListView) findViewById(R.id.tripList);
        lv.setAdapter(activityAdapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        AbsListView.MultiChoiceModeListener mcListener =
                new AbsListView.MultiChoiceModeListener() {
                    private ArrayList selectedItems = new ArrayList<>();

                    @Override
                    public void onItemCheckedStateChanged(ActionMode mode, int
                            position, long id, boolean checked) {
                        if (checked) {
                            selectedItems.add(activityAdapter.getItem(position));
                        } else {
                            selectedItems.remove(activityAdapter.getItem(position));
                        }
                        if (selectedItems.size() == 1 || selectedItems.size()-1 == 1) {
                            mode.invalidate();
                        }
                    }

                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        getMenuInflater().inflate(R.menu.main_context, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        if (selectedItems.size() > 1) {
                            menu.findItem(R.id.menu_Edit).setVisible(false);
                        } else {
                            menu.findItem(R.id.menu_Edit).setVisible(true);
                        }
                        return true;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_Del:
                                if (!selectedItems.isEmpty()) {
                                    int list_size = selectedItems.size();
                                    for (int i = 0; i < list_size; i++) {
                                        // Delete selected items
                                        TravelActivity temp_activity=(TravelActivity) selectedItems.get(i);
                                        int rowId = temp_activity.getId();
                                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                                        db.delete(TravelActivityEntry.TBL_NAME, _ID + "=" + rowId, null);
                                        activities.remove(temp_activity);
                                    }
                                    selectedItems = new ArrayList<>(); //reset selected item list after delete
                                }
                                updateAllData();
                                mode.finish();
                                break;
                            case R.id.menu_Edit:
                                Intent i=new Intent(selfRef,AddEditActivity.class);
                                TravelActivity a = (TravelActivity) selectedItems.get(0);
                                //int travelId=a.getId();
                                int activityId=a.getId();
                                int travelId=a.getTravelId();
                                i.putExtra("TravelID", travelId);
                                i.putExtra("ActivityID", activityId);
                                i.putExtra("ACTION_TYPE", EDIT_ACTIVITY);
                                startActivity(i);
                                updateAllData();
                                mode.finish();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        selectedItems = new ArrayList<>();
                    }
                };
        lv.setMultiChoiceModeListener(mcListener);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                TravelActivity aActivity = (TravelActivity)activityAdapter.getItem(position);
                int travelId=aActivity.getTravelId();
                int activityId=aActivity.getId();
                Intent intent = new Intent(selfRef,BrowseDetailActivity.class);
                intent.putExtra("TravelID", travelId);
                intent.putExtra("ActivityID", activityId);
                startActivity(intent);
            }
        });
        this.registerForContextMenu(lv);
    }

    private void calculateAndSetExpenseAndBudget() {
        float sumOfExpense=0;
        for (TravelActivity a: activities) {
            sumOfExpense+=a.getExpense();
        }
        TextView tvExpenseSum = (TextView) findViewById(R.id.expense_sum);
        tvExpenseSum.setText(String.valueOf(sumOfExpense));
        TextView tvBudget = (TextView) findViewById(R.id.budget_value);
        TextView balance_tag=(TextView) findViewById(R.id.budget_remain_tag) ;
        TextView tvBudgetRemain = (TextView) findViewById(R.id.budget_remain);
        float new_budget = Float.parseFloat(tvBudget.getText().toString())-sumOfExpense;
        if (new_budget<0) {
            balance_tag.setTextColor(getResources().getColor(R.color.over_budget));
            tvBudgetRemain.setTextColor(getResources().getColor(R.color.over_budget));
        } else {
            balance_tag.setTextColor(getResources().getColor(R.color.normal_budget));
            tvBudgetRemain.setTextColor(getResources().getColor(R.color.normal_budget));
        }
        tvBudgetRemain.setText(String.valueOf(new_budget));

    }

    private ArrayList<Travel> getAllPlans() {
        ArrayList<Travel> planList=new ArrayList<>();
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor c = db.query(TravelEntry.TBL_NAME, null, null, null, null, null, null);
        if (c.moveToFirst()){
            do{
                int id = c.getInt(0);
                String name=c.getString(1);
                Float budget=Float.parseFloat(c.getString(4));
                planList.add(new Travel(id, name, budget));
            }while (c.moveToNext());
        }
        c.close();
        return planList;
    }

    private ArrayList<TravelActivity> getAllActivities() {
        ArrayList<TravelActivity> activityList=new ArrayList<>();
        DbHelper dbHelper=new DbHelper(this);
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor c = db.query(TravelActivityEntry.TBL_NAME, null, null, null, null, null, null);
        if (c.moveToFirst()){
            do{
                int id=c.getInt(0);
                String sDate=c.getString(1);
                String eDate=c.getString(2);
                Float expense=Float.parseFloat(c.getString(3));
                String name=c.getString(4);
                String address=c.getString(5);
                String activityType=c.getString(6);
                int travelId=c.getInt(7);
                activityList.add(new TravelActivity(id, sDate, eDate, expense, name, address, activityType, travelId));
            }while (c.moveToNext());
        }
        // Sort by start date and time
        Collections.sort(activityList, new Comparator<TravelActivity>() {
            @Override
            public int compare(TravelActivity activity1, TravelActivity activity2)
            {
                Date date1 = null, date2=null;
                try {
                    String a1sDate=activity1.getStartDate();
                    String a2sDate=activity2.getStartDate();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                    date1 =  df.parse(a1sDate);
                    date2 =  df.parse(a2sDate);
                } catch (ParseException pe) {
                    pe.printStackTrace();
                }
//                System.out.println("date1: "+date1+"date2: "+date2+"date1.compareTo(date2): "+date1.compareTo(date2));    //for testing
                if (date1!=null && date2!=null) {
                    return  date2.compareTo(date1);
                } else {
                    return 0;
                }
            }
        });
        c.close();
        return activityList;
    }

    @Override
    protected void onResume(){
        super.onResume();
        updateAllData();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_Add:
                Intent intent=new Intent(selfRef,AddEditActivity.class);
                //SQLiteDatabase db = dbHelper.getReadableDatabase();
                //Cursor c = db.query(TravelActivityEntry.TBL_NAME, null, null, null, null, null, null);
                int travelId;
                //int activityId;
                /*if (c.moveToLast()) {
                    activityId=c.getInt(0);
                    travelId=c.getInt(7);
                } else {
                    activityId=1;
                    travelId=1;
                }*/
                //As currenly there would be only one plan
                Travel t = plans.get(0);
                travelId=t.getTravelID();
                //intent.putExtra("ActivityID", activityId);
                intent.putExtra("TravelID", travelId);
                intent.putExtra("ACTION_TYPE", ADD_ACTIVITY);
                startActivity(intent);
                updateAllData();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void updateAllData() {
        plans=getAllPlans();
        activities=getAllActivities();
        activityAdapter.updateData(getAllActivities());
        calculateAndSetExpenseAndBudget();
    }

}
