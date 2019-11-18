package com.utravel.app.delegates.main.my;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.utravel.app.R;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.Util;

public class MyXinShouDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private AppCompatImageView iv_back, iv_bg;

    @Override
    public boolean setIsDark() {  return false; }

    @Override
    public Object setLayout() { return R.layout.delegate_my_xinshou; }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewParams();
        initListener();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        loadProcess();
        loadImage();
    }

    private void initViews(View rootView) {
        iv_bg = (AppCompatImageView) rootView.findViewById(R.id.iv_bg);
        iv_back = (AppCompatImageView) rootView.findViewById(R.id.iv_back);
    }

    private void initViewParams() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = DensityUtil.dp2px(getContext(), 44);
        params.width = DensityUtil.dp2px(getContext(), 44);
        params.topMargin = Util.getStatusBarHeight(getContext());
        iv_back.setLayoutParams(params);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if ( v==iv_back ) {
            pop();
        }
    }

    private void loadImage() {
        Glide.with(_mActivity).load(R.mipmap.xinshoubg).asBitmap().skipMemoryCache(true).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> arg1) {
                dismissLoadProcess();
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                params.width = screenWidth;
                params.height = screenWidth*height/width;
                iv_bg.setLayoutParams(params);
                iv_bg.setImageBitmap(bitmap); //显示图片
            }
        });
    }
}
