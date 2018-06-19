package com.hangyjx.syygzapp.model.okhttp;


import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.HashMap;
import java.util.Map;



/**
 * Created by Administrator on 2016/4/8 0008.
 */
public class Headers {
    public static Map<String,String> getHeader(){
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Charset", "UTF-8");
        headers.put("Connection", "close");
        headers.put("ajax", "true");
        headers.put("Content-Type", "application/x-javascript");
         //***获取手机串号
//        TelephonyManager telephonemanage = (TelephonyManager) MyApplication.context.getSystemService(Context.TELEPHONY_SERVICE);
        //手机串号
//        String mime = telephonemanage.getDeviceId();
//        String access_token = SharedPreferencesUtil.getStringData(MyApplication.context, "ACCESS_TOKEN", "");
//        headers.put("accessToken", access_token);//access_token
//        headers.put("clientId","android"+mime);//设备串号
//        headers.put("appversion", AppInfoUtils.getVersionName(MyApplication.context));//版本号
        headers.put("duiapp","1");//终端

        return headers;
    }
}
