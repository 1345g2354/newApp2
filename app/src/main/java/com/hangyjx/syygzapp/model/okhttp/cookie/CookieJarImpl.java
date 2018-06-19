package com.hangyjx.syygzapp.model.okhttp.cookie;


import com.hangyjx.syygzapp.model.okhttp.cookie.store.CookieStore;
import com.hangyjx.syygzapp.model.okhttp.utils.Exceptions;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;


/**
 * Created by 闫官方
 * on 2016/8/17 0017.
 * 邮箱:yanguanfang1987@163.com
 * QQ：392604061
 */
public class CookieJarImpl implements CookieJar
{
    private CookieStore cookieStore;

    public CookieJarImpl(CookieStore cookieStore)
    {
        if (cookieStore == null) Exceptions.illegalArgument("cookieStore can not be null.");
        this.cookieStore = cookieStore;
    }

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies)
    {
        cookieStore.add(url, cookies);
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url)
    {
        return cookieStore.get(url);
    }

    public CookieStore getCookieStore()
    {
        return cookieStore;
    }
}
