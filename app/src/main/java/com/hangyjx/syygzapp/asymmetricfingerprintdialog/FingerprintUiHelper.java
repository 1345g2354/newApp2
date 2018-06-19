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

import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.annotation.VisibleForTesting;
import android.widget.ImageView;
import android.widget.TextView;


import com.hangyjx.syygzapp.R;


/**
 * Small helper class to manage text/icon around fingerprint authentication UI.
 * This class assumes that the {@link android.Manifest.permission#USE_FINGERPRINT}
 * permission has already been granted. (As of API 23 this permission is normal instead of dangerous
 * and is granted at install time.)
 */
@SuppressWarnings("MissingPermission")
public class FingerprintUiHelper extends FingerprintManager.AuthenticationCallback {

    @VisibleForTesting
    static final long ERROR_TIMEOUT_MILLIS = 1600;
    @VisibleForTesting static final long SUCCESS_DELAY_MILLIS = 1300;

    private final FingerprintManager mFingerprintManager;
    private final ImageView mIcon;
    private final TextView mErrorTextView;
    private final Callback mCallback;
    private CancellationSignal mCancellationSignal;

    @VisibleForTesting boolean mSelfCancelled;

    /**
     * Builder class for {@link FingerprintUiHelper} in which injected fields from Dagger
     * holds its fields and takes other arguments in the {@link #build} method.
     */
    public static class FingerprintUiHelperBuilder {
        private final FingerprintManager mFingerPrintManager;


        public FingerprintUiHelperBuilder(FingerprintManager fingerprintManager) {
            mFingerPrintManager = fingerprintManager;
        }

        public FingerprintUiHelper build(ImageView icon, TextView errorTextView, Callback callback) {
            return new FingerprintUiHelper(mFingerPrintManager, icon, errorTextView,
                    callback);
        }
    }

    /**
     * Constructor for {@link FingerprintUiHelper}. This method is expected to be called from
     * only the {@link FingerprintUiHelperBuilder} class.
     */
    private FingerprintUiHelper(FingerprintManager fingerprintManager,
                                ImageView icon, TextView errorTextView, Callback callback) {
        mFingerprintManager = fingerprintManager;
        mIcon = icon;
        mErrorTextView = errorTextView;
        mCallback = callback;
    }

    public boolean isFingerprintAuthAvailable() {
        return mFingerprintManager.isHardwareDetected()
                && mFingerprintManager.hasEnrolledFingerprints();
    }

    public void startListening(FingerprintManager.CryptoObject cryptoObject) {
        if (!isFingerprintAuthAvailable()) {
            return;
        }
        mCancellationSignal = new CancellationSignal();
        mSelfCancelled = false;
        mFingerprintManager
                .authenticate(cryptoObject, mCancellationSignal, 0 /* flags */, this, null);
        mIcon.setImageResource(R.drawable.ic_fp_40px);
    }

    public void stopListening() {
        if (mCancellationSignal != null) {
            mSelfCancelled = true;
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        if (!mSelfCancelled) {
            showError(errString);
            mIcon.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCallback.onError();
                }
            }, ERROR_TIMEOUT_MILLIS);
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        showError(helpString);
    }

    @Override
    public void onAuthenticationFailed() {
        showError(mIcon.getResources().getString(
                R.string.fingerprint_not_recognized));
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        mErrorTextView.removeCallbacks(mResetErrorTextRunnable);
        mIcon.setImageResource(R.drawable.ic_fingerprint_success);
        mErrorTextView.setTextColor(
                mErrorTextView.getResources().getColor(R.color.success_color, null));
        mErrorTextView.setText(
                mErrorTextView.getResources().getString(R.string.fingerprint_success));

        mIcon.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCallback.onAuthenticated();
            }
        }, SUCCESS_DELAY_MILLIS);
//        try {
//            Field field = result.getClass().getDeclaredField("mFingerprint");
//            field.setAccessible(true);
//            Object fingerPrint = field.get(result);
//
//            Class<?> clzz = Class.forName("android.hardware.fingerprint.Fingerprint");
//            Method getName = clzz.getDeclaredMethod("getName");
//            Method getFingerId = clzz.getDeclaredMethod("getFingerId");
//            Method getGroupId = clzz.getDeclaredMethod("getGroupId");
//            Method getDeviceId = clzz.getDeclaredMethod("getDeviceId");
//
//            CharSequence name = (CharSequence) getName.invoke(fingerPrint);
//            int fingerId = (int) getFingerId.invoke(fingerPrint);
//            int groupId = (int) getGroupId.invoke(fingerPrint);
//            long deviceId = (long) getDeviceId.invoke(fingerPrint);
//
//            Log.d(TAG, "name: ---" + name);
//            Log.d(TAG, "fingerId:--- " + fingerId);
//            Log.d(TAG, "groupId: ---" + groupId);
//            Log.d(TAG, "deviceId:---- " + deviceId);
//        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
//            e.printStackTrace();
//        }
    }

    private void showError(CharSequence error) {
        mIcon.setImageResource(R.drawable.ic_fingerprint_error);
        mErrorTextView.setText(error);
        mErrorTextView.setTextColor(
                mErrorTextView.getResources().getColor(R.color.warning_color, null));
        mErrorTextView.removeCallbacks(mResetErrorTextRunnable);
        mErrorTextView.postDelayed(mResetErrorTextRunnable, ERROR_TIMEOUT_MILLIS);
    }

    @VisibleForTesting
    Runnable mResetErrorTextRunnable = new Runnable() {
        @Override
        public void run() {
            mErrorTextView.setTextColor(
                    mErrorTextView.getResources().getColor(R.color.hint_color, null));
            mErrorTextView.setText(
                    mErrorTextView.getResources().getString(R.string.fingerprint_hint));
            mIcon.setImageResource(R.drawable.ic_fp_40px);
        }
    };

    public interface Callback {

        void onAuthenticated();

        void onError();
    }
}
