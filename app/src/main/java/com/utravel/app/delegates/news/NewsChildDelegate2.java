package com.utravel.app.delegates.news;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.utravel.app.R;
import com.utravel.app.delegates.LatterDelegate;

public class NewsChildDelegate2 extends LatterDelegate {

    private static final String BUNDLE_TAG = "NewsChildDelegate2";
    private String tag_value = null;

    public static NewsChildDelegate2 newInstance(String title) {
        NewsChildDelegate2 fragment = new NewsChildDelegate2();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TAG, title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            tag_value = bundle.getString(BUNDLE_TAG);
        }
    }


    @Nullable
    @Override
    public Object setLayout() {
        return R.layout.delegate_news_child1;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }


}
