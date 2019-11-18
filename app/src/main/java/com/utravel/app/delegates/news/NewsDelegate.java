package com.utravel.app.delegates.news;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import com.utravel.app.R;
import com.utravel.app.adapter.TabPagerAdapter;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.utils.Util;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NewsDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {

    private View status_bar;
    private AppCompatImageView iv_back;
    private AppCompatTextView tv_title;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private final List<String> TAB_TITLES = new ArrayList<>();
    private final List<LatterDelegate> DELEGATES = new ArrayList<>();
    private final LinkedHashMap<String, LatterDelegate> ITEMS = new LinkedHashMap<>();

    @Override
    public boolean setIsDark() {
        return true;
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_news_main;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        setItemsData();
        initViews(rootView);
        initViewsParams();
        initAdapter();
        initListener();
    }

    private void initViews(View rootView) {
        status_bar = (View)rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView)rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);
        mTabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout_news);
        mViewPager = (ViewPager) rootView.findViewById(R.id.vp_news);
    }

    private void initViewsParams() {
        tv_title.setText("消息");

        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);

        mTabLayout.setTabMode(TabLayout.MODE_FIXED); //设置平分
        mTabLayout.setSelectedTabIndicatorColor(Color.parseColor("#F62341"));//选中后下划线颜色
        mTabLayout.setTabTextColors(Color.parseColor("#666666"),Color.parseColor("#F62341"));
        mTabLayout.setTabIndicatorFullWidth(false);
        mTabLayout.setBackgroundColor(Color.parseColor("#FEFEFE"));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initAdapter() {
        final TabPagerAdapter adapter = new TabPagerAdapter(getChildFragmentManager(),TAB_TITLES,DELEGATES);
        mViewPager.setAdapter(adapter);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) {
            pop();
        }
    }

    private void setItemsData() {
        final LinkedHashMap<String, LatterDelegate> items = new LinkedHashMap<>();
        String tab1 = _mActivity.getResources().getString(R.string.news_tab1);
        String tab2 = _mActivity.getResources().getString(R.string.news_tab2);
        items.put(tab1, NewsChildDelegate1.newInstance(0));
        items.put(tab2, NewsChildDelegate1.newInstance(1));
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
