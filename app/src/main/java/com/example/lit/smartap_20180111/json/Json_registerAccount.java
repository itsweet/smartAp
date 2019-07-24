package com.example.lit.smartap_20180111.json;

public class Json_registerAccount extends Json {
    String ACCOUNT;
    String  PWD;
    String VCODE;

    public Json_registerAccount(String accout,String pwd,String vcode){
        ACCOUNT = accout;
        PWD = pwd;
        VCODE = vcode;
    }
}
