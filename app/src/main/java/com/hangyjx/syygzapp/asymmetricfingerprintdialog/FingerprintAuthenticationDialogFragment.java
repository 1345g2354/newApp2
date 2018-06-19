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

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.hangyjx.syygzapp.R;
import com.hangyjx.syygzapp.SWYApplication;
import com.hangyjx.syygzapp.asymmetricfingerprintdialog.server.StoreBackend;
import com.hangyjx.syygzapp.asymmetricfingerprintdialog.server.Transaction;
import com.hangyjx.syygzapp.model.okhttp.OkhttpHelper;
import com.hangyjx.syygzapp.model.okhttp.RequestUrl;
import com.hangyjx.syygzapp.model.okhttp.callback.HttpCallbackResult;
import com.hangyjx.syygzapp.scan.activity.DetailActivity;
import com.hangyjx.syygzapp.updateapp.DownloadService;
import com.umeng.socialize.utils.DeviceConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;


import static com.umeng.socialize.utils.DeviceConfig.context;

/**
 * A dialog which uses fingerprint APIs to authenticate the user, and falls back to password
 * authentication if fingerprint is not available.
 */
public class FingerprintAuthenticationDialogFragment extends DialogFragment
        implements TextView.OnEditorActionListener, FingerprintUiHelper.Callback, HttpCallbackResult {

    private Button mCancelButton;
    private Button mSecondDialogButton;
    private View mFingerprintContent;
    private View mBackupContent;
    private EditText mPassword;
    private CheckBox mUseFingerprintFutureCheckBox;
    private TextView mPasswordDescriptionTextView;
    private TextView mNewFingerprintEnrolledTextView;

    private Stage mStage = Stage.FINGERPRINT;

    private FingerprintManager.CryptoObject mCryptoObject;
    private FingerprintUiHelper mFingerprintUiHelper;
    private MainActivity mActivity;

     FingerprintUiHelper.FingerprintUiHelperBuilder mFingerprintUiHelperBuilder;

    InputMethodManager mInputMethodManager;

    SharedPreferences mSharedPreferences;

    StoreBackend mStoreBackend;
    private Context context;
    private EditText username;
    private TextView tv_down;


    public FingerprintAuthenticationDialogFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Do not create a new Fragment when the Activity is re-created such as orientation changes.
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
        context = getContext();
        FingerprintModule mFingerprintModule= FingerprintModule.getInstance(context) ;
        mInputMethodManager = mFingerprintModule.providesInputMethodManager(context);
        mSharedPreferences =  mFingerprintModule.providesSharedPreferences(context);
        mStoreBackend =   mFingerprintModule.providesStoreBackend();
        mFingerprintUiHelperBuilder =new FingerprintUiHelper.FingerprintUiHelperBuilder( mActivity.getSystemService(FingerprintManager.class));
        // We register a new user account here. Real apps should do this with proper UIs.
        enroll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mStage == Stage.APP_DOWN){
            getDialog().setTitle(getString(R.string.app_down));
        }else {
            getDialog().setTitle(getString(R.string.sign_in));
        }

        View v = inflater.inflate(R.layout.fingerprint_dialog_container, container, false);
        mCancelButton = (Button) v.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mSecondDialogButton = (Button) v.findViewById(R.id.second_dialog_button);
        mSecondDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStage == Stage.FINGERPRINT) {
//                    goToBackup();
                }else if(mStage == Stage.APP_DOWN){

                    UpdataAppRequset();
                    dismiss();
                } else {
//                    verifyPassword();
                    String mPasswords = mPassword.getText().toString().trim();
                    String usernames = username.getText().toString().trim();
                    if(TextUtils.isEmpty(mPasswords)){
                        Toast.makeText(getActivity(),"请输入密码",Toast.LENGTH_SHORT).show();
                    }else if(TextUtils.isEmpty(usernames)){
                        Toast.makeText(getActivity(),"请输入用户名",Toast.LENGTH_SHORT).show();
                    }else {
                        mActivity.TestRequsetEnroll(usernames,mPasswords);
                    }

                    dismiss();
                }
            }
        });

        tv_down = (TextView)v.findViewById(R.id.tv_down);
        mFingerprintContent = v.findViewById(R.id.fingerprint_container);
        mBackupContent = v.findViewById(R.id.backup_container);
        username = (EditText) v.findViewById(R.id.name);
        if( mStage == Stage.PASSWORD ){
            showSoftware();
        }

        mPassword = (EditText) v.findViewById(R.id.password);
        mPassword.setOnEditorActionListener(this);
        mPasswordDescriptionTextView = (TextView) v.findViewById(R.id.password_description);
        mUseFingerprintFutureCheckBox = (CheckBox)
                v.findViewById(R.id.use_fingerprint_in_future_check);
        mNewFingerprintEnrolledTextView = (TextView)
                v.findViewById(R.id.new_fingerprint_enrolled_description);
        mFingerprintUiHelper = mFingerprintUiHelperBuilder.build(
                (ImageView) v.findViewById(R.id.fingerprint_icon),
                (TextView) v.findViewById(R.id.fingerprint_status), this);
        updateStage();

        // If fingerprint authentication is not available, switch immediately to the backup
        // (password) screen.
        if (!mFingerprintUiHelper.isFingerprintAuthAvailable()) {
            goToBackup();
        }
        return v;
    }

    /**
     * 下载App
     * @param
     */
    public void UpdataAppRequset(){
        Map<String,Object> param=new HashMap<>();
        param.put("active","");
        param.put("msg", "");
        OkhttpHelper.doRequestOnTimeOut(RequestUrl.UPDATE_APP,param, RequestUrl.UPDATE_APP,1000*60*4,this);
    }

    @Override
    public void onSuccess(String response, String requestTag) {
        parseJson(response);
    }

    private void parseJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String code = (String)  jsonObject.opt("code");
            String message = (String)  jsonObject.opt("message");
            String dataObj =  (String) jsonObject.opt("data");
            if(dataObj  != null){
              Intent intent = new Intent(SWYApplication.context, DownloadService.class);
                intent.setAction(DownloadService.APK_UPDATE_CONTENT);
                intent.putExtra(DownloadService.APK_DOWNLOAD_URL, dataObj);
                SWYApplication.context.startService(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFail(Exception e, String requestTag) {

        Log.d("requestTag",requestTag);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mStage == Stage.FINGERPRINT) {
            mFingerprintUiHelper.startListening(mCryptoObject);
        }
    }

    public void setStage(Stage stage) {
        mStage = stage;
    }

    @Override
    public void onPause() {
        super.onPause();
        mFingerprintUiHelper.stopListening();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) getActivity();
    }

    /**
     * Sets the crypto object to be passed in when authenticating with fingerprint.
     */
    public void setCryptoObject(FingerprintManager.CryptoObject cryptoObject) {
        mCryptoObject = cryptoObject;
    }

    /**
     * Switches to backup (password) screen. This either can happen when fingerprint is not
     * available or the user chooses to use the password authentication method by pressing the
     * button. This can also happen when the user had too many fingerprint attempts.
     */
    private void goToBackup() {
        mStage = Stage.PASSWORD;
        updateStage();
        mPassword.requestFocus();
        // Show the keyboard.
        mPassword.postDelayed(mShowKeyboardRunnable, 500);

        // Fingerprint is not used anymore. Stop listening for it.
        mFingerprintUiHelper.stopListening();
    }

    public void showSoftware(){
        if(username != null){
            username.requestFocus();
            // Show the keyboard.
            username.postDelayed(mShowKeyboardRunnable, 500);
        }

    }

    /**
     * Enrolls a user to the fake backend.
     */
    private void enroll() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            PublicKey publicKey = keyStore.getCertificate(MainActivity.KEY_NAME).getPublicKey();
            // Provide the public key to the backend. In most cases, the key needs to be transmitted
            // to the backend over the network, for which Key.getEncoded provides a suitable wire
            // format (X.509 DER-encoded). The backend can then create a PublicKey instance from the
            // X.509 encoded form using KeyFactory.generatePublic. This conversion is also currently
            // needed on API Level 23 (Android M) due to a platform bug which prevents the use of
            // Android Keystore public keys when their private keys require user authentication.
            // This conversion creates a new public key which is not backed by Android Keystore and
            // thus is not affected by the bug.
            String algorithm = publicKey.getAlgorithm();
            KeyFactory factory = KeyFactory.getInstance(algorithm);
            byte[] encoded = publicKey.getEncoded();
