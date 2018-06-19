package com.hangyjx.syygzapp.zxing.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.hangyjx.syygzapp.scan.activity.DetailActivity;
import com.hangyjx.syygzapp.utils.Utils;
import com.hangyjx.syygzapp.zxing.camera.CameraManager;
import com.hangyjx.syygzapp.zxing.core.CaptureActivityHandler;
import com.hangyjx.syygzapp.zxing.core.InactivityTimer;
import com.hangyjx.syygzapp.zxing.core.ViewfinderView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.hangyjx.syygzapp.R;

import com.hangyjx.syygzapp.zxing.camera.BeepManager;
import com.hangyjx.syygzapp.zxing.core.FinishListener;

public class CaptureActivity extends Activity implements SurfaceHolder.Callback, OnClickListener {

    private static final String TAG = CaptureActivity.class.getSimpleName();
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private TextView statusView;
    private ImageView common_title_TV_left;
    private Result lastResult;
    private boolean hasSurface;
    private IntentSource source;
    public static CaptureActivity captureActivity;
    private Collection<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private final int from_photo = 010;
    static final int PARSE_BARCODE_SUC = 3035;
    static final int PARSE_BARCODE_FAIL = 3036;
    private int REQUEST_CODE = 999;

    private int tag;//哪个界面跳转的标志
    private final int ENTRYEXPRESS = 200;//录入快递的标志
    private final int QUERYEXPRESS = 201;//查询快递的标志
    String photoPath;
    ProgressDialog mProgress;
    private String hostPlugin;
    private String imgHost;
    private String spImgHost;
    //private TextView viewById;
    private boolean isOpenLight;
    //    private SelectScannerDialog selectScannerDialog;
    private String photo_path;
    private BeepManager beepManager;
    private Bitmap barCode;
    private ViewPager viewPager;
    private LayoutInflater inflater;
    private View productView;
    private View nutritionView;
    private View additiveView;
    private ArrayList<View> viewList;
    private PagerAdapter pagerAdapter;
    private String phoneNO;

    private ImageView iv_nutrition;
    private TextView textGoodsTabTitleName;
    private ImageView img_title_nutrition;
    private ImageView img_title_additive;
    private SpannableString spanString;
    private Drawable drawableIcon;

    private TextView tvEnterBarcode;
//    public SelectScannerDialog getInstance() {
//        if (selectScannerDialog == null) {
//            selectScannerDialog = new SelectScannerDialog(this, flagPlugin);
//        }
//        return selectScannerDialog;
//    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
//        if (id == instance.getId("id", "tv_input")) {
//            viewById.setText("支持二维码，条形码，防伪码");
//            getInstance().show();
//        } else if (id == instance.getId("id", "tv_picture")) {
//            viewById.setText("支持二维码，条形码，防伪码");
//            getPicFromBumble();
//        } else if (id == instance.getId("id", "tv_chinese")) {
//            viewById.setText("支持中国追溯码");
//        }
    }

    enum IntentSource {

        ZXING_LINK, NONE

    }

    private void getPicFromBumble() {
        Intent innerIntent = new Intent(); // "android.intent.action.GET_CONTENT"
        if (Build.VERSION.SDK_INT < 19) {
            innerIntent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            innerIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }

        innerIntent.setType("image/*");

        Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");

        CaptureActivity.this
                .startActivityForResult(wrapperIntent, REQUEST_CODE);
    }


    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Window window = getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);

        captureActivity = this;
//        Intent intentPlugin = getIntent();
//        hostPlugin =   intentPlugin.getStringExtra("host");
//        flagPlugin = intentPlugin.getStringExtra("flag");
//        imgHost = intentPlugin.getStringExtra("imgHost");//全息
//        spImgHost = intentPlugin.getStringExtra("spHost");//溯源
//        phoneNO = intentPlugin.getStringExtra("phoneNO");
//        if (flagPlugin.equals("0")) {
//         ViewfinderView.setscannerVisible(View.GONE);
//        } else {
//           ViewfinderView.setscannerVisible(View.VISIBLE);
//        }
        setContentView(R.layout.capture_view);
        tvEnterBarcode = (TextView) findViewById(R.id.tv_enter_barcode);
        tvEnterBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CaptureActivity.this, DetailActivity.class);
                intent.putExtra("url", "http://cp.spaq51.com/field?swym=sapp");
                startActivity(intent);
                CaptureActivity.this.finish();
            }
        });
        hasSurface = false;

        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);

        cameraManager = new CameraManager(getApplication());

        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);
        statusView = (TextView) findViewById(R.id.status_view);
        common_title_TV_left = (ImageView) findViewById(R.id.common_title_TV_left);
        //title = (TitleView) findViewById(R.id.decode_title);
        //from_gallery = (Button) findViewById(R.id.from_gallery);
        // 为标题和底部按钮添加监听事件
        setListeners();


        //viewById = (TextView) findViewById(R.id.tv_camera);
        textGoodsTabTitleName = (TextView) findViewById(R.id.textGoodsTabTitleName);
