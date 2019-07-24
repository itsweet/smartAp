package com.example.lit.smartap_20180111.data;

import android.util.Log;

import com.example.lit.smartap_20180111.Utils;

import com.example.lit.smartap_20180111.core.coap.CoAP;
import com.example.lit.smartap_20180111.core.coap.Message;
import com.example.lit.smartap_20180111.core.coap.OptionSet;
import com.example.lit.smartap_20180111.core.coap.Request;
import com.example.lit.smartap_20180111.core.coap.Response;
import com.example.lit.smartap_20180111.core.network.serialization.DataParser;
import com.example.lit.smartap_20180111.core.network.serialization.UdpDataParser;
import com.example.lit.smartap_20180111.core.network.serialization.UdpDataSerializer;

/**
 * Created by ws on 2018/3/16.
 */

public class DataObject {
    private String TAG="DataObject";
    private int time;
    private int timeout=5000;
    private Long startTime;

    //包的类型，包括request、response、ack
    private boolean isResponse;
    private boolean isRequest;
    //是否收到ACK回包，如果收到则重发包停止
    boolean ACK;
    //private Request mrequest;
    //private Response mresponse;
    private OptionSet optionSet;
    int MID;
    private CoAP.Type TYPE;
    private byte[] TOKEN;
    private Object CODE;
    private String URI_HOST;
    private int URI_PORT;
    private String URI_PATH;
    private String URI_QUERY;
    byte[] Payload;
    byte[] data;
    BaseResultCallback<Object> callback;

    DataObject(){
        this.time=3;
        this.ACK=false;
    }

    public DataObject(byte[] bytes, BaseResultCallback<Object> callback){
        this();
        this.data=bytes;
        this.callback=callback;
        this.startTime=null;
        coapinit();

    }

    public DataObject(Request request, BaseResultCallback callback){
        this(new UdpDataSerializer().serializeRequest(request).getBytes(),callback);
    }
    public DataObject(Response response, BaseResultCallback callback){
        this(new UdpDataSerializer().serializeResponse(response).getBytes(),callback);
    }


    private void coapinit(){
        DataParser dp=new UdpDataParser();
        Message message=dp.parseMessage(data);
        //Log.i(TAG, "DataObject: message:"+message.getRawCode());
        if (CoAP.isResponse(message.getRawCode())) {
            //Log.i(TAG, "DataObject: code:"+message.getRawCode());
            isResponse=true;
            CODE=CoAP.ResponseCode.valueOf(message.getRawCode());
            //Log.i(TAG, "DataObject: MID:"+MID+",respnse:"+message.toString());
        }else if (CoAP.isRequest(message.getRawCode())){
            isRequest=true;
            CODE=CoAP.Code.valueOf(message.getRawCode());
            //Log.i(TAG, "DataObject: MID:"+MID+",request:"+message.toString());
        }else {
            Log.e(TAG, "DataObject: error code"+message.getRawCode(),null );
        }
        MID=message.getMID();
        TYPE=message.getType();
        TOKEN=message.getToken();
        optionSet=message.getOptions();
        URI_PATH = optionSet.getUriPathString();
        URI_QUERY = optionSet.getUriQueryString();
        URI_HOST=optionSet.getUriHost();
        if (optionSet.getUriPort()!=null) {
            URI_PORT = optionSet.getUriPort();
        }else {
            URI_PORT = -1;
        }
        Payload=message.getPayload();
    }

    public boolean isRequest(){return isRequest;}

    public boolean isResponse(){
        return isResponse;
    }

    public boolean getACK(){return ACK;}
    public void setACK(boolean ack){this.ACK=ack;}
    public Object getCode(){return this.CODE;}
    public byte[] getToken(){return this.TOKEN;}
    public CoAP.Type getType(){return this.TYPE;}
    public void setTimeout(int timeout){
        this.timeout=timeout;
    }

    public int getTimeout(){
        return timeout;
    }
    public DataObject setCallback(BaseResultCallback callback){
        this.callback = callback;
        return this;
    }
    public BaseResultCallback<Object> getCallback(){return callback;}
    public Long getStartTime(){
        return startTime;
    }
    public void setStartTime(Long time){this.startTime=time;}

    public int getMID(){
        return MID;
    }

    public String getURI_HOST(){return URI_HOST;}

    public int getURI_PORT(){return URI_PORT;}

    public void setURI_PORT(int uri_port){this.URI_PORT=uri_port;}
    public String getURI_PATH(){
        return URI_PATH;
    }
    public String getURI_QUERY(){
        return URI_QUERY;
    }

    public void setPayload(byte[] payload) {
        Payload = payload;
    }

    public byte[] getPayload(){return Payload;}
    public byte[] getData(){
        return data;
    }
    public int getTime(){
        return time;
    }
    public void setTime(int time){this.time=time;}
    public boolean edu_time() {
        if (time>0) {
            time--;
            return true;
        }else {
            return false;
        }
    }

    public enum Uri_Host{
        GA,
        AG,
        CA,
        AC;
    }



    /*
    void decode(){
        Message msg;
        DataParser dp;
        boolean isRequest;
        try{
            dp=new DataParser(data);
            isRequest=dp.isRequest();
            if (isRequest){
                ACK=false;
                msg=dp.parseRequest();
            }else {
                ACK=true;
                Response response=dp.parseResponse();
                response.getCode();
            }
        }catch (Exception e){
            Log.e(TAG, "decode: error:"+e,null );
        }
        //String uri = msg.getOptions().getUriPathString();
        //List<String> querylist=msg.getOptions().getUriQuery();

    }
    */

    @Override
    public String toString(){
        return String.format("WifiData{time:%s %s-%-6s MID=%5d Token=%s{Uri_Host:%s Uri_Port:%s Uri_Path:%s Uri_Query:%s} Payload:%s}"
                ,time,TYPE,CODE ,MID,Utils.toHexString(TOKEN),URI_HOST,URI_PORT,URI_PATH, URI_QUERY,Utils.bytes2Hex(Payload));
    }
}
