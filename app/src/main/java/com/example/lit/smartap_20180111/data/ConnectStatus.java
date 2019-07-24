package com.example.lit.smartap_20180111.data;

import org.jetbrains.annotations.Contract;

public enum ConnectStatus {
    CONNECTING(2),
    CONNECTED(3),
    DISCONNECT(1),
    ERROR(0);

    private int value;
    ConnectStatus(int value){
        this.value = value;
    }

    @Contract(pure = true)
    int getValue(){
        return this.value;
    }

    @Contract(pure = true)
    ConnectStatus getStatus(){
        switch (value){
            case 1:
                return DISCONNECT;
            case 2:
                return CONNECTING;
            case 3:
                return CONNECTED;
            default:
                return ERROR;
        }
    }
}
