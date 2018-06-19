/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.hangyjx.syygzapp.asymmetricfingerprintdialog;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.hangyjx.syygzapp.R;
import com.hangyjx.syygzapp.SWYApplication;
import com.hangyjx.syygzapp.asymmetricfingerprintdialog.cipher.SM4Utils;
import com.hangyjx.syygzapp.fragment.ScanFragment;
import com.hangyjx.syygzapp.model.okhttp.OkhttpHelper;
import com.hangyjx.syygzapp.model.okhttp.RequestUrl;
import com.hangyjx.syygzapp.model.okhttp.callback.HttpCallbackResult;
import com.hangyjx.syygzapp.scan.activity.DetailActivity;
import com.hangyjx.syygzapp.updateapp.SharedPreferencesUtil;
import com.hangyjx.syygzapp.utils.JniUtil;
import com.hangyjx.syygzapp.zxing.ui.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.ECGenParameterSpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static android.content.ContentValues.TAG;

/**
 * Main entry point for the sample, showing a backpack and "Purchase" button.
 */
public class MainActivity extends Activity implements HttpCallbackResult {

    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    /** Alias for our key in the Android Key Store */
    public static final String KEY_NAME = "my_key";


    KeyguardManager mKeyguardManager;

    FingerprintManager mFingerprintManager;
     FingerprintAuthenticationDialogFragment mFragment;

    KeyStore mKeyStore;

    KeyPairGenerator mKeyPairGenerator;

    Signature mSignature;

    SharedPreferences mSharedPreferences;
    private byte[] encoded;
    private byte[] signature;
    private int secret_position =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((SWYApplication) getApplication()).inject(this);

        setContentView(R.layout.activity_main2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        ImmersiveMode(0);
        fitWindowView();
        FingerprintModule mFingerprintModule= FingerprintModule.getInstance(this) ;
        mKeyguardManager = mFingerprintModule.providesKeyguardManager(this);
        mFingerprintManager = mFingerprintModule.providesFingerprintManager(this);
        mFragment = new FingerprintAuthenticationDialogFragment();
        mKeyStore = mFingerprintModule.providesKeystore();
        mKeyPairGenerator = mFingerprintModule.providesKeyPairGenerator();
        mSignature = mFingerprintModule.providesSignature();
        mSharedPreferences = mFingerprintModule.providesSharedPreferences(this);


        int postion_secret = SharedPreferencesUtil.getIntData(this, "postion_secret", secret_position);
        if(postion_secret >0){
            this.secret_position = postion_secret;
        }
//        Toast.makeText(this,postion_secret,Toast.LENGTH_SHORT).show();
        ImageView purchaseButton = (ImageView) findViewById(R.id.iv_scan);
        if (!mKeyguardManager.isKeyguardSecure()) {
            // Show a message that the user hasn't set up a fingerprint or lock screen.
            Toast.makeText(this,
                    "没有设置屏幕锁屏功能.\n"
                            + "前往 '设置 -> 安全 -> 指纹' 然后请至少录入一个指纹",
                    Toast.LENGTH_LONG).show();
            purchaseButton.setEnabled(false);
            return;
        }
        //noinspection ResourceType
        if (!mFingerprintManager.hasEnrolledFingerprints()) {
            purchaseButton.setEnabled(false);
            // This happens when no fingerprints are registered.
            Toast.makeText(this,
                    "前往 '设置 -> 安全 -> 指纹' 然后请至少录入一个指纹",
                    Toast.LENGTH_LONG).show();
            return;
        }
        createKeyPair();
        purchaseButton.setEnabled(true);
        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set up the crypto object for later. The object will be authenticated by use
                // of the fingerprint.
                if (initSignature()) {

                    // Show the fingerprint dialog. The user has the option to use the fingerprint with
                    // crypto, or you can fall back to using a server-side verified password.
                    mFragment.setCryptoObject(new FingerprintManager.CryptoObject(mSignature));
                    boolean useFingerprintPreference = mSharedPreferences
                            .getBoolean(getString(R.string.use_fingerprint_to_authenticate_key),
                                    true);
                    if (useFingerprintPreference) {
                        mFragment.setStage(
                                FingerprintAuthenticationDialogFragment.Stage.FINGERPRINT);
                    } else {
                        mFragment.setStage(
                                FingerprintAuthenticationDialogFragment.Stage.PASSWORD);
                    }
                    mFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
                } else {
                    // This happens if the lock screen has been disabled or or a fingerprint got
                    // enrolled. Thus show the dialog to authenticate with their password first
                    // and ask the user if they want to authenticate with fingerprints in the
                    // future
                    mFragment.setStage(
                            FingerprintAuthenticationDialogFragment.Stage.NEW_FINGERPRINT_ENROLLED);
                    mFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
                }
            }
        });

        findViewById(R.id.img_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                findViewById(R.id.encrypted_message).setVisibility(View.GONE);
                mFragment.setStage(
                        FingerprintAuthenticationDialogFragment.Stage.PASSWORD);
                mFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
//                Intent intent = new Intent(getApplication(), DetailActivity.class);
//                   startActivity(intent);

            }
        });
        findViewById(R.id.img_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog(FingerprintAuthenticationDialogFragment.Stage.APP_DOWN);
//                Intent intent = new Intent(getApplication(), DetailActivity.class);
//                   startActivity(intent);

            }
        });
