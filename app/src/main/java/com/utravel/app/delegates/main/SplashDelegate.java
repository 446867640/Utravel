package com.utravel.app.delegates.main;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.entity.VersionBean;
import com.utravel.app.latte.Latte;
import com.utravel.app.service.DownAPKService;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.Util;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;

public class SplashDelegate extends LatterDelegate {
    private Handler handler = new Handler();
    private ImageView iv1;
    private ImageView iv2;
    private RelativeLayout rl_loading;
    private String flagState = "cancel"; // ok,cancel

    @Nullable
    @Override
    public Object setLayout() {
        return R.layout.delegate_splash;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        loadImage(iv1, R.mipmap.qidong);
        checkVersion();
    }

    private void initViews(View rootView) {
        iv1 = (ImageView) rootView.findViewById(R.id.iv1);
        iv2 = (ImageView) rootView.findViewById(R.id.iv2);
        rl_loading = (RelativeLayout) rootView.findViewById(R.id.rl_loading);
    }

    private void loadImage(ImageView iv, int resourcelId) {
        Glide.with(getContext()).load(resourcelId).crossFade(1000).into(iv);
//        Glide.with(getContext()).load(resourcelId).into(iv);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler!=null) {
            handler.removeCallbacksAndMessages(null);
            handler=null;
        }
        dismissLoadProcess();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if (handler!=null) {
            handler.removeCallbacksAndMessages(null);
            handler=null;
        }
        dismissLoadProcess();
    }

    public void handGoToMain() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportDelegate().start(new MainDelegate());
            }
        }, 1000);
    }

    private void checkVersion() { //检测版本更新接口
        String url = Config.FIR_API_URL; //接口url
        String apiName = "检测版本更新接口"; // 接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        processVersionData(arg0);
                    }
                }, new NetConnectionNew.FailCallback() {
                    @Override
                    public void onFail(Call arg0, Exception arg1, int arg2) {
                        handGoToMain();
                    }
                });
    }

    private void processVersionData(String result) {
        VersionBean bean = new Gson().fromJson(result, VersionBean.class);
        int version = Integer.parseInt(bean.getVersion());
        int versionName = Integer.parseInt(Util.getVersionInfo(Latte.getApplicationContext())[0]);
        if (version > versionName) { //有版本更新了，请下载
            showPopUpdate(bean);
        } else { //没有版本更新，今日首页
            handGoToMain();
//            getSupportDelegate().start(new MainDelegate());
        }
    }

    private void showPopUpdate(final VersionBean bean) {
        View parent = ((ViewGroup) _mActivity.findViewById(android.R.id.content)).getChildAt(0);
        View popView = View.inflate(getContext(), R.layout.pop_update, null);
        TextView tv_exit_cancel = (TextView) popView.findViewById(R.id.tv_exit_cancel);
        TextView tv_exit_ok = (TextView) popView.findViewById(R.id.tv_exit_ok);
        TextView tv1 = (TextView) popView.findViewById(R.id.tv1);
        TextView tv2 = (TextView) popView.findViewById(R.id.tv2);
        tv1.setText("版本号：" + bean.getVersionShort());
        tv2.setText(bean.getChangelog());
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        final PopupWindow popWindow = new PopupWindow(popView, width, height);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);// 设置同意在外点击消失
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_exit_cancel:
                        flagState = "cancel";
                        // 结束当前的Activity、进程、虚拟机
                        _mActivity.finish();
                        Process.killProcess(Process.myPid());
                        System.exit(0);
                        break;
                    case R.id.tv_exit_ok:
                        flagState = "ok";
                        Util.deleAPKFile(_mActivity);
                        Intent intent = new Intent(_mActivity, DownAPKService.class);
                        //设置服务器下载地址
                        intent.putExtra("apk_url",Config.APK_DOWNLOAD_URL);
                        getContext().startService(intent);
                        Toast.makeText(getContext(), "正在后台更新，请稍等...", Toast.LENGTH_LONG).show();
                        break;
                }
                popWindow.dismiss();
            }
        };
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (flagState.equals("ok")) {
                    //不做操作，不允许进入首页
                    rl_loading.setVisibility(View.VISIBLE);
                    Glide.with(_mActivity)
                            .load(R.mipmap.jiazai2)
                            .asGif()
                            .dontAnimate() //去掉显示动画
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE) //DiskCacheStrategy.NONE
                            .into(iv2);
                } else {//结束当前的Activity、进程、虚拟机
                    _mActivity.finish();
                    Process.killProcess(Process.myPid());
                    System.exit(0);
                }
            }
        });
        tv_exit_cancel.setOnClickListener(listener);
        tv_exit_ok.setOnClickListener(listener);
        ColorDrawable dw = new ColorDrawable(0x90000000);
        popWindow.setBackgroundDrawable(dw);
        popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }
}
