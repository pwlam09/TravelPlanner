package com.example.puiwalam2.asg2;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by PuiWa on 13/11/2016.
 */

public class TripList extends BaseAdapter {
    private static final int mTripItemLayout = R.layout.trip_item;
    private LayoutInflater mInflater;
    private ArrayList<Trip> mTrips;

    public TripList(Activity context, ArrayList<Trip> details) {
        this.mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        this.mTrips = details;
    }

    public void addItem(Trip aTrip) {
        mTrips.add(aTrip);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(mTripItemLayout, parent, false);
        } else {
            view = convertView;
        }

        TextView tx;
        ImageView iv;

        tx = (TextView) view.findViewById(R.id.trip_details);
        tx.setText(mTrips.get(position).toString());
        iv = (ImageView) view.findViewById(R.id.trip_icon);
        iv.setImageResource(0); //0 is to be changed

        return view;
    }

    @Override
    public int getCount() {
        return mTrips.size();
    }

    @Override
    public Object getItem(int position) {
        return mTrips.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
