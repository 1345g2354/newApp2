package com.hangyjx.syygzapp.asymmetricfingerprintdialog;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by a on 2018/5/23.
 * XuYongChao
 */

public class DeviceUtils {
    private static String mFingerprintInfo;

    public static String getUniqueId(Context context){
        String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String id = androidID + Build.SERIAL;
        try {
            return toMD5(id);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return id;
        }
    }


    public static String toMD5(String text) throws NoSuchAlgorithmException {
        //获取摘要器 MessageDigest
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        //通过摘要器对字符串的二进制字节数组进行hash计算
        byte[] digest = messageDigest.digest(text.getBytes());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            //循环每个字符 将计算结果转化为正整数;
            int digestInt = digest[i] & 0xff;
            //将10进制转化为较短的16进制
            String hexString = Integer.toHexString(digestInt);
            //转化结果如果是个位数会省略0,因此判断并补0
            if (hexString.length() < 2) {
                sb.append(0);
            }
            //将循环结果添加到缓冲区
            sb.append(hexString);
        }
        //返回整个结果
        return sb.toString();
    }

    public static String getFingerprintInfo(Context context) {

        if(!TextUtils.isEmpty(mFingerprintInfo)){
            return mFingerprintInfo;
        }
        try {
            Log.d(TAG, "getUniqueId:--- " +  DeviceUtils.getUniqueId(context)+"");

            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            Method method = FingerprintManager.class.getDeclaredMethod("getEnrolledFingerprints");
            Object obj = method.invoke(fingerprintManager);
            String fingerId = "";
            if (obj != null) {
                Class<?> clazz = Class.forName("android.hardware.fingerprint.Fingerprint");
                Method getFingerId = clazz.getDeclaredMethod("getFingerId");
                Method getDeviceId = clazz.getDeclaredMethod("getDeviceId");
                for (int i = 0; i < ((List) obj).size(); i++) {
                    Object item = ((List) obj).get(i);
                    if (null == item) {
                        continue;
                    }
                    fingerId =getFingerId.invoke(item)+"";
                    Log.d(TAG, "fingerId:--- " + getFingerId.invoke(item));
                    break;
                }
            }else {
                return "";
            }
            String deviceId =  DeviceUtils.getUniqueId(context);
            try {
                 mFingerprintInfo = DeviceUtils.toMD5(deviceId + fingerId);
                return mFingerprintInfo;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return  "";
    }

}
