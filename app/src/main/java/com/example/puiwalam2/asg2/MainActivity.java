package com.example.puiwalam2.asg2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

    private ActivityList activityAdapter;
    private ArrayList<Travel> plans;
    private ArrayList<TravelActivity> activities;
    private DbHelper dbHelper;
    private LayoutInflater layoutInflater;
    private View edit_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this.deleteDatabase("travelData.db"); //delete database
        DatabaseTest.Test(this);  //generate db test data
        dbHelper=new DbHelper(this);
        plans = getAllPlans();
        activities = getAllActivities();

        layoutInflater=LayoutInflater.from(this);
        Button btnEditBudget = (Button) findViewById(R.id.edit_budget);
        btnEditBudget.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_dialog = layoutInflater.inflate(R.layout.edit_budget_dialog, null);
                AlertDialog.Builder tempAlertDialog = new AlertDialog.Builder(MainActivity.this);
                tempAlertDialog.setTitle(R.string.edit_budget);
                tempAlertDialog.setView(edit_dialog);
                tempAlertDialog.setPositiveButton(R.string.confirm_edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editText = (EditText) edit_dialog.findViewById(R.id.edit_budget_value);
                        TextView tvBudget = (TextView) findViewById(R.id.budget_value);
                        tvBudget.setText(editText.getText().toString());
                        calculateAndSetExpenseAndBudget();
                    }
                });
                tempAlertDialog.show();
            }
        });

        if (!plans.isEmpty() && !activities.isEmpty()) {
            TextView tvPlanTitle = (TextView) findViewById(R.id.plan_title);
            tvPlanTitle.setText(String.valueOf(plans.get(0).getTravel_name())); //only use 1st travel plan

            TextView tvBudget = (TextView) findViewById(R.id.budget_value);
            tvBudget.setText(String.valueOf(plans.get(0).getBudget())); //only use 1st travel plan

            calculateAndSetExpenseAndBudget();

            activityAdapter = new ActivityList(this, activities);
            activityAdapter.notifyDataSetChanged();
            ListView lv = (ListView) findViewById(R.id.tripList);
            lv.setAdapter(activityAdapter);
            lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            AbsListView.MultiChoiceModeListener mcListener =
                    new AbsListView.MultiChoiceModeListener() {
                        private ArrayList selectedItems = new ArrayList<>();

                        @Override
                        public void onItemCheckedStateChanged(ActionMode mode, int
                                position, long id, boolean checked) {
                            System.out.println("onItemCheckedStateChanged");
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
                        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                            if (selectedItems.size() > 1) {
                                menu.findItem(R.id.menu_Edit).setVisible(false);
                            } else {
                                menu.findItem(R.id.menu_Edit).setVisible(true);
                            }
                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
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
                                            calculateAndSetExpenseAndBudget();  //renew expense and balance
                                        }
                                        selectedItems = new ArrayList<>(); //reset selected item list after delete
                                    }
                                    activityAdapter.notifyDataSetChanged();
                                    actionMode.finish();
                                    break;
                                case R.id.menu_Edit:
                                    //to be added
                                    break;
                                default:
                                    break;
                            }
                            return false;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                            selectedItems = new ArrayList<>();
                        }
                    };
            lv.setMultiChoiceModeListener(mcListener);
            this.registerForContextMenu(lv);
        }
    }

    private void calculateAndSetExpenseAndBudget() {
        float sumOfExpense=0;
        for (TravelActivity a: activities) {
            sumOfExpense+=a.getExpense();
        }
        TextView tvExpenseSum = (TextView) findViewById(R.id.expense_sum);
        tvExpenseSum.setText(String.valueOf(sumOfExpense));
        TextView tvBudget = (TextView) findViewById(R.id.budget_value);
        TextView tvBudgetRemain = (TextView) findViewById(R.id.budget_remain);
        tvBudgetRemain.setText(String.valueOf(Float.parseFloat(tvBudget.getText().toString())-sumOfExpense));
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
                String imgPath=c.getString(6);
                String activityType=c.getString(7);
                activityList.add(new TravelActivity(id, sDate, eDate, expense, name, address, activityType));
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
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mmaa");
                    date1 =  df.parse(a1sDate);
                    date2 =  df.parse(a2sDate);
                } catch (ParseException pe) {
                    pe.printStackTrace();
                }
//                System.out.println("date1: "+date1+"date2: "+date2+"date1.compareTo(date2): "+date1.compareTo(date2));    //for testing
                if (date1!=null && date2!=null) {
                    return  date1.compareTo(date2);
                } else {
                    return 0;
                }
            }
        });
        c.close();
        return activityList;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_Del:
                System.out.println("item id: "+item.getItemId());
                break;
            case R.id.menu_Edit:
                System.out.println("item id: "+item.getItemId());
                break;
            default:
                return super.onContextItemSelected(item);
        }
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);

        int id = item.getItemId();
        switch (id){
            case R.id.menu_Add:
                //to be added
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
