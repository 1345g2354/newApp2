package com.hangyjx.syygzapp.model.okhttp.builder;

import java.util.Map;

/**
 * Created by 闫官方
 * on 2016/8/17 0017.
 * 邮箱:yanguanfang1987@163.com
 * QQ：392604061
 */
public interface HasParamsable
{
    OkHttpRequestBuilder params(Map<String, String> params);
    OkHttpRequestBuilder addParams(String key, String val);
}
