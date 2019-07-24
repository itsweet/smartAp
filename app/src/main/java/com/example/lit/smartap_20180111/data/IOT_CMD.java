package com.example.lit.smartap_20180111.data;

public enum IOT_CMD {
    //网络与安全
    Ping(0x0000),
    Beacon(0x0001),
    Reboot(0x0002),
    Default(0x0003),
    Userconfig_Reset(0x0004),
    NWK_Reset(0x0005),
    NWK_Leave(0x0006),
    NWK_Join(0x0007),
    NWK_Dissolve(0x0008),
    NWK_Status(0x009),
    Server_Addr(0x000A),
    Public_key(0x000B),
    Symmetric_key(0x000C),
    Get_verification_code(0x000D),

    //公共操作命令 0x0100
    Register_account(0x0100),
    Register_device(0x0101),
    Cancellation_account(0x0102),
    Cancellation_device(0x0103),
    Login(0x0104),
    Proxy_login(0x0105),
    Logout(0x0106),
    Proxy_logout(0x0107),
    heartbeat(0x0108),
    Proxy_heartbeat(0x0109),
    Info_get(0x010A),
    Info_set(0x010B),
    Info_del(0x10C),
    Info_change(0x010D),
    Db_info_get(0x10E),
    Db_info_set(0x10F),
    Db_info_del(0x110),
    Db_info_change(0x111),
    Log (0x0112),
    Notification(0x0113),
    Route_msg(0x0114),
    Knock_off(0x0115),

    //云端,APP 0x0200
    /*
    Comet_server_login(0x200),
    Logic_server_login(0x201),
    Database_server_login(0x202),
    Router_server_login(0x203),
    */

    Scan_gateway(0x0200),
    Scan_free_gateway(0x0201),

    Family_create(0x0210),
    Family_delete(0x0211),
    Family_search(0x0212),
    Family_rename(0x0213),
    Family_join(0x0214),
    Family_member_add(0x0215),
    Family_member_get(0x0216),
    Family_member_del(0x0217),

    Friend_search(0x220),
    Friend_request(0x221),
    Friend_accept(0x222),
    Friend_del(0x223),
    Friend_list_get(0x224),

    Offline_message_offline(0x230),
    Offline_message_send(0x231),

    //ZigBee 0x0400
    Binding(0x0400),
    Bingding_free(0x0401),

    Response_bit(0x0800)
    ;

    private int value;

    IOT_CMD(int value){
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static IOT_CMD getCMD(int value){
        switch (value){
            case 10:
                return Server_Addr;
            case 11:
                return Public_key;
            case 12:
                return Symmetric_key;
            case 13:
                return Get_verification_code;
            case 256:
                return Register_account;
            case 260:
                return Login;
            default:
                return null;
        }
    }

    public static IOT_CMD getCMD(String value){
        int num = Integer.valueOf(value);
        return getCMD(num);
    }

}
