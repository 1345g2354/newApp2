package com.hangyjx.syygzapp.model.okhttp.builder;


import com.hangyjx.syygzapp.model.okhttp.OkHttpUtils;
import com.hangyjx.syygzapp.model.okhttp.request.OtherRequest;
import com.hangyjx.syygzapp.model.okhttp.request.RequestCall;

/**
 * Created by 闫官方
 * on 2016/8/17 0017.
 * 邮箱:yanguanfang1987@163.com
 * QQ：392604061
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
