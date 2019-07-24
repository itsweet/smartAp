package com.example.lit.smartap_20180111.Mqtt;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MyMqttClient {
    private String TAG = "MyMqttClient";
    private Socket socket;

    public MyMqttClient(String addr,int port){
        try {
            socket = new Socket(addr, port);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void connectserver(){
        MyMqtt mqtt = new MyMqtt();
        mqtt.setClient_id("liutao");
        mqtt.setUser_name("liutao");
        mqtt.setPassword("123456");
    }
}
