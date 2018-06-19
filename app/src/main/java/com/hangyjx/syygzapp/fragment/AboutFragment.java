package com.hangyjx.syygzapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.hangyjx.syygzapp.R;
import com.hangyjx.syygzapp.view.X5WebView;

public class AboutFragment extends Fragment {
    //
    // view
    private View rootView;
    private TextView tvTitle;
    private X5WebView mWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_about, container, false);
            //假如有网络操作建议放在这里面，避免重复加载
            tvTitle = (TextView) rootView.findViewById(R.id.tv_title);
            tvTitle.setText("关于食无忧");
            mWebView = (X5WebView) rootView.findViewById(R.id.wv_about);
            mWebView.loadUrl("http://cp.spaq51.com/");
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
        StatService.onPageStart(getActivity(), "AboutFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPageEnd(getActivity(), "AboutFragment");
    }
}
