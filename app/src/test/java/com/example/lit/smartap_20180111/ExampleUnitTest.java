package com.example.lit.smartap_20180111;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;
import com.example.lit.smartap_20180111.Structure.BaseInfoReadOnlyDef;
import com.example.lit.smartap_20180111.elements.RsaUtils;
import com.example.lit.smartap_20180111.elements.Xxtea;
import com.example.lit.smartap_20180111.json.FamilyList;
import com.example.lit.smartap_20180111.json.Json;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.Key;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void bytetest() throws Exception{
        BaseInfoReadOnlyDef test=new BaseInfoReadOnlyDef("1234567890ABCDEF");
        test.decode();
        byte[] nbyte = new byte[0];
        if (nbyte .length==0){
            Log.i("bytetest", "==null");
        }
        //assertEquals(nbyte,null);
        String s = "3586";
        int i = Integer.decode(s);
        //int j = Integer.getInteger(s);
        int n = Integer.valueOf(s);
        //assertArrayEquals();
    }
    byte[] getbytes(){
        byte[] bytes=new byte[14];
        for (int i=0;i<14;i++){
            bytes[i]=(byte)(i+1);
        }
        return bytes;
    };

    @Test
    public void xxteatest() throws Exception{
        String string = "{error_code=200}";
        for (int i = 0 ; i<100000000;i++) {
            Xxtea xxteainstance=new Xxtea();
            byte[] source = string.getBytes();
            String s = new String(source);
            byte[] sou2 = xxteainstance.Encrypt(source);
            byte[] res = xxteainstance.Decrypt(sou2);
            String source_str= Arrays.toString(source);
            String res_str = Arrays.toString(res);
            assertEquals(res_str,source_str);
        }
    }

    @Test
    public void classtest() throws Exception{
        class Base{
            public Base(){
                //Log.i("classtest", "Base: constructor");
                System.out.println("Base: constructor");
            }
            public void basetest(){
                System.out.println("Base: basetest");
            }
        }

        class Test extends Base{
            public Test(){
                //super();
                //Log.i("classtest", "Test: constructor");
                System.out.println("Test: constructor");
            }
            @Override
            public void basetest(){
                super.basetest();
                System.out.println("Test: basetest");

            }
        }
        //Log.i("classtest", "start ");
        //Base test1=new Test();
        //Base test2 = new Base();
        Test test3 = new Test();
        //Test test4 = new Base();
        //test.basetest();
        if (test3 instanceof Test){
            System.out.println("test instanceof Test");
        }
        if (test3 instanceof Base){
            System.out.println("test instanceof Base");
        }
    }

    @Test
    public void utilstest() throws Exception{

        String s="ab:cd:ef:12:34:56";
        String[] split=s.split(":");
        s=split.toString();
        String result=Utils.hexString2bytestring("0123456789abcedf");
        long lon = Long.valueOf("1234");
        long longtime = 1000*60*10L;
        String longstr = String.valueOf(0x12345678L);
        long key1 = 0x12345678L;
        long key2 = 0x9abcdef0L;
        long[] key4 = {key1,key1,key1,key1};
        Xxtea xxteainstance = new Xxtea();
        long[] key = xxteainstance.getKEY();
        String string = "{error_code=200}";
        byte[] source = getbytes();
        String s1 = Utils.bytes2Hex(source);
        byte[] sou2 = xxteainstance.Encrypt(source,key4);
        String s_sou = Utils.bytes2Hex(sou2);
        byte[] res = xxteainstance.Decrypt(sou2,key4);
        String s_res = Utils.bytes2Hex(res);


        Map<String, Key> keyMap = RsaUtils.createKeys(1024);
        RSAPublicKey  publicKey = (RSAPublicKey) keyMap.get("publicKey");
        RSAPrivateKey privateKey = (RSAPrivateKey)keyMap.get("privateKey");
        String privatekeystr="";
        String publickeystr="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDZsfv1qscqYdy4vY+P4e3cAtmvppXQcRvrF1cB4drkv0haU24Y7m5qYtT52Kr539RdbKKdLAM6s20lWy7+5C0DgacdwYWd/7PeCELyEipZJL07Vro7Ate8Bfjya+wltGK9+XNUIHiumUKULW4KDx21+1NLAUeJ6PeW+DAkmJWF6QIDAQAB";
        String str = "站在大明门前守卫的禁卫军，事先没有接到\n" +
                "有关的命令，但看到大批盛装的官员来临，也就\n" +
                "以为确系举行大典，因而未加询问。进大明门即\n" +
                "为皇城。文武百官看到端门午门之前气氛平静，\n" +
                "城楼上下也无朝会的迹象，既无几案，站队点名\n" +
                "的御史和御前侍卫“大汉将军”也不见踪影，不免\n" +
                "心中揣测，互相询问：所谓午朝是否讹传？";
        String teststr="0123456789abcdef";
        RSAPublicKey publickey=RsaUtils.getPublicKey(publicKey);
        byte[] encodedData = RsaUtils.publicEncrypt(teststr, publickey);
        //Log.d("RsaUtils", "utilstest: "+ new String(encodedData));
        RSAPrivateKey privatekey = RsaUtils.getPrivateKey(privateKey);
        byte[] decodedData = RsaUtils.privateDecrypt(encodedData, privatekey);
        String decodedStr = new String(decodedData);
        //Log.d("RsaUtils", "utilstest: "+decodedStr);
    }

    @Test
    public void listtest() throws Exception{
        String TAG="UnitTest";
        int[] a = {1,2,3,4,5,6,7,8,9,10};
        Log.i("UnitTest", "listtest: ");
        String s ="[\"1\",\"2\",\"3\",\"4\",\"5\"]";
        List<Integer> b = new LinkedList<>();
        b.add(1);
        b.add(2);
        b.add(3);
        System.out.println("listtest: "+b.toString());
        Log.i(TAG, "listtest: "+b.toString());
        String json = "{\"FAMILY\":[{\"FID\":\"1\",\"FNAME\":\"我的家庭\"},{\"FID\":\"2\",\"FNAME\":\"jiating\"}],\"TIME\":\"1245678\"}";
        String jsonArrStr = "{\"1000\":2,\"100\":111},{\"1000\":2,\"100\":222}";
        String list = "[{\"FID\":\"1\",\"FNAME\":\"我的家庭\"},{\"FID\":\"2\",\"FNAME\":\"jiating\"}]";
        List<Map<String,String>> mapList=SendTool.jsontolistmap(list);

        Map<String,String> map = new HashMap<String, String>();
        map.put("LIST","1");
        map.put("LIST2","2");
        //JSONObject jsonObject = jsonArray.getJSONObject(0);
        FamilyList familyList = new FamilyList();
        familyList.fromJson(list);
        JsonParser jsonParser = new JsonParser();
        String s1 = map.toString();
        String s2 = "[{\"LIST\":\"1\"}]";
        JsonObject jsonObject = (JsonObject) jsonParser.parse(json);
        JsonArray jsonArray = (JsonArray)jsonParser.parse(s2);
        //String one = jsonObject.getString("FID");
        //String two = jsonObject.getString("FNAME");
        JsonObject jsonObject1 = (JsonObject)jsonArray.get(0);
        System.out.println(jsonObject1.get("LIST"));
        //System.out.println(jsonObject.get("LIST")+" ");
    }

    @Test
    public void threadtest() throws Exception{
        final Thread start=new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()){
                    try {
                        Thread.sleep(50);
                        System.out.println("thread run");
                    }catch (InterruptedException e){
                        Thread.currentThread().interrupt();
                    }
                }

            }
        });
        start.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(5000);
                    start.interrupt();
                    System.out.println("thread interrupt");
                    Thread.sleep(5000);
                    start.start();
                    System.out.println("thread start");

                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    @Test
    public void sockettest(){
        try {
            System.out.println("socket start");
            Socket socket = new Socket(ConfigList.getServerAddress(),ConfigList.defTCPPort);
            //socket.connect();
            System.out.println("new socket");
            OutputStream outputStream = socket.getOutputStream();
            System.out.println("get outputstream");
            outputStream.write("baby".getBytes());
            Thread.sleep(1000);
            InputStream inputStream = socket.getInputStream();
            int n=inputStream.available();
            if (n>0){
                byte[] bytes = new  byte[n];
                inputStream.read(bytes);
                System.out.println(bytes);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testthread(){
        new Thread().start();
    }

    @Test
    public void mqttTest(){
        byte[] bytes = {(byte) 0x82,0x8,0x00,0x4,0x1,0x3,0x6c,0x65,0x64,0x0};
        byte[] temp = {};
        for (byte b:temp
             ) {
            switch (b){
                case 0x00:
                    break;

            }
            System.out.println(b);
        }
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.getId();
        byte[] payload = mqttMessage.getPayload();
    }

    @Test
    public void timerTest(){
        Timer timer = new Timer(true);

        System.out.println("timer ");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("time now " + new Date());
            }
        }, new Date(), 1 * 1000);
        try {
            Thread.sleep(5000);
            timer.cancel();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }

    }
}