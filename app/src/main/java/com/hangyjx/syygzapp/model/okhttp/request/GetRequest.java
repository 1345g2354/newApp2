package com.hangyjx.syygzapp.model.okhttp.request;

import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by 闫官方
 * on 2016/8/17 0017.
 * 邮箱:yanguanfang1987@163.com
 * QQ：392604061
 */
public class GetRequest extends OkHttpRequest
{
    public GetRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, int id)
    {
        super(url, tag, params, headers,id);
    }

    @Override
    protected RequestBody buildRequestBody()
    {
        return null;
    }

    @Override
    protected Request buildRequest(RequestBody requestBody)
    {
        return builder.get().build();
    }


}
