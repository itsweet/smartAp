package com.example.lit.smartap_20180111;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.lit.smartap_20180111.data.BaseResultCallback;
import com.example.lit.smartap_20180111.data.ConnectStatus;
import com.example.lit.smartap_20180111.data.DataMQTT;
import com.example.lit.smartap_20180111.data.DataObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.lit.smartap_20180111.core.coap.CoAP.ResponseCode;
import com.example.lit.smartap_20180111.data.IOT_CMD;
import com.example.lit.smartap_20180111.data.MqttSenderListener;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;


/**
 * wifi 收发管理
 * Created by ws on 2018/3/14.
 */

public class DataAdapter {
    private static final String TAG="DataAdapter";
    Context context;
    WifiManager wifiManager;
    WifiInfo wifiInfo;
    static DatagramSocket datagramSocket;
    Socket socket;
    SocketType mSocketType;
    DataMQTT.DataType mDataType;
    //对上层的接口，收到回包就丢到这里
    MqttSenderListener DAforSTListener;
    MqttClient mqttClient;
    MqttSender mqttSender;

    enum SocketType{
        UDP,
        TCP,
        MQTT
    }
    DataAdapter(){}
    
    DataAdapter(@NonNull Context context, SocketType type, DataMQTT.DataType dataType) {
        this.context = context;
        this.mSocketType = type;
        this.mDataType = dataType;
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        try {
            wifiInfo = wifiManager.getConnectionInfo();
        } catch (Exception e) {
            Log.e(TAG, "DataAdapter: getConnectionInfo error:" + e);
        }
        switch (type) {
            case UDP:
                try {
                    datagramSocket = new DatagramSocket();
                } catch (Exception e) {
                    Log.e(TAG, "DataAdapter: create socket:" + e);
                }
                break;
            case TCP:
                socketconnect();
                break;
            case MQTT:
                mqttSender = new MqttSender();
                mqttSender.setCallback(new MqttSenderListener() {
                    @Override
                    public void statusChange(ConnectStatus status) {
                        if(DAforSTListener != null) {
                            DAforSTListener.statusChange(status);
                        }
                    }

                    @Override
                    public void onSuccess(Object data) {
                        DataMQTT dataMQTT = (DataMQTT) data;
                        if (DAforSTListener !=null){
                            DAforSTListener.onSuccess(dataMQTT);
                            //mqttSender.unsetCallback();
                        }
                    }

                    @Override
                    public void onFail(int status, String data) {
                        if (DAforSTListener == null){
                            return;
                        }
                        DAforSTListener.onFail(status,data);
                        //mqttSender.unsetCallback();
                    }
                });
        }
    }

    public void onDestroy(){
        switch (mSocketType){
            case MQTT:
                mqttSender.mqttSenderTimer_stop();
        }
    }

    public void setmSocketType(SocketType mSocketType) {
        this.mSocketType = mSocketType;
    }

    public void mqtt_reconnect(){
        connect_change(ConfigList.getServerAddress().toString(),ConfigList.defMqttPort);
    }

    void connect_change(String serveraddress,int port){
        switch (mSocketType){
            case MQTT:
                if (mqttSender == null){
                    return;
                }
                mqttSender.connect_change(serveraddress,port,ConfigList.getUsername(),
                        null,null);
                break;
        }
    }

    void connect_def(){
        switch (mSocketType){
            case MQTT:
                mqttSender.connect_change(ConfigList.defServerAddress.toString(),
                        ConfigList.defMqttPort,ConfigList.getUsername(),null,null);
                break;
        }
    }

    void connect_change(){
        switch (mSocketType){
            case MQTT:
                mqttSender.connect_change(ConfigList.getServerAddress().toString(),
                        ConfigList.TcpPort,ConfigList.getUsername(),null,null);
                break;
            case TCP:

        }
    }

    void  mqtt_send(final String topic, final MqttMessage message){
        mqttSender.send(topic, message);
    }

    void mqtt_send(DataMQTT dataMQTT){
        mqttSender.setNowcmd(dataMQTT.getCmd());//标记当前命令，用于判断连接默认服务器还是应用服务器
        mqtt_send(dataMQTT.getTopic(), dataMQTT.getMqttMessage());
    }

