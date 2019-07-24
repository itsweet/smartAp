package com.example.lit.smartap_20180111.data;

public interface MqttSenderListener<T> extends BaseResultCallback<T>{
    void statusChange(ConnectStatus status);
}
