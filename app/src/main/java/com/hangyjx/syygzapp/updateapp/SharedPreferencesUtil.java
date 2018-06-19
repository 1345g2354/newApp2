package com.hangyjx.syygzapp.updateapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.hangyjx.syygzapp.SWYApplication;

import java.io.File;



public class SharedPreferencesUtil {
    private static SharedPreferences sharedPreferences;
    public static String CONFIG = "config";
    public static String ACCOUNT = "acount";
    public static void saveStringData(Context context, String key, String value) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CONFIG,
                    Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getStringData(Context context, String key,
                                       String defValue) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CONFIG,
                    Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(key, defValue);
    }


    public static void saveLongData(Context context, String key, Long value) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CONFIG,
                    Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putLong(key, value).apply();
    }


    public static Long getLongData(Context context, String key,
                                   long defValue) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CONFIG,
                    Context.MODE_PRIVATE);
        }
        return sharedPreferences.getLong(key, defValue);
    }



    public static void deleteAccount(Context context) {//删除文件
        /** 删除SharedPreferences文件 **/
        File file = new File("/data/data/project.ygf.com.ygfprojiect" + "/shared_prefs/" + ACCOUNT + ".xml");
        if (file.exists()) {
            file.delete();
        }
        boolean commit = sharedPreferences.edit().clear().commit();
        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
    }

    public static void deleteUserInfo(Context context) {//删除文件
        /** 删除SharedPreferences文件 **/
        File file = new File("/data/data/project.ygf.com.ygfprojiect" + "/shared_prefs/" + "userinfo" + ".xml");
        if (file.exists()) {
            file.delete();
        }
        boolean commit = sharedPreferences.edit().clear().commit();
        Toast.makeText(context, "请重新登录", Toast.LENGTH_SHORT).show();
    }






    public static void saveIntData(Context context, String key, int value) {
        if (sharedPreferences == null) {
            sharedPreferences = SWYApplication.context.getSharedPreferences(CONFIG,
                    Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putInt(key, value).commit();
    }

    public static int getIntData(Context context, String key,
                                 int defValue) {
        if (sharedPreferences == null) {
            sharedPreferences = SWYApplication.context.getSharedPreferences(CONFIG,
                    Context.MODE_PRIVATE);
        }
        return sharedPreferences.getInt(key, defValue);
    }

}
