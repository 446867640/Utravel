package com.utravel.app.delegates.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.delegates.login.LoginChoiceDelegate;
import com.utravel.app.delegates.main.MainDelegate;
import com.utravel.app.entity.CalculateDetailBean;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class CalculatePromotionsDetailDelegate extends LatterDelegate {

    private AppCompatTextView tv_total_amounts, tv_tbk_amounts, tv_tbk_count, tv_jingdong_amounts, tv_jingdong_count, tv_pdd_amounts, tv_pdd_count, tv_downlines_tbk_amounts, tv_downlines_tbk_count, tv_downlines_jingdong_amounts, tv_downlines_jingdong_count, tv_downlines_pdd_amounts, tv_downlines_pdd_count;

    private CalculateDetailBean.DataBean calculateBean;

    @Nullable
    @Override
    public Object setLayout() { return R.layout.delegate_calculate_base; }

    private static final String BUNDLE_TAG = "CalculatePromotionsDetailDelegate";
    private String tag_value = null;

    public static CalculatePromotionsDetailDelegate newInstance(String value) {
        CalculatePromotionsDetailDelegate fragment = new CalculatePromotionsDetailDelegate();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TAG, value);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            tag_value = bundle.getString(BUNDLE_TAG);
        }
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        if (!TextUtils.isEmpty(tag_value)) {
            loadProcess();
            getCalculateDetailData();
        }
    }

    private void initViews(View rootView) {
        tv_total_amounts = (AppCompatTextView) rootView.findViewById(R.id.tv_total_amounts);
        tv_tbk_amounts = (AppCompatTextView) rootView.findViewById(R.id.tv_tbk_amounts);
        tv_tbk_count = (AppCompatTextView) rootView.findViewById(R.id.tv_tbk_count);
        tv_jingdong_amounts = (AppCompatTextView) rootView.findViewById(R.id.tv_jingdong_amounts);
        tv_jingdong_count = (AppCompatTextView) rootView.findViewById(R.id.tv_jingdong_count);
        tv_pdd_amounts = (AppCompatTextView) rootView.findViewById(R.id.tv_pdd_amounts);
        tv_pdd_count = (AppCompatTextView) rootView.findViewById(R.id.tv_pdd_count);
        tv_downlines_tbk_amounts = (AppCompatTextView) rootView.findViewById(R.id.tv_downlines_tbk_amounts);
        tv_downlines_tbk_count = (AppCompatTextView) rootView.findViewById(R.id.tv_downlines_tbk_count);
        tv_downlines_jingdong_amounts = (AppCompatTextView) rootView.findViewById(R.id.tv_downlines_jingdong_amounts);
        tv_downlines_jingdong_count = (AppCompatTextView) rootView.findViewById(R.id.tv_downlines_jingdong_count);
        tv_downlines_pdd_amounts = (AppCompatTextView) rootView.findViewById(R.id.tv_downlines_pdd_amounts);
        tv_downlines_pdd_count = (AppCompatTextView) rootView.findViewById(R.id.tv_downlines_pdd_count);
    }

    private void getCalculateDetailData() {
        String url = Config.CALCULATE_DETAIL; //接口url
        String apiName = "预估收益明细"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("scope", tag_value);
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processCalculateDetailData(arg0);
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

    private void processCalculateDetailData (String arg0){
        JSONObject json = JSON.parseObject(arg0);
        if (json != null) {
            if (json.containsKey("error")) {
                show(json.getString("error"));
                return;
            }
            calculateBean = parseCalculateDetailData(arg0).getData();
            if ( !TextUtils.isEmpty(calculateBean.getTotal_amounts())) {
                tv_total_amounts.setText("￥" + calculateBean.getTotal_amounts());
            }
            if ( !TextUtils.isEmpty(calculateBean.getTbk_amounts()) ) {
                tv_tbk_amounts.setText(calculateBean.getTbk_amounts());
            }
            if ( !TextUtils.isEmpty(calculateBean.getJingdong_amounts()) ) {
                tv_jingdong_amounts.setText(calculateBean.getJingdong_amounts());
            }
            if ( !TextUtils.isEmpty(calculateBean.getPdd_amounts()) ) {
                tv_pdd_amounts.setText(calculateBean.getPdd_amounts());
            }
            if ( !TextUtils.isEmpty(calculateBean.getDownlines_tbk_amounts()) ) {
                tv_downlines_tbk_amounts.setText(calculateBean.getDownlines_tbk_amounts());
            }
            if ( !TextUtils.isEmpty(calculateBean.getDownlines_jingdong_amounts()) ) {
                tv_downlines_jingdong_amounts.setText(calculateBean.getDownlines_jingdong_amounts());
            }
            if ( !TextUtils.isEmpty(calculateBean.getDownlines_pdd_amounts()) ) {
                tv_downlines_pdd_amounts.setText(calculateBean.getDownlines_pdd_amounts());
            }
        }
    }

    private CalculateDetailBean parseCalculateDetailData(String arg0) {
        return new Gson().fromJson(arg0, CalculateDetailBean.class);
    }
}
