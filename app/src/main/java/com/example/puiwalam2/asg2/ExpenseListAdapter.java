package com.example.puiwalam2.asg2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Eva Hung on 15/11/2016.
 */
public class ExpenseListAdapter extends BaseAdapter {
    ArrayList<Expense> data;
    LayoutInflater lI;

    static class ViewHolder{
        TextView name;
        TextView tag;
        TextView price;
    }

    public ExpenseListAdapter(Context context, ArrayList<Expense> data){
        this.data=data;
        lI=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            convertView=lI.inflate(R.layout.expense_row,null);
            holder=new ViewHolder();
            holder.name=(TextView)convertView.findViewById(R.id.tv_expense_name);
            holder.tag=(TextView)convertView.findViewById(R.id.tv_expense_tag);
            holder.price=(TextView)convertView.findViewById(R.id.tv_expense_price);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }
        holder.name.setText(data.get(position).getName());
        holder.tag.setText(data.get(position).getTag());
        holder.price.setText(String.valueOf(data.get(position).getExpense()));
        return convertView;
    }
}
