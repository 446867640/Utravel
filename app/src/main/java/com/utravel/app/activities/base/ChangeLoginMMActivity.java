package com.utravel.app.activities.base;

import android.graphics.Color;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.utravel.app.R;
import com.utravel.app.activities.proxy.LoginActivity;
import com.utravel.app.config.Config;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import com.utravel.app.utils.timer.BaseTimerTask;
import com.utravel.app.utils.timer.ITimerListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import okhttp3.Call;

public class ChangeLoginMMActivity extends BaseActivity implements View.OnClickListener, ITimerListener {
    private View status_bar;
    private AppCompatImageView iv_back;
    private AppCompatTextView tv_title;
    private AppCompatEditText et_phone;
    private AppCompatEditText et_yanzhengma;
    private AppCompatTextView tv_yanzhengma;
    private AppCompatEditText et_mima;
    private AppCompatEditText et_mima_again;
    private AppCompatTextView tv_ok;

    Timer mTimer = null;
    int timeCount = 60;
    private String forgetType = null;

    @Override
    public boolean setIsDark() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.delegate_setting_change_login_password;
    }

    @Override
    protected void initParams() {
        initViews();
        initViewParams();
        initListener();
    }

    private void initViews() {
        status_bar = (View) findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView)findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) findViewById(R.id.tv_title);
        et_phone = (AppCompatEditText) findViewById(R.id.et_phone);
        et_yanzhengma = (AppCompatEditText) findViewById(R.id.et_yanzhengma);
        tv_yanzhengma = (AppCompatTextView) findViewById(R.id.tv_yanzhengma);
        et_mima = (AppCompatEditText) findViewById(R.id.et_mima);
        et_mima_again = (AppCompatEditText) findViewById(R.id.et_mima_again);
        tv_ok = (AppCompatTextView) findViewById(R.id.tv_ok);
    }

    private void initViewParams() {
        if (!TextUtils.isEmpty(getIntent().getStringExtra("type"))) {
            forgetType = getIntent().getStringExtra("type");
            if (forgetType.equals("forget")) {
                tv_title.setText("忘记密码");
            }else {
                tv_title.setText("修改密码");
            }
        }
        if (Util.isToken(this)) {
            et_phone.setText(SharedPreferencesUtil.getString(this,"mobile"));
        }
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(this);
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
            finish();
        }else if (v==tv_yanzhengma) { //获取验证码
            if (TextUtils.isEmpty(et_phone.getText())) {
                show("错误的手机号");
                return;
            }
            loadProcess();
            sendSms(et_phone.getText().toString());
        }else if (v==tv_ok) { //确定
            if (et_phone.getText().toString().isEmpty()) {
                Toast.makeText(this,"请输入新手机号码",Toast.LENGTH_SHORT).show();
                return;
            }
            if (et_phone.getText().toString().length()!=11) {
                Toast.makeText(this,"错误的手机号",Toast.LENGTH_SHORT).show();
                return;
            }
            if (et_yanzhengma.getText().toString().isEmpty()) {
                Toast.makeText(this,"请输入验证码",Toast.LENGTH_SHORT).show();
                return;
            }
            if (et_mima.getText().toString().isEmpty()) {
                Toast.makeText(this,"请输入新密码",Toast.LENGTH_SHORT).show();
                return;
            }
            if (et_mima.getText().toString().length()<6) {
                Toast.makeText(this,"密码至少6位",Toast.LENGTH_SHORT).show();
                return;
            }
            if ( !Util.isToken(this) ) {
                show401();
            }
            loadProcess();
            putPassWordData();
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
        ChangeLoginMMActivity.this.runOnUiThread(new Runnable() {
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

    private void putPassWordData() {
        String url = Config.PASSWORD; //接口url
        String apiName = "忘记密码接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Content-Type", "application/x-www-form-urlencoded");
        addParams.put("mobile", et_phone.getText().toString());
        addParams.put("sms_code", et_yanzhengma.getText().toString());
        addParams.put("password", et_mima.getText().toString());
        addParams.put("password_confirmation", et_mima_again.getText().toString());
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.put(apiName, this, url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processPassWordData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            showMsg401();
                            finish();
                        }
                    }
                }
            });
    }

    private void processPassWordData (String arg0){
        JSONObject json = JSON.parseObject(arg0);
        if (json!=null && json.containsKey("error")) {
            show(json.getString("error"));
            return;
        }
        show("重置成功");
        finish();
    }

    private void sendSms(String mobile) {
        String url = Config.SEND_SMS_URL; //请求接口url
        String apiName = "发送短信验证码接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Content-Type", "application/x-www-form-urlencoded");
        addParams.put("mobile", mobile);
        NetConnectionNew.post( apiName, this, url, addHeader, addParams,
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
}
