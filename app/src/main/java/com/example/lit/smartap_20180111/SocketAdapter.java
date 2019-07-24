package com.example.lit.smartap_20180111;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.lit.smartap_20180111.data.DataObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class SocketAdapter {
    static final String TAG="SocketAdapter";
    Context context;
    WifiManager wifiManager;
    WifiInfo wifiInfo;
    DatagramSocket datagramSocket;
    static Socket socket;
    ArrayList<Object> datalist;
    SocketType connecttype; // 1 是 UDP，2 是 TCP

    public SocketAdapter(Context context,SocketType connecttype){
        this.context=context;
        this.connecttype = connecttype;
        this.datalist = new ArrayList<>();
        wifiManager=(WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            wifiInfo = wifiManager.getConnectionInfo();
        }catch (Exception e){
            Log.e(TAG, "DataAdapter: getConnectionInfo error:"+e );
        }
    }

    enum SocketType{
        UDP,
        TCP,
        MQTT
    }

    public void change_sockettype(SocketType type){
        switch (type){
            case UDP:
                try {
                    socket.close();
                    datagramSocket = new DatagramSocket();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case TCP:
                try{
                    datagramSocket.close();
                    socket = new Socket(ConfigList.getServerAddress(),ConfigList.TcpPort);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            default:

        }
        this.connecttype = type;
    }

    public String getMacFromWifiInfo(Context context) {
        try {
            wifiInfo = wifiManager.getConnectionInfo();
            Log.i(TAG, "getMacFromWifiInfo: macaddress" + wifiInfo.getMacAddress());
            String s = wifiInfo.getMacAddress();
            String[] split = s.split(":");
            if (split.length == 6) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String i : split) {
                    stringBuilder.append(i);
                }
                return stringBuilder.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "getMacFromWifiInfo: " + e);
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

    void sendServer(final DataObject dataObject, InetAddress serverAddress, int socketPort){
        switch (connecttype){
            case UDP:
                byte[] bytes = dataObject.getData();
                DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, serverAddress, socketPort);
                try {
                    datagramSocket.send(datagramPacket);
                }catch (Exception e){
                    e.printStackTrace();
                }
        }
    }
}

