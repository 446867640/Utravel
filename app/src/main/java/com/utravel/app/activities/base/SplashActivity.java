package com.utravel.app.activities.base;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.activities.proxy.LoginActivity;
import com.utravel.app.activities.proxy.MainActivity;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.login.LoginChoiceDelegate;
import com.utravel.app.delegates.main.MainDelegate;
import com.utravel.app.entity.MemeberEntity;
import com.utravel.app.entity.VersionBean;
import com.utravel.app.latte.Latte;
import com.utravel.app.service.DownAPKService;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;

public class SplashActivity extends BaseActivity{
    private Handler handler = new Handler();
    private ImageView iv1;
    private ImageView iv2;
    private RelativeLayout rl_loading;
    private String flagState = "cancel"; // ok,cancel

    @Override
    public boolean setIsDark() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.delegate_splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }
    }

    @Override
    protected void initParams() {
        initViews();
        loadImage(iv1, R.mipmap.qidong);
        checkVersion();
    }

    private void initViews() {
        iv1 = (ImageView) findViewById(R.id.iv1);
        iv2 = (ImageView) findViewById(R.id.iv2);
        rl_loading = (RelativeLayout) findViewById(R.id.rl_loading);
    }

    private void loadImage(ImageView iv, int resourcelId) {
        Glide.with(this).load(resourcelId).crossFade(1000).into(iv);
    }

    public void enterMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    public void handGoToMain() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!Util.isToken(SplashActivity.this)) {
                    goToNextAty(LoginActivity.class);
                    finish();
                    SharedPreferencesUtil.putBoolean(SplashActivity.this, "isSplashCome", true);
                    return;
                }
                loadProcess();
                getMemberData();
            }
        }, 1500);
    }

    private void checkVersion() {
        String url = Config.FIR_API_URL; //接口url
        String apiName = "检测版本更新接口"; // 接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, SplashActivity.this, url, addHeader, addParams,
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
        }
    }

    private void showPopUpdate(final VersionBean bean) {
        View parent = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        View popView = View.inflate(this, R.layout.pop_update, null);
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
                        finish();
                        Process.killProcess(Process.myPid());
                        System.exit(0);
                        break;
                    case R.id.tv_exit_ok:
                        flagState = "ok";
                        Util.deleAPKFile(SplashActivity.this);
                        Intent intent = new Intent(SplashActivity.this, DownAPKService.class);
                        //设置服务器下载地址
                        intent.putExtra("apk_url", Config.APK_DOWNLOAD_URL);
                        startService(intent);
                        Toast.makeText(SplashActivity.this, "正在后台更新，请稍等...", Toast.LENGTH_LONG).show();
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
                    Glide.with(SplashActivity.this)
                            .load(R.mipmap.jiazai2)
                            .asGif()
                            .dontAnimate() //去掉显示动画
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE) //DiskCacheStrategy.NONE
                            .into(iv2);
                }else {//结束当前的Activity、进程、虚拟机
                    finish();
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

    private void getMemberData() {
        String url = Config.DASHBOARD; //请求接口url
        String apiName = "判断用户是否登录接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(SplashActivity.this, "Token"));
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, SplashActivity.this, url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String result, int arg1) {
                    dismissLoadProcess();
                    parseGRZXData(result);
                }
            },
            new NetConnectionNew.FailCallback(){
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    goToNextAty(LoginActivity.class);
                    SharedPreferencesUtil.putBoolean(SplashActivity.this, "isSplashCome", true);
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            SharedPreferencesUtil.clearlogin(SplashActivity.this);
                            SharedPreferencesUtil.putBoolean(SplashActivity.this, "isSplashCome", true);
                            goToNextAty(LoginActivity.class);
                        }
                    }
                }
            });
    }

    private void parseGRZXData(String result) {
        JSONObject json = JSON.parseObject(result);
        if (json != null) {
            if (json.containsKey("error")) {
                show(json.getString("error"));
                return;
            }
            enterMainActivity();
        }
    }
}
