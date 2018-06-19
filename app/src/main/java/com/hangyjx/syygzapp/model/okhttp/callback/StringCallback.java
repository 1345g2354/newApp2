package com.hangyjx.syygzapp.model.okhttp.callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by 闫官方
 * on 2016/8/17 0017.
 * 邮箱:yanguanfang1987@163.com
 * QQ：392604061
 */
public abstract class StringCallback extends Callback<String>
{
    @Override
    public String parseNetworkResponse(Response response, int id) throws IOException
    {
        return response.body().string();
    }

    @Override
    public void inProgress(float progress, long total, int id) {
    }
}
