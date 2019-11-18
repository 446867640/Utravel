package com.utravel.app.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.activities.proxy.LoginActivity;
import com.utravel.app.config.Config;
import com.utravel.app.entity.YaoQingWoderenBean;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.GlideCircleTransform;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.ToastUtil;
import com.utravel.app.utils.Util;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class MyYaoQingRenDialog extends BaseDialogDelegate implements View.OnClickListener {
    private AppCompatImageView iv_avartor, iv_cancel;
    private AppCompatTextView tv_name, tv_phone;
    private YaoQingWoderenBean.DataBean parentData;

    public static MyYaoQingRenDialog newInstance() {
        MyYaoQingRenDialog delegate = new MyYaoQingRenDialog();
        return delegate;
    }

    @Override
    public Object setLayout() {
        return R.layout.dialog_myyaoqingren;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = Util.getScreenWidth(_mActivity) - DensityUtil.dp2px(_mActivity, 64);
        params.height = DensityUtil.dp2px(_mActivity, 100);
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        getMyParentData();
    }

    private void initViews(View rootView) {
        iv_avartor = (AppCompatImageView) rootView.findViewById(R.id.iv_avartor);
        iv_cancel = (AppCompatImageView) rootView.findViewById(R.id.iv_cancel);
        tv_name = (AppCompatTextView) rootView.findViewById(R.id.tv_name);
        tv_phone = (AppCompatTextView) rootView.findViewById(R.id.tv_phone);
        iv_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == iv_cancel) {
            getDialog().cancel();
        }
    }

    private void getMyParentData() {
        String url = Config.INVITER; //接口url
        String apiName = "邀请我的人信息接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    processMyParentData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            show(Config.ERROR401);
                            goToNextAty(LoginActivity.class);
                            getDialog().cancel();
                        }else if (arg1.getMessage().contains("404")){
                            ToastUtil.showShortToast(Config.ERROR404);
                        }
                    }
                }
            });
    }

    private void processMyParentData(String arg0) {
        JSONObject json = JSON.parseObject(arg0);
        if (json != null) {
            if (json.containsKey("error")) {
                show(json.getString("error"));
                return;
            }
            parentData = parseMyParentData(arg0).getData();
            if (parentData!=null) {
                Glide.with(getContext())
                        .load(parentData.getAvatar().getUrl())
                        .transform(new GlideCircleTransform(getContext()))
                        .into(iv_avartor);
                if (!TextUtils.isEmpty(parentData.getName())) {
                    tv_name.setText(parentData.getName());
                }
                if (!TextUtils.isEmpty(parentData.getMobile())) {
                    tv_phone.setText("账号：" + parentData.getMobile());
                }
            }else {
                show("没有邀请人");
            }
        }
    }

    private YaoQingWoderenBean parseMyParentData(String arg0) {
        return new Gson().fromJson(arg0, YaoQingWoderenBean.class);
    }
}
