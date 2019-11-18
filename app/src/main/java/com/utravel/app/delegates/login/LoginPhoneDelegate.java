package com.utravel.app.delegates.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.utravel.app.R;
import com.utravel.app.activities.base.ChangeLoginMMActivity;
import com.utravel.app.activities.proxy.MainActivity;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.utils.ActivityStack;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import com.utravel.app.utils.timer.BaseTimerTask;
import com.utravel.app.utils.timer.ITimerListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import okhttp3.Call;

public class LoginPhoneDelegate extends LatterSwipeBackDelegate implements View.OnClickListener, ITimerListener, View.OnKeyListener {
    View status_bar;
    AppCompatImageView iv_back;
    RelativeLayout rv_mima_btn; //密码登陆btn
    AppCompatTextView tv_mima_btn; //密码登陆btn
    View line1;//密码登陆下划线
    RelativeLayout rv_yanzhengme_btn; //验证码登陆btn
    AppCompatTextView tv_yanzhengma_btn; //验证码登陆btn
    View line2;//验证码登陆下划线
    TextInputEditText et_phone;//手机号码编辑框
    RelativeLayout rv_yanzhengma;//验证码输入部分
    TextInputEditText et_yanzhengma;//验证码编辑框
    AppCompatTextView tv_yanzhengma;//获取验证码
    RelativeLayout rv_mima;//密码输入部分
    TextInputEditText et_mima;//密码输入编辑框
    AppCompatImageView iv_biyan;//获取验证码
    AppCompatTextView tv_forget;//忘记密码
    AppCompatTextView tv_login;//登陆

    Timer mTimer = null;
    int timeCount = 60;
    boolean isMima = true;
    boolean isVisible = false;

    @Override
    public Object setLayout() {
        return R.layout.delegate_login_phone;
    }

    @Override
    public boolean setIsDark() {
        return true;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewParams();
        initListener();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (rootView!=null) {
            rootView.setFocusableInTouchMode(true);
            rootView.requestFocus();
            rootView.setOnKeyListener(this);
        }
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if (rootView!=null) {
            rootView.setOnKeyListener(null);
        }
    }

