package com.hangyjx.syygzapp.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hangyjx.syygzapp.R;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class X5WebView extends WebView {
    TextView title;
    public static ProgressBar progressBar;
    public static boolean isAdd = false;
    private WebViewClient client = new WebViewClient() {
        /**
         * 防止加载网页时调起系统浏览器
         */
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    };

    public X5WebView(Context context) {
        super(context);
        setBackgroundColor(85621);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public X5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setWebViewClient(client);
        initWebViewSettings();
        this.getView().setClickable(true);
            progressBar = new ProgressBar(context, null,
                    android.R.attr.progressBarStyleHorizontal);
            progressBar.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.MATCH_PARENT,
                    5, 0, 0));
            progressBar.setMax(100);
            progressBar.setIndeterminate(false);
            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.pregress_bg));
            Drawable drawable = getResources().getDrawable(R.drawable.pregress_bg);
            progressBar.setIndeterminateDrawable(drawable);
            addView(progressBar);
            setWebChromeClient();
    }

    private void initWebViewSettings() {
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

//	@Override
//	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
//		boolean ret = super.drawChild(canvas, child, drawingTime);
//		canvas.save();
//		Paint paint = new Paint();
//		paint.setColor(0x7fff0000);
//		paint.setTextSize(24.f);
//		paint.setAntiAlias(true);
//		if (getX5WebViewExtension() != null) {
//			canvas.drawText(this.getContext().getPackageName() + "-pid:"
//					+ android.os.Process.myPid(), 10, 50, paint);
//			canvas.drawText(
//					"X5  Core:" + QbSdk.getTbsVersion(this.getContext()), 10,
//					100, paint);
//		} else {
//			canvas.drawText(this.getContext().getPackageName() + "-pid:"
//					+ android.os.Process.myPid(), 10, 50, paint);
//			canvas.drawText("Sys Core", 10, 100, paint);
//		}
//		canvas.drawText(Build.MANUFACTURER, 10, 150, paint);
//		canvas.drawText(Build.MODEL, 10, 200, paint);
//		canvas.restore();
//		return ret;
//	}


    @Override
    public void setWebChromeClient(WebChromeClient webChromeClient) {
        super.setWebChromeClient(webChromeClient);
    }

    public void setWebChromeClient() {
        WebChromeClient webChromeClient = new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int newProgress) {
                if (progressBar != null) {
                    if (newProgress == 100) {
                        webView.getSettings().setBlockNetworkImage(false);
                        progressBar.setVisibility(View.GONE);
//                        isAdd = false;
                    } else {
                        if (progressBar.getVisibility() == View.GONE) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        progressBar.setProgress(newProgress);
                    }
                }
                super.onProgressChanged(webView, newProgress);
            }
        };
        this.setWebChromeClient(webChromeClient);
    }
}
