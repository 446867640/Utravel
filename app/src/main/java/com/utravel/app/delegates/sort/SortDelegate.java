package com.utravel.app.delegates.sort;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;

import com.utravel.app.R;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.Util;

public class SortDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    View status_bar;
    AppCompatImageView iv_back;
    AppCompatTextView tv_title;

    private int mCurrentPosition = 0;
    public static final String SORT_LEFT_POSITION = "SORT_LEFT_POSITION";

    @Override
    public Object setLayout() {
        return R.layout.delegate_sort;
    }

    public static SortDelegate newInstance(int position) {
        SortDelegate fragment = new SortDelegate();
        Bundle bundle = new Bundle();
        bundle.putInt(SORT_LEFT_POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCurrentPosition = bundle.getInt(SORT_LEFT_POSITION);
            LatteLogger.e("mCurrentPosition=="+mCurrentPosition,"mCurrentPosition=="+mCurrentPosition);
        }
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(savedInstanceState,rootView);
        initStatusBar();
        initChildDelegate();
    }

    private void initChildDelegate() {
        if (findChildFragment(SortLeftDelegate.class) == null ) {
            loadRootFragment(R.id.vertical_list_container,SortLeftDelegate.newInstance(mCurrentPosition));
            loadRootFragment(R.id.sort_content_container,SortRightDelegate.newInstance(mCurrentPosition),false,false );
        }
    }

    private void initViews(Bundle savedInstanceState, View rootView) {
        status_bar = (View) rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView)rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);
        tv_title.setText("分类");
        iv_back.setOnClickListener(this);
    }

    private void initStatusBar() {
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) {
            pop();
        }
    }

    public void switchContentFragment(SortRightDelegate fragment) {
        LatterDelegate contentFragment = findChildFragment(SortRightDelegate.class);
        if (contentFragment != null) {
            contentFragment.replaceFragment(fragment, false);
        }
    }

    @Override
    public boolean setIsDark() {
        return true;
    }
}
