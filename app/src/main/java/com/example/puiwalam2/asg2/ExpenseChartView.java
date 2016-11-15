package com.example.puiwalam2.asg2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Winnie on 15/11/2016.
 */
public class ExpenseChartView extends View {
    RectF box = new RectF(10, 10,400 , 400);
    ArrayList<TagTotalExpense> slices;
    Paint p;

    public ExpenseChartView(Context ctx, ArrayList<TagTotalExpense> expenses) {
        this(ctx, null,expenses);
    }

    public ExpenseChartView(Context ctx, AttributeSet attrs, ArrayList<TagTotalExpense> expenses) {
        this(ctx, attrs, 0,expenses);
    }

    public ExpenseChartView(Context ctx, AttributeSet attrs, int defaultStyle,ArrayList<TagTotalExpense> expenses) {
        super(ctx, attrs, defaultStyle);
        slices=expenses;
        init();
    }

    public void init(){
        p=new Paint();
//        p.setColor(Color.DKGRAY);
//        p.setStyle(Paint.Style.STROKE);
//        p.setTextSize(36);
        p.setAntiAlias(true);

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(1f);
        p.setStyle(Paint.Style.FILL_AND_STROKE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int sum = 0;
        for (TagTotalExpense slice : slices) {
            sum += slice.getExpense();
        }
        int[] colors=new int[7];
        colors[0]= Color.BLUE;
        colors[1]=Color.GREEN;
        colors[2]=Color.CYAN;
        colors[3]=Color.MAGENTA;
        colors[4]=Color.RED;
        colors[5]=Color.YELLOW;
        colors[6]=Color.DKGRAY;
        float start=0;
        int rectStart=10;
        for(int i =0; i < slices.size(); i++){
            p.setColor(colors[i]);
            p.setTextSize(40);
            float angle;
            angle = ((360.0f / sum) * slices.get(i).getExpense());
            canvas.drawArc(box, start, angle, true, p);
            start += angle;
            //canvas.drawRect(570,rectStart,570+50,rectStart+50,p);
            canvas.drawRect(10,rectStart+400,10+50,rectStart+400+50,p);
            canvas.drawText(slices.get(i).getTag()+" = "+slices.get(i).getExpense(),10+50+20,rectStart+400+50,p);
            rectStart+=90;
            p.setColor(Color.BLACK);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int desiredWidth = 700;
        int desiredHeight = 1250;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }
        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }
}
