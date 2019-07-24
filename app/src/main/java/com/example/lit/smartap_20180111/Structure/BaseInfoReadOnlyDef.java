package com.example.lit.smartap_20180111.Structure;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ws on 2018/3/12.
 */

public class BaseInfoReadOnlyDef{
    public int imageType;
    public int deviceType;
    public int macaddr;
    public int parentaddr;
    public int mversion;
    public int maxbytes;
    public int status;


    public BaseInfoReadOnlyDef(byte[] bytes){
        encode(bytes);
    }
    public BaseInfoReadOnlyDef(String string){
        byte[] bytes=string.getBytes();
        encode(bytes);
    }

    private void encode(byte[] bytes){
        imageType=bytes[0]+bytes[1];
        deviceType=bytes[2];
        macaddr=bytes[3];
        parentaddr=bytes[4];
        mversion=bytes[5];
        maxbytes=bytes[7];
        status=bytes[8];
    }

    public Map<String,Integer> decode(){
        Map<String,Integer> baseMap=new HashMap<String, Integer>();
        baseMap.put("imageTpye",imageType);
        baseMap.put("deviceType",deviceType);
        baseMap.put("macaddr",macaddr);
        baseMap.put("parentaddr",parentaddr);
        baseMap.put("mversion",mversion);
        baseMap.put("maxbytes",maxbytes);
        baseMap.put("status",status);
        return baseMap;
    }
}