    private void initViews(View rootView) {
        status_bar = (View) rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView) rootView.findViewById(R.id.iv_back);
        rv_mima_btn = (RelativeLayout) rootView.findViewById(R.id.rv_mima_btn);
        tv_mima_btn = (AppCompatTextView) rootView.findViewById(R.id.tv_mima_btn);
        line1 = (View) rootView.findViewById(R.id.line1);
        rv_yanzhengme_btn = (RelativeLayout) rootView.findViewById(R.id.rv_yanzhengme_btn);
        tv_yanzhengma_btn = (AppCompatTextView) rootView.findViewById(R.id.tv_yanzhengma_btn);
        line2 = (View) rootView.findViewById(R.id.line2);
        et_phone = (TextInputEditText) rootView.findViewById(R.id.et_phone);
        rv_yanzhengma = (RelativeLayout) rootView.findViewById(R.id.rv_yanzhengma);
        et_yanzhengma = (TextInputEditText) rootView.findViewById(R.id.et_yanzhengma);
        tv_yanzhengma = (AppCompatTextView) rootView.findViewById(R.id.tv_yanzhengma);
        rv_mima = (RelativeLayout) rootView.findViewById(R.id.rv_mima);
        et_mima = (TextInputEditText) rootView.findViewById(R.id.et_mima);
        iv_biyan = (AppCompatImageView) rootView.findViewById(R.id.iv_biyan);
        tv_forget = (AppCompatTextView) rootView.findViewById(R.id.tv_forget);
        tv_login = (AppCompatTextView) rootView.findViewById(R.id.tv_login);
    }

    private void initViewParams() { //加布局
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
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

    private void initListener() {
        iv_back.setOnClickListener(this);
        rv_mima_btn.setOnClickListener(this);
        rv_yanzhengme_btn.setOnClickListener(this);
        tv_yanzhengma.setOnClickListener(this);
        iv_biyan.setOnClickListener(this);
        tv_forget.setOnClickListener(this);
        tv_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        hideKey(et_mima);
        hideKey(et_phone);
        hideKey(et_yanzhengma);
        if (v==iv_back) {
            pop();
        }else if (v==rv_mima_btn) {//密码登陆
            isMima = true;
            changeBtn(tv_mima_btn,tv_yanzhengma_btn,line1,line2,rv_mima,rv_yanzhengma,isMima);
        }else if (v==rv_yanzhengme_btn) {//验证码登陆
            isMima = false;
            changeBtn(tv_yanzhengma_btn,tv_mima_btn,line2,line1,rv_yanzhengma,rv_mima,isMima);
        }else if (v==tv_yanzhengma) {//获取验证码
            loadProcess();
            sendSms(et_phone.getText().toString());
        }else if (v==iv_biyan) {//明暗密码切换
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
        }else if (v==tv_forget) {//忘记密码
            Intent intent = new Intent(_mActivity, ChangeLoginMMActivity.class);
            intent.putExtra("type", "forget");
            startActivity(intent);
//            getSupportDelegate().start(ChangeLoginMMDelegate.newInstance("forget"));
        }else if (v==tv_login) {//登陆
            if (et_phone.getText().toString().isEmpty()) {
                Toast.makeText(_mActivity,"请输入手机号！",Toast.LENGTH_SHORT).show();
                return;
            }
            if (et_phone.getText().toString().length() != 11) {
                Toast.makeText(_mActivity,"错误的手机号！",Toast.LENGTH_SHORT).show();
                return;
            }
            if (isMima) {
                if (et_mima.getText().toString().isEmpty()) {
                    Toast.makeText(_mActivity,"请输入密码",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (et_mima.getText().toString().length()<6) {
                    Toast.makeText(_mActivity,"密码不能少于6位！",Toast.LENGTH_SHORT).show();
                    return;
                }
                loadProcess();
                sendLogin(et_phone.getText().toString(), et_mima.getText().toString(), null);
            }else{
                if (et_yanzhengma.getText().toString().isEmpty()) {
                    Toast.makeText(_mActivity,"请输入验证码！",Toast.LENGTH_SHORT).show();
                    return;
                }
                loadProcess();
                sendLogin(et_phone.getText().toString(), null, et_yanzhengma.getText().toString());
            }
        }
    }

    private void changeBtn(AppCompatTextView tv1, AppCompatTextView tv2, View v1, View v2, RelativeLayout rv1, RelativeLayout rv2, boolean b) {
        tv1.setTextColor(Color.parseColor("#6B70F7"));
        tv2.setTextColor(Color.parseColor("#1A1A1A"));
        v1.setVisibility(View.VISIBLE);
        v2.setVisibility(View.GONE);
        rv1.setVisibility(View.VISIBLE);
        rv2.setVisibility(View.GONE);
        if (b) {
            tv_forget.setVisibility(View.VISIBLE);
        }else {
            tv_forget.setVisibility(View.GONE);
        }
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

    private void sendLogin(String mobile, String password, String sms_code) {
        String url = Config.AUTH_TOKEN; //请求接口url
        String apiName = "登录接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>();//请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Content-Type", "application/x-www-form-urlencoded");
        addParams.put("mobile", mobile);
        if (password!=null) { addParams.put("password", password); }
        if (sms_code!=null) { addParams.put("sms_code", sms_code); }
        NetConnectionNew.post(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processLoginData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("404")) {
                            show(Config.ERROR_LOGIN_404);
                        }
                    }
                }
            });
    }

    private void processLoginData(String arg0) {
        final JSONObject json = JSON.parseObject(arg0);
        if (json.containsKey("error")) {
            showError(json.getString("error"));
            return;
        }
        int id = json.getJSONObject("data").getInteger("id");
        Util.setTagAndAlias(getContext(), id + "");
        //把相关参数存入缓存
        String mobile = json.getJSONObject("data").getString("mobile");
        String token  = json.getJSONObject("data").getString("token");
        String authorization = "Token token=" + token + ",mobile=" + mobile;
        SharedPreferencesUtil.putString(getContext(), "id", id + "");
        SharedPreferencesUtil.putString(getContext(), "mobile", mobile);
        SharedPreferencesUtil.putString(getContext(), "token", token);
        SharedPreferencesUtil.putString(getContext(), "Token", authorization);
        //隐藏软键盘
        hideKey(et_mima);
        hideKey(et_phone);
        hideKey(et_yanzhengma);
        showSuccess("登录成功");
        if (SharedPreferencesUtil.getBoolean(getContext(), "isSplashCome")) {
            ActivityStack.getInstance().finishActivity(MainActivity.class);
            Intent intent = new Intent(_mActivity, MainActivity.class);
            startActivity(intent);
            SharedPreferencesUtil.putBoolean(_mActivity, "isSplashCome", false);
        }
        pop();
    }

    private long mExitTime = 0;
    private static final int EXIT_TIME = 2000;
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN) {
            long mSecondTime = System.currentTimeMillis();
            final String exit = _mActivity.getResources().getString(R.string.exit);
            if (mSecondTime-mExitTime > EXIT_TIME) {
                Toast.makeText(getContext(), exit, Toast.LENGTH_SHORT).show();
                mExitTime = mSecondTime;
            }else {
                ActivityStack.getInstance().finishAllActivity();
                android.os.Process.killProcess(android.os.Process.myPid()); //结束当前的进程
                System.exit(0);//结束虚拟机
                if (mExitTime!=0) {
                    mExitTime=0;
                }
            }
            return true;
        }
        return false;
    }
}
