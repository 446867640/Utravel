//package com.utravel.app.ui.zxing.activity;
//
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.util.Log;
//import android.view.View;
//
//import com.utravel.app.R;
//import com.utravel.app.delegates.LatterSwipeBackDelegate;
//
//public class CaptureDelegate extends LatterSwipeBackDelegate {
//    @Override
//    public boolean setIsDark() {return false;}
//
//    @Override
//    public Object setLayout() { return R.layout.camera; }
//
//    @Override
//    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
//        CaptureFragment captureFragment = new CaptureFragment();
//        captureFragment.setAnalyzeCallback(analyzeCallback);
//        loadRootFragment(R.id.fl_zxing_container, captureFragment);
//        captureFragment.setCameraInitCallBack(new CaptureFragment.CameraInitCallBack() {
//            @Override
//            public void callBack(Exception e) {
//                if (e == null) {
//
//                } else {
//                    Log.e("TAG", "callBack: ", e);
//                }
//            }
//        });
//    }
//
//    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
//        @Override
//        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
//
//        }
//        @Override
//        public void onAnalyzeFailed() {
//
//        }
//    };
//
//
//}
