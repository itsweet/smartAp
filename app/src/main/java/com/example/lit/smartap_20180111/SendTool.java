package com.example.lit.smartap_20180111;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.lit.smartap_20180111.core.coap.CoAP;
import com.example.lit.smartap_20180111.core.coap.Request;
import com.example.lit.smartap_20180111.core.network.serialization.UdpDataSerializer;
import com.example.lit.smartap_20180111.data.BaseResultCallback;
import com.example.lit.smartap_20180111.data.ConnectStatus;
import com.example.lit.smartap_20180111.data.DataMQTT;
import com.example.lit.smartap_20180111.data.DataObject;
import com.example.lit.smartap_20180111.data.IOT_CMD;
import com.example.lit.smartap_20180111.data.MqttSenderListener;
import com.example.lit.smartap_20180111.elements.RawData;
import com.example.lit.smartap_20180111.elements.RsaUtils;
import com.example.lit.smartap_20180111.elements.Xxtea;
import com.example.lit.smartap_20180111.json.Json_getPublicKey;
import com.example.lit.smartap_20180111.json.Json_getServer;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by ws on 2018/3/13.
 */

public class SendTool {
    private static String TAG="SendTool";
    private int mid=0;
    private String wifiMac;
    private String gateWayMAC;
    private Xxtea xxtea;
    private String publickey_modulus;
    private String publickey_exponent;
    private DataAdapter dataAdapter;
    private boolean login = false;
    private STforMainListener STforMainListener; //用于零时的监听回包，例如 获取校验码
    private MqttSenderListener<DataMQTT> listener;
    private Context context;
    private TelephonyManager tm;
    DataAdapter.SocketType stSocktetType;
    DataMQTT.DataType stDataType;

