package com.hangyjx.syygzapp.model.okhttp;


import com.hangyjx.syygzapp.model.okhttp.builder.PostFormBuilder;
import com.hangyjx.syygzapp.model.okhttp.callback.HttpCallbackResult;
import com.hangyjx.syygzapp.model.okhttp.callback.MyStringCallback;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;




/**
 * Created by 闫官方
 * on 2016/8/17 0017.
 * 邮箱:yanguanfang1987@163.com
 * QQ：392604061
 */
public class OkhttpHelper {

    /**
     * 网络请求
     * @param url  请求地址
     * @param requestParams 参数
     * @param requestTag  请求标识
     * @param httpCallbackResult 请求结果处理回调
     */
    public static void doRequest(String url, Map<String, Object> requestParams, String requestTag, HttpCallbackResult httpCallbackResult){
        try {
            PostFormBuilder post = OkHttpUtils.post();
            post.url(url);
            post.headers(Headers.getHeader());
            if (requestParams != null) {
                addParam(requestParams, post);
            }
            post.tag(requestTag);
            post.build().execute(new MyStringCallback(requestTag, httpCallbackResult));
        }catch (Exception e){
//            Log.e("OkhttpHelper","error:"+e.getMessage());
            if(httpCallbackResult != null){
                httpCallbackResult.onFail(e,requestTag);
            }
        }
    }

    /**
     * 网络请求 设置超时时间
     * @param url  请求地址
     * @param requestParams 参数
     * @param requestTag  请求标识
     * @param timeOut  超时时间 毫秒
     * @param httpCallbackResult 请求结果处理回调
     */
    public static void doRequestOnTimeOut(String url, Map<String, Object> requestParams, String requestTag, long timeOut, HttpCallbackResult httpCallbackResult){
        try {
            PostFormBuilder post = OkHttpUtils.post();
            post.url(url);
            post.headers(Headers.getHeader());
            if (requestParams != null) {
                addParam(requestParams, post);
            }
            post.tag(requestTag);
            post.build()
                    .connTimeOut(timeOut)
                    .writeTimeOut(timeOut)
                    .readTimeOut(timeOut)
                    .execute(new MyStringCallback(requestTag, httpCallbackResult));
        }catch (Exception e){
//            Log.e("OkhttpHelper","error:"+e.getMessage());
            if(httpCallbackResult != null){
                httpCallbackResult.onFail(e,requestTag);
            }
        }
    }

    /**
     * 传递参数处理
     * @param requestParams
     * @param post
     */
    private static void addParam(Map<String, Object> requestParams, PostFormBuilder post) {
        Set<String> keySet = requestParams.keySet();
        for (String key : keySet) {
            Object object = requestParams.get(key);
            if (object instanceof String) {
                post.addParams(key, (String) object);
                // 只包含字符串参数时默认使用BodyParamsEntity，
                // 类似于UrlEncodedFormEntity（"application/x-www-form-urlencoded"）。
//                    params.addBodyParameter(key, (String) object);
            } else if (object instanceof File) {
                // 加入文件参数后默认使用MultipartEntity（"multipart/form-data"），
                // 如需"multipart/related"，xUtils中提供的MultipartEntity支持设置subType为"related"。
                // 使用params.setBodyEntity(httpEntity)可设置更多类型的HttpEntity（如：
                // MultipartEntity,BodyParamsEntity,FileUploadEntity,InputStreamUploadEntity,StringEntity）。
                // 例如发送json参数：params.setBodyEntity(new
                // StringEntity(jsonStr,charset));

            } else if (object instanceof List) {// 如果是List<String>集合类型的 而且
                // 内部是file类型的 则调用
                try {
                    List<String> pathPci = (List<String>) object;
                    for (String path : pathPci) {
                        post.addFile(key, new File(path).getName(), new File(path));
//                            params.addBodyParameter(key, new File(path));
                    }

                } catch (Exception e) {
                    // TODO: handle exception
                }
            }else{
                post.addParams(key,  object+"");
            }
        }
    }
}
