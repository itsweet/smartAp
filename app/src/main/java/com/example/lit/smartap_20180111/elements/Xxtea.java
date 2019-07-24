package com.example.lit.smartap_20180111.elements;


import com.example.lit.smartap_20180111.Utils;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Xxtea {
    private static long DELTA=0x9E3779B9L;
    private static int MIN_LENGTH = 32;
    private static char SPECIAL_CHAR = '\0';
    // C 的long型是4个字节，所以所有运算都会使用MASK进行截取
    private static long MASK=0xffffffffL;
    private long[] KEY;

    public Xxtea(){
        randomKey();
    }

    private void randomKey(){
        long[] key = new long[4];
        Random random = new Random();
        for(int i =0;i < 4;i++){
            key[i]=Math.abs(random.nextLong()) & MASK | 0x10000000L; //|0x10000000L 保证随机数在4字节
        }
        this.KEY=key;
    }

    public long[] getKEY(){
        if (this.KEY !=null)
            return this.KEY;
        else
            randomKey();
            return this.KEY;
    }

    private String getKey(){
        if (this.KEY !=null)
            return Utils.convertByteToHexString(ToByteArray(this.KEY));
        else
            return "";
    }


    public byte[] Encrypt(byte[] data,long[] key) throws NullPointerException{
        long[] teadata = ToLongArray(data);
        long[] teaEncrypt = TEAEncrypt(teadata,key);
        return ToByteArray(teaEncrypt);
    }


    /*
    public static byte[] Encrypt(byte[] data,long[] key) throws NullPointerException{
        long[] teadata = ToLongArray(data);
        long[] teaEncrypt = TEAEncrypt(teadata,key);
        byte[] bytes = ToByteArray(teaEncrypt);
        return bytes;
    }
    */

    public byte[] Encrypt(byte[] data) throws NullPointerException{
        return Encrypt(data,this.KEY);
    }

    /*
    public static String Decrypt(String data, String key) {
        if (data == null || data.length() < MIN_LENGTH) {
            return data;
        }
        byte[] code = ToByteArray(TEADecrypt(
                ToLongArray(data),
                ToLongArray(PadRight(key, MIN_LENGTH).getBytes(
                        Charset.forName("UTF8")))));
        return new String(code, Charset.forName("UTF8"));
    }
    */


    public  byte[] Decrypt(long[] data,long[] key){
        long[] teaDecrypt = TEADecrypt(data,key);
        byte[] bytes = ToByteArray(teaDecrypt);
        return bytes;
    }

    public  byte[] Decrypt(byte[] data,long[] key){
        long[] longs = ToLongArray(data);
        return Decrypt(longs,key);
    }

    public byte[] Decrypt(byte[] data){
        return Decrypt(data,this.KEY);
    }

    private static long masknum(long num){
        return num & MASK;
    }

    public static long[] TEAEncrypt(long[] data, long[] key) {
        int n = data.length;
        if (n < 1) {
            return data;
        }

        long z = data[n - 1], y = data[0], sum = 0, e ,p, q;
        q = 6 + 52 / n;
        while (q-- > 0) {
            sum =(sum+DELTA) & MASK;
            e = masknum(sum >> 2) & 3 & MASK;
            for (p = 0; p < n - 1; p++) {
                y = data[(int) (p + 1)];
                long temp1 = masknum(z >> 5 ^ y << 2) + masknum(y >> 3 ^ z << 4);
                long temp2 = masknum(sum ^ y) + masknum(key[(int) (p & 3 ^ e)] ^ z);
                long temp3 = masknum(temp1 ^ temp2);
                long temp4 = masknum(data[(int)p] + temp3);
                long temp = data[(int) p] += (z >> 5 ^ y << 2) +  (y >> 3 ^ z << 4)
                        ^  (sum ^ y) +  (key[(int) (p & 3 ^ e)] ^ z);
                z = data[(int) p] = temp4 ;
            }
            y = data[0];
            long temp1 = masknum(z >> 5 ^ y << 2) + masknum(y >> 3 ^ z << 4);
            long temp2 = masknum(sum ^ y) + masknum(key[(int) (p & 3 ^ e)] ^ z);
            long temp3 = masknum(temp1 ^ temp2);
            long temp4 = masknum(data[n - 1] + temp3);
            long temp = (z >> 5 ^ y << 2) +  (y >> 3 ^ z << 4)
                    ^  (sum ^ y) +  (key[(int) (p & 3 ^ e)] ^ z);
            z = data[n - 1] =  temp4;
        }

        return data;
    }

    public static long[] TEADecrypt(long[] data, long[] key) {
        int n = data.length;
        if (n < 1) {
            return data;
        }

        long z = data[n - 1], y = data[0], sum = 0, e, p, q;
        q = 6 + 52 / n;
        sum = masknum(q * DELTA);
        while (sum != 0) {
            e = masknum(sum >> 2) & 3 & MASK;
            for (p = n - 1; p > 0; p--) {
                z = data[(int) (p - 1)] & MASK;
                long temp1 = masknum(z >> 5 ^ y << 2) + masknum(y >> 3 ^ z << 4);
                long temp2 = masknum(sum ^ y) + masknum(key[(int) (p & 3 ^ e)] ^ z);
                long temp3 = masknum(temp1 ^ temp2);
                long temp4 = data[(int)p] - temp3;
                //y = data[(int) p] -= (z >> 5 ^ y << 2) + (y >> 3 ^ z << 4)
                 //       ^ (sum ^ y) + (key[(int) (p & 3 ^ e)] ^ z);
                y = data[(int)p] = temp4 & MASK;
            }
            z = data[n - 1];
            //y = data[0] -= (z >> 5 ^ y << 2) + (y >> 3 ^ z << 4) ^ (sum ^ y)
             //       + (key[(int) (p & 3 ^ e)] ^ z);
            long temp1 = masknum(z >> 5 ^ y << 2) + masknum(y >> 3 ^ z << 4);
            long temp2 = masknum(sum ^ y) + masknum(key[(int) (p & 3 ^ e)] ^ z);
            long temp3 = masknum(temp1 ^ temp2);
            long temp4 = data[0] - temp3;
            y = data[0] = temp4 & MASK;
            sum = masknum(sum-DELTA);
        }

        return data;
    }


    private long[] ToLongArray(byte[] data) {
        int n = (data.length % 4 == 0 ? 0 : 1) + data.length / 4;
        long[] result = new long[n];

        for (int i = 0; i < n - 1; i++) {
            result[i] = bytes2long(data, i * 4);
        }

        byte[] buffer = new byte[4];
        for (int i = 0, j = (n - 1) * 4; j < data.length; i++, j++) {
            buffer[i] = data[j];
        }
        result[n - 1] = bytes2long(buffer, 0);

        return result;
    }


    /**
     * @param data 16进制显示的字符串
     * @return long型数组 可用于xxtea加密
     */
    private static long[] ToLongArray(String data) {
        int len = data.length() / 8;
        long[] result = new long[len];
        for (int i = 0; i < len; i++) {
            String earchdata = data.substring(i * 8, i * 8 + 8);
            String earchdataDec = Utils.hexStr2DecStr(earchdata);
            BigInteger bigInteger = new BigInteger(earchdataDec);
            result[i] = bigInteger.longValue();
        }
        return result;
    }

    public static byte[] ToByteArray(long[] data){
        return ToByteArray(data,ByteOrder.LITTLE_ENDIAN);
    }

    public static byte[] ToByteArray(long[] data,ByteOrder byteOrder) {
        List<Byte> result = new ArrayList<Byte>();

        for (int i = 0; i < data.length; i++) {
            byte[] bs = long2bytes(data[i],byteOrder);
            for (int j = 0; j < 4; j++) {
                result.add(bs[j]);
            }
        }

        while (result.get(result.size() - 1) == SPECIAL_CHAR) {
            result.remove(result.size() - 1);
        }

        byte[] ret = new byte[result.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = result.get(i);
        }
        return ret;
    }


    /*
    public static byte[] long2bytes(long num){
        byte[] bytes = new byte[4];
        Long.toString(num);

        return ()
    }
    */

    public static byte[] long2bytes(long num, ByteOrder endian) {
        ByteBuffer buffer = ByteBuffer.allocate(8).order(
                endian);
        buffer.putLong(num);
        byte[] bytes=new byte[4];
        if (endian == ByteOrder.LITTLE_ENDIAN) {
            System.arraycopy(buffer.array(), 0, bytes, 0, 4);
        }else if (endian == ByteOrder.BIG_ENDIAN){
            System.arraycopy(buffer.array(),4,bytes,0,4);
        }
        return bytes;
    }


    public static int bytes2int(byte[] b ,int index){
        ByteBuffer byteBuffer =ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(b,index,4);
        return byteBuffer.getInt(0);
    }

    /*
    public static long bytes2long(byte[] b, int index){
        byte[] bytes = new byte[4];
        System.arraycopy(b,index,bytes,0,4);
        String bytes2Hex = Utils.bytes2Hex(bytes);
        return Long.valueOf(Utils.hexStr2DecStr(bytes2Hex));
    }
    */

    public static long bytes2long(byte[] b, int index) {
        ByteBuffer buffer = ByteBuffer.allocate(8).order(
                ByteOrder.LITTLE_ENDIAN);
        buffer.put(b, index, 4);
        return buffer.getLong(0);
    }



    public static String ToHexString(long[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            sb.append(PadLeft(Long.toHexString(data[i]), 8));
        }
        return sb.toString();
    }


    private static String padRight(String source){
        int len = source.length();
        for (int i = 0; i<(4-len%4)&&i<4;i++){
            source += SPECIAL_CHAR;
        }
        return source;
    }

    private static String PadRight(String source, int length) {
        while (source.length() < length) {
            source += SPECIAL_CHAR;
        }
        return source;
    }

    private static String PadLeft(String source, int length) {
        while (source.length() < length) {
            source = '0' + source;
        }
        return source;
    }
}
