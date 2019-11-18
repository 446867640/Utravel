package com.utravel.app.delegates.login;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.utravel.app.R;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.delegates.detail.GoodsInfoDelegate;
import com.utravel.app.delegates.main.MainDelegate;
import com.utravel.app.ui.zxing.activity.CaptureActivity1;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.Util;
import com.utravel.app.utils.callback.CallbackManager;
import com.utravel.app.utils.callback.CallbackType;
import com.utravel.app.utils.callback.IGlobalCallback;
import com.utravel.app.utils.timer.BaseTimerTask;
import com.utravel.app.utils.timer.ITimerListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import okhttp3.Call;

public class ZhuCeDelegate1 extends LatterSwipeBackDelegate implements View.OnClickListener, ITimerListener {
    AppCompatImageView iv_back;
    AppCompatTextView tv_zhuce;
    AppCompatImageView iv_yaoqingma;
    AppCompatImageView iv_biyan;
    AppCompatImageView iv_biyan1;
    AppCompatTextView tv_yanzhengma;
    TextInputEditText et_yaoqingma;
    TextInputEditText et_phone;
    TextInputEditText et_yanzhengma;
    TextInputEditText et_mima;
    TextInputEditText et_mima1;

    Timer mTimer = null;
    int timeCount = 60;
    boolean isVisible = false;
    boolean isVisible1 = false;

    @Override
    public Object setLayout() {
        return R.layout.delegate_login_zhuce;
    }

    @Override
    public boolean setIsDark() {
        return true;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewParams();
        CallbackManager.getInstance().addCallback(CallbackType.ON_SCAN, new IGlobalCallback<String>() {
            @Override
            public void executeCallback(String args) {
                LatteLogger.e("扫码信息", args);
                if (!TextUtils.isEmpty(args)) {
                    String code = Util.analyzingCode(args);
                    et_yaoqingma.setText(code);
                }
            }
        });
    }

    private void initViews(View rootView) {
        iv_back = (AppCompatImageView) rootView.findViewById(R.id.iv_back);
        tv_zhuce = (AppCompatTextView) rootView.findViewById(R.id.tv_zhuce);
        iv_yaoqingma = (AppCompatImageView) rootView.findViewById(R.id.iv_yaoqingma);
        iv_biyan = (AppCompatImageView) rootView.findViewById(R.id.iv_biyan);
        iv_biyan1 = (AppCompatImageView) rootView.findViewById(R.id.iv_biyan1);
        tv_yanzhengma = (AppCompatTextView) rootView.findViewById(R.id.tv_yanzhengma);
        et_yaoqingma = (TextInputEditText)rootView.findViewById(R.id.et_yaoqingma);
        et_phone = (TextInputEditText)rootView.findViewById(R.id.et_phone);
        et_yanzhengma = (TextInputEditText)rootView.findViewById(R.id.et_yanzhengma);
        et_mima = (TextInputEditText)rootView.findViewById(R.id.et_mima);
        et_mima1 = (TextInputEditText)rootView.findViewById(R.id.et_mima1);
        iv_back.setOnClickListener(this);
        iv_yaoqingma.setOnClickListener(this);
        tv_yanzhengma.setOnClickListener(this);
        iv_biyan.setOnClickListener(this);
        iv_biyan1.setOnClickListener(this);
        tv_zhuce.setOnClickListener(this);
    }

