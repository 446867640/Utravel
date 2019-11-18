package com.utravel.app.delegates.main.shequ;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import com.utravel.app.R;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.delegates.main.OnBackDelegate;
import com.utravel.app.utils.Util;

public class SortDelegate1 extends OnBackDelegate {
    View status_bar;
    AppCompatImageView iv_back;
    AppCompatTextView tv_title;

    private int mCurrentPosition = 0;
    public static final String SORT_LEFT_POSITION = "SORT_LEFT_POSITION";

    @Override
    public Object setLayout() {
        return R.layout.delegate_sort;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(savedInstanceState,rootView);
        initStatusBar();
        initChildDelegate();
    }

    private void initChildDelegate() {
        if (findChildFragment(SortLeftDelegate1.class) == null ) {
            loadRootFragment(R.id.vertical_list_container,SortLeftDelegate1.newInstance(mCurrentPosition));
            loadRootFragment(R.id.sort_content_container,SortRightDelegate1.newInstance(mCurrentPosition),false,false );
        }
    }

    private void initViews(Bundle savedInstanceState, View rootView) {
        status_bar = (View) rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView)rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);
    }

    private void initStatusBar() {
        tv_title.setText("分类");
        iv_back.setVisibility(View.GONE);

        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
    }

    public void switchContentFragment(SortRightDelegate1 fragment) {
        LatterDelegate contentFragment = findChildFragment(SortRightDelegate1.class);
        if (contentFragment != null) {
            contentFragment.replaceFragment(fragment, false);
        }
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        Util.setStatusBarMode(_mActivity, true);
    }
}