package com.utravel.app.ui.scanner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.utils.callback.CallbackManager;
import com.utravel.app.utils.callback.CallbackType;
import com.utravel.app.utils.callback.IGlobalCallback;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class ScannerDelegate extends LatterSwipeBackDelegate implements ZBarScannerView.ResultHandler {
    private ScanView mScanView = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mScanView == null) {
            mScanView = new ScanView(getContext());
        }
        mScanView.setAutoFocus(true);
        mScanView.setResultHandler(this);
    }

    @Override
    public boolean setIsDark() { return false; }

    @Override
    public Object setLayout() {
        return mScanView;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {}

    @Override
    public void handleResult(Result result) {
        @SuppressWarnings("unchecked")
        final IGlobalCallback<String> callback = CallbackManager
            .getInstance()
            .getCallback(CallbackType.ON_SCAN);
        if (callback != null) {
            callback.executeCallback(result.getContents());
        }
        pop();     //出栈，相当finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mScanView != null) {
            mScanView.startCamera();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mScanView != null) {
            mScanView.stopCameraPreview();
            mScanView.stopCamera();
        }
    }
}
