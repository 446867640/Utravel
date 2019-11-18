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
import com.utravel.app.activities.proxy.LoginActivity;
import com.utravel.app.config.Config;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class GRChangePayPwdActivity extends BaseActivity implements View.OnClickListener {
    private View status_bar;
    private ImageView iv_back;
    private TextView tv_title;
    private LinearLayout ll_old_pwd;
    private EditText et_old_pwd;
    private TextView tv_pwd;
    private TextView etPhone;
    private EditText etYanzhengma;
    private TextView tvYanzhengma;
    private EditText etPwd;
    private EditText etPwd1;
    private TextView tv_forget_pay;
    private Button btnGo;
    private String old_payment_password="";
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
    public boolean setIsDark() { return true; }

    @Override
    protected int getLayoutId() { return R.layout.activity_grchange_pay_pwd;  }

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
        ll_old_pwd = (LinearLayout)findViewById( R.id.ll_old_pwd );
        et_old_pwd = (EditText)findViewById( R.id.et_old_pwd );
        tv_pwd = (TextView)findViewById( R.id.tv_pwd );
        etPhone = (TextView)findViewById( R.id.et_phone );
        etYanzhengma = (EditText)findViewById( R.id.et_yanzhengma );
        tvYanzhengma = (TextView)findViewById( R.id.tv_yanzhengma );
        etPwd = (EditText)findViewById( R.id.et_pwd );
        etPwd1 = (EditText)findViewById( R.id.et_pwd1 );
        tv_forget_pay = (TextView)findViewById( R.id.tv_forget_pay );
        btnGo = (Button)findViewById( R.id.btn_go );
    }

    private void initViewParams() {
        if (getIntent().getStringExtra("has_payment_password") != null) {
            has_payment_password = getIntent().getStringExtra("has_payment_password");
            if (has_payment_password.equals("0")) {
                //已设置过支付密码
                tv_title.setText("修改支付密码");
                tv_pwd.setText("新支付密码");
                ll_old_pwd.setVisibility(View.VISIBLE);
            }else {
                //为设置过支付密码
                tv_title.setText("设置支付密码");
                tv_pwd.setText("输入支付密码");
                ll_old_pwd.setVisibility(View.GONE);
            }
        }
        etPhone.setText(SharedPreferencesUtil.getString(GRChangePayPwdActivity.this, "mobile"));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(this);
        status_bar.setLayoutParams(params);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        tvYanzhengma.setOnClickListener( this );
        tv_forget_pay.setOnClickListener( this );
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
                    loadProcess();
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
            if (has_payment_password.equals("0")) {
                //已设置过支付密码
                if (TextUtils.isEmpty(et_old_pwd.getText())) {
                    show("旧密码不能为空");
                    return;
                }
                old_payment_password = et_old_pwd.getText().toString();
            }else {
                //未设置过支付密码
                old_payment_password = null;
            }
            loadProcess();
            baocunPayPwd();
        }else if (v == tv_forget_pay) {
            Intent intent = new Intent(GRChangePayPwdActivity.this, ResetPayPwdRZActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected synchronized void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void baocunPayPwd() {
        String url = Config.CHANGE_PAY_PWD;
        String apiName = "修改支付密码接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Content-Type", "application/x-www-form-urlencoded");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(GRChangePayPwdActivity.this, "Token"));
        addParams.put("mobile", SharedPreferencesUtil.getString(GRChangePayPwdActivity.this, "mobile"));
        addParams.put("sms_code", etYanzhengma.getText().toString());
        addParams.put("payment_password",etPwd.getText().toString());
        addParams.put("payment_password_confirmation",etPwd1.getText().toString());
        if (old_payment_password!=null) {
            addParams.put("old_payment_password", old_payment_password);
        }
        NetConnectionNew.put( apiName,GRChangePayPwdActivity.this,url,addHeader,addParams,
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
                        show("修改成功");
                        finish();
                    }else {
                        show("修改成功");
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
                        }
                    }
                }
            });
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
        NetConnectionNew.post( apiName, GRChangePayPwdActivity.this,  url, addHeader, addParams,
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
                }
            });
    }
}
