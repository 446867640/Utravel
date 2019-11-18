package com.utravel.app.delegates.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.utravel.app.R;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import com.utravel.app.wechat.LatteWeChat;
import com.utravel.app.wechat.callbacks.IWeChatSignInCallback;

public class LoginChoiceDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    AppCompatImageView iv_back, iv_login_zhuce, iv_login_phone, iv_login_weixin;
    AppCompatTextView tv_xieyi;

    @Override
    public Object setLayout() {
        return R.layout.delegate_login_choice;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewParams();
        initListener();
    }

    private void initViews(View rootView) {
        iv_back = (AppCompatImageView) rootView.findViewById(R.id.iv_back);
        iv_login_zhuce = (AppCompatImageView) rootView.findViewById(R.id.iv_login_zhuce);
        iv_login_phone = (AppCompatImageView) rootView.findViewById(R.id.iv_login_phone);
        iv_login_weixin = (AppCompatImageView) rootView.findViewById(R.id.iv_login_weixin);
        tv_xieyi = (AppCompatTextView) rootView.findViewById(R.id.tv_xieyi);
    }

    private void initViewParams() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        params.width = DensityUtil.dp2px(_mActivity, 44);
        params.height = DensityUtil.dp2px(_mActivity, 44);
        params.topMargin = Util.getStatusBarHeight(_mActivity);
        params.leftMargin = DensityUtil.dp2px(_mActivity, 10);
        iv_back.setLayoutParams(params);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        iv_login_zhuce.setOnClickListener(this);
        iv_login_phone.setOnClickListener(this);
        iv_login_weixin.setOnClickListener(this);
        tv_xieyi.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) {//返回
            pop();
        }else if (v==iv_login_zhuce) {//注册
            getSupportDelegate().startWithPop(new ZhuCeDelegate());
        }else if (v==iv_login_phone) {//手机登陆
            getSupportDelegate().startWithPop(new LoginPhoneDelegate());
        }else if (v==iv_login_weixin) {//微信登陆
            LatteWeChat.getInstance().onSignSuccess(new IWeChatSignInCallback(){
                @Override
                public void onSignInSuccess(String userInfo) {
                    JSONObject json = JSON.parseObject(userInfo);
                    if (json!=null) {
                        if (json.containsKey("error")) {
                            show(json.getString("error"));
                            return;
                        }
                        JSONObject data = json.getJSONObject("data");
                        int id = json.getJSONObject("data").getInteger("id");
                        //把相关参数存入缓存
                        String mobile = data.getString("mobile");
                        String token  = data.getString("token");
                        String authorization = "Token token=" + token + ",mobile=" + mobile;
                        SharedPreferencesUtil.putString(getContext(), "id", id + "");
                        SharedPreferencesUtil.putString(getContext(), "mobile", mobile);
                        SharedPreferencesUtil.putString(getContext(), "token", token);
                        SharedPreferencesUtil.putString(getContext(), "Token", authorization);
                        showSuccess("登录成功");
                        pop();
                    }
                }
            }).signIn();
        }else if (v==tv_xieyi) {//协议
//            getSupportDelegate().start(new LoginXieYiDelegate());
        }
    }

    @Override
    public boolean setIsDark() {
        return false;
    }
}