//        if (flagPlugin.equals("0")) {
//            textGoodsTabTitleName.setText("全景扫码");
//            findViewById(R.id.tv_all_tip).setVisibility(View.VISIBLE);
//
//            findViewById(R.id.ll_bottom).setVisibility(View.GONE);
//            findViewById(R.id.ib_camera).setVisibility(View.GONE);
//            viewById.setVisibility(View.GONE);
//        } else {
//            findViewById(R.id.ll_bottom).setVisibility(View.VISIBLE);
//            findViewById(R.id.ib_camera).setVisibility(View.VISIBLE);
//            textGoodsTabTitleName.setText("扫码溯源");
//
//
//            viewById.setVisibility(View.VISIBLE);
//            findViewById(R.id.ib_camera).setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (cameraManager != null) {
//                        if (!isOpenLight) {
//                            cameraManager.OpenLightOn();
//                            isOpenLight = true;
//                        } else {
//                            cameraManager.CloseLightOff();
//                            isOpenLight = false;
//                        }
//                    }
//                }
//            });
//            findViewById(R.id.tv_input).setOnClickListener(this);
//            findViewById(R.id.tv_picture).setOnClickListener(this);
//            findViewById(R.id.tv_chinese).setOnClickListener(this);
//        }
//        initImageLoader(getApplicationContext());
//        drawableIcon = getApplicationContext().getResources().getDrawable(
//                R.mipmap.ico_more);
    }


//    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
//    public static void initImageLoader(Context context) {
//        //
////1.8.6包使用时候，括号里面传入参数true
////同上
//        defaultOptions = new DisplayImageOptions.Builder() //
//                .cacheInMemory()  //1.8.6包使用时候，括号里面传入参数true
//                .cacheOnDisc()   //同上
//                .showImageForEmptyUri(R.mipmap.icon)
//                .showImageOnFail(R.mipmap.icon)
//                .build();
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
//                context).threadPriority(Thread.NORM_PRIORITY - 2).defaultDisplayImageOptions(defaultOptions)
////                .denyCacheImageMultipleSizesInMemory()
//                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
//                .tasksProcessingOrder(QueueProcessingType.LIFO)
//                .threadPoolSize(3)
//                .diskCacheSize(400*1024*1024)
//                .memoryCacheSize((int) (Runtime.getRuntime().maxMemory() / 12))
////                .writeDebugLogs() // Remove for release app
//                .build();
//        // Initialize ImageLoader with configuration.
////        ImageLoader.getInstance().init(config);
//    }


    public void setListeners() {
        common_title_TV_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureActivity.this.finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {//选择相册解析分
            switch (requestCode) {
                case 999:
//                    String[] proj = {MediaStore.Images.Media.DATA};
//                    // 获取选中图片的路径
//                    Cursor cursor = getContentResolver().query(data.getData(),
//                            proj, null, null, null);
//                    if (cursor.moveToFirst()) {
//                        int column_index = cursor
//                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                        photo_path = cursor.getString(column_index);
//                        if (photo_path == null) {
//                            photo_path = Utils.getPath(getApplicationContext(),
//                                    data.getData());
//                            Log.i("123path  Utils", photo_path);
//                        }
//                        Log.i("123path", photo_path);
//                    }
//                    cursor.close();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String result = Utils.scanningImage(photo_path);
                            // String result = decode(photo_path);
                            if (result == null) {
                                Looper.prepare();
                                Toast.makeText(getApplicationContext(), "图片格式有误", Toast.LENGTH_SHORT)
                                        .show();
                                Looper.loop();
                            } else {
                                Log.i("123result", result.toString());
                                // Log.i("123result", result.getText());
                                // 数据返回
//								String recode = Utils.recode(result.toString());
                                Intent data = new Intent();
                                data.putExtra("codedContent", result);
                                setResult(-1, data);
                                finish();
                            }
                        }
                    }).start();
                    break;
                case 888:

                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler = null;
        lastResult = null;
        resetStatusView();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        inactivityTimer.onResume();
        beepManager.updatePrefs();
        source = IntentSource.NONE;
        decodeFormats = null;
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        try {
            inactivityTimer.onPause();
            beepManager.close();
        } catch (Exception e) {

        }


        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        if (mProgress != null) {
            mProgress.dismiss();
        }
        captureActivity = null;

        traceHaldler.removeMessages(2);
        traceHaldler.removeMessages(0);
        traceHaldler.removeMessages(1);
        traceHaldler.removeCallbacksAndMessages(null);
        traceHaldler = null;
