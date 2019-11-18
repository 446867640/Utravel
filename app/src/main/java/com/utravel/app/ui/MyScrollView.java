package com.utravel.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {
    private  ScrollViewListener mScrollViewListener;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        mScrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mScrollViewListener != null) {
            mScrollViewListener.onScrollChanged(this,l,t,oldl,oldt);
        }
    }

    /**
     * 防止触摸被拦截
     * 比如，父scrollview嵌套子scrollview，如果不设置，就会出现父的能滑动，子的不能滑动，即父子控件滑动冲突
     * 设置后：
     * 手指放在子控件上面，滑动就能子动，父不动
     * 手指放在子控件之外的父控件上面，滑动就能父动，子不动
     *
     * 如果遇到  这种冲突的问题，再打开，暂不用到，所以先注释
     */
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        getParent().requestDisallowInterceptTouchEvent(true);
//        return super.onInterceptTouchEvent(ev);
//    }

    /**
     * 定义一个监听器
     */
    public interface ScrollViewListener {
        void onScrollChanged(MyScrollView scrollView, int x, int y, int oldx, int oldy);
    }
}



