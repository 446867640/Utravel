package com.utravel.app.delegates.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.utravel.app.R;
import com.utravel.app.adapter.TabPagerAdapter;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.utils.Util;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MyOrderDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
    private AppCompatImageView iv_back;
    private AppCompatTextView tv_title;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private final List<String> TAB_TITLES = new ArrayList<>();
    private final List<LatterDelegate> DELEGATES = new ArrayList<>();
    private final LinkedHashMap<String, LatterDelegate> ITEMS = new LinkedHashMap<>();

    private static final String BUNDLE_POSITION = "BUNDLE_POSITION";
    private int value_position = 0;

    public static MyOrderDelegate newInstance(int position) {
        MyOrderDelegate fragment = new MyOrderDelegate();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            value_position = bundle.getInt(BUNDLE_POSITION);
        }
        initItemDatas();
    }

    @Override
    public boolean setIsDark() { return true;  }

    @Override
    public Object setLayout() { return R.layout.delegate_order_main; }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewParams();
        initAdapter();
        initListener();
    }

    private void initItemDatas() {
        final LinkedHashMap<String, LatterDelegate> items = new LinkedHashMap<>();
        items.put("全部",BaseOrderDelegate.newInstance(null));
        items.put("待付款",BaseOrderDelegate.newInstance("unpaid"));
        items.put("待发货",BaseOrderDelegate.newInstance("paid"));
        items.put("待收货",BaseOrderDelegate.newInstance("shipped"));
        items.put("已完成",BaseOrderDelegate.newInstance("received"));
        ITEMS.clear();
        ITEMS.putAll(items);
        for(Map.Entry<String, LatterDelegate> item : ITEMS.entrySet()){
            final String key = item.getKey();
            final LatterDelegate value = item.getValue();
            TAB_TITLES.add(key);
            DELEGATES.add(value);
        }
    }

    private void initViews(View rootView) {
        status_bar = (View) rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView) rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);
        mTabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout_order);
        mViewPager = (ViewPager) rootView.findViewById(R.id.vp_order);
    }

    private void initViewParams() {
        tv_title.setText(getResources().getString(R.string.order_main_title));

        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);

        mTabLayout.setTabMode(TabLayout.MODE_FIXED); //设置平分
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getContext(), R.color.text_color_blue));//选中后下划线颜色
        mTabLayout.setTabTextColors(ContextCompat.getColor(getContext(), R.color.text_color_black), ContextCompat.getColor(getContext(), R.color.text_color_blue));
        mTabLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.delegate_white));
        mTabLayout.setTabIndicatorFullWidth(false);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initAdapter() {
        final TabPagerAdapter adapter = new TabPagerAdapter(getChildFragmentManager(),TAB_TITLES,DELEGATES);
        mViewPager.setAdapter(adapter);
        setViewPagerItem(value_position);
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

    public void setViewPagerItem(int position) {
        mViewPager.setCurrentItem(position); //跳转到相应item
    }
}
