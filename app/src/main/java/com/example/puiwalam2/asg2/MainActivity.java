package com.example.puiwalam2.asg2;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends Activity {

//    private ArrayList<String> data;
//    private ArrayAdapter<String> adapter;
    private TripList testAdapter;
    ArrayList<Trip> details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        data=new ArrayList();
//        data.add("Empty Trip List");
//        adapter=new ArrayAdapter<String>(
//                this,
//                android.R.layout.simple_expandable_list_item_1,
//                data
//        );
//        ListView lv=(ListView)findViewById(R.id.tripList);
//        lv.setAdapter(adapter);
//        this.registerForContextMenu(lv);

        details=new ArrayList<>();
        testAdapter =new TripList(this, details);
        testAdapter.addItem(new Trip("test",1,1,1));
        ListView lv=(ListView)findViewById(R.id.tripList);
        lv.setAdapter(testAdapter);
        this.registerForContextMenu(lv);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        System.out.println("item id: "+item.getItemId());
        switch (item.getItemId()) {
            case R.id.menu_Del:
                break;
            case R.id.menu_Edit:
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
//                data.add("New Item");
//                adapter.notifyDataSetChanged();
                testAdapter.addItem(new Trip("test2",1,1,1));   //to be changed
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
