package com.hangyjx.syygzapp.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import com.hangyjx.syygzapp.R;
import com.hangyjx.syygzapp.activity.MainActivity;
import com.hangyjx.syygzapp.view.X5WebView;
import com.tencent.smtt.sdk.WebBackForwardList;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;
import com.tencent.smtt.sdk.WebSettings;

public class IndexFragment extends Fragment {
    private View rootView;
    private TextView tvTitle;
    private ImageView ivBack;
    private ImageView ivHome;
    private X5WebView mWebView;
    private Dialog progressDialog;
    private UMShareListener mShareListener;
    private ShareAction mShareAction;
    private Timer timer = new Timer();
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                ivBack.setVisibility(View.VISIBLE);
                if (mWebView.canGoBack()) {
                    ivBack.setVisibility(View.VISIBLE);
                    ivHome.setVisibility(View.VISIBLE);
                } else {
                    ivBack.setVisibility(View.GONE);
                    ivHome.setVisibility(View.GONE);
                }
            }
            super.handleMessage(msg);
        }
    };
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_index, container, false);
            tvTitle = (TextView) rootView.findViewById(R.id.tv_title);
            tvTitle.setText("营养食品");
            ivBack = (ImageView) rootView.findViewById(R.id.iv_back);
            ivHome = (ImageView) rootView.findViewById(R.id.iv_home);
            mWebView = (X5WebView) rootView.findViewById(R.id.wv_food);
//            mWebView = new X5WebView(getContext(), null);
            timer.schedule(task, 1000, 1000); // 1s后执行task,经过1s再次执行
            mShareListener = new CustomShareListener(getActivity());
            mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "AndroidBridge");
            //加载资产目录下的html页面
//            mWebView.loadUrl("http://192.168.1.168:4000/productlist?swym=sapp");
//            mWebView.loadUrl("http://192.168.1.168:9000/productlist?swym=sapp");
//            mWebView.loadUrl("file:///android_asset/test.html");
            mWebView.loadUrl("http://cp.spaq51.com/productlist?swym=sapp");
            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    }
                }
            });
            ivHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mWebView.canGoBack()) {
                        goBackHome();
                    }
                }
            });
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onPageStart(getActivity(), "IndexFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPageEnd(getActivity(), "IndexFragment");
    }

    private void goBackHome() {
        WebBackForwardList list = mWebView.copyBackForwardList();
        Integer backi = -list.getCurrentIndex();
        mWebView.goBackOrForward(backi);
    }

    private void showWaitingDialog() {
    /* 等待Dialog具有屏蔽其他控件的交互能力
     * @setCancelable 为使屏幕不可点击，设置为不可取消(false)
     * 下载等事件完成后，主动调用函数关闭该Dialog
     */
        if (progressDialog == null) {
            progressDialog = new Dialog(getContext(), R.style.progress_dialog);
            progressDialog.setContentView(R.layout.dialog);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
            msg.setText("加载中");
            progressDialog.show();
//            waitingDialog = new ProgressDialog(getContext());
//            //waitingDialog.setTitle("我是一个等待Dialog");
//            waitingDialog.setMessage("努力加载中，请稍后...");
//            waitingDialog.setIndeterminate(true);
//            waitingDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    public class MyJavaScriptInterface {
        public MyJavaScriptInterface() {
        }

        @JavascriptInterface
        public void shareMes(final String content, final String url, final String image, final String title) {
            //Toast.makeText(getContext(), content + ":" + url + ":" + image, Toast.LENGTH_SHORT).show();
            /*增加自定义按钮的分享面板*/
            final String Url = url.replace("amp;", "");
            mShareAction = new ShareAction(getActivity()).setDisplayList(
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
                    web.setThumb(new UMImage(getContext(), image));
                    new ShareAction(getActivity()).withMedia(web)
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
                Toast.makeText(mActivity.get(), "分享成功啦", Toast.LENGTH_SHORT).show();
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
}
