package com.utravel.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MyListView extends ListView {

	public MyListView(Context context) {
		super(context);
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
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
//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		getParent().requestDisallowInterceptTouchEvent(true);
//		return super.onInterceptTouchEvent(ev);
//	}
}