//            String msg = new String(encoded);
//            Log.d("encoded----msg", msg);
//            byte[] bytes = msg.getBytes();
//            String s = new String(bytes);
//            Log.d("encoded----s", msg);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(encoded);
            PublicKey verificationKey = factory.generatePublic(spec);
            mStoreBackend.enroll("user", "password", verificationKey);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException |
                IOException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks whether the current entered password is correct, and dismisses the the dialog and lets
     * the activity know about the result.
     */
    private void verifyPassword() {
        Transaction transaction = new Transaction("user", 1, new SecureRandom().nextLong());
        if (!mStoreBackend.verify(transaction, mPassword.getText().toString())) {
            return;
        }
        if (mStage == Stage.NEW_FINGERPRINT_ENROLLED) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(getString(R.string.use_fingerprint_to_authenticate_key),
                    mUseFingerprintFutureCheckBox.isChecked());
            editor.apply();

            if (mUseFingerprintFutureCheckBox.isChecked()) {
                // Re-create the key so that fingerprints including new ones are validated.
                mActivity.createKeyPair();
                mStage = Stage.FINGERPRINT;
            }
        }
        mPassword.setText("");
        mActivity.onPurchased(null);
        dismiss();
    }

    private final Runnable mShowKeyboardRunnable = new Runnable() {
        @Override
        public void run() {
            mInputMethodManager.showSoftInput(username, 0);
        }
    };

    private void updateStage() {
        switch (mStage) {
            case FINGERPRINT:
                mCancelButton.setVisibility(View.GONE);
                mSecondDialogButton.setVisibility(View.GONE);
                mFingerprintContent.setVisibility(View.VISIBLE);
                mBackupContent.setVisibility(View.GONE);
                tv_down.setVisibility(View.GONE);
                break;
            case NEW_FINGERPRINT_ENROLLED:
                // Intentional fall through
            case PASSWORD:
                mCancelButton.setText(R.string.cancel);
                mSecondDialogButton.setText(R.string.ok);
                mFingerprintContent.setVisibility(View.GONE);
                mBackupContent.setVisibility(View.VISIBLE);
                tv_down.setVisibility(View.GONE);
                break;
            case APP_DOWN:
                mCancelButton.setText(R.string.cancel);
                mSecondDialogButton.setText(R.string.ok);
                mFingerprintContent.setVisibility(View.GONE);
                mBackupContent.setVisibility(View.GONE);
                tv_down.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            verifyPassword();
            return true;
        }
        return false;
    }

    @Override
    public void onAuthenticated() {
        // Callback from FingerprintUiHelper. Let the activity know that authentication was
        // successful.
        mPassword.setText("");
        Signature signature = mCryptoObject.getSignature();
        // Include a client nonce in the transaction so that the nonce is also signed by the private
        // key and the backend can verify that the same nonce can't be used to prevent replay
        // attacks.
        String fingerprint = DeviceUtils.getFingerprintInfo(getActivity());

        Transaction transaction = new Transaction("user", 1, new SecureRandom().nextLong());
        try {
            signature.update(fingerprint.getBytes());
            byte[] sigBytes = signature.sign();
//            if (mStoreBackend.verify(transaction, sigBytes)) {
                mActivity.onPurchased(sigBytes);
                dismiss();
//            } else {
//                mActivity.onPurchaseFailed();
//                dismiss();
//            }
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onError() {
        goToBackup();
    }



    /**
     * Enumeration to indicate which authentication method the user is trying to authenticate with.
     */
    public enum Stage {
        FINGERPRINT,
        NEW_FINGERPRINT_ENROLLED,
        PASSWORD,
        APP_DOWN
    }
}