//        haldler.removeMessages(1);
//        haldler.removeMessages(2);
//        haldler.removeMessages(3);
//        haldler = null;

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
//		case KeyEvent.KEYCODE_BACK:
//			if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK) && lastResult != null) {
//				restartPreviewAfterDelay(0L);
//				return true;
//			}
//			break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                cameraManager.setTorch(false);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                cameraManager.setTorch(true);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 这里初始化界面，调用初始化相机
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    private static ParsedResult parseResult(Result rawResult) {
        return ResultParser.parseResult(rawResult);
    }

    private Handler traceHaldler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case 0://溯源
//                    if (traceabilityModel != null && traceabilityModel.flag) {
//                        intentData(null, null);
//
//                    }else if(traceabilityModel != null){
//                        Toast.makeText(CaptureActivity.this, traceabilityModel.v_msg, Toast.LENGTH_SHORT).show();
//                        onCamerResume();
//                    } else {
//
//                        onCamerResume();
//                        Toast.makeText(CaptureActivity.this, "网络异常!", Toast.LENGTH_SHORT).show();
//                    }
//                    break;
                case 1://全息

//                    if (productModel != null &&  productModel.flag) {
//                        intentData(null, null);
//                    }else if(productModel != null){
//                        Toast.makeText(CaptureActivity.this, productModel.v_msg, Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(CaptureActivity.this, "网络异常!", Toast.LENGTH_SHORT).show();
//                    }
                    break;
                case 2:
                    onCamerResume();
                    break;
                default:
                    break;

            }
        }
    };

    private void onCamerResume() {
        try {
            closeCamera();
            restartCamera();
        } catch (Exception e) {

        }

    }

    public void intentData(String content, String Date) {
        textGoodsTabTitleName.setVisibility(View.GONE);
//        if (flagPlugin.equals("0")) {//全息
//            //addThreeView();
//        } else {//溯源
//            Intent   intent = new Intent(CaptureActivity.this, TraceabilityActivity.class);
//            intent.putExtra("traceabilityModel", traceabilityModel);
//            intent.putExtra("spImgHost", spImgHost);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            barCode.compress(Bitmap.CompressFormat.PNG, 100, baos);
//            byte[] bytes=baos.toByteArray();
//            Bundle b = new Bundle();
//            b.putByteArray("bitmap", bytes);
//            intent.putExtras(b);
//            startActivityForResult(intent,888);
    }
//
//    }

    // 解析二维码
    public void handleDecode(Result rawResult, final Bitmap barcode) {
        inactivityTimer.onActivity();
        Intent data = new Intent();
        data.putExtra("data",rawResult.getText());
        setResult(997, data);
        finish();
//        Toast.makeText(CaptureActivity.this, rawResult.getText(), Toast.LENGTH_SHORT).show();
//        String codeType = rawResult.getBarcodeFormat().toString();
//        if (codeType == "EAN_8" || codeType == "EAN_13" || codeType == "UPC_A" || codeType == "UPC_E") {
//            Intent intent = new Intent(CaptureActivity.this, DetailActivity.class);
//            //http://cp.spaq51.com/field?swym=sapp&&appcode=
//            String detailUrl = "http://cp.spaq51.com/field?swym=sapp&&appcode=" + rawResult.getText();//rawResult.getText();
////        String detailUrl = "http://192.168.1.147:9000/field?swym=sapp&&appcode=6901236341582";//rawResult.getText();
//            intent.putExtra("url", detailUrl);
//            startActivity(intent);
//            finish();
//        } else {
//            showErrorDialog();
//        }
        //barCode = barcode;
        //http://cp.spaq51.com/field?swym=sapp&&appcode=

//        boolean fromLiveScan = barcode != null;
//        iv_image.setImageBitmap(barcode);
        //这里处理解码完成后的结果，此处将参数回传到Activity处理
//        if (fromLiveScan) {
//            beepManager.playBeepSoundAndVibrate();
//        }
//        showErrorDialog();
    }

