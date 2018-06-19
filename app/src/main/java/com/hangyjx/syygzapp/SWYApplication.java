package com.hangyjx.syygzapp;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.hangyjx.syygzapp.asymmetricfingerprintdialog.FingerprintModule;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.common.QueuedWork;


/**
 * Created by a on 2017/9/19.
 */

public class SWYApplication extends Application {
    public static Context context;
    private static final String TAG = SWYApplication.class.getSimpleName();

//    private ObjectGraph mObjectGraph;



    /**
     * Initialize the Dagger module. Passing null or mock modules can be used for testing.
     *
     * @param module for Dagger
     */
    public void initObjectGraph(Object module) {
//        mObjectGraph = module != null ? ObjectGraph.create(module) : null;
    }

    public void inject(Object object) {
//        if (mObjectGraph == null) {
//            // This usually happens during tests.
//            Log.i(TAG, "Object graph is not initialized.");
//            return;
//        }
//        mObjectGraph.inject(object);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
//        initObjectGraph(new FingerprintModule(this));
        //开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
        Config.DEBUG = false;
        QueuedWork.isUseThreadPool = false;
        UMShareAPI.get(this);
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  cb);
    }

    //各个平台的配置，建议放在全局Application或者程序入口
    {
        PlatformConfig.setWeixin("wxafa72e11e5751e86", "24dfc1f3cfeb8fca1e3833eee75c1690");
    }
}
