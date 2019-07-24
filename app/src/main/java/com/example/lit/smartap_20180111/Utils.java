package com.example.lit.smartap_20180111;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ws on 2018/3/12.
 */

public class Utils {
    public static String bytes2Hex(byte[] src) {
        if (src == null || src.length <= 0) {
            return null;
        }

        char[] res = new char[src.length * 2]; // 每个byte对应两个字符
        final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        for (int i = 0, j = 0; i < src.length; i++) {
            res[j++] = hexDigits[src[i] >> 4 & 0x0f]; // 先存byte的高4位
            res[j++] = hexDigits[src[i] & 0x0f]; // 再存byte的低4位
        }

        return new String(res);
    }

    public static String hexStr2DecStr(String hexString){
        BigInteger bigInteger=BigInteger.ZERO;
        String hexDigits="0123456789abcdef";
        hexString=hexString.trim().toLowerCase();
        char[] chars=hexString.toCharArray();
        for(int i = 0; i<chars.length;i++){
            int num = hexDigits.indexOf(chars[i]);
            BigInteger bigbase = new BigInteger(String.valueOf(16));//计算16进制，所以底数是16
            int exponent = chars.length - (i + 1);//指数是字符串长度 减 i+1
            BigInteger bigpower = bigbase.pow(exponent);//幂计算
            BigInteger bigvalue=new BigInteger(String.valueOf(num)).multiply(bigpower);//16进制计算，计算i位置的数值
            bigInteger = bigInteger.add(bigvalue); //每个位置数值相加算出最终10进制数值
        }
        return bigInteger.toString();
    }


    public static String hexString2bytestring(String hexString){
        StringBuilder result=new StringBuilder();
        String string="0123456789abcdef";
        hexString=hexString.toLowerCase();
        hexString=hexString.trim();
        char[] charArray=hexString.toCharArray();
        int length=hexString.length()/2;
        byte[] bytes=new  byte[length];
        for (int i = 0 ;i < length;i++){
            int pos = i*2;
            byte g = (byte) string.indexOf(charArray[pos]);
            byte d = (byte) string.indexOf(charArray[pos+1]);
            result.append((char)(g<<4 | d));
        }
        //result=String.format("%s",bytes);
        return result.toString();
    }

    public static String convertByteToHexString(byte[] bytes){
        String result="";
        for (int i = 0; i < bytes.length; i++) {
            int temp=bytes[i]&0xff;
            String tempHex=Integer.toHexString(temp);
            if(tempHex.length()<2){
                result+="0"+tempHex;
            }else{
                result+=tempHex;
            }
        }
        return result;
    }

    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        if (bytes == null) {
            sb.append("null");
        } else {
            for (byte b : bytes) {
                sb.append(String.format("%02x", b & 0xFF));
            }
        }
        return sb.toString();
    }

    public static String toHexText(byte[] bytes, int length) {
        if (bytes == null) return "null";
        if (length > bytes.length) length = bytes.length;
        StringBuilder sb = new StringBuilder();
        if (16 < length) sb.append(System.lineSeparator());
        for(int index = 0; index < length; ++index) {
            sb.append(String.format("%02x", bytes[index] & 0xFF));
            if (31 == (31 & index)) {
                sb.append(System.lineSeparator());
            } else {
                sb.append(' ');
            }
        }
        if (length < bytes.length) {
            sb.append(" .. ").append(bytes.length).append(" bytes");
        }
        return sb.toString();
    }

    public static String getTime(){
        Date date=new Date();
        DateFormat dateFormat=new SimpleDateFormat("MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

}