    private void initViewParams() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        params.width = DensityUtil.dp2px(_mActivity, 44);
        params.height = DensityUtil.dp2px(_mActivity, 44);
        params.topMargin = Util.getStatusBarHeight(_mActivity);
        params.leftMargin = DensityUtil.dp2px(_mActivity, 10);
        iv_back.setLayoutParams(params);
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) {//返回
            pop();
        }else if(v==iv_yaoqingma){//邀请码
//            loadProcess();
//            getYaoQingMaData();
            goToNextAty(CaptureActivity1.class);
        }else if(v==tv_yanzhengma){//获取验证码
            loadProcess();
            sendSms(et_phone.getText().toString());
        }else if(v==iv_biyan){//密码是否可见
            if (isVisible) {
                //可见--不可见
                isVisible = !isVisible;
                iv_biyan.setImageResource(R.mipmap.biyan);
                et_mima.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }else {
                //不可见--可见
                isVisible = !isVisible;
                iv_biyan.setImageResource(R.mipmap.kaiyan);
                et_mima.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
        }else if(v==iv_biyan1){//密码是否可见
            if (isVisible1) {
                //可见--不可见
                isVisible1 = !isVisible1;
                iv_biyan1.setImageResource(R.mipmap.biyan);
                et_mima1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }else {
                //不可见--可见
                isVisible1 = !isVisible1;
                iv_biyan1.setImageResource(R.mipmap.kaiyan);
                et_mima1.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
        }else if (v==tv_zhuce) {//注册
            if (et_yaoqingma.getText().toString().isEmpty()) {
                Toast.makeText(_mActivity,"请输入邀请码！",Toast.LENGTH_SHORT).show();
                return;
            }
            if (et_phone.getText().toString().isEmpty()) {
                Toast.makeText(_mActivity,"请输入手机！",Toast.LENGTH_SHORT).show();
                return;
            }
            if (et_phone.getText().toString().length() != 11) {
                Toast.makeText(_mActivity,"错误的手机号！",Toast.LENGTH_SHORT).show();
                return;
            }
            if (et_yanzhengma.getText().toString().isEmpty()) {
                Toast.makeText(_mActivity,"请输入验证码！",Toast.LENGTH_SHORT).show();
                return;
            }
            if (et_mima.getText().toString().isEmpty()) {
                Toast.makeText(_mActivity,"请输入密码！",Toast.LENGTH_SHORT).show();
                return;
            }
            if (et_mima.getText().toString().length()<6) {
                Toast.makeText(_mActivity,"密码不能少于6位！",Toast.LENGTH_SHORT).show();
                return;
            }
            if (et_mima1.getText().toString().isEmpty()) {
                Toast.makeText(_mActivity,"请输入密码！",Toast.LENGTH_SHORT).show();
                return;
            }
            if (et_mima1.getText().toString().length()<6) {
                Toast.makeText(_mActivity,"密码不能少于6位！",Toast.LENGTH_SHORT).show();
                return;
            }
            if(!et_mima1.getText().toString().equals(et_mima.getText().toString())){
                Toast.makeText(_mActivity,"两次密码不一致！",Toast.LENGTH_SHORT).show();
                return;
            }
            loadProcess();
            register();
        }
    }

    private void initTimer(){
        if (mTimer==null) {
            mTimer = new Timer();
            final BaseTimerTask task = new BaseTimerTask(this);
            mTimer.schedule(task,0,1000);
        }
    }

    @Override
    public void onTimer() {
        getProxyActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mTimer != null) {
                    tv_yanzhengma.setText("获取（" + timeCount + "）s");
                    tv_yanzhengma.setTextColor(Color.parseColor("#CCCCCC"));
                    tv_yanzhengma.setEnabled(false);
                    timeCount--;
                    if (timeCount<0) {
                        if (mTimer != null){
                            timeCount = 60;
                            tv_yanzhengma.setText("重新获取");
                            tv_yanzhengma.setTextColor(Color.parseColor("#2295F6"));
                            tv_yanzhengma.setEnabled(true);
                            mTimer.cancel();
                            mTimer = null;
                        }
                    }
                }
            }
        });
    }

    private void sendSms(String mobile) {
        String url = Config.SEND_SMS_URL; //请求接口url
        String apiName = "发送短信验证码接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Content-Type", "application/x-www-form-urlencoded");
        addParams.put("mobile", mobile);
        NetConnectionNew.post( apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processSendSmsData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                }
            });
    }

    private void processSendSmsData(String arg0) {
        JSONObject json = JSON.parseObject(arg0);
        if (json==null) {
            show("发送中...");
            initTimer();
        }else {
            if (json.containsKey("error")) {
                show(json.getString("error"));
                return;
            }
        }
    }

    private void register() {
        String url = Config.REGISTER_URL; //请求接口url
        String apiName = "注册接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>();//请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Content-Type", "application/x-www-form-urlencoded");
        addParams.put("mobile", et_phone.getText().toString());
        addParams.put("sms_code", et_yanzhengma.getText().toString());
        addParams.put("password", et_mima.getText().toString());
        addParams.put("password_confirmation", et_mima1.getText().toString());
        addParams.put("invitation_code", et_yaoqingma.getText().toString());
        NetConnectionNew.post(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processRegisterData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                }
            });
    }

    private void processRegisterData(String arg0) {
        JSONObject json = JSON.parseObject(arg0);
        if (json.containsKey("error")) {
            show(json.getString("error"));
            return;
        }
        showSuccess("注册成功");
        pop();
    }

    private void getYaoQingMaData() {
        String url = Config.SYSTEM_INVITATION_CODE; //请求接口url
        String apiName = "获取系统邀请码接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>();//请求体
        addHeader.put("Accept", "application/json");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        dismissLoadProcess();
                        processYaoQingMaData(arg0);
                    }
                },
                new NetConnectionNew.FailCallback() {
                    @Override
                    public void onFail(Call arg0, Exception arg1, int arg2) {
                        dismissLoadProcess();
                    }
                });
    }

    private void processYaoQingMaData(String arg0) {
        JSONObject data = JSON.parseObject(arg0).getJSONObject("data");
        String invitation_code = data.getString("invitation_code");
        if (!TextUtils.isEmpty(invitation_code)){
            et_yaoqingma.setText(invitation_code);
        }
    }
}
