package com.utravel.app.activities.base;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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

public class ResetPayPwdRZActivity extends BaseActivity implements View.OnClickListener {
    private View status_bar;
    private ImageView iv_back;
    private TextView tv_title;
    private EditText et1;
    private TextView tv_ok;

    @Override
    public boolean setIsDark() {  return true; }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_pay_pwd_rz;
    }

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
        et1 = (EditText) findViewById(R.id.et1);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
    }

    private void initViewParams() {
        tv_title.setText("安全认证");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(this);
        status_bar.setLayoutParams(params);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        tv_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) {
            finish();
        }else if (v==tv_ok) {
            if (TextUtils.isEmpty(et1.getText())) {
                show("身份证号码不能为空");
                return;
            }
            loadProcess();
            putShenFenRenZheng();
        }
    }

    private void putShenFenRenZheng() {
        String url = Config.PUT_SHENFENZHENG;
        String apiName = "安全认证接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(ResetPayPwdRZActivity.this, "Token"));
        addHeader.put("Content-Type", "application/x-www-form-urlencoded");
        addParams.put("identity_number", et1.getText().toString());
        addParams.put("time", System.currentTimeMillis()+"");
        NetConnectionNew.put(apiName, ResetPayPwdRZActivity.this, url,addHeader, addParams,
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
                        Intent intent = new Intent(ResetPayPwdRZActivity.this, ForgetPayPwdActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent = new Intent(ResetPayPwdRZActivity.this,ForgetPayPwdActivity.class);
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