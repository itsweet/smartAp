package com.example.lit.smartap_20180111;

import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConfigList {
    static String TAG = "ConfigList";
    //static InetAddress defServerAddress = getInetAddress("120.77.176.255"); //默认服务器地址，用于获取应用服务器地址
    static InetAddress defServerAddress = getInetAddress("120.77.176.1"); //默认服务器地址，用于获取应用服务器地址
    static final int defUDPPort = 5683; //默认服务器端口
    private static InetAddress serverAddress = defServerAddress; //应用服务器地址
    static final int defTCPPort = 5684;//应用服务器端口
    static final int defMqttPort = 1880;
    public static int TcpPort = defMqttPort;
    public static int UdpPort = defUDPPort;


    private static String username; //用户登录的名
    private static String userpwd;  //用户登录密码
    private static String vcode;    //短信验证码

    public static void setServerAddress(String serverAddress) {
        ConfigList.serverAddress = getInetAddress(serverAddress);
    }
    public static void setServerAddress(InetAddress serverAddress){
        ConfigList.serverAddress = serverAddress;
    }

    public static void setVcode(String vcode) {
        ConfigList.vcode = vcode;
    }

    public static String getVcode() {
        return vcode;
    }

    public static void setUsername(String username){
        ConfigList.username = username;
    }

    public static void setUserpwd(String userpwd){
        ConfigList.userpwd = userpwd;
    }

    public static String getUsername() {
        if (username != null)
            return username;
        else return "";
    }

    public static String getUserpwd() {
        if (userpwd != null) {
            return userpwd;
        }
        else return "";
    }

    private static InetAddress getInetAddress(String s){
        try {
            return InetAddress.getByName(s);
        }catch (UnknownHostException e){
            Log.e(TAG, "getInetAddress error: "+e,null );
            return null;
        }
    }

    public static InetAddress getServerAddress() {
        return serverAddress;
    }
}
