package com.utravel.app.delegates.main.my;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;

import com.utravel.app.R;
import com.utravel.app.adapter.TabPagerAdapter;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.delegates.detail.CalculatePromotionsDetailDelegate;
import com.utravel.app.utils.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MyShouYiDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    View status_bar;
    AppCompatImageView iv_back;
    TabLayout mTabLayout;
    ViewPager mViewPager;

    private final List<String> TAB_TITLES = new ArrayList<>();
    private final List<LatterDelegate> DELEGATES = new ArrayList<>();
    private final LinkedHashMap<String, LatterDelegate> ITEMS = new LinkedHashMap<>();

    public static MyShouYiDelegate newInstance() {
        final Bundle args = new Bundle();
        final MyShouYiDelegate delegate = new MyShouYiDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    @Override
    public boolean setIsDark() {
        return false;
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_my_shouyi;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        setItemsData();
        initViews(rootView);
        initViewParams();
        initTabLayout();
        initAdapter();
        initListener();
    }

    private void initViews(View rootView) {
        status_bar = (View) rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView)rootView.findViewById(R.id.iv_back);
        mTabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
    }

    private void initViewParams() {
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
    }

    private void initTabLayout() {
        mTabLayout.setTabMode(TabLayout.MODE_FIXED); //设置平分
        mTabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));//选中后下划线颜色
        mTabLayout.setTabTextColors(Color.parseColor("#FFFFFF"),Color.parseColor("#FFFFFF"));
        mTabLayout.setTabIndicatorFullWidth(false);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initAdapter() {
        final TabPagerAdapter adapter = new TabPagerAdapter(getChildFragmentManager(),TAB_TITLES,DELEGATES);
        mViewPager.setAdapter(adapter);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {

            }
            @Override
            public void onPageScrolled(int i, float v, int i1) {}
            @Override
            public void onPageScrollStateChanged(int i) {}
        });
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) {//返回
            pop();
        }
    }

    private void setItemsData() {
        final LinkedHashMap<String, LatterDelegate> items = new LinkedHashMap<>();
        items.put("今日",CalculatePromotionsDetailDelegate.newInstance("today"));
        items.put("昨日",CalculatePromotionsDetailDelegate.newInstance("yesterday"));
        items.put("本月",CalculatePromotionsDetailDelegate.newInstance("current_month"));
        items.put("上月",CalculatePromotionsDetailDelegate.newInstance("last_month"));
        ITEMS.clear();
        ITEMS.putAll(items);
        for(Map.Entry<String, LatterDelegate> item : ITEMS.entrySet()){
            final String key = item.getKey();
            final LatterDelegate value = item.getValue();
            TAB_TITLES.add(key);
            DELEGATES.add(value);
        }
    }
}
