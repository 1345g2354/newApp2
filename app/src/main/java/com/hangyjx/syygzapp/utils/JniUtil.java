package com.hangyjx.syygzapp.utils;

import android.content.Context;

/**
 * Created by a on 2018/6/1.
 * XuYongChao
 */

public class JniUtil {
    private static JniUtil mJniUtil;

    public static JniUtil getInstance(){

        if (mJniUtil == null){
            mJniUtil = new JniUtil();
        }

        return mJniUtil;
    }


    public native String getSecretCode(Context context,int position);

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
