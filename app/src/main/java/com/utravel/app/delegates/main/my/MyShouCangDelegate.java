package com.utravel.app.delegates.main.my;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.utravel.app.R;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.adapter.RvIconAdapter;
import com.utravel.app.adapter.ShouCangAdapter;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.entity.ShouCangEntity;
import com.utravel.app.ui.MyGridView;
import com.utravel.app.utils.GlideRoundTransform;
import com.utravel.app.utils.Util;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

public class MyShouCangDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    View status_bar;
    AppCompatImageView iv_back;
    AppCompatTextView tv_title;
    SmartRefreshLayout mSmartRefreshLayout;
    RecyclerView rv_shoucang;

    ShouCangAdapter adapter;
    List<ShouCangEntity> shoucangDatas = new ArrayList<>();

    private int pageNo = 1;
    private boolean isNoMore = false;

    @Override
    public boolean setIsDark() {
        return true;
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_my_shoucang;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewParams();
        initAdapter();
        initListener();
        getShouCangData();
    }

    private void initViews(View rootView) {
        status_bar = (View) this.rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView) this.rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);
        mSmartRefreshLayout = (SmartRefreshLayout)rootView.findViewById(R.id.refresh);
        rv_shoucang = (RecyclerView) rootView.findViewById(R.id.rv_shoucang);
        tv_title.setText("收藏");
    }

    private void initViewParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
    }

    private void initAdapter() {
        if (adapter == null) {
            adapter = new ShouCangAdapter(shoucangDatas);
            GridLayoutManager rv_Manager = new GridLayoutManager(getContext(), 1);
            rv_shoucang.setLayoutManager(rv_Manager);
            rv_shoucang.setAdapter(adapter);
        }
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        rv_shoucang.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                show(position+"");
            }
        });
        mSmartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (isNoMore) {
                    Toast.makeText(getContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                    refreshLayout.finishLoadMore();
                    return;
                }
                pageNo += 1;
                getShouCangData();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) { //返回
            pop();
        }
    }

    private void getShouCangData() {
        if (pageNo>=10) {
            show("没有更多数据了");
            isNoMore = true;
            mSmartRefreshLayout.finishLoadMore();
            return;
        }
        for(int i = pageNo; i < 10 ; i++){
            shoucangDatas.add(new ShouCangEntity("","","","",""));
        }
        adapter.setNewData(shoucangDatas);
        mSmartRefreshLayout.finishLoadMore(5000);
    }
}



