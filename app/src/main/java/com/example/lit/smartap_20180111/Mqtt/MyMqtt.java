package com.example.lit.smartap_20180111.Mqtt;


import android.util.Log;

import java.util.LinkedList;

public class MyMqtt {
    private final String TAG = "MyMqtt";
    byte headerflags = 0x10;  //Connect Command
    int MSG_LENGTH;     //整个包长
    int LEN_STEP = 128;
    int MAX_LENGTH =(int) Math.pow(LEN_STEP,4);
    int PNL=0x0004;    //protocol name length 两个字节
    byte[] PN="MQTT".getBytes();//PN 默认为MQTT ;  //protocol name
    byte version = 0x04; //版本为v3.1.1
    final int LEN_HEADER = 2+4+1;  //headerflags+length+PNL+PN(MQTT)+version
    final int LEN_CONNECTFLAGS = 1;
    ConnectFlags connectflags;
    private int keepalive = 120;
    final int LEN_KEEPALIVE =2;
    final int LEN_BASE = LEN_HEADER +LEN_CONNECTFLAGS+LEN_KEEPALIVE;

    int len_client_id;
    String client_id;
    int len_User_name ;
    String user_name;
    int len_Password ;
    String password;

    byte ACK_FLAGS;
    byte RETURN_CODE;
    byte[] PAYLOAD;

    public MyMqtt(){
        this.connectflags = new ConnectFlags();
    }

    MyMqtt(ConnectFlags connectflags){

        this.connectflags = connectflags;
    }

    public void setKeepalive(int keepalive) {
        this.keepalive = keepalive;
    }

    public MyMqtt setClient_id(String client_id) {
        this.client_id = client_id;
        return this;
    }

    public MyMqtt setPassword(String password) {
        this.password = password;
        this.connectflags.setPassword();
        return this;
    }

    public MyMqtt setUser_name(String user_name) {
        this.user_name = user_name;
        this.connectflags.setUsername();
        return this;
    }

    public byte[] getPAYLOAD() {
        return PAYLOAD;
    }

    private byte[] encode_msg_length(int len){
        LinkedList<Integer> list = new LinkedList<Integer>();
        do{
            int digit = len % LEN_STEP;
            len = len/LEN_STEP;
            if (len>0){
                digit = digit | 0x80;
            }
            list.add(digit);
            Log.i(TAG, "calc_length digit: "+digit);
        }while ( len > 0);

        int listsize = list.size();
        byte[] bytes = new byte[listsize];
        for (int i=0;i < listsize;i++) {
            bytes[listsize-1-i] = list.pop().byteValue();
            Log.i(TAG, "calc_length return bytes: "+bytes);
        }
        return bytes;
    }

    public byte[] encode(){
        int len = LEN_BASE;
        len_client_id = client_id.length();
        len_User_name = user_name.length();
        len_Password = password.length();
        if (len_client_id>0){
            len += 2+len_client_id;  //len_client_id 占用2个字节
        }
        if (len_User_name>0){
            len += 2+len_User_name;
        }
        if (len_Password>0){
            len += 2+len_Password;
        }
        MSG_LENGTH = len;

        byte[] bytes_MSG_LENGTH= encode_msg_length(MSG_LENGTH);


        byte[] bytes= new byte[1+bytes_MSG_LENGTH.length+MSG_LENGTH]; //包头1个字节 然后MSG_LENGTH长度1-4个字节，然后是包长
        int num=0;
        bytes[num++] = headerflags;
        System.arraycopy(bytes_MSG_LENGTH,0,bytes,num,bytes_MSG_LENGTH.length);
        num+=bytes_MSG_LENGTH.length;
        bytes[num++] = (byte)(PNL >> 8 & 0xff);
        bytes[num++] = (byte)(PNL & 0xff);
        bytes[num++] = PN[0];
        bytes[num++] = PN[1];
        bytes[num++] = PN[2];
        bytes[num++] = PN[3];
        bytes[num++] = version;
        bytes[num++] = connectflags.encode();
        bytes[num++] = (byte) (keepalive >> 8 &0xff);
        bytes[num++] = (byte) (keepalive & 0xff);

        if (len_client_id>0){
            bytes[num++] = (byte)(len_client_id >> 8 & 0xff);
            bytes[num++] = (byte)(len_client_id & 0xff);
            System.arraycopy(client_id.getBytes(),0,bytes,num,len_client_id);
            num+=len_client_id;
        }
        if (len_User_name>0) {
            bytes[num++] = (byte)(len_User_name >> 8 & 0xff);
            bytes[num++] = (byte)(len_User_name & 0xff);
            System.arraycopy(user_name.getBytes(),0,bytes,num,len_User_name);
            num += len_User_name;
        }
        if(len_Password>0){
            bytes[num++] = (byte)(len_Password >> 8 & 0xff);
            bytes[num++] = (byte)(len_Password & 0xff);
            System.arraycopy(password.getBytes(),0,bytes,num,len_Password);
            num+=len_Password;
        }
        return bytes;
    }

    public void decode(byte[] bytes){
        int num = 0;
        headerflags = bytes[num++];
        int multiplier = 1;
        MSG_LENGTH = 0;
        byte digit;
        do {
            digit = bytes[num++];
            MSG_LENGTH += (digit & LEN_STEP-1) * multiplier;
            multiplier *= LEN_STEP;
        }while ((digit & LEN_STEP) != 0);
        ACK_FLAGS = bytes[num++];
        RETURN_CODE = bytes[num++];
        try {
            PAYLOAD = new byte[bytes.length - num];
            System.arraycopy(bytes, num, PAYLOAD, 0, bytes.length - num);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
