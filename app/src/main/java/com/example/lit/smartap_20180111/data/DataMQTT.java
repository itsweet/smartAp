package com.example.lit.smartap_20180111.data;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class DataMQTT extends DataObject {
    IOT_CMD cmd;
    String topic;
    DataType dataType;
    MqttMessage mqttMessage;
    public enum DataType{
        MQTT,
        COAP
    }

    public DataMQTT(byte[] bytes){
        super(bytes,null);
        this.dataType = DataType.COAP;

    }

    public IOT_CMD getCmd() {
        return cmd;
    }

    public String getTopic(){
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public MqttMessage getMqttMessage() {
        return mqttMessage;
    }
    @Override
    public void setPayload(byte[] bytes){
        this.Payload=bytes;
        mqttMessage.setPayload(bytes);
    }

    public DataMQTT(MqttMessage message, String topic){
        this.dataType = DataType.MQTT;
        this.mqttMessage = message;
        this.MID = message.getId();
        String[] cmdlist = topic.split("/");
        String cmdstring = cmdlist[cmdlist.length -1];
        int cmdnum = Integer.valueOf(cmdstring);
        if (cmdnum < 0x8000){
            this.cmd = IOT_CMD.getCMD(cmdstring);
        }else {
            this.setACK(true);
            this.cmd = IOT_CMD.getCMD(cmdnum - 0x8000);
        }
        this.topic = topic;
        this.Payload = message.getPayload();
    }


}
