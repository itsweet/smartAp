package com.example.lit.smartap_20180111.json;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Since;

import java.io.UnsupportedEncodingException;

/**
 * Created by ws on 2018/2/3.
 */

public class Json {
    private static final String TAG = "GsonableObject";

    public static final double VERSION = 1.0;
    public static final double VERSION_FOR_HIDING = 1.1;

    /**
     * 原始的数据（JSON字符串）
     */
    @Since(VERSION_FOR_HIDING)
    public byte[] rawData;
    public Json(){

    }
    public Json(byte[] bytes){
        this.rawData=bytes;
    }

    public String toJson(){
        Gson gson =new GsonBuilder().setVersion(VERSION).create();
        return gson.toJson(this);
    }
    public Json fromJson(byte[] jsonData) {
        if (null == jsonData) return null;

        rawData = jsonData;
        try {
            return fromJson(new String(rawData, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "parse Gsonable encode:" + Log.getStackTraceString(e));
            return null;
        }
    }
    public Json fromJson(String json) {
        if (null == json) return null;
        json = json.trim();

        Gson gson = new GsonBuilder().setVersion(VERSION).create();
        try {
            Json temp = gson.fromJson(json, getClass());
            if (null != temp) {
                temp.rawData = rawData;
            }

            return temp;
        } catch (Exception e) {
            Log.e(TAG, "parse Gsonable err:" + Log.getStackTraceString(e));
            return null;
        }
    }
    public String toJsonString() {
        if (null == rawData) return null;
        try {
            return new String(rawData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "parse Gsonable encode:" + Log.getStackTraceString(e));
            return null;
        }
    }
    public String toString() {
        final StringBuilder sb = new StringBuilder("Gsonable{");
        sb.append("rawData=").append(toJsonString());
        sb.append('}');
        return sb.toString();
    }
}