//        getSignInfo();
        String fingerprint = DeviceUtils.getFingerprintInfo(this);
    }
    public void getSignInfo() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            android.content.pm.Signature[] signs = packageInfo.signatures;
            android.content.pm.Signature sign = signs[0];
            System.out.println("==MySign="+sign.toCharsString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showDialog(FingerprintAuthenticationDialogFragment.Stage appDown) {
        findViewById(R.id.encrypted_message).setVisibility(View.GONE);
        mFragment.setStage(
                appDown);
        mFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
    }

    private String SM4func(String fingerPrint) {
        String plainText = fingerPrint;
        SM4Utils sm4 = new SM4Utils();

        secret_position = SharedPreferencesUtil.getIntData(this,"postion_secret",secret_position);

        String secretCode = JniUtil.getInstance().getSecretCode(this,secret_position);
        if(secretCode == "downnew"){
            return "";
        }
        SharedPreferencesUtil.saveIntData(this,"postion_secret",++secret_position);
//        Toast.makeText(this,secretCode,Toast.LENGTH_SHORT).show();
        sm4.setSecretKey(secretCode);
        sm4.setHexString(false);
        String cipherText = sm4.encryptData_ECB(plainText);
        return cipherText;
    }


    /**
     * 用户采集指纹信息
     */
    public void TestRequsetEnroll(String username,String passwords){
        Map<String,Object> param=new HashMap<>();
        param.put("client_id",username);
        try {
            String dy =  DeviceUtils.toMD5(passwords);//TestFingerPrint
            String password =  DeviceUtils.toMD5(passwords+dy);
            param.put("client_secret",password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String fingerprint =   DeviceUtils.getFingerprintInfo(this);
        if(fingerprint.isEmpty()){
            Toast.makeText(this,"请录入指纹信息",Toast.LENGTH_SHORT).show();
            return;
        }
        param.put("fingerprint",fingerprint);
        OkhttpHelper.doRequest(RequestUrl.LOGIN_URL,param, RequestUrl.LOGIN_URL,this);
    }

    /**
     * 登录
     * @param ordertext
     */
    public void TestRequset(String ordertext, byte[] sigBytes,byte[] publickey){
        Map<String,Object> param=new HashMap<>();
        param.put("active",ordertext+"");
        String fingerprint = DeviceUtils.getFingerprintInfo(this);
        if(TextUtils.isEmpty(fingerprint)){
            Toast.makeText(this,"请录入指纹信息",Toast.LENGTH_SHORT).show();
        }
        String value1 = SM4func(fingerprint);
        if(TextUtils.isEmpty(value1)){
            showDialog(FingerprintAuthenticationDialogFragment.Stage.APP_DOWN);
            return;
        }
        param.put("fingerprint", value1);
        String value = Base64.encodeToString(sigBytes, 0);
        param.put("msg", value);
        param.put("publickey",Base64.encodeToString(publickey,1));
        param.put("position",secret_position-1);
        OkhttpHelper.doRequest(RequestUrl.LOGIN_SIGN,param, RequestUrl.LOGIN_SIGN,this);
    }





    /**
     * Initialize the {@link Signature} instance with the created key in the
     * {@link #createKeyPair()} method.
     *
     * @return {@code true} if initialization is successful, {@code false} if the lock screen has
     * been disabled or reset after the key was generated, or if a fingerprint got enrolled after
     * the key was generated.
     */
    private boolean initSignature() {
        try {
            mKeyStore.load(null);
            PrivateKey key = (PrivateKey) mKeyStore.getKey(KEY_NAME, null);
            mSignature.initSign(key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    public void onPurchased(byte[] signatures) {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            PublicKey publicKey = keyStore.getCertificate(MainActivity.KEY_NAME).getPublicKey();
            encoded = publicKey.getEncoded();
            signature = signatures;
            goScanner();

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        showConfirmation(signature);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                //取消自定义
                return;
            }
        }
        if (requestCode == 1) {
            Intent intent = new Intent(this, CaptureActivity.class);
            startActivityForResult(intent,997);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 997:
                if(data != null){
                    String text =   data.getStringExtra("data");
                    TestRequset(text,signature,encoded);
//                  Toast.makeText(this/,text+"",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void goScanner() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            Intent intent = new Intent(this, CaptureActivity.class);
            startActivityForResult(intent,997);
        }
    }

    public void onPurchaseFailed() {
        Toast.makeText(this, R.string.purchase_fail, Toast.LENGTH_SHORT).show();
    }

    // Show confirmation, if fingerprint was used show crypto information.
    private void showConfirmation(byte[] encrypted) {
        if (encrypted != null) {
            TextView v = (TextView) findViewById(R.id.encrypted_message);
            v.setVisibility(View.VISIBLE);
            v.setText(Base64.encodeToString(encrypted, 0 /* flags */));
        }
    }

    /**
     * Generates an asymmetric key pair in the Android Keystore. Every use of the private key must
     * be authorized by the user authenticating with fingerprint. Public key use is unrestricted.
     */
    public void createKeyPair() {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder
            mKeyPairGenerator.initialize(
                    new KeyGenParameterSpec.Builder(KEY_NAME,
                            KeyProperties.PURPOSE_SIGN)
                            .setDigests(KeyProperties.DIGEST_SHA256)
                            .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1"))
                            // Require the user to authenticate with a fingerprint to authorize
                            // every use of the private key
                            .setUserAuthenticationRequired(true)
                            .build());
            mKeyPairGenerator.generateKeyPair();
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.action_settings) {
//            Intent intent = new Intent(this, SettingsActivity.class);
//            startActivity(intent);
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(String response, String requestTag) {

        if(requestTag == RequestUrl.LOGIN_SIGN){
            parseJson(response);
        } else if (requestTag == RequestUrl.LOGIN_URL) {
            parseJson(response);
        }

    }

    private void parseJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String code = (String)  jsonObject.opt("code");
            String message = (String)  jsonObject.opt("message");
            Toast.makeText(this,message,Toast.LENGTH_LONG).show();
            JSONObject dataObj =  (JSONObject) jsonObject.opt("data");
            if(dataObj  != null){
//                JSONObject dataObj = new JSONObject(data);
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra("access_token",(String)  dataObj.opt("access_token"));
                intent.putExtra("industry_name",(String)  dataObj.opt("industry_name"));
                intent.putExtra("industry_id",(String)  dataObj.opt("industry_id"));
                intent.putExtra("role_id",(String)  dataObj.opt("role_id"));
                intent.putExtra("role_type",(String)  dataObj.opt("role_type"));
                intent.putExtra("adminflag",(String)  dataObj.opt("adminflag"));
                intent.putExtra("enttype",(String)  dataObj.opt("enttype"));
                intent.putExtra("ent_id",(String)  dataObj.opt("ent_id"));
                intent.putExtra("validity",(String)  dataObj.opt("validity"));
                intent.putExtra("ent_name",(String)  dataObj.opt("ent_name"));
                intent.putExtra("client_name",(String)  dataObj.opt("client_name"));
                intent.putExtra("publickey",Base64.encodeToString(encoded,1));
                intent.putExtra("sigBytes",Base64.encodeToString(signature,0));
                startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFail(Exception e, String requestTag) {
        Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
    }

    protected void ImmersiveMode(int flag) {

        if (Build.VERSION.SDK_INT >= 19) {
            Window window = getWindow();
            ViewGroup decorView = (ViewGroup)window.getDecorView();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            View statusBarView = new View(window.getContext());
            int statusBarHeight = getStatusBarHeight(window.getContext());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
            params.gravity = Gravity.TOP;
            statusBarView.setLayoutParams(params);

             statusBarView.setBackgroundColor(Color.parseColor("#69AF33"));


            decorView.addView(statusBarView);
        }
    }
    public  int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }
    protected void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    protected   void fitWindowView() {
        ViewGroup mContentView = (ViewGroup) getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
            mChildView.setFitsSystemWindows(true);
        }
    }
}
