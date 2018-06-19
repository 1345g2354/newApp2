package com.hangyjx.syygzapp.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.hangyjx.syygzapp.R;
import com.hangyjx.syygzapp.fragment.AboutFragment;
import com.hangyjx.syygzapp.fragment.IndexFragment;
import com.hangyjx.syygzapp.fragment.ScanFragment;

public class BaseActivity extends FragmentActivity {
    //定义FragmentTabHost对象
    private FragmentTabHost mTabHost;
    // 定义一个布局
    private LayoutInflater layoutInflater;
    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {IndexFragment.class, ScanFragment.class, AboutFragment.class};
    //定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.tab_nutri_food_selector, R.drawable.tab_nutri_analysis_selector, R.drawable.tab_us_selector};
    //Tab选项卡的文字
    private String mTextviewArray[] = {"营养食品", "营养分析", "食无忧"};
    private long oldBackTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatService.start(this);
        initViews();
    }

    private void initViews() {
        //实例化布局对象
        layoutInflater = LayoutInflater.from(this);
        // 实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.container_fragment);
        // 得到fragment的个数
        int count = fragmentArray.length;
        for (int i = 0; i < count; i++) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            //可以根据自己的需求设置Tab按钮的背景//
//            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.tab_icon);
        imageView.setImageResource(mImageViewArray[index]);
        TextView textView = (TextView) view.findViewById(R.id.tab_text);
        textView.setText(mTextviewArray[index]);
        return view;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - oldBackTime > 2000) {
                oldBackTime = System.currentTimeMillis();
                Toast.makeText(this, R.string.back, Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}