    private void socketconnect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ConfigList.getServerAddress(),ConfigList.defTCPPort);
                    Log.i(TAG, "DataAdapter: socket connected");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public SocketType getmSocketType() {
        return mSocketType;
    }

    public String getMacFromWifiInfo(Context context){
        try {
            wifiInfo=wifiManager.getConnectionInfo();
            Log.i(TAG, "getMacFromWifiInfo: macaddress"+wifiInfo.getMacAddress());
            String s=wifiInfo.getMacAddress();
            String[] split=s.split(":");
            if (split.length==6){
                StringBuilder stringBuilder=new StringBuilder();
                for (String i:split){
                    stringBuilder.append(i);
                }
                return stringBuilder.toString();
            }else {
                return null;
            }
        }catch (Exception e)
        {
            Log.e(TAG, "getMacFromWifiInfo: "+e );
            return null;
        }
    }

    public String getMacFromWifiInfo(){
        return getMacFromWifiInfo(this.context);
    }

    int getIPAdress(){
        wifiInfo=wifiManager.getConnectionInfo();
        return wifiInfo.getIpAddress();
    }

    byte[] getbBroadcastAdress(){
        int ip=getIPAdress();
        /*
        if(ByteOrder.nativeOrder()==ByteOrder.BIG_ENDIAN){
            Log.i(TAG, "getbBroadcastAdress: byte order Big_ENDIAN");
        }else {
            Log.i(TAG, "getbBroadcastAdress: byte order Little_ENDIAN");
        }
        */
        return intAdressToByteBroadcast(ip);
    }
    public static byte[] intAdressToByteBroadcast(int a) {
        return new byte[] {
                (byte) (a & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) (0xFF)
        };
    }

    public static byte[] intAdressToByteArray(int a) {
        return new byte[] {
                (byte) (a & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 24) & 0xFF)
        };
    }

    void setBroadcastAdress(){
        try {
            ConfigList.setServerAddress(InetAddress.getByAddress(getbBroadcastAdress()));
        }catch (Exception e){
            Log.e(TAG, "setBroadcastAdress: error:"+e,null );
        }
    }

    public boolean isConnect(){
        int rssi = wifiInfo.getRssi();
        return rssi > -200;
    }

    void write(final DataObject dataObject){
        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (mSocketType){
                    case UDP:
                        sendServer(dataObject,ConfigList.getServerAddress(),ConfigList.UdpPort);
                        break;
                    case TCP:
                        sendServer(dataObject,ConfigList.getServerAddress(),ConfigList.TcpPort);
                }
            }
        }).start();
    }
    synchronized void sendServer(final DataObject dataObject, InetAddress serverAddress, int socketPort){
        final BaseResultCallback<Object> callback = dataObject.getCallback();
        if (callback== null){
            Log.e(TAG, "sendServer: callback == null", null);
        }else {
            Log.i(TAG, "sendServer: callback = "+callback.toString());
        }
        final int mid = dataObject.getMID();

        try {

            byte[] bytes = dataObject.getData();
            //超时逻辑部分
            while (!dataObject.getACK()) {
                //收到回复，退出循环
                Long newTime = System.currentTimeMillis();
                Long startTime = dataObject.getStartTime();
                int outTime = dataObject.getTimeout();
                //第一次发包starttime 为null；小于超时就continue;是respone直接发;
                if (startTime != null && newTime - startTime < outTime && dataObject.isRequest()) {
                    continue;
                }
                DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, serverAddress, socketPort);
                switch (mSocketType){
                    case UDP:
                        if (datagramSocket != null){
                            datagramSocket.send(datagramPacket);
                            Log.i(TAG, Utils.getTime() + " UDP sendServer:" + dataObject.toString());
                        }
                        break;
                    case TCP:
                        if (socket == null ){
                            socketconnect();
                        }
                        /*
                        if (!socket.getInetAddress().equals(ConfigList.getServerAddress()) ||
                                socket.getPort()!=ConfigList.TcpPort) {
                            socket = new Socket(ConfigList.getServerAddress(), ConfigList.TcpPort);
                        }
                        */
                        OutputStream outputStream = socket.getOutputStream();
                        outputStream.write(bytes);
                        Log.i(TAG, Utils.getTime() + " TCP sendServer:" + dataObject.toString());
                        outputStream.flush();
                        break;
                    default:return;
                }
                //Log.i(TAG, Utils.getTime() + " sendServer:" + dataObject.toString());
                dataObject.edu_time();
                dataObject.setStartTime(newTime);
                //是回复包，发送完成后，退出循环
                if (dataObject.isResponse()) {
                    return;
                }

                //发送超过包内设置次数，退出循环
                if (dataObject.getTime() < 1) {
                    //callback.onFail(2, "out off time",null);
                    unsetOnDataAvailableListener();
                    return;
                }
            }
        }catch (Exception e){
            //callback.onFail(1,String.format("%s",e), dataObject);
            e.printStackTrace();
        }

    }
    /*
    public interface DAforSTListener {
        void connectStatus(int status);

        void onResponse(Object dataObject);

        void onError(int status, String msg, Object dataObject);
    }
    */

    void setDAforSTListener(MqttSenderListener listener){
        DAforSTListener = listener;
    }
    void unsetOnDataAvailableListener(){
        DAforSTListener =null;
    }

    private byte[] udprec(){
        try {
            byte[] bytes = new byte[1024];
            DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
            if (datagramSocket != null) {
                datagramSocket.receive(datagramPacket);
                Log.i(TAG, "receiveServer: " + datagramPacket.getLength());
            } else {
                Log.e(TAG, "receive: socket==null", null);
            }
            /*
            if (datagramPacket.getPort() != ConfigList.UdpPort &&
                    datagramPacket.getPort() != ConfigList.TcpPort) {
                return null;
            }
            */
            int length = datagramPacket.getLength();
            byte[] nbyte = new byte[length];
            System.arraycopy(datagramPacket.getData(), 0, nbyte, 0, length);
            return nbyte;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private byte[] tcprec(){
        if(socket == null) {
            //Log.e(TAG, "tcprec: socket == null",null );
            return null;
        }
        try {
            //Log.i(TAG, "tcprec: new inputStream");
            InputStream inputStream = socket.getInputStream();;
            int n = inputStream.available();
            if (n>0){
                byte[] bytes = new byte[n];
                inputStream.read(bytes);
                return bytes;
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    void receiveServer(){
        byte[] nbyte=null;
        DataObject dataObject;
        switch (mSocketType){
            case UDP:
                nbyte = udprec();
                break;
            case TCP:
                nbyte = tcprec();
                //Log.i(TAG, "receiveServer: TCP rec :"+nbyte);
        }
        //Log.i(TAG, String.format("receive: data %s address %s port %s {%s}",datagramPacket.getLength(),
         //       datagramPacket.getAddress(),datagramPacket.getPort(),new String(datagramPacket.getData())));
        if (nbyte == null) return;
        dataObject =new DataObject(nbyte,null);
        /*
        int uri_port = dataObject.getURI_PORT();
        uri_port &= 0x7fff;
        dataObject.setURI_PORT(uri_port);
        */
        Log.i(TAG,  Utils.getTime()+" receive: "+ dataObject.toString());
        if (dataObject.getToken()[3] != "A".getBytes()[0]){
            Log.e(TAG, "receiveServer: Token != A",null );
            return;
        }

        if (DAforSTListener !=null)
        {
            if (dataObject.isResponse()){
                DAforSTListener.onSuccess(dataObject);
            }else if (dataObject.isRequest()){
               // DAforSTListener.onDataChange(dataObject);
            }else {
                DAforSTListener.onFail(0,"useless");
            }
        }
        /*
        if (!DAforSTListenerList.isEmpty()){
            for (DAforSTListener listener: DAforSTListenerList){
                if (dataObject.isResponse()){
                    listener.onResponse(dataObject);
                }else if (dataObject.isRequest()){
                    //listener.onDataChange(dataObject);
                }else {
                    listener.onError(0,"useless", dataObject);
                }
            }
        }
        */
    }

}
