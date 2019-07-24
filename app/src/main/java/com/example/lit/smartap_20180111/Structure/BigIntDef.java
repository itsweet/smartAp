package com.example.lit.smartap_20180111.Structure;

import java.math.BigInteger;

public class BigIntDef {
    private int sign;
    private int len;
    private BigInteger[] value;

    public BigIntDef(){

    }

    public BigIntDef(int sign,int len ,BigInteger[] value){
        this.sign=sign;
        this.len=len;
        this.value=value;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public void setLen(long len){
        this.len = (int)len;
    }

    public void setValue(BigInteger[] value) {
        this.value = value;
    }

    public void setValue(BigInteger value,int position){
        this.value[position] = value;
    }

    public int getLen() {
        return len;
    }

    public int getSign() {
        return sign;
    }

    public BigInteger[] getValue() {
        return value;
    }
}
