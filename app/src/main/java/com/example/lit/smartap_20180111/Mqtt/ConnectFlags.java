package com.example.lit.smartap_20180111.Mqtt;

public class ConnectFlags {
    /*
        7               6          5            4     3         2               1           0
    user-name       password    will-retain     will-QoS     will-flag  clean-session   reserved
     */
    byte username ;
    byte password ;
    byte willretain ;
    byte willQos = 0x00 ;
    byte willflag ;
    byte cleansession ;
    byte reserved ;
    byte connectflags;

    public ConnectFlags(){}

    public ConnectFlags(byte connectflags){
        decode(connectflags);
    }

    public void setUsername() {
        this.connectflags |= (byte)0x80;
    }

    public void setPassword() {
        this.connectflags |= (byte)0x40;
    }

    public void setWillretain() {
        this.connectflags |= (byte)0x20;
    }

    public void setWillQos(byte qos){
        this.connectflags |= (qos<<3);
    }

    public void setWillflag() {
        this.connectflags |= (byte) 0x04;
    }

    public void setCleansession() {
        this.connectflags |= (byte) 0x02;
    }

    public byte encode(){
        return connectflags;
    }

    public void decode(byte connectflags){
        int temp =  connectflags>>7;
        username = (byte)(temp & 0x01);

        temp = connectflags >> 6;
        password = (byte)(temp & 0x01);

        willretain = (byte)(connectflags >> 5 & 0x01);

        willQos =(byte)(connectflags >> 3 & 0x03);

        willflag =(byte)(connectflags >> 2 & 0x01);

        cleansession = (byte)(connectflags >> 1 & 0x01);
    }
}
