package com.example.lit.smartap_20180111.json;

public class Json_SignIn extends Json{
    public String ID;
    public String PWD;

    public Json_SignIn(String account,String pwd){
        this.ID=account;
        PWD=pwd;
    }

    void getJsonString(){
        StringBuilder stringBuilder=new StringBuilder();
    }
}
