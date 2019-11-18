package com.utravel.app.web;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;

import com.utravel.app.R;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.utils.Util;

import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

public class WebRootDelegate extends LatterSwipeBackDelegate {
    private View status_bar;

    private static final String WEB_URL = "WEB_URL";
    private String webUrl = null;

    public static WebRootDelegate newInstance(String url) {
        WebRootDelegate fragment = new WebRootDelegate();
        Bundle bundle = new Bundle();
        bundle.putString(WEB_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            webUrl = bundle.getString(WEB_URL);
        }
    }

    @Override
    public boolean setIsDark() { return true; }

    @Nullable
    @Override
    public Object setLayout() { return R.layout.delegate_web_root; }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
//        initViewParams();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        final WebDelegateImpl mWebViewClientImpl = WebDelegateImpl.creat(webUrl);
        loadRootFragment(R.id.fl_web_root, mWebViewClientImpl);
    }

    private void initViews(View rootView) {
        status_bar = (View) rootView.findViewById(R.id.status_bar);
    }

    private void initViewParams() {
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(getContext());
        status_bar.setLayoutParams(params);
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }




}
