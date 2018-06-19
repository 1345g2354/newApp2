package com.hangyjx.syygzapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import com.hangyjx.syygzapp.asymmetricfingerprintdialog.MainActivity;
import com.hangyjx.syygzapp.R;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends Activity {
    private ViewPager viewPager;//需要ViewPaeger
    private PagerAdapter mAdapter;//需要PagerAdapter适配器
    private List<View> mViews = new ArrayList<>();//准备数据源
    private ImageView iv_home;//在ViewPager的最后一个页面设置一个按钮，用于点击跳转到MainAc

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏
        setContentView(R.layout.activity_guide);
        initView();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        LayoutInflater inflater = LayoutInflater.from(this);//将每个xml文件转化为View
        View guideOne = inflater.inflate(R.layout.guide1, null);//每个xml中就放置一个imageView
        View guideTwo = inflater.inflate(R.layout.guide2, null);
        View guideThree = inflater.inflate(R.layout.guide3, null);

        mViews.add(guideOne);//将view加入到list中
        mViews.add(guideTwo);
        mViews.add(guideThree);

        mAdapter = new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = mViews.get(position);//初始化适配器，将view加到container中
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                View view = mViews.get(position);
                container.removeView(view);//将view从container中移除
            }

            @Override
            public int getCount() {
                return mViews.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;//判断当前的view是我们需要的对象
            }
        };

        viewPager.setAdapter(mAdapter);

        iv_home = (ImageView) guideThree.findViewById(R.id.btn_to_main);
        iv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("dd","999----");
                Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
