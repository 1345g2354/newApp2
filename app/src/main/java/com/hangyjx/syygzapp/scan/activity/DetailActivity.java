package com.hangyjx.syygzapp.scan.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.foamtrace.photopicker.ImageCaptureManager;
import com.foamtrace.photopicker.PhotoPickerActivity;
import com.foamtrace.photopicker.SelectModel;
import com.foamtrace.photopicker.intent.PhotoPickerIntent;
import com.hangyjx.syygzapp.R;
import com.hangyjx.syygzapp.asymmetricfingerprintdialog.DeviceUtils;
import com.hangyjx.syygzapp.asymmetricfingerprintdialog.cipher.SM4Utils;
import com.hangyjx.syygzapp.model.okhttp.OkhttpHelper;
import com.hangyjx.syygzapp.model.okhttp.RequestUrl;
import com.hangyjx.syygzapp.model.okhttp.callback.HttpCallbackResult;
import com.hangyjx.syygzapp.utils.Bimp;
import com.hangyjx.syygzapp.utils.GetPathFromUri4kitkat;
import com.hangyjx.syygzapp.view.ActionSheetDialog;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.hangyjx.syygzapp.activity.MainActivity;
import com.hangyjx.syygzapp.view.X5WebView;
import com.hangyjx.syygzapp.zxing.ui.CaptureActivity;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import org.json.JSONException;
import org.json.JSONObject;

import static android.support.v4.content.FileProvider.getUriForFile;

/**
 * 营养分析
 */
public class DetailActivity extends Activity implements HttpCallbackResult {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView tvTitle;
    private ImageView ivBack;
    private X5WebView mWebView;
    public OpenFileWebChromeClient mOpenFileWebChromeClient = new OpenFileWebChromeClient();
    private static final int REQUEST_CODE_PICK_PICETURE = 11;
    private static final int REQUEST_CODE_TAKE_CAMERA = 12;
    private String EXTRA_RESTORE_PHOTO = "EXTRA_RESTORE_PHOTO";

    private ValueCallback mFilePathCallback;
    private File picturefile;
    private UMShareListener mShareListener;
    private ShareAction mShareAction;
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private int formMadia;
    private String sigBytes;
    private String publickey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        String detailUrl = intent.getStringExtra("url");
        String access_token = intent.getStringExtra("access_token");
        String industry_name = intent.getStringExtra("industry_name");
        String industry_id = intent.getStringExtra("industry_id");
        String role_type = intent.getStringExtra("role_type");
        String adminflag = intent.getStringExtra("adminflag");
        String enttype = intent.getStringExtra("enttype");
        String ent_id = intent.getStringExtra("ent_id");
        String validity = intent.getStringExtra("validity");
        String ent_name = intent.getStringExtra("ent_name");
        String client_name = intent.getStringExtra("client_name");
        sigBytes = intent.getStringExtra("sigBytes");
        publickey = intent.getStringExtra("publickey");
        String param = "syyfromm=app&client_name="+client_name+"&ent_name="+ent_name
                +"&validity="+validity
                +"&ent_id="+ent_id
                +"&enttype="+enttype
                +"&adminflag="+adminflag
                +"&role_type="+role_type
                +"&industry_id="+industry_id
                +"&industry_name="+industry_name
                +"&access_token="+access_token;

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText("溯源云追溯平台");
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setVisibility(View.VISIBLE);


