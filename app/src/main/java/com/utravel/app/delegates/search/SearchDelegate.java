package com.utravel.app.delegates.search;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;
import com.utravel.app.R;
import com.utravel.app.adapter.TabPagerAdapter;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.utils.Util;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SearchDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    LinearLayoutCompat ll_toolbar;
    AppCompatImageView iv_back;
    AppCompatEditText et_search;
    AppCompatImageView iv_search;
    TabLayout mTabLayout;
    ViewPager mViewPager;
    RecyclerView mRecyclerView;

    private final List<String> TAB_TITLES = new ArrayList<>();
    private final List<LatterDelegate> DELEGATES = new ArrayList<>();
    private final LinkedHashMap<String, LatterDelegate> ITEMS = new LinkedHashMap<>();

    private String content_et = null;

    @Override
    public Object setLayout() {
        return R.layout.delegate_search;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setItemsData();
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(savedInstanceState,rootView);
        initViewParams();
        initTabLayout();
        initAdapter();
        initListener();
    }

    private void initViewParams() {
        LinearLayoutCompat.LayoutParams toolbar_params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        toolbar_params.topMargin= Util.getStatusBarHeight(getContext());
        ll_toolbar.setLayoutParams(toolbar_params);
    }

    private void setItemsData() {
        final LinkedHashMap<String, LatterDelegate> items = new LinkedHashMap<>();
        items.put("淘宝",new TBSearchDelegate());
        items.put("天猫",new TMSearchDelegate());
        items.put("京东",new JDSearchDelegate());
        items.put("拼多多",new PDDSearchDelegate());
        ITEMS.clear();
        ITEMS.putAll(items);
        for(Map.Entry<String, LatterDelegate> item : ITEMS.entrySet()){
            final String key = item.getKey();
            final LatterDelegate value = item.getValue();
            TAB_TITLES.add(key);
            DELEGATES.add(value);
        }
    }

    private void initViews(Bundle savedInstanceState, View rootView) {
        ll_toolbar = (LinearLayoutCompat)rootView.findViewById(R.id.ll_toolbar);
        iv_back = (AppCompatImageView) rootView.findViewById(R.id.iv_back);
        et_search = (AppCompatEditText) rootView.findViewById(R.id.et_search);
        iv_search = (AppCompatImageView) rootView.findViewById(R.id.iv_search);
        mTabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        //默认设置
        mViewPager.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void initTabLayout() {
        mTabLayout.setTabMode(TabLayout.MODE_FIXED); //设置平分
        mTabLayout.setSelectedTabIndicatorColor(Color.parseColor("#F62341"));//选中后下划线颜色
        mTabLayout.setTabTextColors(Color.parseColor("#666666"),Color.parseColor("#F62341"));
        mTabLayout.setBackgroundColor(Color.parseColor("#FEFEFE"));
        mTabLayout.setTabIndicatorFullWidth(false);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initAdapter() {
        final TabPagerAdapter adapter = new TabPagerAdapter(getChildFragmentManager(),TAB_TITLES,DELEGATES);
        mViewPager.setAdapter(adapter);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    content_et = et_search.getText().toString();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) {
            this.pop();
        }else if (v==iv_search) {
            if (TextUtils.isEmpty(et_search.getText())) {
                Toast.makeText(getContext(),"请输入搜索内容！",Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    @Override
    public boolean setIsDark() {
        return true;
    }

}
