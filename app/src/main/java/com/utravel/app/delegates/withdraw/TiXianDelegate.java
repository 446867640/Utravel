package com.utravel.app.delegates.withdraw;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.AuthTask;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.activities.base.GRChangePayPwdActivity;
import com.utravel.app.activities.base.PersonRzActivity;
import com.utravel.app.alipay.AuthResult;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.delegates.detail.AccountDetailDelegate;
import com.utravel.app.delegates.login.LoginChoiceDelegate;
import com.utravel.app.entity.OverViewBean;
import com.utravel.app.latte.ConfigType;
import com.utravel.app.latte.Latte;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import com.utravel.app.web.WebDelegateImpl;
import com.utravel.app.wechat.LatteWeChat;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;

public class TiXianDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
    private AppCompatImageView iv_back;
    private AppCompatTextView tv_total;
    private AppCompatEditText et_zhanghao;
    private AppCompatEditText et_name;
    private AppCompatEditText et_money;
    private EditText et_money_old;
    private AppCompatTextView tv_ok;
    private LinearLayoutCompat ll_detail;

    private EditText etMoney;//提现金额
    private LinearLayout ll_day1;//到账日T+1栏
    private LinearLayout ll_day2;//到账日T+7栏
    private LinearLayout ll_weixin;//到账平台-微信栏
    private LinearLayout ll_zhifubao;//到账平台-支付宝栏
    private ImageView iv_day1;//到账日T+1是否选中
    private ImageView iv_day2;//到账日T+7是否选中
    private ImageView iv_weixin1;//到账平台-微信是否选中
    private ImageView iv_zhifubao2;//到账平台-支付宝是否选中
    private TextView tv_del;//解除支付宝绑定

    private String arrival_periods = "1";//到账日
    private String channel = "alipay";//到账平台
    private boolean isAlipayToken = false;//支付宝是否授权
    private String state = "uncertified";//个人认证状态（0：未认证；3：认证中；2：认证驳回；1：已认证）
    private String auth_request_parameters;

    //*****************微信*******************//
    private IWXAPI api;

    //*****************支付宝start*******************//
    private static final int SDK_AUTH_FLAG = 2;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();
                    if (Config.BASE.equals("http://staging.wanteyun.com/api/v1/")) {
                        LatteLogger.e("authResult", authResult.getResult());
                    }
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        String[] str1 = authResult.getResult().split("[&]");
                        String target_id = "";
                        String auth_code = "";
                        for (int i = 0; i < str1.length; i++) {
                            if (str1[i].contains("target_id")) {
                                target_id = str1[i].split("[=]")[1];
                            }
                            if (str1[i].contains("auth_code")) {
                                auth_code = str1[i].split("[=]")[1];
                            }
                        }
                        loadProcess();
                        patchAlipayUserInfo(target_id,auth_code);
                    } else {
                        Toast.makeText(getContext(),"授权失败,可点击提现重新进行授权", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    public boolean setIsDark() {
        return false;
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_my_tixian;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewParams();
        initListener();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        loadProcess();
        getOverViewData();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        loadProcess();
        renzhengInfo();
    }

    private void initViews(View rootView) {
        status_bar = (View) this.rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView) this.rootView.findViewById(R.id.iv_back);
        tv_total = (AppCompatTextView) this.rootView.findViewById(R.id.tv_total);
        et_zhanghao = (AppCompatEditText) this.rootView.findViewById(R.id.et_zhanghao);
        et_name = (AppCompatEditText) this.rootView.findViewById(R.id.et_name);
        et_money = (AppCompatEditText) this.rootView.findViewById(R.id.et_money);
        et_money_old = (EditText) this.rootView.findViewById(R.id.et_money_old);
        tv_ok = (AppCompatTextView) this.rootView.findViewById(R.id.tv_ok);
        ll_detail = (LinearLayoutCompat) this.rootView.findViewById(R.id.ll_detail);

        tv_del = (TextView) rootView.findViewById(R.id.tv_del);
        etMoney = (EditText)rootView.findViewById( R.id.et_money );
        ll_day1 = (LinearLayout)rootView.findViewById( R.id.ll_day1 );
        ll_day2 = (LinearLayout)rootView.findViewById( R.id.ll_day2 );
        ll_weixin = (LinearLayout)rootView.findViewById( R.id.ll_weixin );
        ll_zhifubao = (LinearLayout)rootView.findViewById( R.id.ll_zhifubao );
        iv_day1 = (ImageView)rootView.findViewById( R.id.iv_day1 );
        iv_day2 = (ImageView)rootView.findViewById( R.id.iv_day2 );
        iv_weixin1 = (ImageView)rootView.findViewById( R.id.iv_weixin1 );
        iv_zhifubao2 = (ImageView)rootView.findViewById( R.id.iv_zhifubao2 );
    }

    private void initViewParams() {
        api = LatteWeChat.getInstance().getWXAPI();

        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        ll_detail.setOnClickListener(this);
        tv_ok.setOnClickListener(this);

        tv_del.setOnClickListener(this);
        ll_day1.setOnClickListener(this);
        ll_day2.setOnClickListener(this);
        ll_zhifubao.setOnClickListener(this);
        ll_weixin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) {//返回
            pop();
        }else if (v==ll_detail) {//余额明细
//            getSupportDelegate().start(AccountDetailDelegate.newInstance("balance"));
            getSupportDelegate().start(TixianDetailDelegate.newInstance("tixian"));
        }if (v==tv_del) { //解除支付宝绑定
            loadProcess();
            delAlipayData();
        }else if (v==ll_day1) {
            isChoiceDay("1", iv_day1, iv_day2);
        }else if (v==ll_day2) {
            isChoiceDay("7", iv_day2, iv_day1);
        }else if (v==tv_ok) {//提现
            //确认提现
            if (state.equals("uncertified") || state.equals("rejected")) {
                //未认证或认证驳回
                show("提现之前请先通过个人认证");
                Intent intent = new Intent(_mActivity, PersonRzActivity.class);
                startActivity(intent);
            }else if (state.equals("certified")) {
                //已认证
                if (TextUtils.isEmpty(et_money_old.getText())) {
                    show("请输入提现金额");
                    return;
                }
                if (channel.equals("alipay")) {
                    if (isAlipayToken) {
                        //已授权，验证是否有支付密码
                        loadProcess();
                        isNoPayPwd();
                    }else {
                        //未授权，判断是否安装有支付宝
                        if (checkAliPayInstalled()) {
                            loadProcess();
                            getAlipayUserInfo();
                        }else {
                            show("您尚未安装支付宝，请先下载安装");
                            Util.webUrl(getContext(), "https://t.alipayobjects.com/L1/71/100/and/alipay_wap_main.apk");
                        }
                    }
                }else if (channel.equals("wxpay")) {
                    loadProcess();
                    getWxStaute(); //授权状态
                    show("暂未开放");
                }
            }
        }
    }

    private void isChoiceDay(String st, ImageView day1,ImageView day2) {
        arrival_periods = st;
        day1.setBackgroundResource(R.mipmap.choice_on);
        day2.setBackgroundResource(R.mipmap.choice_non);
    }

    private void isNoPayPwd() {
        String url = Config.CHANGE_PAY_PWD;
        String apiName = "判断是否有支付密码接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processIsPayPwd(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            showMsg401();
                        }else if (arg1.getMessage().contains("404")){
                            //未设置
                            show("还没有设置密码");
                            Intent intent = new Intent(getContext(), GRChangePayPwdActivity.class);
                            intent.putExtra("has_payment_password", "1");
                            startActivity(intent);
                        }
                    }
                }
            });
    }

    private void processIsPayPwd(String arg0) {
        JSONObject json = JSON.parseObject(arg0);
        if (json!=null) {
            if (json.containsKey("error")) {
                show(json.getString("error"));
                return;
            }
            showPayPwdPopwindow(); //已经设置
        }else {
            showPayPwdPopwindow(); //已经设置
        }
    }

    private void showPayPwdPopwindow() {
        View parent = ((ViewGroup)_mActivity.findViewById(android.R.id.content)).getChildAt(0);
        View popView = View.inflate(getContext(), R.layout.pop_pay_pwd, null);
        final EditText et_payPwd = (EditText) popView.findViewById(R.id.et_name);
        Button btn_cancel = (Button) popView.findViewById(R.id.btn_cancel);
        Button btn_ok = (Button) popView.findViewById(R.id.btn_ok);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        final PopupWindow popWindow = new PopupWindow(popView, width, height);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);// 设置同意在外点击消失
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_cancel:
                        break;
                    case R.id.btn_ok:
                        String payPwd = et_payPwd.getText().toString();
                        if (et_payPwd.getText().toString().length() == 0) {
                            show("密码不能为空");
                            return;
                        }
                        //已授权，进行提现逻辑
                        if (channel.equals("unionpay")) {
                            //提现到银行卡
                        }else {
                            loadProcess();
                            postWithdrawals(payPwd);
                        }
                        break;
                }
                popWindow.dismiss();
            }
        };
        btn_cancel.setOnClickListener(listener);
        btn_ok.setOnClickListener(listener);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        popWindow.setBackgroundDrawable(dw);
        popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        //这里给它设置了弹出的时间，
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //检测是否安装支付宝
    public boolean checkAliPayInstalled() {
        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(_mActivity.getPackageManager());
        return componentName != null;
    }

    private void getOverViewData() {
        String url = Config.WITHDRAWALS_OVERVIEW; //接口url
        String apiName = "提现统计接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processOverViewData(arg0);
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

    private void processOverViewData(String arg0) {
        OverViewBean.DataBean data = parseOverViewData(arg0).getData();
        tv_total.setText("¥" + data.getBalance()); //可提现
    }

    private OverViewBean parseOverViewData(String arg0) {
        return new Gson().fromJson(arg0, OverViewBean.class);
    }

    private void renzhengInfo() {
        String url = Config.GET_RENZHENG_INFO;
        String apiName = "查看个人认证信息情况接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url,addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    //支付宝是否授权接口
                    getIsAlipayToken();
                    parseRenZhengInfoData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    //支付宝是否授权接口
                    getIsAlipayToken();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            showMsg401();
                        }else if (arg1.getMessage().contains("404")){
                            state = "uncertified";
                        }
                    }
                }
            });
    }

    private void getIsAlipayToken() {
        String url = Config.ALIPAY_USER_INFO1;
        String apiName = "判断是否已经支付宝授权的接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url,addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        dismissLoadProcess();
                        processIsAlipayTokenData(arg0);
                    }
                },
                new NetConnectionNew.FailCallback() {
                    @Override
                    public void onFail(Call arg0, Exception arg1, int arg2) {
                        dismissLoadProcess();
                        if (arg1.getMessage()!=null) {
                            if (arg1.getMessage().contains("401")) {
                                showMsg401();
                            }else if (arg1.getMessage().contains("404")) {
                                //未授权，跳转支付宝授权页面
                                isAlipayToken = false;
                                tv_del.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    private void processIsAlipayTokenData(String arg0) {
        JSONObject json = JSON.parseObject(arg0);
        if (json!=null) {
            if (json.containsKey("error")) {
                show(json.getString("error"));
                return;
            }
            isAlipayToken = true;
            tv_del.setVisibility(View.VISIBLE);
        }else {
            isAlipayToken = true;
            tv_del.setVisibility(View.VISIBLE);
        }
    }

    private void parseRenZhengInfoData(String result) {
        JSONObject json = JSON.parseObject(result);
        if (json.containsKey("error")) {
            show(json.getString("error"));
            return;
        }
        JSONObject data = json.getJSONObject("data");
        //0-未认证，1-已认证，2-驳回认证，3-认证中
        state = data.getString("state");
    }

    public void postWithdrawals(String payPwd) {
        String url = Config.WITHDRAWALS;
        String apiName = "余额提现接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("amount",et_money_old.getText().toString());
        addParams.put("channel",channel);
        addParams.put("payment_password", payPwd);
        addParams.put("arrival_periods", arrival_periods);
        NetConnectionNew.post(apiName, getContext(), url,addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        dismissLoadProcess();
                        processWithdrawals(arg0);
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

    private void processWithdrawals(String arg0) {
        JSONObject json = JSON.parseObject(arg0);
        if (json!=null) {
            if (json.containsKey("error")) {
                show(json.getString("error"));
                return;
            }
            //收支明细详情页面
            ll_detail.performClick();
            show("提现成功");
        }else {
            //收支明细详情页面
            ll_detail.performClick();
            show("提现成功");
        }
    }

    private void getAlipayUserInfo() {
        String url = Config.ALIPAY_USER_INFO;
        String apiName = "支付宝授权请求参数接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("time", System.currentTimeMillis()+"");
        NetConnectionNew.get(apiName, getContext(), url,addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processAlipayUserInfo(arg0);
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

    private void processAlipayUserInfo(String arg0) {
        JSONObject json = JSON.parseObject(arg0);
        if (json!=null) {
            if (json.containsKey("error")) {
                show(json.getString("error"));
                return;
            }
            JSONObject data = json.getJSONObject("data");
            if (!TextUtils.isEmpty(data.getString("auth_request_parameters"))) {
                auth_request_parameters = data.getString("auth_request_parameters");
                Thread authThread = new Thread(authRunnable);
                authThread.start();
            }
        }
    }

    Runnable authRunnable = new Runnable() {
        @Override
        public void run() {
            AuthTask authTask = new AuthTask((Activity) Latte.getConfigurator().getConfiguration(ConfigType.ACTIVITY)); // 构造AuthTask 对象
            Map<String, String> result = authTask.authV2(auth_request_parameters, true); // 调用授权接口，获取授权结果
            Message msg = new Message();
            msg.what = SDK_AUTH_FLAG;
            msg.obj = result;
            mHandler.sendMessage(msg);
        }
    };

    public void getWxStaute(){
        String url = Config.PUT_WX_CODE; //接口url
        String apiName = "微信授权状况接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processWxStaute(arg0);
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

    private void processWxStaute(String arg0) {
        JSONObject json = JSON.parseObject(arg0);
        if (json!=null) {
            if (json.containsKey("error")) {
                show(json.getString("error"));
                //微信未授权,跳转微信授权页面
                if (WXCanPay()) { //先判断是否安装了微信
                    startWxLogin();
                }
                return;
            }
            //微信已绑定,已授权，进行提现逻辑,验证是否设置有支付密码
            loadProcess();
            isNoPayPwd();
        }else {
            //微信已绑定,已授权，进行提现逻辑,验证是否设置有支付密码
            loadProcess();
            isNoPayPwd();
        }
    }

    private void startWxLogin() { //微信授权登录
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        api.sendReq(req);
    }

    private boolean WXCanPay(){
        try {
            if (!api.isWXAppInstalled()) {
                show("请先安装微信！");
                getSupportDelegate().start(WebDelegateImpl.creat("https://dldir1.qq.com/weixin/android/weixin673android1360.apk"));
                return false;
            }
        } catch (Exception e) {
            show("请安装或者升级微信版本！");
            getSupportDelegate().start(WebDelegateImpl.creat("https://dldir1.qq.com/weixin/android/weixin673android1360.apk"));
            Util.webUrl(getContext(), "https://dldir1.qq.com/weixin/android/weixin673android1360.apk");
            return false;
        }
        return true;
    }

    private void delAlipayData() {
        String url = Config.ALIPAY_USER_INFO1; //请求接口url
        String apiName = "解除支付宝绑定接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>();//请求头
        Map<String, String> addParams = new HashMap<String, String>();//请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("time", System.currentTimeMillis()+"");
        NetConnectionNew.delete(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processDelAlipayData(arg0);
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

    private void processDelAlipayData(String arg0) {
        JSONObject json = JSON.parseObject(arg0);
        if (json!=null){
            if (json.containsKey("error")) {
                show(json.getString("error"));
                return;
            }
            show("已解除绑定");
            isAlipayToken = false;
            tv_del.setVisibility(View.GONE);
        }else{
            show("已解除绑定");
            isAlipayToken = false;
            tv_del.setVisibility(View.GONE);
        }
    }

    public void patchAlipayUserInfo(String target_id, String auth_code) {
        String url = Config.ALIPAY_USER_INFO1;
        String apiName = "换取授权访问令牌接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addHeader.put("Content-Type", "application/x-www-form-urlencoded");
        addParams.put("target_id", target_id);
        addParams.put("auth_code", auth_code);
        NetConnectionNew.patch(apiName, getContext(), url,addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processPatchAlipayUserInfoData(arg0);
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

    private void processPatchAlipayUserInfoData(String arg0) {
        try {
            org.json.JSONObject json = new org.json.JSONObject(arg0);
            if (json!=null) {
                if (json.has("error")) {
                    show(json.optString("error",""));
                    return;
                }
                loadProcess();
                renzhengInfo();
            }
        } catch (Exception e) {
            LatteLogger.e("异常", e.getMessage());
            loadProcess();
            renzhengInfo();
        }
    }
}
