package com.utravel.app.ui.zxing;

import android.content.Context;

/**
 * Created by aaron on 16/8/3.
 */
public class DisplayUtil {

    public static int screenWidthPx;
    public static int screenhightPx;
    public static float density;
    public static int densityDPI;
    public static float screenWidthDip;
    public static float screenHightDip;

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
