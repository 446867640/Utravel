package com.utravel.app.alipay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import com.utravel.app.R;
import com.utravel.app.activities.base.GRChangePayPwdActivity;
import com.utravel.app.activities.proxy.AccountLogActivity;
import com.utravel.app.activities.proxy.LoginActivity;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.delegates.bankcard.EditBankCardDelegate;
import com.utravel.app.delegates.detail.AccountDetailDelegate1;
import com.utravel.app.delegates.order.MyOrderDelegate;
import com.utravel.app.entity.PayChoiceBean;
import com.utravel.app.jpush.MyReceiver;
import com.utravel.app.latte.Latte;
import com.utravel.app.ui.Loading_view;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import com.utravel.app.wechat.LatteWeChat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class PayDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
    private AppCompatImageView iv_back;
    private AppCompatTextView tv_title;
    private TextView tvMoney;//支付金额
    private TextView tv_keyongyue;//我的可用余额
    private LinearLayout ll_balance;//我的余额栏
    private LinearLayout ll_weixin;//微信栏
    private LinearLayout ll_zhifubao;//支付宝栏
    private LinearLayout ll_bankcard;//银联栏
    private ImageView iv_yue;//我的余额是否选中图标
    private ImageView iv_weixin;//微信是否选中图标
    private ImageView iv_zhifubao;//支付宝是否选中图标
    private ImageView iv_yinhangka;//银联是否选中图标
    private TextView tvSureOk;//确认支付
    private Loading_view loading;// 进度加载dailog
    private String amount="";//支付金额
    private String payment_method_code = "alipay";//支付方式
    private PayChoiceBean.DataBean payTypeBean;
    private IWXAPI api;	//微信api

    private static final int SDK_PAY_FLAG = 1;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            if (msg.what==SDK_PAY_FLAG) {
                @SuppressWarnings("unchecked")
                PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                //对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                String resultStatus = payResult.getResultStatus();
                if (TextUtils.equals(resultStatus, "9000")) { // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                    show(getResources().getString(R.string.pay_success));
                    if (object_type.equals("order")) {
                        if ( findFragment(MyOrderDelegate.class) != null) {
                            findFragment(MyOrderDelegate.class).getSupportDelegate().pop();
                        }
                        getSupportDelegate().startWithPop(MyOrderDelegate.newInstance(3));
                    }else if (object_type.equals("national_porcelain_order")) {//国瓷订单
                        getSupportDelegate().startWithPop(MyOrderDelegate.newInstance(3));
                    }else if (object_type.equals("points_order")) {
                        getSupportDelegate().startWithPop(MyOrderDelegate.newInstance(3));
                    }else if (object_type.equals("exchanged_order")) {
                        getSupportDelegate().startWithPop(MyOrderDelegate.newInstance(3));
                    }else if (object_type.equals("points_as_gift")) {
                        getSupportDelegate().startWithPop(AccountDetailDelegate1.newInstance("balance"));
                    }else if (object_type.equals("balance_recharge")) {
                        getSupportDelegate().startWithPop(AccountDetailDelegate1.newInstance("balance"));
                    }
                } else { // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                    show(getResources().getString(R.string.pay_fail));
                }
            }
        };
    };

    private static final String BUNDLE_OBJECT_ID = "BUNDLE_OBJECT_ID";
    private static final String BUNDLE_OBJECT_TYPE = "BUNDLE_OBJECT_TYPE";
    private static final String BUNDLE_PRODUCT_ID = "BUNDLE_PRODUCT_ID";
    private String object_id="";//支付对象ID
    private String object_type="";//支付对象类型
    private String value_product_id = null;

    public static PayDelegate newInstances(String objectId, String objectType, String productId) {
        PayDelegate fragment = new PayDelegate();
        Bundle bundle = new Bundle();
        if (objectId!=null) {
            bundle.putString(BUNDLE_OBJECT_ID, objectId);
        }
        if (objectType!=null) {
            bundle.putString(BUNDLE_OBJECT_TYPE, objectType);
        }
        if (productId!=null) {
            bundle.putString(BUNDLE_PRODUCT_ID, productId);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            object_id = bundle.getString(BUNDLE_OBJECT_ID);
            object_type = bundle.getString(BUNDLE_OBJECT_TYPE);
            value_product_id = bundle.getString(BUNDLE_PRODUCT_ID);
        }
    }

    @Override
    public boolean setIsDark() {return true;}

    @Override
    public Object setLayout() {return R.layout.delegate_pay;}

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initWX();
        initViews(rootView);
        initViewsParams();
        initListener();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        loadProcess();
        getPayTypeData();
    }

    private void initWX() {
        api = LatteWeChat.getInstance().getWXAPI();
    }

    private void initViews(View rootView) {
        status_bar = (View) this.rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView) this.rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);
        tvMoney = (TextView) rootView.findViewById( R.id.tv_money );
        tv_keyongyue = (TextView) rootView.findViewById( R.id.tv_keyongyue);
        ll_balance = (LinearLayout) rootView.findViewById( R.id.ll_balance);
        ll_weixin = (LinearLayout) rootView.findViewById( R.id.ll_weixin );
        ll_zhifubao = (LinearLayout) rootView.findViewById( R.id.ll_zhifubao );
        ll_bankcard = (LinearLayout) rootView.findViewById( R.id.ll_bankcard );
        iv_yue = (ImageView) rootView.findViewById( R.id.iv_yue );
        iv_weixin = (ImageView) rootView.findViewById( R.id.iv_weixin );
        iv_zhifubao = (ImageView) rootView.findViewById( R.id.iv_zhifubao );
        iv_yinhangka = (ImageView) rootView.findViewById( R.id.iv_yinhangka );
        tvSureOk = (TextView) rootView.findViewById( R.id.tv_sure_ok );
    }

    private void initViewsParams() {
        tv_title.setText(getResources().getString(R.string.pay_order_title));
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
        if (Config.ISDEBUG) {
            ll_balance.setVisibility(View.VISIBLE);
        }else {
            ll_balance.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        ll_balance.setOnClickListener(this);
        ll_weixin.setOnClickListener(this);
        ll_zhifubao.setOnClickListener(this);
        ll_bankcard.setOnClickListener(this);
        tvSureOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) { //返回
            pop();
        }else if (v==ll_balance) {//选择我的余额支付
            isChoice("balance", iv_yue, iv_weixin, iv_zhifubao, iv_yinhangka);
        }else if (v==ll_weixin) {//选择微信支付
            isChoice("wxpay", iv_weixin, iv_yue, iv_zhifubao, iv_yinhangka);
        }else if (v==ll_zhifubao) {//选择支付宝支付
            isChoice("alipay", iv_zhifubao, iv_yue, iv_weixin, iv_yinhangka);
        }else if (v==ll_bankcard) {//选择银联支付
            isChoice("unionpay", iv_yinhangka, iv_yue, iv_weixin, iv_zhifubao);
        }else if (v==tvSureOk) {
            if (payment_method_code.equals("balance")) {//我的余额支付
                //验证是否设置有支付
                loadProcess();
                isNoPayPwd();
            }else if (payment_method_code.equals("alipay")) {//支付宝支付
                loadProcess();
                postAlipay();
            }else if (payment_method_code.equals("wxpay")) {//微信支付
                if (WXCanPay()) {
                    loadProcess();
                    postWxPay();
                }
            }else if (payment_method_code.equals("unionpay")) {//银联支付
                show("暂未开放");
            }
        }
    }

    private boolean WXCanPay(){//是否安装了微信
        try {
            if (!api.isWXAppInstalled()) {
                show("请先安装微信！");
                Util.webUrl(getContext(), "https://dldir1.qq.com/weixin/android/weixin673android1360.apk");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            show("请安装或者升级微信版本！");
            Util.webUrl(getContext(), "https://dldir1.qq.com/weixin/android/weixin673android1360.apk");
            return false;
        }
        return true;
    }

    private void isChoice(String str,ImageView iv1,ImageView iv2,ImageView iv3,ImageView iv4) {
        payment_method_code = str;
        iv1.setBackgroundResource(R.drawable.qi_xuanze);
        iv2.setBackgroundResource(R.drawable.qb_weixuan);
        iv3.setBackgroundResource(R.drawable.qb_weixuan);
        iv4.setBackgroundResource(R.drawable.qb_weixuan);
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
                        loadProcess();
                        postBalancePay(payPwd);
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

    private void getPayTypeData() {
        String url = Config.PAY_TYPE;
        final String apiName = "选择支付接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("object_type", object_type);
        addParams.put("object_id", object_id);
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url,addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processPayTypeData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    tvSureOk.setEnabled(false);
                    tvSureOk.setBackgroundResource(R.drawable.bg_huise_40_daojiao);
                    tvSureOk.setTextColor(Color.parseColor("#F4F4F4"));
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            showMsg401();
                        }else if (arg1.getMessage().contains("404")){
                            Toast.makeText(getContext(), "未找到支付对象", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
    }

    private void processPayTypeData(String arg0) {
        try {
            JSONObject json = new JSONObject(arg0);
            if (json.has("error")) {
                show(json.optString("error",""));
                tvSureOk.setEnabled(false);
                tvSureOk.setBackgroundResource(R.drawable.bg_huise_40_daojiao);
                tvSureOk.setTextColor(Color.parseColor("#F4F4F4"));
                return;
            }
            tvSureOk.setEnabled(true);
            payTypeBean = parsePayTypeJson(arg0).getData();
            if (!TextUtils.isEmpty(payTypeBean.getAmount())) {
                amount = payTypeBean.getAmount();//支付价格
                tvMoney.setText(amount);
            }
            if (!TextUtils.isEmpty(payTypeBean.getAccount_balance())) {
                tv_keyongyue.setText("（可用余额" + payTypeBean.getAccount_balance() + "）");//我的可用余额
            }
        } catch (Exception e) {}
    }

    private PayChoiceBean parsePayTypeJson(String arg0) {
        return new Gson().fromJson(arg0, PayChoiceBean.class);
    }

    private void isNoPayPwd() {
        String url = Config.CHANGE_PAY_PWD;
        String apiName = "判断是否有支付密码接口";
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
                            Intent intent = new Intent(getContext(), GRChangePayPwdActivity.class);
                            intent.putExtra("has_payment_password", "1");
                            startActivity(intent);
                        }
                    }
                }
            });
    }

    private void processIsPayPwd(String arg0) {
        try {
            JSONObject json = new JSONObject(arg0);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            showPayPwdPopwindow();//已经设置
        } catch (Exception e) {
            showPayPwdPopwindow(); //已经设置
        }
    }

    private void processWXPay(String arg0) {
        try {
            JSONObject json = new JSONObject(arg0);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            JSONObject data = json.optJSONObject("data");
            PayReq req = new PayReq();
            req.appId			= data.optString("appid");
            req.nonceStr		= data.optString("noncestr");
            req.packageValue	= data.optString("package");
            req.partnerId		= data.optString("partnerid");
            req.prepayId		= data.optString("prepayid");
            req.sign			= data.optString("sign");
            req.timeStamp		= data.optString("timestamp");
            Toast.makeText(getContext(), "获取订单中...", Toast.LENGTH_SHORT).show();
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
            api.sendReq(req);
            if (object_type.equals("order")) {
                SharedPreferencesUtil.putString(getContext(), "object_type", "order");
            }else if (object_type.equals("member_level_stage")) {
                SharedPreferencesUtil.putString(getContext(), "object_type", "member_level_stage");
            }else if (object_type.equals("member_level_order")) {
                SharedPreferencesUtil.putString(getContext(), "object_type", "member_level_order");
            }else if (object_type.equals("points_order")) {
                SharedPreferencesUtil.putString(getContext(), "object_type", "points_order");
            }else if (object_type.equals("exchanged_order")) {
                SharedPreferencesUtil.putString(getContext(), "object_type", "exchanged_order");
            }else if (object_type.equals("balance_recharge")) {
                SharedPreferencesUtil.putString(getContext(), "object_type", "balance_recharge");
            }else if (object_type.equals("points_as_gift")) {
                SharedPreferencesUtil.putString(getContext(), "object_type", "points_as_gift");
            }else if (object_type.equals("national_porcelain_order")) {//国瓷订单
                SharedPreferencesUtil.putString(getContext(), "object_type", "national_porcelain_order");
            }
            pop();
        } catch (Exception e) {}
    }

    private void postAlipay() {
        String url = Config.ALIPAY_PAY;
        String apiName = "支付宝支付接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("object_type", object_type);
        addParams.put("object_id", object_id);
        addParams.put("time", System.currentTimeMillis()+"");
        NetConnectionNew.post(apiName, getContext(), url,addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        dismissLoadProcess();
                        processAliPay(arg0);
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
                                show(Config.ERROR404);
                            }
                        }
                    }
                });
    }

    String orderInfo;
    private void processAliPay(String result) {
        try {
            JSONObject json = new JSONObject(result);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            JSONObject data = json.optJSONObject("data");
            orderInfo = data.optString("payment_parameters");
            // 必须异步调用
            Thread payThread = new Thread(payRunnable);
            payThread.start();
        } catch (JSONException e) {}
    }

    Runnable payRunnable = new Runnable() {
        @Override
        public void run() {
            PayTask alipay = new PayTask(_mActivity);
            Map<String, String> result = alipay.payV2(orderInfo, true);
            LatteLogger.i("msp", result.toString());
            Message msg = new Message();
            msg.what = SDK_PAY_FLAG;
            msg.obj = result;
            mHandler.sendMessage(msg);
        }
    };

    private void postWxPay() {
        String url = Config.WXPAY_PAY;
        final String apiName = "微信支付接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("object_type", object_type);
        addParams.put("object_id", object_id);
        addParams.put("time", System.currentTimeMillis()+"");
        NetConnectionNew.post(apiName, getContext(), url,addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processWXPay(arg0);
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
                            show(Config.ERROR404);
                        }
                    }
                }
            });
    }

    private void postBalancePay(String payPwd) {
        String url = Config.BALANCE_PAY;
        final String apiName = "余额支付接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("object_type", object_type);
        addParams.put("object_id", object_id);
        addParams.put("payment_password", payPwd);
        NetConnectionNew.post(apiName, getContext(), url,addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        dismissLoadProcess();
                        processBanlancePay(arg0);
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
                                show(Config.ERROR404);
                            }
                        }
                    }
                });
    }

    private void processBanlancePay(String arg0) {
        try {
            JSONObject json = new JSONObject(arg0);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            JSONObject data = json.optJSONObject("data");
            String state_zh_cn = data.optString("state_zh_cn","");
            if (state_zh_cn.equals("已支付")) {
                if (object_type.equals("order")) {
                    if ( findFragment(MyOrderDelegate.class)!=null ) {
                        findFragment(MyOrderDelegate.class).getSupportDelegate().pop();
                    }
                    getSupportDelegate().startWithPop(MyOrderDelegate.newInstance(3));
                }else if (object_type.equals("national_porcelain_order")) {//国瓷订单
                    getSupportDelegate().startWithPop(MyOrderDelegate.newInstance(3));
                }else if (object_type.equals("points_order")) {
                    getSupportDelegate().startWithPop(MyOrderDelegate.newInstance(3));
                }else if (object_type.equals("exchanged_order")) {
                    getSupportDelegate().startWithPop(MyOrderDelegate.newInstance(3));
                }else if (object_type.equals("points_as_gift")) {
                    getSupportDelegate().startWithPop(AccountDetailDelegate1.newInstance("balance"));
                }else if (object_type.equals("balance_recharge")) {
                    getSupportDelegate().startWithPop(AccountDetailDelegate1.newInstance("balance"));
                }
            }else {
                show("支付失败");
            }
        } catch (JSONException e) {}
    }
}
