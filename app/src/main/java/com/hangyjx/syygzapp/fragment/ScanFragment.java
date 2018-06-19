package com.hangyjx.syygzapp.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.hangyjx.syygzapp.R;
import com.hangyjx.syygzapp.asymmetricfingerprintdialog.DeviceUtils;
import com.hangyjx.syygzapp.model.okhttp.OkhttpHelper;
import com.hangyjx.syygzapp.model.okhttp.RequestUrl;
import com.hangyjx.syygzapp.model.okhttp.callback.HttpCallbackResult;
import com.hangyjx.syygzapp.scan.activity.DetailActivity;

import com.hangyjx.syygzapp.zxing.ui.CaptureActivity;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class ScanFragment extends Fragment implements HttpCallbackResult {
    private TextView tvTitle;
    private TextView tvEnterBarcode;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvTitle.setText("营养分析");
//        view.findViewById(R.id.btn_1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), ImageActivity.class);
//                startActivity(intent);
//            }
//        });
        view.findViewById(R.id.iv_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ScanFragment.this.requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    StatService.onEvent(getActivity(), "102", "打开扫码界面",1);

                    Intent intent = new Intent(getContext(), CaptureActivity.class);
                    startActivityForResult(intent,997);
                }
            }
        });
        view.findViewById(R.id.test_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TestRequset();
            }
        });
        tvEnterBarcode = (TextView) view.findViewById(R.id.tv_enter_barcode);
        tvEnterBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatService.onEvent(getActivity(), "101", "打开手工输入条码",1);
                Intent intent = new Intent(getContext(), DetailActivity.class);
//                intent.putExtra("url", "http://cp.spaq51.com/field?swym=sapp");
                intent.putExtra("url", "http://192.168.1.151:9100/field?swym=sapp");
                startActivity(intent);
            }
        });
        return view;
    }

    public void TestRequset(String ordertext){
        Map<String,Object> param=new HashMap<>();
        param.put("active",ordertext);
        String fingerprint = DeviceUtils.getFingerprintInfo(getActivity());
        if(TextUtils.isEmpty(fingerprint)){
            Toast.makeText(getActivity(),"请录入指纹信息",Toast.LENGTH_SHORT).show();
        }
        param.put("fingerprint",fingerprint);
        param.put("msg","");
        OkhttpHelper.doRequest(RequestUrl.LOGIN_SIGN,param, RequestUrl.LOGIN_SIGN,this);
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
            Intent intent = new Intent(getContext(), CaptureActivity.class);
            startActivityForResult(intent,997);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onPageStart(getActivity(),"ScanFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPageEnd(getActivity(),"ScanFragment");
    }

    @Override
    public void onSuccess(String response, String requestTag) {
            Toast.makeText(getActivity(),response,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFail(Exception e, String requestTag) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 997:
                if(data != null){
                    String text =   data.getStringExtra("data");
                    TestRequset(text);
//                    Toast.makeText(getActivity(),text+"",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