        mShareListener = new CustomShareListener(DetailActivity.this);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.this.finish();
            }
        });
        findViewById(R.id.bt_scanner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goScanner();
            }
        });
        mWebView = (X5WebView) findViewById(R.id.wv_detail);
        mWebView.setWebChromeClient(mOpenFileWebChromeClient);
        mWebView.addJavascriptInterface(new DetailActivity.MyJavaScriptInterface(), "AndroidBridge");
        mWebView.loadUrl("http://192.168.1.131:70/pages/login"+param);

    }

    private void goScanner() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            Intent intent = new Intent(this, CaptureActivity.class);
            startActivityForResult(intent,997);
        }
    }

    @Override
    public void onSuccess(String response, String requestTag) {
        if(requestTag == RequestUrl.LOGIN_SIGN){
            parseJson(response);
        }
    }

    private void parseJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String code = (String)  jsonObject.opt("code");
            String message = (String)  jsonObject.opt("message");
            Toast.makeText(this,message,Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onFail(Exception e, String requestTag) {
        Toast.makeText(this,"数据连接断开",Toast.LENGTH_SHORT).show();
    }


    public class OpenFileWebChromeClient extends WebChromeClient {
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {//5.0+
            Log.e(TAG, "--------调用onShowFileChooser");
            mFilePathCallback = filePathCallback;
            //showDialog();
            checkPermission();
            return true;
        }

        //openFileChooser 方法是隐藏方法
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {// android 系统版本>4.1.1
            mFilePathCallback = uploadMsg;
            checkPermission();
        }

    }

    private void checkPermission() {
        if(Build.VERSION.SDK_INT > 22){
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
            } else {
                showDialog();
            }
        }else{
            showDialog();
        }
    }

    private void showDialog() {
        ActionSheetDialog dialog = new ActionSheetDialog(DetailActivity.this).builder().addSheetItem("拍照", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                takeForCamera();
//                shoot(null);
            }
        }).addSheetItem("相册", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                takeForPicture();
            }
        }).setCancelable(false).setCanceledOnTouchOutside(false);

        dialog.show();
        //设置点击“取消”按钮监听，目的取消mFilePathCallback回调，可以重复调起弹窗
        dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelFilePathCallback();
            }
        });
    }

    /**
     * 调用相册
     */
    private void takeForPicture() {
        formMadia = REQUEST_CODE_PICK_PICETURE;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_PICETURE);
    }

    /**
     * 调用相机
     */
    private void takeForCamera() {
        formMadia = REQUEST_CODE_TAKE_CAMERA;
        File pFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyPictures");//图片位置
//        File pFile = new File(Environment.getExternalStorageDirectory(), "MyPictures");//图片位置
        if (!pFile.exists()) {
            pFile.mkdirs();
        }
        //拍照所存路径
        picturefile = new File(pFile + File.separator + "IvMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        Log.e(TAG, "拍照所存路径: ===" + picturefile.getAbsolutePath());

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT > 23) {//7.0及以上
            Uri contentUri =  getUriForFile(DetailActivity.this, getResources().getString(R.string.filepath), picturefile);
            grantUriPermission(getPackageName(), contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {//7.0以下
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picturefile));
        }
        startActivityForResult(intent, REQUEST_CODE_TAKE_CAMERA);
    }

    private void cancelFilePathCallback() {
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
            mFilePathCallback = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PICK_PICETURE:
                takePictureResult(resultCode, data);
                break;

            case REQUEST_CODE_TAKE_CAMERA:
                takeCameraResult(resultCode);
                break;
            case 997:
                if(data != null){
                    String text =   data.getStringExtra("data");
                    TestRequset(text,sigBytes,publickey);
//                  Toast.makeText(this/,text+"",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void takePictureResult(int resultCode, Intent data) {
        if (mFilePathCallback != null) {
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result != null) {
                String path = GetPathFromUri4kitkat.getPath(this, result);
                Uri uri = null;
                uri = toZeroPic(path, uri);
//                 uri = Uri.fromFile(new File(path));
//                Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", picturefile);

                if (Build.VERSION.SDK_INT > 18) {
                    mFilePathCallback.onReceiveValue(new Uri[]{uri});
                } else {
                    mFilePathCallback.onReceiveValue(uri);
                }

            } else {
                mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = null;
            }
        }
    }

    private Uri toZeroPic(String path, Uri uri) {
        Bitmap bmp;
        try {
            bmp = Bimp.revitionImageSize(path);
            uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bmp, null, null));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return uri;
    }

    private void takeCameraResult(int resultCode) {
        if (mFilePathCallback != null) {
            if (resultCode == RESULT_OK) {
                Uri uri = null;

                String path = picturefile.getAbsolutePath();
                uri = toZeroPic(path, uri);
//                Uri uri = Uri.fromFile(picturefile);
//                Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", picturefile);

                if (Build.VERSION.SDK_INT > 18) {
                    mFilePathCallback.onReceiveValue(new Uri[]{uri});
                } else {
                    mFilePathCallback.onReceiveValue(uri);

                }
            } else {
                //点击了file按钮，必须有一个返回值，否则会卡死
                mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = null;
            }
        }

    }

    public class MyJavaScriptInterface {
        public MyJavaScriptInterface() {

        }

        @JavascriptInterface
        public void shareMes(final String content, final String url, final String image, final String title) {
            //Toast.makeText(getContext(), content + ":" + url + ":" + image, Toast.LENGTH_SHORT).show();
            /*增加自定义按钮的分享面板*/
            final String Url = url.replace("amp;", "");
            mShareAction = new ShareAction(DetailActivity.this).setDisplayList(
                    SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE).setShareboardclickCallback(new ShareBoardlistener() {
                @Override
                public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                    UMWeb web = new UMWeb(Url);
                    if (share_media == SHARE_MEDIA.WEIXIN) {
                        web.setTitle(title);
                        web.setDescription(content);
                    } else if (share_media == SHARE_MEDIA.WEIXIN_CIRCLE) {
                        web.setTitle(title + "，" + content);
                    }
                    web.setThumb(new UMImage(DetailActivity.this, image));
                    new ShareAction(DetailActivity.this).withMedia(web)
                            .setPlatform(share_media)
                            .setCallback(mShareListener)
                            .share();
                }
            });
            mShareAction.open();
        }
    }

    private static class CustomShareListener implements UMShareListener {

        private WeakReference<MainActivity> mActivity;

        private CustomShareListener(Activity activity) {
            mActivity = new WeakReference(activity);
        }

        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                    && platform != SHARE_MEDIA.EMAIL
                    && platform != SHARE_MEDIA.FLICKR
                    && platform != SHARE_MEDIA.FOURSQUARE
                    && platform != SHARE_MEDIA.TUMBLR
                    && platform != SHARE_MEDIA.POCKET
                    && platform != SHARE_MEDIA.PINTEREST

                    && platform != SHARE_MEDIA.INSTAGRAM
                    && platform != SHARE_MEDIA.GOOGLEPLUS
                    && platform != SHARE_MEDIA.YNOTE
                    && platform != SHARE_MEDIA.EVERNOTE) {
                Toast.makeText(mActivity.get(), " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                    && platform != SHARE_MEDIA.EMAIL
                    && platform != SHARE_MEDIA.FLICKR
                    && platform != SHARE_MEDIA.FOURSQUARE
                    && platform != SHARE_MEDIA.TUMBLR
                    && platform != SHARE_MEDIA.POCKET
                    && platform != SHARE_MEDIA.PINTEREST

                    && platform != SHARE_MEDIA.INSTAGRAM
                    && platform != SHARE_MEDIA.GOOGLEPLUS
                    && platform != SHARE_MEDIA.YNOTE
                    && platform != SHARE_MEDIA.EVERNOTE) {
                Toast.makeText(mActivity.get(), "分享失败啦", Toast.LENGTH_SHORT).show();
                if (t != null) {
                    com.umeng.socialize.utils.Log.d("throw", "throw:" + t.getMessage());
                }
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {

            Toast.makeText(mActivity.get(), "分享取消了", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                //取消自定义
                return;
            }
        }
        if (requestCode == 0) {
            showDialog();
        }else if(requestCode == 1){
            Intent intent = new Intent(this, CaptureActivity.class);
            startActivityForResult(intent,997);
        }
    }

    /**
     * 登录
     * @param ordertext
     */
    public void TestRequset(String ordertext, String sigBytes,String publickey){
        Map<String,Object> param=new HashMap<>();
        param.put("active",ordertext+"");
        String fingerprint = DeviceUtils.getFingerprintInfo(this);
        if(TextUtils.isEmpty(fingerprint)){
            Toast.makeText(this,"请录入指纹信息",Toast.LENGTH_SHORT).show();
        }
        param.put("fingerprint",SM4func(fingerprint));
//        String value = Base64.encodeToString(sigBytes, 0);
        param.put("msg", sigBytes);
        param.put("publickey",publickey);

        OkhttpHelper.doRequest(RequestUrl.LOGIN_SIGN,param, RequestUrl.LOGIN_SIGN,this);
    }
    private String SM4func(String fingerPrint) {
        String plainText = fingerPrint;
        SM4Utils sm4 = new SM4Utils();
        sm4.setSecretKey("q1w2e3r4t5y6kiju");
        sm4.setHexString(false);
        String cipherText = sm4.encryptData_ECB(plainText);
        return cipherText;
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("onResume","DetailActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("onDestroy","DetailActivity");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (picturefile != null) {
            outState.putSerializable(EXTRA_RESTORE_PHOTO, picturefile);
        }
        if(formMadia == REQUEST_CODE_PICK_PICETURE) {
            SystemClock.sleep(2000);
        }else if(formMadia == REQUEST_CODE_TAKE_CAMERA){
            SystemClock.sleep(4000);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        picturefile = (File) savedInstanceState.getSerializable(EXTRA_RESTORE_PHOTO);
    }


}
