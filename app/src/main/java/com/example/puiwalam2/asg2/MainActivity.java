package com.example.puiwalam2.asg2;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;


public class MainActivity extends Activity {

    private ActivityList activityAdapter;
    private ArrayList<Travel> plans;
    private ArrayList<TravelActivity> activities;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this.deleteDatabase("travelData.db"); //delete database
        //DatabaseTest.Test(this);  //generate db test data
        dbHelper=new DbHelper(this);
        plans = getAllPlans();
        activities = getAllActivities();

        if (!plans.isEmpty() && !activities.isEmpty()) {
            TextView tvPlanTitle = (TextView) findViewById(R.id.plan_title);
            tvPlanTitle.setText(String.valueOf(plans.get(0).getTravel_name())); //only use 1st travel plan

            TextView tvBudget = (TextView) findViewById(R.id.budget_value);
            tvBudget.setText(String.valueOf(plans.get(0).getBudget())); //only use 1st travel plan

            activityAdapter = new ActivityList(this, activities);
            activityAdapter.notifyDataSetChanged();
            ListView lv = (ListView) findViewById(R.id.tripList);
            lv.setAdapter(activityAdapter);
            lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            AbsListView.MultiChoiceModeListener mcListener =
                    new AbsListView.MultiChoiceModeListener() {
                        private int numOfChecked = 0;
                        private ArrayList list_items = new ArrayList();

                        @Override
                        public void onItemCheckedStateChanged(ActionMode mode, int
                                position, long id, boolean checked) {
                            if (checked) {
                                numOfChecked++;
                                list_items.add(position);
                                if (numOfChecked > 1) {
                                    mode.invalidate();
                                }
                            } else {
                                numOfChecked--;
                                list_items.remove(position);
                                if (numOfChecked == 1) {
                                    mode.invalidate();
                                }
                            }
                        }

                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            getMenuInflater().inflate(R.menu.main_context, menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                            if (numOfChecked > 1) {
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
                                    if (!list_items.isEmpty()) {
                                        for (int i = 0; i < list_items.size(); i++) {
                                            // Delete selected items
                                            int position = (int) list_items.get(i);
                                            int rowId = activities.get(position).getId();
                                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                                            db.delete(TravelActivityEntry.TBL_NAME, _ID + "=" + rowId, null);
                                            activityAdapter.removeItem(activities.get(position));
                                        }
                                        list_items = new ArrayList(); //reset selected item list after delete
                                    }
                                    //activityAdapter.notifyDataSetChanged();
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
                            numOfChecked = 0;
                        }
                    };
            lv.setMultiChoiceModeListener(mcListener);
            this.registerForContextMenu(lv);
        }
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
