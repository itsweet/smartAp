package com.example.lit.smartap_20180111;

import android.util.Log;

import java.math.BigDecimal;

/**
 * Created by admin on 2017/9/16.
 */

public class ArithUtil {
    private static final int DEF_DIV_SCALE=10;
    private static final String TAG="ArithUtil";

    //相加
    public static float add(float d1,float d2){
        BigDecimal b1=new BigDecimal(Float.toString(d1));
        BigDecimal b2=new BigDecimal(Float.toString(d2));
        float temp=b1.add(b2).floatValue();
        Log.i(TAG, "add: "+temp);
        return temp;

    }
    //相减
    public static float sub(float d1,float d2){
        BigDecimal b1=new BigDecimal(Float.toString(d1));
        BigDecimal b2=new BigDecimal(Float.toString(d2));
        float temp= b1.subtract(b2).floatValue();
        Log.i(TAG, "sub: "+temp);
        return temp;
    }
    //相乘
    public static float mul(float d1,float d2){
        BigDecimal b1=new BigDecimal(Float.toString(d1));
        BigDecimal b2=new BigDecimal(Float.toString(d2));
        float temp= b1.multiply(b2).floatValue();
        Log.i(TAG, "mul: "+temp);
        return temp;
    }
    //相除
    public static float div(float d1,float d2){

        float temp= div(d1,d2,DEF_DIV_SCALE);
        Log.i(TAG, "div: "+temp);
        return  temp;
    }

    public static float div(float d1,float d2,int scale){
        if(scale<0){
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1=new BigDecimal(Float.toString(d1));
        BigDecimal b2=new BigDecimal(Float.toString(d2));

        float temp= b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).floatValue();
        Log.i(TAG, "div: "+temp);
        return temp;
    }
}
