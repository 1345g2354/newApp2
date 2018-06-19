package com.hangyjx.syygzapp.model.okhttp.callback;

/**
 * Created by 闫官方
 * on 2016/8/17 0017.
 * 邮箱:yanguanfang1987@163.com
 * QQ：392604061
 */
public interface IGenericsSerializator {
    <T> T transform(String response, Class<T> classOfT);
}
