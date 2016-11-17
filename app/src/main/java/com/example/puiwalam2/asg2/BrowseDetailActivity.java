package com.example.puiwalam2.asg2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BrowseDetailActivity extends Activity {
    int travelId;
    int activityId;
    private final int EDIT_ACTIVITY=1;

    double lat = 0;
    double lon = 0;
    double lastLat = 0;
    double lastLon = 0;

    TextView name, sttime, endtime, address, type;
    ImageView image;
    //ActivityEntry activityEntry;
    String address_string,location_string;
    int zoom;
    ExpenseChartView expenseChartView;
    String url;

    DbHelper dbHelper=new DbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_detail);
        Intent i=getIntent();
        travelId=i.getExtras().getInt("TravelID");
        activityId=i.getExtras().getInt("ActivityID");

        //-----TEST---------
       //travelId=4;
        //activityId=4;
        //DatabaseTest.Test(this);

        //get data from db
        TravelActivity ta=dbHelper.getTravelActivity(activityId);
        zoom = 14;
        //address_string = "CityU,HongKong";

        //set value of text view
        name = (TextView) findViewById(R.id.detail_activityName);
        sttime = (TextView) findViewById(R.id.detail_sttime);
        endtime = (TextView) findViewById(R.id.detail_endtime);
        address = (TextView) findViewById(R.id.detail_address);
        type = (TextView) findViewById(R.id.detail_type);
        name.setText(ta.getLocation_name());
        sttime.setText(ta.getStartDate());
        endtime.setText(ta.getEndDate());
        address.setText(ta.getAddress());
        type.setText(ta.getActivity_type());
        address_string = ta.getAddress();
        location_string=ta.getLocation_name();

        ArrayList<Expense> e=dbHelper.getExpense(activityId);
        float airp=0,acc=0,trans=0,meal=0,ent=0,shop=0,oth=0;
        for(Expense ae: e){
            if(ae.getTag().equals("Airline"))
                airp+=ae.getExpense();
            if(ae.getTag().equals("Accommodation"))
                acc+=ae.getExpense();
            if(ae.getTag().equals("Transportation"))
                trans+=ae.getExpense();
            if(ae.getTag().equals("Meal"))
                meal+=ae.getExpense();
            if(ae.getTag().equals("Entertainment"))
                ent+=ae.getExpense();
            if(ae.getTag().equals("Shopping"))
                shop+=ae.getExpense();
            if(ae.getTag().equals("Other"))
                oth+=ae.getExpense();
        }
        ArrayList<TagTotalExpense> expenses = new ArrayList<>();
        expenses.add(new TagTotalExpense("Airline",airp));
        expenses.add(new TagTotalExpense("Accommodation",acc));
        expenses.add(new TagTotalExpense("Transportation",trans));
        expenses.add(new TagTotalExpense("Meal",meal));
        expenses.add(new TagTotalExpense("Entertainment",ent));
        expenses.add(new TagTotalExpense("Shopping",shop));
        expenses.add(new TagTotalExpense("Other",oth));

        //draw pie chart
        expenseChartView = new ExpenseChartView(this, expenses);
        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);
        ll.addView(expenseChartView);


        //get map
        Button zoomin = (Button) findViewById(R.id.detail_zoomin);
        Button zoomout = (Button) findViewById(R.id.detail_zoomout);
        Button mapPath = (Button) findViewById(R.id.detail_path);
        image = (ImageView) findViewById(R.id.imageView);
        if (isOnline() == false) {
            File mediaImage = null;
            File sdCard = Environment.getExternalStorageDirectory();
            mediaImage = new File(sdCard.getAbsolutePath() + "/TravelPlanner" + "/"+location_string + ".jpg");
            String path = mediaImage.getAbsolutePath();
            if (mediaImage.exists() && path != null) {
                image.setImageBitmap(BitmapFactory.decodeFile(path));
            } else {
                Toast.makeText(getApplicationContext(), "Map not found", Toast.LENGTH_LONG).show();
            }
        } else {
            url = "https://maps.googleapis.com/maps/api/staticmap?center=" + address_string + "&zoom=" + zoom + "&size=500x500";
            BitmapWorkerTask task = new BitmapWorkerTask(image, getResources());
            task.execute(url);
            //zoom in
            zoomin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    zoom += 1;
                    url = "https://maps.googleapis.com/maps/api/staticmap?center=" + address_string + "&zoom=" + zoom + "&size=500x500";
                    BitmapWorkerTask task = new BitmapWorkerTask(image, getResources());
                    task.execute(url);
                }
            });
            //zoom out
            zoomout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    zoom -= 1;
                    url = "https://maps.googleapis.com/maps/api/staticmap?center=" + address_string + "&zoom=" + zoom + "&size=500x500";
                    BitmapWorkerTask task = new BitmapWorkerTask(image, getResources());
                    task.execute(url);
                }
            });

            //get Location and get Path image
            LocationManager locationManager =
                    (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    if(location!=null) {
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                    }
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0, //minimum time interval between notification
                    0, //minimum change in distance between notification
                    locationListener);
            String locationProvider = LocationManager.NETWORK_PROVIDER;
            Location lastKnownLocation=locationManager.getLastKnownLocation(locationProvider);
            if(lastKnownLocation!=null) {
                lastLat = lastKnownLocation.getLatitude();
                lastLon = lastKnownLocation.getLongitude();
            }
            mapPath.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(lat!=0&&lon!=0) {
                        url = "https://maps.googleapis.com/maps/api/staticmap?path=color:0xff0000ff|weight:5|" + String.valueOf(lat) + "," + String.valueOf(lon) +
                                "|" + address_string + "&size=500x500";
                        BitmapWorkerTask task = new BitmapWorkerTask(image, getResources());
                        task.execute(url);
                    }
                    else{
                        if(lastLat!=0 && lastLon!=0){
                            url = "https://maps.googleapis.com/maps/api/staticmap?path=color:0xff0000ff|weight:5|" + String.valueOf(lastLat) + "," +
                                    String.valueOf(lastLon) + "|" + address_string + "&size=500x500";
                            BitmapWorkerTask task = new BitmapWorkerTask(image, getResources());
                            task.execute(url);
                            Toast.makeText(getApplicationContext(),"Last location",Toast.LENGTH_LONG).show();
                        }else
                            Toast.makeText(getApplicationContext(),"no location",Toast.LENGTH_LONG).show();
                    }
                }
            }));
            //save image context menu
            this.registerForContextMenu(image);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_browse_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent i;
        switch (id){
            case R.id.menu_editActivity:
                Intent editActivity=new Intent(BrowseDetailActivity.this,AddEditActivity.class);
                editActivity.putExtra("TravelID",travelId);
                editActivity.putExtra("ActivityID",activityId);
                editActivity.putExtra("ACTION_TYPE",EDIT_ACTIVITY);
                startActivityForResult(editActivity,1);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_map_context,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_saveMap:
//                BitmapWorkerTask task = new BitmapWorkerTask(image,getResources(),"Save");
//                task.execute(url);
//                Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_LONG).show();
                FileOutputStream outStream = null;
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/TravelPlanner");
                //Toast.makeText(getApplicationContext(),""+sdCard.getAbsolutePath(),Toast.LENGTH_LONG).show();
                if (!dir.exists()){
                    dir.mkdirs();
                }
                String fileName = location_string+".jpg";
                File outFile = new File(dir, fileName);
                try {
                    outStream = new FileOutputStream(outFile);
                    Bitmap bitmap=viewToBitmap(image,image.getWidth(),image.getHeight());
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                    //MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "MapFileName.png","Map");
                    refreshGallery(outFile);
                    Toast.makeText(getApplicationContext(),"Map Saved",Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    public static Bitmap viewToBitmap(View view, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public void refreshGallery(File file){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            if(resultCode == RESULT_OK){
//
//            }
//        }
//    }
}
