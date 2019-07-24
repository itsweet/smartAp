package com.example.lit.smartap_20180111;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lit.smartap_20180111.data.ConnectStatus;
import com.example.lit.smartap_20180111.data.DataMQTT;
import com.example.lit.smartap_20180111.data.IOT_CMD;
import com.example.lit.smartap_20180111.data.MqttSenderListener;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MqttSender  {
    private MqttConnectOptions options;
    private MqttClient client;
    private MqttCallback mqttCallback;
    private MqttSenderListener<DataMQTT> callback;
    static String TAG = "MqttSender";
    private ConnectStatus connectStatus = ConnectStatus.DISCONNECT;
    private Timer  mqttSenderTimer;
    private ConcurrentLinkedQueue<DataMQTT> sendList = new ConcurrentLinkedQueue<>();
    IOT_CMD nowcmd = IOT_CMD.Ping;
    MqttSender(){
        options = new MqttConnectOptions();
        options.setUserName("2019040301");
        options.setPassword("123456".toCharArray());
        options.setAutomaticReconnect(false);
        options.setCleanSession(false);
        options.setConnectionTimeout(10);
        //options.setKeepAliveInterval(2*60);
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        mqttCallback = new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                //Log.i(TAG, "connectComplete:  status change connected");
                setConnectStatus(ConnectStatus.CONNECTED);

            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.e(TAG, "connectionLost: "+cause,null);
                setConnectStatus(ConnectStatus.DISCONNECT);
                if (nowcmd.getValue() > IOT_CMD.Server_Addr.getValue() && callback != null){
                    callback.onFail(1,cause.getMessage());
                }

                //cause.getMessage();
                //callback.onFail(1,"connection lost",cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                DataMQTT dataMQTT = new DataMQTT(message,topic);
                IOT_CMD cmd = dataMQTT.getCmd();
                Log.i(TAG, "messageArrived: topic="+dataMQTT.getTopic()+" MID="+dataMQTT.getMID()+
                        " message="+dataMQTT.getMqttMessage().toString());
                for (DataMQTT data:sendList
                     ) {
                    if(data.getCmd() != cmd){
                        continue;
                    }
                    data.setACK(true);
                    if (callback!=null) {
                        callback.onSuccess(dataMQTT);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        };
        mqttSenderTimer_start();
        //conncetTimer = new Timer();
        //conncetTimer.scheduleAtFixedRate(connectTimerTask, new Date(),1000);
        //connect();
    }

    synchronized public void setNowcmd(IOT_CMD nowcmd) {
        this.nowcmd = nowcmd;
    }

    public IOT_CMD getNowcmd() {
        return nowcmd;
    }

    synchronized public void setConnectStatus(ConnectStatus connectStatus) {
        this.connectStatus = connectStatus;
    }

    void syncStatusTask(){
        switch (connectStatus){
            case DISCONNECT:
                callback.statusChange(ConnectStatus.DISCONNECT);
                break;
            case CONNECTING:
                callback.statusChange(ConnectStatus.CONNECTING);
                break;
            case CONNECTED:
                callback.statusChange(ConnectStatus.CONNECTED);
                break;
        }
    }

    void sendTask(){
        for (DataMQTT structure:sendList
        ) {
            if (structure.getACK()){
                sendList.remove(structure);
                //Log.i(TAG, "mqttSenderTask: remove "+structure.getMID());
                continue;
            }
            //setNowcmd(structure.getCmd());
            try {
                if (!structure.edu_time()){
                    Log.e(TAG, "send: out of time "+structure.getTime(), null);
                    sendList.remove(structure);
                    if (callback != null){
                        callback.onFail(1,"send out of time 3");
                    }
                    continue;
                }
                String topic = structure.getTopic();
                MqttMessage message = structure.getMqttMessage();
                client.publish(topic, message);
                Log.i(TAG, "send: topic="+topic+";QOS="+message.getQos()+";MID="
                        +message.getId()+";message="+message.toString());
            }catch (MqttException emqtt){
                emqtt.printStackTrace();
            }
        }
    }

    TimerTask mqttSenderConnectTask = new TimerTask() {
        @Override
        public void run() {
            syncStatusTask();
        }
    };

    TimerTask mqttSenderTimerTask = new TimerTask() {
        @Override
        public void run() {

            syncStatusTask();

            sendTask();

        }
    };

    void mqttSenderTimer_start(){
        mqttSenderTimer_start(0);
    }

    void mqttSenderTimer_start(int delay){
        mqttSenderTimer = new Timer();
        if (delay > 1000) {
            mqttSenderTimer.scheduleAtFixedRate(mqttSenderTimerTask, delay, 1000);
            //mqttSenderTimer.scheduleAtFixedRate(mqttSenderConnectTask,delay,3000);
        }else {
            mqttSenderTimer.scheduleAtFixedRate(mqttSenderTimerTask,1000,1000);
            //mqttSenderTimer.scheduleAtFixedRate(mqttSenderConnectTask,1000,3000);
        }
    }

    void mqttSenderTimer_stop(){
        mqttSenderTimerTask.cancel();
        mqttSenderTimer.cancel();
    }

    public void setCallback(MqttSenderListener callback) {
        this.callback = callback;
    }

    void unsetCallback(){
        this.callback = null;
    }


    void connect_re(){
        if (nowcmd.getValue() > IOT_CMD.Server_Addr.getValue()) {
            connect_change(ConfigList.getServerAddress().toString(), ConfigList.TcpPort,
                    ConfigList.getUsername(), null, null);
        }else {
            connect_change(ConfigList.defServerAddress.toString(),ConfigList.defMqttPort,
                    ConfigList.getUsername(),null,null);
        }
    }

    void connect(String uri,String id){
        try {

            //协议规定id 后加M mobile
            client = new MqttClient(uri,id+"M",null);
            client.setCallback(mqttCallback);
            setConnectStatus(ConnectStatus.CONNECTING);
            client.connect(options);
        }catch (MqttException e){
            setConnectStatus(ConnectStatus.DISCONNECT);
            if (callback != null) {
                callback.onFail(0, "连接失败");
            }else {
                Log.e(TAG, "connect : callback is null");
            }
            e.printStackTrace();
        }
    }

    void disconnect(){
        try {
            client.disconnect();
            connectStatus = ConnectStatus.DISCONNECT;
        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    boolean isconnect(){
        if (client != null) {
            return client.isConnected();
        }else {
            return false;
        }
    }

    void connect_change(String ipaddress,int port , String id,@Nullable String username,@Nullable String password){
        if (ipaddress == "" || ipaddress ==null ){
            Log.e(TAG, "connect_change: ip address is null",null );
            return;
        }
        String uri = "tcp:/"+ipaddress+":"+port;
        if(username != null) {
            options.setUserName(username);
        }
        if (password != null){
            options.setPassword(password.toCharArray());
        }
        try {
            if (isconnect()){
                //client.disconnect();
                disconnect();
            }
            connect(uri,id);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void send(final String topic,final MqttMessage message){
        this.send(new DataMQTT(message,topic));
    }


    void send(final DataMQTT structure){
        final String topic = structure.getTopic();
        final MqttMessage message = structure.getMqttMessage();
        if (sendList.contains(structure)) {
            return;
        }
        if (structure.getCmd() == IOT_CMD.Server_Addr) {
            for (DataMQTT dataMQTT : sendList
            ) {
                if (dataMQTT.getCmd() == IOT_CMD.Server_Addr){
                    return;
                }
            }
        }
        sendList.add(structure);

        //Log.i(TAG, "send: add " +structure.getMID());
    }

}
