package com.hangyjx.syygzapp.model.okhttp.callback;


import okhttp3.Call;

/**
 * 自定义网络回调
 * Created by 杨卫宁
 * on 2016/11/7
 */
public class MyStringCallback extends StringCallback{

    private String requestTag;
    private HttpCallbackResult httpCallbackResult;

    public MyStringCallback(String requestTag, HttpCallbackResult httpCallbackResult){
        this.requestTag=requestTag;
        this.httpCallbackResult=httpCallbackResult;
    }
    @Override
    public void onError(Call call, Exception e, int id) {
//        Log.e("MyStringCallback","error:"+e.getMessage());
        if(httpCallbackResult != null){
            httpCallbackResult.onFail(e,requestTag);
        }

    }

    @Override
    public void onResponse(String response, int id) {
        if(httpCallbackResult != null){
            httpCallbackResult.onSuccess(response,requestTag);
        }

    }


}
