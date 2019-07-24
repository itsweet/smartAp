package com.example.lit.smartap_20180111.data;

/**
 * Created by ws on 2018/3/21.
 */

public interface BaseResultCallback<D> {
    /**
     * 收到回复
     *
     * @param data 回传的数据
     */
    void onSuccess(D data);


    /**
     * 操作失败
     *
     * @param status    错误码
     * @param data      返回的异常信息
     */
    void onFail(int status,String data);
}
