package com.example.lit.smartap_20180111;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.res.TypedArray;
import android.drm.DrmStore;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.example.lit.smartap_20180111.core.CoapClient;
import com.example.lit.smartap_20180111.core.server.resources.Resource;
import com.example.lit.smartap_20180111.data.IOT_CMD;

import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageButton;
import pl.droidsonroids.gif.GifImageView;

public class SearchActivity extends AppCompatActivity {
    boolean searchcheck = false;
    List<String> mdata;
    String TAG = "SearchActivity";
    AnimationDrawable animationDrawable;
    boolean isSearchcheck=false;
    final int GATEWAY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState ){
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        try {
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        }catch (NullPointerException e){
            Log.e(TAG, "onCreate: actionBar eroor: "+e,null );
        }
        //MyView myView = new MyView(this);
        //setContentView(myView);
        setContentView(R.layout.search_activity);
        init();
    }
    private void init(){
        /*
        try {
            SmartConfig smartConfig = new SmartConfig(this);
            smartConfig.send("hello");
        }catch (IOException e){
            e.printStackTrace();
        }
         */
        final GifImageButton searching = findViewById(R.id.searchimage);
        searching.setImageResource(R.drawable.saomiao);
        /*
        searching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSearchcheck){
                    isSearchcheck = true;
                    searching.setImageResource(R.drawable.saomiao);
                }else {
                    isSearchcheck = false;
                    searching.setImageResource(android.R.drawable.ic_media_play);
                }
            }
        });
        */

        /*
        ImageView imageView = findViewById(R.id.searchimage);
        animationDrawable=(AnimationDrawable) imageView.getBackground();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSearchcheck) {
                    isSearchcheck=true;
                    animationDrawable.start();
                }else {
                    isSearchcheck = false;
                    animationDrawable.stop();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchcheck){
                    searchcheck = false;
                    imageView.setImageResource(android.R.drawable.ic_media_play);
                }else {
                    searchcheck = true;
                    imageView.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });
        */
        //initdata();
        //ListView listView = findViewById(R.id.search_listview);

        //listView.setAdapter(new SimpleAdapter(this,));
        new  ScanGW_Thread().start();
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == GATEWAY) {
                Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
            }
        }
    };

    private class ScanGW_Thread extends Thread{
        @Override
        public void run() {
            SendTool sendTool = new SendTool(getApplicationContext());
            Message msg=new Message ();
            msg.what = GATEWAY;
            msg.obj = sendTool.scanGateWay();
            handler.sendMessage(msg);
        }
    }


    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initdata(){
        mdata = new ArrayList<String>();
        for (int i = 'A';i<'Z';i++){
            mdata.add(""+(char)i);
        }
    }

    class MyView extends View implements View.OnClickListener{
        Paint paint;
        Movie movie;
        MyView(Context context){
            super(context);
            paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(3);
            int resourceId = getResources().getIdentifier("saomiao","drawable",
                    "com.example.lit.smartap_20180111");
            InputStream is = getResources().openRawResource(resourceId);
            movie = Movie.decodeStream(is);

        }

        @Override
        public void onClick(View view){

        }

        @Override
        protected void onDraw(Canvas canvas){
            /*
            Path path = new Path();
            path.addCircle(getWidth()/2,getHeight()/2,90, Path.Direction.CW);
            canvas.drawPath(path,paint);
            canvas.drawLine(10,10,100,100,paint);
            */
            movie.setTime(0);
            movie.draw(canvas, 0, 0);
            //canvas.drawCircle(100, 100, 90, paint);
        }
        private int getResourceId(TypedArray a, Context context, AttributeSet attrs) {
            try {
                Field field = TypedArray.class.getDeclaredField("mValue");
                field.setAccessible(true);
                TypedValue typedValueObject = (TypedValue) field.get(a);
                return typedValueObject.resourceId;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (a != null) {
                    a.recycle();
                }
            }
            return 0;
        }
    }
}
