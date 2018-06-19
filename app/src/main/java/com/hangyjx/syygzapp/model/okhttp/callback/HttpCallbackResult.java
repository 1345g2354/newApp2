package com.hangyjx.syygzapp.model.okhttp.callback;

/**
 * 自定义网络回调结果处理接口
 * Created by 杨卫宁
 * on 2016/11/7
 */
public interface HttpCallbackResult {
    public void onSuccess(String response, String requestTag);
    public void onFail(Exception e, String requestTag);
}