    SendTool(Context context){
        stSocktetType = DataAdapter.SocketType.MQTT;
        stDataType = DataMQTT.DataType.MQTT;
        dataAdapter = new DataAdapter(context, stSocktetType, stDataType);
        this.context = context;
        this.wifiMac = dataAdapter.getMacFromWifiInfo();
        xxtea = new Xxtea();
        //recThread.start();
        tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        switch (stDataType){
            case MQTT:
                mqtt_message_handle();
                break;
            case COAP:

        };
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public boolean getLogin(){
        return login;
    }

    public void setSTforMainListener(STforMainListener listener) {
        this.STforMainListener = listener;
    }
    public void rmSTforMainListener(){
        this.STforMainListener = null;
    }

    public void setListener(MqttSenderListener listener) {
        this.listener = listener;
    }


    Handler handler = new Handler();

    Thread recThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                    //Log.i(TAG, "run:  wifiadapter == "+dataAdapter.toString());
                    dataAdapter.receiveServer();
                    //dataAdapter.sendByte(5000);
                }catch (Exception e){
                    Log.e(TAG, "Main runner send and receive: error:"+e );
                    //return;
                }
            }
        }
    });


    private int midgenerate(){
        if (mid<65536){
            return mid++;
        }else {
            mid=0;
            return 0;
        }
    }

    void setGateWayMAC(String s){
        this.gateWayMAC=s;
    }

    String getGateWayMAC(){
        return this.gateWayMAC;
    }

    void setPublickey_modulus(String modulus){
        this.publickey_modulus = modulus;
    }

    String getPublickey_modulus(){
        return this.publickey_modulus;
    }

    void setPublickey_exponent(String exponent){
        this.publickey_exponent = exponent;
    }

    String getPublickey_exponent(){
        return this.publickey_exponent;
    }

    byte[] publicEncrypt(String data){
        if (publickey_modulus!=null&&publickey_exponent!=null){
            try {
                RSAPublicKey rsaPublicKey= RsaUtils.getPublicKey(publickey_modulus,publickey_exponent);

                return RsaUtils.publicEncrypt(data,rsaPublicKey);
            }catch (Exception e){
                throw new RuntimeException("使用公钥加密错误："+e);
            }
        }else {
            throw new RuntimeException("未获取到公钥");
        }
    }

    byte[] xxteaDecrypt(byte[] data){
        return xxtea.Decrypt(data);
    }

    byte[] xxteaDecrypt(byte[] data,long[] key){
        return xxtea.Decrypt(data,key);
    }

    byte[] xxteaEncrypt(byte[] data){
        return xxtea.Encrypt(data);
    }

    byte[] xxteaEncrypt(String data){
        byte[] bytes = data.getBytes();
        return xxtea.Encrypt(bytes);
    }

    public interface STforMainListener{
        void onSuccess(String s);
        void onFail(String s);
    }

    public void onDestroy(){
        dataAdapter.onDestroy();
    }

    /**
     * @param type
     * @param command
     * @return
     */
    private byte[] basetoken(String type,int command){
        String version = "V1";
        if (type.length()!=2){
            Log.e(TAG, "basetoken: "+ type +"must = 2", null);
            return null;
        }
        byte[] bytes = new byte[8];
        for (int i = 0;i < 2;i++) {
            bytes[i] = version.getBytes()[i];
        };
        for (int j = 2,k =0; j < 4;j++,k++){
            bytes[j] = type.getBytes()[k];
        }
        //4-5 构成 IOT_COMMAND 例如0x0001 详情见IOT_Command

        bytes[4]=(byte) (command >> 8);
        bytes[5]=(byte) (command & 0xff);

        return bytes;
    }

    private byte[] acktoken(int command){
        String version = "V1";
        String type = "AC";
        byte[] bytes = new byte[8];
        for (int i = 0;i < 2;i++) {
            bytes[i] = version.getBytes()[i];
        };
        for (int j = 2; j < 4;j++){
            bytes[j] = type.getBytes()[j];
        }
        //4-5 构成 IOT_COMMAND 例如0x0001 详情见IOT_Command
        bytes[4]=(byte) 0x80;
        bytes[5]=(byte) command;

        return bytes;
    }

    private Request makeRequest(CoAP.Type type,String host,int port,String uri_path,String uri_query,byte[] token,byte[] payload){
        Request request=Request.newGet();
        try{
            request.mysetURI(getCoapUri(host,port,uri_path,uri_query));
        }catch (Exception e){
            Log.e(TAG, "makeRequest:URI error:"+e,null );
        }
        request.setToken(token);
        if (type==null){
            type= CoAP.Type.CON;
        }
        request.setType(type);
        request.setMID(midgenerate());
        request.setPayload(payload);
        return request;
    }

    /**
     * @param type
     * @param host
     * @param port
     * @param uri_path
     * @param uri_query
     * @param command 用于和云端通讯的IOT_COMMAND
     * @param payload
     * @return
     */
    private Request makeCloudRequest(CoAP.Type type,String host,int port,String uri_path,String uri_query,IOT_CMD command,byte[] payload){
        Request request=Request.newGet();
        try{
            request.mysetURI(getCoapUri(host,port,uri_path,uri_query));
        }catch (NullPointerException e){

        }catch (Exception e){
            Log.e(TAG, "makeCloudRequest: URI error:"+e,null );
        }
        request.setToken(basetoken("AC",command.getValue()));
        if (type==null){
            type= CoAP.Type.CON;
        }
        request.setType(type);
        request.setMID(midgenerate());
        request.setPayload(payload);
        return request;
    }

    private Request makeCloudRequest(String uri_path,String uri_query,IOT_CMD command,byte[] payload){
        return makeCloudRequest(CoAP.Type.CON,"host",0,uri_path,uri_query,command,payload);
    }

    @Nullable
    public static String getCoapUri(@Nullable String host,@Nullable int port,@Nullable String uri_path,@Nullable String uri_query){
        StringBuilder stringBuilder=new StringBuilder("coap://");
        if (host!=null) {
            stringBuilder.append(host);
        }
        stringBuilder.append(":");
        stringBuilder.append(port);
        if (uri_path!=null){
            stringBuilder.append("/");
            stringBuilder.append(uri_path);
        }
        if(uri_query!=null) {
            stringBuilder.append("?");
            stringBuilder.append(uri_query);
        }
        return stringBuilder.toString();
    }

    byte[] handshakingByte(){
        Request request=handshakingRequest();
        RawData data=new UdpDataSerializer().serializeRequest(request);
        return data.getBytes();
    }

    Request handshakingRequest(){
        return new Request(CoAP.Code.POST);
    }

    DataMQTT mqtt_Engineer(IOT_CMD iot_cmd){
        MqttMessage message = new MqttMessage();
        message.setQos(0);
        message.setId(midgenerate());
        String id = ConfigList.getUsername();
        String cmd = String.valueOf(iot_cmd.getValue());
        String topic = "/" + id + "/cmd/" + cmd;
        switch (iot_cmd){
            default:
                return null;
            case Server_Addr:
                return new DataMQTT(message, topic);

            case Public_key:
                return new DataMQTT(message,topic);

            case Symmetric_key:
                message.setPayload(symmetric_key().getPayload());
                return new DataMQTT(message,topic);
            case Login: {
                Object object = signin(ConfigList.getUsername(),
                        ConfigList.getUserpwd(), ConfigList.getVcode());
                DataObject dataObject = (DataObject) object;
                message.setPayload(dataObject.getPayload());
                return new DataMQTT(message, topic);
            }
            case Register_account: {
                Object object = registerAccount(ConfigList.getUsername(),
                        ConfigList.getUserpwd(), ConfigList.getVcode());

                DataObject dataObject = (DataObject) object;
                message.setPayload(dataObject.getPayload());
                return new DataMQTT(message,topic);
            }
            case Get_verification_code:
                Object o = verification_code();
                DataObject dataObject = (DataObject) o;
                message.setPayload(dataObject.getPayload());
                return new DataMQTT(message,topic);
        }
    }

    //处理所有返回的包，有连续发包流程的在switch中处理，default 往界面返回payload
    void mqtt_message_handle(){
        dataAdapter.setDAforSTListener(new MqttSenderListener() {
            @Override
            public void statusChange(ConnectStatus status) {
                if (listener == null){
                    return;
                }
                if (status == ConnectStatus.DISCONNECT){
                    listener.statusChange(ConnectStatus.DISCONNECT);
                    if (login){
                        connect_def();
                        connectserver();
                    }
                }
            }

            @Override
            public void onSuccess(Object data) {
                DataMQTT dataMQTT = (DataMQTT) data;
                IOT_CMD iot_cmd = dataMQTT.getCmd();
                if (!dataMQTT.getACK()){
                    Log.e(TAG, "mqtt_message_handle onResponse: error not ack",null );
                    return;
                }
                byte[] bytes_payload = xxteaDecrypt(dataMQTT.getPayload());
                String payload = new String(bytes_payload);
                //Log.i(TAG, "mqtt_message_handle onResponse: "+payload);
                if (iot_cmd == null){
                    Log.e(TAG, "mqtt_message_handle: error CMD", null);
                    return;
                }
                switch (iot_cmd){
                    case Server_Addr: {
                        byte[] s = dataMQTT.getPayload();
                        Json_getServer jsonGetServer = (Json_getServer) (new Json_getServer()).fromJson(s);
                        if (!jsonGetServer.IP.equals("127.0.0.1")) {
                            ConfigList.setServerAddress(jsonGetServer.IP);
                        }
                        int tcpport = Integer.decode(jsonGetServer.TCPPORT);
                        int udpport = Integer.decode(jsonGetServer.UDPPORT);
                        ConfigList.TcpPort = tcpport;
                        ConfigList.UdpPort = udpport;
                        Log.i(TAG, "getserver: " + jsonGetServer.IP + " TCPPORT:" + jsonGetServer.TCPPORT +
                                " UDPPORT:" + jsonGetServer.UDPPORT);
                        try {
                            dataAdapter.connect_change();
                            dataAdapter.mqtt_send(mqtt_Engineer(IOT_CMD.Public_key));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    break;
                    case Public_key:
                    {
                        byte[] bytes = dataMQTT.getPayload();
                        Log.i(TAG, "getpublickeyCallBack: " + new String(bytes));
                        Json_getPublicKey jsonGetPublicKey =
                                (Json_getPublicKey) (new Json_getPublicKey()).fromJson(bytes);
                        String modulus_16 = jsonGetPublicKey.N;
                        String exponent_16 = jsonGetPublicKey.E;
                        setPublickey_modulus(Utils.hexStr2DecStr(modulus_16));
                        setPublickey_exponent(Utils.hexStr2DecStr(exponent_16));
                        //获取公钥后获取 静态密钥
                        DataMQTT symmetric_key = mqtt_Engineer(IOT_CMD.Symmetric_key);
                        dataAdapter.mqtt_send(symmetric_key);
                    }
                    break;
                    default:
                    {
                        Map<String,String> map = jsontomap(payload);
                        try {
                            long timeserver = Long.valueOf(map.get("TIME"));
                            long time = System.currentTimeMillis() / 1000;
                            Log.i(TAG, "time:" + time + " cmd:"+iot_cmd+"{XxteaCallBack: " + payload + "}");
                            //过滤时间（绝对值）超过5分钟的包
                            if (Math.abs(time - timeserver) > 60 * 5L) {
                                return;
                            }
                            String errcode = map.get("CODE");
                            if (SendTool.this.STforMainListener != null) {
                                switch (errcode) {
                                    case "0":
                                        STforMainListener.onSuccess(payload_handle(iot_cmd,
                                                payload));
                                        break;
                                    default:
                                        STforMainListener.onFail(errcode);
                                        break;
                                }
                            }
                            if (SendTool.this.listener != null){
                                switch (errcode) {
                                    case "0":
                                        if (iot_cmd == IOT_CMD.Login){
                                            setLogin(true);
                                        }
                                        listener.onSuccess(dataMQTT);
                                        //UI界面上的已连接状态是完成登录之后表示，不同于下层的ConnectStatus
                                        listener.statusChange(ConnectStatus.CONNECTED);
                                        break;
                                    default:
                                        listener.onFail(0,errcode);
                                        break;
                                }
                            }
                        }catch (NullPointerException e){
                            Log.e(TAG, "mqtt_message_handle onResponse: "+e,null );
                            return;
                        }
                        //Log.i(TAG, "mqtt_message_handle: test");
                    }
                }
            }

            @Override
            public void onFail(int status, String msg) {
                listener.onFail(status,msg);
            }
        });
    }

    String payload_handle(IOT_CMD iot_cmd,String payload){
        Map<String,String> map = jsontomap(payload);
        switch (iot_cmd){
            case Get_verification_code:
                String value = map.get("VCODE");
                Log.i(TAG, "payload_handle: vcode:"+value);
                return value;

            default:
                return payload;
        }
    }

    void  interactive(IOT_CMD iot_cmd){
        switch (stSocktetType){
            case MQTT:
                DataMQTT dataMQTT = mqtt_Engineer(iot_cmd);
                dataAdapter.mqtt_send(dataMQTT);
                break;
            case UDP:
        }
        if (listener == null){
            return;
        }
        if (iot_cmd == IOT_CMD.Server_Addr){
            listener.statusChange(ConnectStatus.CONNECTING);
        }
    }

    void interactive(IOT_CMD iot_cmd,STforMainListener listener){
        setSTforMainListener(listener);
        interactive(iot_cmd);
    }

    void connect_def(){
        switch (stSocktetType){
            case MQTT:
                dataAdapter.connect_def();
                break;
        }
    }

    void connectserver(){
        switch (stSocktetType){
            case MQTT:
                //dataAdapter.connect_change(ConfigList.defServerAddress.toString(),ConfigList.defMqttPort);
                interactive(IOT_CMD.Server_Addr);
        }
        //ConnectServer connectServer=new ConnectServer(ConfigList.getUsername(),listener);
        //connectServer.send();
    }


    /**获取服务器地址和端口号
     * name 用于注册或登录的用户名
     * @return  callback(实现了successAction)为自己的 WiFiDataObject
     */
    abstract class GetServer extends SendAction {
        GetServer(String name, STforMainListener listener){
            super(listener);
            switch (stDataType){
                case COAP:
                    setDataObject(new DataObject(makeCloudRequest(name,null,
                            IOT_CMD.Server_Addr,null),this));
                    break;
                case MQTT:
                    MqttMessage message = new MqttMessage();
                    message.setQos(0);
                    message.setId(midgenerate());
                    String id = ConfigList.getUsername();
                    String cmd = String.valueOf(IOT_CMD.Server_Addr.getValue());
                    DataMQTT dataMQTT = new DataMQTT(message,
                            "/"+id + "/cmd/"+cmd);
                    Map map = new HashMap();
                    long time = System.currentTimeMillis()/1000;
                    map.put("TIME",time);
                    String payload = new Gson().toJson(map);
                    //dataMQTT.setPayload(payload.getBytes());
                    setDataObject(dataMQTT);
            }
        }

        @Override
        void successAction(byte[] s) {
            Json_getServer jsonGetServer = (Json_getServer) (new Json_getServer()).fromJson(s);
            if(!jsonGetServer.IP.equals("127.0.0.1")){
                ConfigList.setServerAddress(jsonGetServer.IP);
            };
            int tcpport = Integer.decode(jsonGetServer.TCPPORT);
            int udpport = Integer.decode(jsonGetServer.UDPPORT);
            ConfigList.TcpPort = tcpport;
            ConfigList.UdpPort = udpport;
            Log.i(TAG, "getserver: "+jsonGetServer.IP + " TCPPORT:"+ jsonGetServer.TCPPORT+
                    " UDPPORT:"+jsonGetServer.UDPPORT);
            after();
        }

        abstract void after();

    };

    @NotNull
    private DataObject getpublickey(){
        Request request = makeCloudRequest(null,null,
                IOT_CMD.Public_key,null);
        return new DataObject(request,null);
    }

    abstract class Getpublickey extends SendAction {
        Getpublickey(STforMainListener listener){
            super(listener);
            switch (stDataType) {
                case COAP:
                    setDataObject(getpublickey().setCallback(this));
                    break;
                case MQTT:
                    MqttMessage mqttMessage = new MqttMessage();
                    mqttMessage.setQos(0);
                    mqttMessage.setId(midgenerate());
                    String id = ConfigList.getUsername();
                    String cmd = String.valueOf(IOT_CMD.Public_key.getValue());
                    DataMQTT dataMQTT = new DataMQTT(mqttMessage,
                            "/"+id + "/cmd/"+cmd);
                    setDataObject(dataMQTT);
            }
        }
        @Override
        void successAction(byte[] bytes) {
            Log.i(TAG, "getpublickeyCallBack: "+new String(bytes));
            Json_getPublicKey jsonGetPublicKey =
                    (Json_getPublicKey)(new Json_getPublicKey()).fromJson(bytes);
            String modulus_16 = jsonGetPublicKey.N;
            String exponent_16 = jsonGetPublicKey.E;
            setPublickey_modulus(Utils.hexStr2DecStr(modulus_16));
            setPublickey_exponent(Utils.hexStr2DecStr(exponent_16));
            String username = ConfigList.getUsername();
            String userpwd = ConfigList.getUserpwd();
            after();
        }

        abstract void after();
    };

    private class XxteaCloudRequest {
        private DataObject dataObject;
        private long[] key ;
        private IOT_CMD iot_cmd;
        private SendAction callBack;
        private Map<String,Object> map;
        private XxteaCloudRequest(){
            this.key = xxtea.getKEY();
            this.map = new HashMap<String, Object>();
        }

        private XxteaCloudRequest(IOT_CMD command, SendAction callBack){
            this();
            this.iot_cmd = command;
            this.callBack = callBack;
        }

        private void putMap(String key,Object  value) {
            this.map.put(key, value);
        }

        byte[] makepayload(){
            long time = System.currentTimeMillis()/1000;
            map.put("TIME",time);
            String payload = new Gson().toJson(map);
            Log.i(TAG, "makepayload: "+payload);
            return xxteaEncrypt(payload);
        };

        private Object toWifiData(){
            Request request = makeCloudRequest(ConfigList.getUsername(),null,iot_cmd,makepayload());
            return new DataObject(request,callBack);
        }
    }

    //交互获取对称密钥，使用公钥加密
    private DataObject symmetric_key(){
        long[] key = xxtea.getKEY();
        byte[] bytes = Xxtea.ToByteArray(key,ByteOrder.BIG_ENDIAN);
        Map<String,Object> map = new HashMap<String, Object>();
        String keyhexstring = Utils.toHexString(bytes);
        map.put("KEY",keyhexstring);
        long time = System.currentTimeMillis()/1000;
        Log.i(TAG, "symmetric_key: time:"+time+" key:"+keyhexstring);
        map.put("TIME",time);
        String payload = new Gson().toJson(map);
        Request request = makeCloudRequest(ConfigList.getUsername(), null,
                IOT_CMD.Symmetric_key, publicEncrypt(payload));
        return new DataObject(request, null);
    }

    //XXtea加密的都使用该类，增加了默认xxtea加密，并增加TIME
    abstract class XxteaSendAction extends SendAction {
        XxteaSendAction(STforMainListener listener){
            super(listener);
        }
        @Override
        public void onSuccess(DataObject dataObject) {

            byte[] bytespayload = xxteaDecrypt(dataObject.getPayload());
            String payload = new String(bytespayload);
            Map<String,String> map = jsontomap(payload);
            long timeserver = Long.valueOf(map.get("TIME"));
            long time = System.currentTimeMillis()/1000;
            Log.i(TAG, "time:"+time+"{XxteaCallBack: "+payload+"}");
            //过滤时间（绝对值）超过5分钟的包
            if (Math.abs(time-timeserver) > 60*5L){
                return;
            }
            successAction(bytespayload);
        }

    }

    //在sendtoolcallback基础上增加，发包(send)操作

    abstract class SendAction implements BaseResultCallback<DataObject>{
        private STforMainListener listener;
        private Object dataObject;
        Timer timer;
        //DataMQTT dataStructure;
        //SendAction(){}

        SendAction(final STforMainListener listener){
            this.listener = listener;
            timer = new Timer(true);
            final long timeout = 30*1000;
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    listener.onFail("time out "+timeout);
                }
            };
            timer.schedule(task,timeout);
        }

        Object setDataObject(Object dataObject) {
            this.dataObject = dataObject;
            return dataObject;
        }

        Object getDataObject() {
            return dataObject;
        }

        public void setListener(STforMainListener listener) {
            this.listener = listener;
        }

        STforMainListener getListener() {
            return listener;
        }

        //继承BaseResultCallback ,底层收到回复后回调的接口，
        //成功就就将payload提取出来上抛
        //失败就将msg上抛
        @Override
        public void onSuccess(DataObject dataObject){
            timer.cancel();
            successAction(dataObject.getPayload());
        }

        @Override
        public void onFail(int status, String msg){
            Log.i(TAG, "SendAction: onFail :"+msg);
            timer.cancel();
            failAction(msg,listener);
        }

        //成功后的操作
        abstract void successAction(byte[] bytes);

        //abstract void successAction(String s);

        //失败的动作，默认把msg抛出，如果有其它动作，需要重写
        void failAction( String msg, STforMainListener listener){
            listener.onFail(msg);
        };

        //发包动作,wifiadapter.write（wifidataobject）
        public void send() throws NullPointerException{
            if (dataObject == null ) {
                throw new  NullPointerException("data is null");
            }else if(dataObject instanceof DataMQTT){
                dataAdapter.mqtt_send((DataMQTT) dataObject);
            }else {
                dataAdapter.write((DataObject) dataObject);
            }
        };
    }



    //获取短信验证码对上层的方法接口
    void verification_code_action(STforMainListener listener){
        new VerificationCode(listener).send();
    }

    class VerificationCode extends XxteaSendAction{
        VerificationCode(STforMainListener listener){
            super(listener);
            setDataObject(new XxteaCloudRequest(IOT_CMD.Get_verification_code,
                    this).toWifiData());
        }

        @Override
        public void successAction(byte[] bytes){
            String payload = new String(bytes);
            Map<String,String> map = jsontomap(payload);
            String vcode = map.get("VCODE");
            getListener().onSuccess(vcode);
        }
    }

    //获取短信验证码
    private Object verification_code(){
        XxteaCloudRequest request = new XxteaCloudRequest(
                IOT_CMD.Get_verification_code,null);
        return request.toWifiData();
    }

    static public List<Map<String,String>> jsontolistmap(String s){
        List<Map<String,String>> mapList = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = (JsonArray)jsonParser.parse(s);
        /*
        for (int i=0; i < jsonArray.size();i++) {
            JsonObject jsonObject=(JsonObject) jsonArray.get(i);
            Map<String,String> map = jsontomap(jsonObject.toString());
            mapList.add(map);
        }
        */
        for (JsonElement js:jsonArray
             ) {
            JsonObject jsonObject = (JsonObject)js;
            Map<String,String> map = jsontomap(jsonObject.toString());
            mapList.add(map);
        }
        return mapList;
    }


    static public Map<String,String> jsontomap(String s){
        Gson gson = new Gson();
        Map<String,String> map = gson.fromJson(s,
                new TypeToken<HashMap<String,String>>(){}.getType());
        return map;
    }

    String maptojson(Map<String,String> map){
        return new Gson().toJson(map);
    }

    public void registerAccount_action(String name , String pwd , String vcode, STforMainListener listener){
        switch (stDataType){
            case COAP:
                new RegisterAccount(name,pwd,vcode,listener).send();
                break;
            case MQTT:
                interactive(IOT_CMD.Register_account);
        }
    }


    Object registerAccount(String name , String pwd , String vcode){
        String md5_pwd = getMD5(name,pwd);
        XxteaCloudRequest request = new XxteaCloudRequest(IOT_CMD.Register_account,
                null );
        request.putMap("ACCOUNT",name);
        request.putMap("PWD",md5_pwd);
        request.putMap("VCODE",vcode);
        request.putMap("ATYPE",0);
        return request.toWifiData();
    }

    private class RegisterAccount extends XxteaSendAction {
        RegisterAccount(String name, String pwd, String vcode, STforMainListener listener){
            super(listener);
            DataObject dataObject = (DataObject) setDataObject(registerAccount(name,pwd,vcode));
            dataObject.setCallback(this);
        }
        @Override
        void successAction(byte[] s) {
            String payload="";
            if (s != null) {
                payload = new String(xxteaDecrypt(s));
            }
            getListener().onSuccess(payload);
        }

    };


    public static String getMD5(String name,String pwd) throws RuntimeException{
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(name.getBytes());
            byte[] temp1 = messageDigest.digest();

            messageDigest.reset();
            messageDigest.update(pwd.getBytes());
            byte[] temp2 =messageDigest.digest();

            messageDigest.reset();
            messageDigest.update(temp1);
            messageDigest.update(temp2);
            return Utils.bytes2Hex(messageDigest.digest());
        }catch (Exception e){
            throw new RuntimeException("MD5加密错误");
        }
    }

    void signin_action(String name, String pwd, String vcode, STforMainListener listener){
        switch (stDataType){
            case MQTT:
                interactive(IOT_CMD.Login,listener);
                break;
            case COAP:
                new SigninAction(name,pwd,vcode,listener).send();
                break;
        }

    }

    private String getdeviceid(){
        String imei;
        try {
            imei=tm.getDeviceId();
        }catch (SecurityException e){
            imei = "";
            Log.e(TAG, "connectServerListener: getDeviceId error"+e,null );
        }
        return imei;
    }

    //账户登录
    private Object signin(String name, String pwd, String vcode){

        return signin(name,pwd,getdeviceid(),vcode);
    }

    private Object signin(String name, String pwd, String imei, String vcode){
        String md5_pwd = getMD5(name,pwd);
        XxteaCloudRequest request = new XxteaCloudRequest(IOT_CMD.Login,null);
        request.putMap("ACCOUNT",name);
        request.putMap("PWD",md5_pwd);
        request.putMap("IMEI",imei);
        request.putMap("ATYPE",0);
        if (vcode != null) {
            request.putMap("VCODE", vcode);
        }
        return request.toWifiData();
    }

    class SigninAction extends XxteaSendAction {
        SigninAction(String name, String pwd, String vocde, STforMainListener listener){
            super(listener);
            DataObject dataObject = (DataObject)
            setDataObject(signin(name,pwd,vocde));
            dataObject.setCallback(this);
        }
        @Override
        void successAction(byte[] s) {
            String payload = new String(s);
            getListener().onSuccess(payload);
        }
    };

    void startHeartbeat(String name, STforMainListener listener){
        new HeartBeat(name,listener).send();
    }

    private class HeartBeat extends XxteaSendAction{
        HeartBeat(String name, STforMainListener listener){
            super(listener);
            DataObject dataObject =
                    (DataObject) setDataObject(heartbeat(name));
            dataObject.setCallback(this);
        }
        @Override
        void successAction(byte[] bytes){
            getListener().onSuccess(new String(bytes));
        }
    }

    Object heartbeat(String name){
        XxteaCloudRequest request = new XxteaCloudRequest(IOT_CMD.Proxy_heartbeat,null);

        return request.toWifiData();
    }

    private Object searchFamily(String name){
        XxteaCloudRequest request = new XxteaCloudRequest(IOT_CMD.Family_search,null);
        request.putMap("ACCOUNT",name);
        request.putMap("INDEX","0");
        request.putMap("NUM","65535");
        return request.toWifiData();
    }

    private class SearchFamily extends XxteaSendAction{
        SearchFamily(String name, STforMainListener listener){
            super(listener);
            DataObject dataObject =(DataObject) setDataObject(searchFamily(name));
            dataObject.setCallback(this);
        }
        @Override
        void successAction(byte[] bytes){
            String s =new String(bytes);
            Map<String,String> map=jsontomap(s);
            getListener().onSuccess(map.get("FAMILY"));
        }
    }

    void searchFamily_action(String name, STforMainListener listener){
        new SearchFamily(name,listener).send();
    }

    private Object searchfriend(String name){
        XxteaCloudRequest request = new XxteaCloudRequest(IOT_CMD.Friend_search,null);
        request.putMap("ACCOUNT",name);
        return request.toWifiData();
    }

    private class Friend_search extends XxteaSendAction{
        Friend_search(String name, STforMainListener listener){
            super(listener);
            setDataObject(searchfriend(name));
        }
        @Override
        void successAction(byte[] bytes){
            String s = new String( bytes);
            Map<String,String> map = jsontomap(s);
            getListener().onSuccess(map.get("NAME"));
        }
    }

    void searchfriend_action(String name , STforMainListener listener){
        new Friend_search(name,listener).send();
    }

    private Object friendrequest(String account, String name, String info, String message, String friend, String markname, String group){
        XxteaCloudRequest request = new XxteaCloudRequest(IOT_CMD.Friend_request,null);
        request.putMap("ACCOUNT",account);
        request.putMap("NAME",name);
        request.putMap("INFO",info);
        request.putMap("MESSAGE",message);
        request.putMap("FRIEND",friend);
        request.putMap("MARKNAME",markname);
        request.putMap("GROUP",group);
        return request.toWifiData();
    }

    private class Friend_request extends XxteaSendAction{
        Friend_request(String account, String name, String info, String message, String friend,
                       String markname, String group, STforMainListener listener){
            super(listener);
            setDataObject(friendrequest(account, name, info, message, friend, markname, group));
        }
        @Override
        void successAction(byte[] bytes){
            String s = new String(bytes);
            Map<String,String> map = jsontomap(s);
            getListener().onSuccess(s);

        }
    }

}