//    private void postAllTraceData(final String code) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Map<String, String> param = new HashMap<String, String>();
//                param.put("prod_code", code);
//                param.put("account",phoneNO);
//                productModel = (ProductModel) HttpUtils.httpPost(hostPlugin + "queryProduct", param, "utf-8", 1);
//                Message msg = new Message();
//                msg.what = 1;
//                traceHaldler.sendMessage(msg);
//            }
//        }).start();
//    }

//    public void postTraceData(final String prod_code, final String batch_no) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Map<String, String> param = new HashMap<String, String>();
//                param.put("prod_code", prod_code);
//                param.put("batch_no", batch_no);
//                param.put("account",phoneNO);
//                traceabilityModel = (TraceabilityModel) HttpUtils.httpPost(hostPlugin + "queryTraceabilityProduct", param, "utf-8", 0);
//                Message msg = new Message();
//                msg.what = 0;
//                traceHaldler.sendMessage(msg);
//            }
//        }).start();
//    }

//    private Handler haldler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            int type = msg.what;
//            switch (type) {
//                case 1:
//                    initProductView(productModel);
//                    break;
//                case 2:
//                    initNutrView(productNutrModel);
//                    break;
//                case 3:
//                    initAddView(productAddModel);
//                    break;
//                default:
//                    break;
//            }
//
//        }
//    };

    private void postData() {
        new Thread(new Runnable() {
            @Override
            public void run() {

//                if (productModel != null && productModel.prod_id != "") {
//                    Map<String, String> paramNutr = new HashMap<String,String>();
//                    paramNutr.put("prod_id", productModel.prod_id);
//                    paramNutr.put("type", "nutr");
//                    productNutrModel = (ProductNutrModel) HttpUtils.httpPost(hostPlugin+"queryProductAtta", paramNutr, "utf-8", 2);
//                    if (productNutrModel != null) {
//                        Message msg = new Message();
//                        msg.what = 2;
//                        haldler.sendMessage(msg);
//                    }
//                }

//                if (productModel != null && productModel.prod_id != "") {
//                    Map<String, String> paramAdd = new HashMap<String,String>();
//                    paramAdd.put("prod_id", productModel.prod_id);
//                    paramAdd.put("type", "additiveid");
//                    productAddModel = (ProductAddModel) HttpUtils.httpPost(hostPlugin+"queryProductAtta", paramAdd, "utf-8", 3);
//                    if (productAddModel != null) {
//                        Message msg = new Message();
//                        msg.what = 3;
//                        haldler.sendMessage(msg);
//                    }
//                }
            }
        }).start();
    }

    private void showErrorDialog() {
        closeCamera();
        viewfinderView.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("请扫描食品条码！");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                restartCamera();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                CaptureActivity.this.finish();
            }
        });
        builder.show();
    }

    void restartCamera() {
        Log.d(TAG, "hasSurface " + hasSurface);

        viewfinderView.setVisibility(View.VISIBLE);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        initCamera(surfaceHolder);

        // 恢复活动监控器
        inactivityTimer.onResume();
    }

    void closeCamera() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }

        if (inactivityTimer != null) {

            inactivityTimer.onPause();
        }

        // 关闭摄像头
        cameraManager.closeDriver();
    }

    // 初始化照相机，CaptureActivityHandler解码
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats, characterSet, cameraManager);
            }
        } catch (IOException ioe) {
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("建业APP");
        builder.setMessage("开启摄像头失败，请尝试：设置->权限管理->应用程序->食无忧,调取摄像头>允许");//或者确定是否允许了使用摄像头权限
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
                finish();
            }
        });
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
        resetStatusView();
    }

    private void resetStatusView() {
        statusView.setText("请将二维码置于方框中");
        statusView.setVisibility(View.VISIBLE);
        viewfinderView.setVisibility(View.VISIBLE);
        lastResult = null;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

}
