package com.utravel.app.activities.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.utravel.app.R;
import com.utravel.app.config.Config;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class ForgetPayPwdActivity extends BaseActivity implements View.OnClickListener {
    private View status_bar;
    private ImageView iv_back;
    private TextView tv_title;
    private TextView etPhone;
    private EditText etYanzhengma;
    private TextView tvYanzhengma;
    private EditText etPwd;
    private EditText etPwd1;
    private Button btnGo;
    public String has_payment_password;
    //倒计时
    private boolean isGetModify = false;
    private int time = 60;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //如果时间不为0，就true,为0,就false
            isGetModify = true;
            time = time - 1;
            tvYanzhengma.setText("获取（"+ time +"s）");
            handler.removeMessages(0);
            handler.sendEmptyMessageDelayed(0,1000);
            if(time <=0){
                isGetModify=false;
                tvYanzhengma.setText("重新获取");
                tvYanzhengma.setTextColor(Color.parseColor("#F985B3"));
                time = 60;
                //把消息移除
                handler.removeCallbacksAndMessages(null);
            }
        }
    };

    @Override
    public boolean setIsDark() {  return true;  }

    @Override
    protected int getLayoutId() { return R.layout.activity_forget_pay_pwd; }

    @Override
    protected void initParams() {
        initViews();
        initViewParams();
        initListener();
    }

    private void initViews() {
        status_bar = (View) findViewById(R.id.status_bar);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        etPhone = (TextView)findViewById( R.id.et_phone );
        etPhone.setText(SharedPreferencesUtil.getString(ForgetPayPwdActivity.this, "mobile"));
        etYanzhengma = (EditText)findViewById( R.id.et_yanzhengma );
        tvYanzhengma = (TextView)findViewById( R.id.tv_yanzhengma );
        etPwd = (EditText)findViewById( R.id.et_pwd );
        etPwd1 = (EditText)findViewById( R.id.et_pwd1 );
        btnGo = (Button)findViewById( R.id.btn_go );
    }

    private void initViewParams() {
        tv_title.setText("重置支付密码");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(this);
        status_bar.setLayoutParams(params);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        tvYanzhengma.setOnClickListener( this );
        btnGo.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {
        if ( v == iv_back ) {
            finish();
        }else if ( v == tvYanzhengma ) {
            if(TextUtils.isEmpty(etPhone.getText())){
                show("请输入手机号");
                return;
            }else {
                if(isGetModify){
                    return;
                }else {
                    //网络请求验证码
                    sendSms(etPhone.getText().toString());
                }
            }
        }else if ( v == btnGo ) {
            if(TextUtils.isEmpty(etPhone.getText())){
                show("请输入手机号");
                return;
            }
            if(TextUtils.isEmpty(etYanzhengma.getText())){
                show("请输入验证码");
                return;
            }
            if(TextUtils.isEmpty(etPwd.getText()) || etPwd.getText().length()!=6){
                show("请输入6位支付密码");
                return;
            }
            if(TextUtils.isEmpty(etPwd1.getText()) || etPwd1.getText().length()!=6){
                show("请输入6位支付密码");
                return;
            }
            if(!etPwd.getText().toString().equals(etPwd1.getText().toString())){
                show("两次支付密码不一致");
                return;
            }
            loadProcess();
            baocunPayPwd();
        }
    }

    @Override
    protected synchronized void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void sendSms(String str) {
        String url = Config.SEND_SMS_URL;
        final String apiName = "发送短信验证码接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Content-Type", "application/x-www-form-urlencoded");
        addParams.put("mobile", str);
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.post( apiName, ForgetPayPwdActivity.this,  url, addHeader, addParams,
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
                            show("发送中...");
                            tvYanzhengma.setTextColor(Color.parseColor("#cdcdcd")); //按钮变暗
                            handler.sendEmptyMessageDelayed(0,1000); //发送倒计时消息
                        }else {
                            show("发送中...");
                            tvYanzhengma.setTextColor(Color.parseColor("#cdcdcd")); //按钮变暗
                            handler.sendEmptyMessageDelayed(0,1000); //发送倒计时消息
                        }
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

    private void baocunPayPwd() {
        String url = Config.PUT_RESET_PAYPWD;
        String apiName = "修改支付密码接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(ForgetPayPwdActivity.this, "Token"));
        addHeader.put("Content-Type", "application/x-www-form-urlencoded");
        addParams.put("mobile", etPhone.getText().toString());
        addParams.put("sms_code", etYanzhengma.getText().toString());
        addParams.put("payment_password", etPwd.getText().toString());
        addParams.put("payment_password_confirmation", etPwd1.getText().toString());
        NetConnectionNew.put(apiName, ForgetPayPwdActivity.this, url,addHeader, addParams,
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
                        show("重置成功");
                        Intent intent = new Intent(ForgetPayPwdActivity.this, SettingActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        show("重置成功");
                        Intent intent = new Intent(ForgetPayPwdActivity.this,SettingActivity.class);
                        startActivity(intent);
                        finish();
                    }
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
}

