package com.utravel.app.delegates.setting;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.utravel.app.R;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.delegates.login.LoginChoiceDelegate;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import com.utravel.app.utils.timer.BaseTimerTask;
import com.utravel.app.utils.timer.ITimerListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import okhttp3.Call;

public class ChangePhoneOneDelegate extends LatterSwipeBackDelegate implements View.OnClickListener, ITimerListener {
    View status_bar;
    AppCompatImageView iv_back;
    AppCompatTextView tv_title;
    AppCompatTextView tv_phone;
    AppCompatEditText et_login;
    AppCompatEditText et_phone;
    AppCompatEditText et_yanzhengma;
    AppCompatTextView tv_yanzhengma;
    AppCompatTextView tv_ok;

    Timer mTimer = null;
    int timeCount = 60;
    private static final String AER_MOBILE = "MOBILE";
    public String mobile  = null;

    @Override
    public boolean setIsDark() {
        return true;
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_setting_change_phone1;
    }

    public static ChangePhoneOneDelegate newInstance(String title) {
        ChangePhoneOneDelegate fragment = new ChangePhoneOneDelegate();
        Bundle bundle = new Bundle();
        bundle.putString(AER_MOBILE, title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mobile = bundle.getString(AER_MOBILE);
        }
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewParams();
        initListener();
    }

    private void initViews(View rootView) {
        status_bar = (View) rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView)rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);
        tv_phone = (AppCompatTextView) rootView.findViewById(R.id.tv_phone);
        et_login = (AppCompatEditText) rootView.findViewById(R.id.et_login);
        et_phone = (AppCompatEditText) rootView.findViewById(R.id.et_phone);
        et_yanzhengma = (AppCompatEditText) rootView.findViewById(R.id.et_yanzhengma);
        tv_yanzhengma = (AppCompatTextView) rootView.findViewById(R.id.tv_yanzhengma);
        tv_ok = (AppCompatTextView) rootView.findViewById(R.id.tv_ok);
    }

    private void initViewParams() {
        tv_title.setText("修改手机号");
        if (mobile!=null) {
            tv_phone.setText(mobile);
        }
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        tv_yanzhengma.setOnClickListener(this);
        tv_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) { //返回
            pop();
        }else if (v==tv_yanzhengma) { //获取验证码
            if ( mobile==null ) {
                show("错误的手机号");
                return;
            }
            loadProcess();
            sendSms(mobile);
        }else if (v==tv_ok) { //确认
            if (TextUtils.isEmpty(et_login.getText())) {
                show("请输入登陆密码");
                return;
            }
            if (TextUtils.isEmpty(et_phone.getText())) {
                show("请输入更换的手机号");
                return;
            }
            if (TextUtils.isEmpty(et_yanzhengma.getText())) {
                show("请输入验证码");
                return;
            }
            loadProcess();
            patchMoblie();
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

    private void patchMoblie() {
        String url = Config.CHANGE_PHONE;//请求接口url
        String apiName = "修改手机号码接口";//接口名
        Map<String, String> addHeader = new HashMap<String, String>();//请求头
        Map<String, String> addParams = new HashMap<String, String>();//请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addHeader.put("Content-Type", "application/x-www-form-urlencoded");
        addParams.put("password", et_login.getText().toString());
        addParams.put("mobile", et_phone.getText().toString());
        addParams.put("sms_code", et_yanzhengma.getText().toString());
        addParams.put("time", System.currentTimeMillis()+"");

        NetConnectionNew.patch(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    JSONObject json = JSON.parseObject(arg0);
                    if (json!=null) {
                        if (json.containsKey("error")) {
                            show(json.getString("error"));
                            return;
                        }
                    }else {
                        show("修改成功");
                        SharedPreferencesUtil.clearlogin(getContext());
                        getSupportDelegate().startWithPop(new LoginChoiceDelegate());
                    }
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            show(Config.ERROR401);
                            SharedPreferencesUtil.clearlogin(getContext());
                            getSupportDelegate().startWithPop(new LoginChoiceDelegate());
                        }
                    }
                }
            });
    }
}
