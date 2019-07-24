package com.example.lit.smartap_20180111.data;

/**
 * 自定义code 部分
 * 【200】 成功
 * 【400】 参数不足
 * 【401】 参数错误
 * 【402】 源设备类型错误
 * 【403】 账号已存在
 * 【404】 账号不存在
 * 【405】 密码或账号错误
 * 【406】 IMEI错误
 * 【407】 账号未登陆或已离线
 * 【408】 对称加密秘钥错误
 * 【409】 服务器内部错误
 * 【501】 服务器不开放
 */
public enum Code {

    success(200),
    deficiency(400),
    code_error(401),
    class_error(402),
    account_exists(403),
    account_noexists(404),
    account_verify_error(405),
    imei_error(406),
    offline(407),
    xxtea_error(408),
    server_error(409),
    server_closing(501);


    int value;
    Code(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Code valueOf(int value){
        switch (value){
            case 200 : return success;
            case 400 : return deficiency;
            case 401 : return code_error;
            case 402 : return class_error;
            case 403 : return account_exists;
            case 404 : return account_noexists;
            case 405 : return account_verify_error;
            case 406 : return imei_error;
            case 407 : return offline;
            case 408 : return xxtea_error;
            case 409 : return server_error;
            case 501 : return server_closing;
            default: throw new IllegalArgumentException("Unknown Code:"+value);
        }
    }
}
