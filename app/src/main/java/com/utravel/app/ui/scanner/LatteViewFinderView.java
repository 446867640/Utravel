package com.utravel.app.ui.scanner;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import me.dm7.barcodescanner.core.ViewFinderView;

public class LatteViewFinderView extends ViewFinderView {
    public LatteViewFinderView(Context context) { this(context,null); }
    public LatteViewFinderView(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
        mSquareViewFinder = true;          //扫描框是否为正方形
        mBorderPaint.setColor(Color.RED);   //边框颜色
        mLaserPaint.setColor(Color.YELLOW); //主题色
    }
}