package com.utravel.app.delegates.main.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import com.utravel.app.R;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.entity.ZhuanShuEntity;
import com.utravel.app.ui.MyGridView;

import java.util.ArrayList;
import java.util.List;

public class ZuanShuFansDelegate extends LatterDelegate {
    MyGridView gv_fans;
    AppCompatTextView tv_fansnums;

    List<ZhuanShuEntity> gvDatas = new ArrayList<>();
    CommonAdapter<ZhuanShuEntity> gvAdapter;

    @Nullable
    @Override
    public Object setLayout() {
        return R.layout.delegate_my_fans_zuanshu;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initAdapter();
        initListener();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        getFansData();
    }

    private void initViews(View rootView) {
        gv_fans = (MyGridView) rootView.findViewById(R.id.gv_fans);
        tv_fansnums = (AppCompatTextView) rootView.findViewById(R.id.tv_fansnums);
    }

    private void initAdapter() {
        gvAdapter = new CommonAdapter<ZhuanShuEntity>(getContext(), gvDatas, R.layout.item_myfans_zhuanshu) {
            @Override
            public void convert(BaseViewHolder holder, ZhuanShuEntity zhuanShuEntity) {

            }
        };
        gv_fans.setAdapter(gvAdapter);
    }

    private void initListener() {

    }

    private void getFansData() {
        for(int i = 0; i < 10; i++){
            gvDatas.add(new ZhuanShuEntity("","","",""));
        }
        gvAdapter.refreshDatas(gvDatas);
    }
}
