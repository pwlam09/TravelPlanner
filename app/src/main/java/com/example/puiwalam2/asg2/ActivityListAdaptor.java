package com.example.puiwalam2.asg2;

import android.app.Activity;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by PuiWa on 13/11/2016.
 */

public class ActivityListAdaptor extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private ArrayList<TravelActivity> activityList;
    private int screenWidth;
    private int screenHeight;
    private static class ViewHolder{
        ImageView ivTripIcon;
        TextView tvName;
        TextView tvStartDate;
        TextView tvEndDate;
        TextView tvBudget;
    }

    public ActivityListAdaptor(Activity context, ArrayList<TravelActivity> details) {
        this.layoutInflater = LayoutInflater.from(context);
        this.activityList = details;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        this.screenWidth = displaymetrics.widthPixels;
        this.screenHeight = displaymetrics.heightPixels;
    }

    public void addItem(TravelActivity a) {
        activityList.add(a);
        notifyDataSetChanged();
    }

    public void removeItem(TravelActivity a) {
        activityList.remove(a);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            convertView=layoutInflater.inflate(R.layout.activity_item,null);
            holder=new ViewHolder();
            holder.ivTripIcon =(ImageView)convertView.findViewById(R.id.activity_icon);
            holder.tvName=(TextView)convertView.findViewById(R.id.activity_name);
            holder.tvStartDate=(TextView)convertView.findViewById(R.id.activity_sDate);
            holder.tvEndDate=(TextView)convertView.findViewById(R.id.activity_eDate);
            holder.tvBudget=(TextView)convertView.findViewById(R.id.activity_expense);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }

        // Set layout padding
        int left_right_padding=(int)(screenWidth*0.02);
        int top_bottom_padding=(int)(screenHeight*0.02);
        LinearLayout tripWrapper=(LinearLayout)convertView.findViewById(R.id.tripWrapper);
        LinearLayout tvWrapper=(LinearLayout)convertView.findViewById(R.id.tvWrapper);
        tripWrapper.setPadding(left_right_padding, top_bottom_padding, left_right_padding, top_bottom_padding);
        tvWrapper.setPadding(left_right_padding, top_bottom_padding, left_right_padding, top_bottom_padding);

        holder.ivTripIcon.setBackground(getTripIcon(convertView));   //null to be changed for drawing
        holder.tvName.setText(activityList.get(position).getLocation_name());
        holder.tvStartDate.setText(String.valueOf(activityList.get(position).getStartDate()));
        holder.tvEndDate.setText(String.valueOf(activityList.get(position).getEndDate()));
        DecimalFormat REAL_FORMATTER = new DecimalFormat("0.##");
        holder.tvBudget.setText(String.valueOf(REAL_FORMATTER.format(activityList.get(position).getExpense())));

        return convertView;
    }

    private ShapeDrawable getTripIcon(View convertView) {
        ShapeDrawable sd = new ShapeDrawable(new OvalShape());
        sd.setIntrinsicHeight(100);
        sd.setIntrinsicWidth(100);
        sd.getPaint().setColor(convertView.getResources().getColor(R.color.activity_icon_stroke));
        return sd;
    }

    @Override
    public int getCount() {
        return activityList.size();
    }

    @Override
    public Object getItem(int position) {
        return activityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
