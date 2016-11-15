package com.example.puiwalam2.asg2;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Winnie on 15/11/2016.
 */
public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
    private WeakReference<ImageView> imageViewReference;
    private int data=0;
    Resources res;

    public BitmapWorkerTask(ImageView iv,Resources r){
        imageViewReference=new WeakReference<ImageView>(iv);
        res=r;
    }
    protected Bitmap doInBackground(String... s) {
        Bitmap bitmap=null;
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=false;
        try{
            URL url=new URL(s[0]);
            HttpURLConnection urlc=(HttpURLConnection)url.openConnection();
            bitmap=BitmapFactory.decodeStream(urlc.getInputStream(),
                    null,options);
        }catch (IOException ioe){ /* handle exception here*/ }
        return bitmap;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        ImageView iv = imageViewReference.get();
        if (iv != null) {
            iv.setImageBitmap(bitmap);
        }
    }
}
