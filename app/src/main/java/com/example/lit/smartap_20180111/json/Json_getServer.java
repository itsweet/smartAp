package com.example.lit.smartap_20180111.json;

public class Json_getServer extends Json{
    public String IP;
    public String TIME;
    public String TCPPORT;
    public String UDPPORT;

    public Json_getServer(){}

    public Json_getServer(String addr,String tcpport,String time,String udpport){
        IP = addr;
        TIME = time;
        TCPPORT = tcpport;
        UDPPORT = udpport;
    }
}
