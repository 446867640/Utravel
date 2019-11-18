package com.utravel.app.ui.zxing.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.utravel.app.R;
import com.utravel.app.utils.callback.CallbackManager;
import com.utravel.app.utils.callback.CallbackType;
import com.utravel.app.utils.callback.IGlobalCallback;

public class CaptureActivity1 extends FragmentActivity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        CaptureFragment captureFragment = new CaptureFragment();
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_zxing_container, captureFragment).commit();
        captureFragment.setCameraInitCallBack(new CaptureFragment.CameraInitCallBack() {
            @Override
            public void callBack(Exception e) {
                if (e == null) {

                } else {
                    Log.e("TAG", "callBack: ", e);
                }
            }
        });

    }
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            final IGlobalCallback<String> callback = CallbackManager
                    .getInstance()
                    .getCallback(CallbackType.ON_SCAN);
            if (callback != null) {
                callback.executeCallback(result);
            }
            CaptureActivity1.this.finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Toast.makeText(CaptureActivity1.this,"扫描失败",Toast.LENGTH_SHORT).show();
            CaptureActivity1.this.finish(); 
        }
    };
}
