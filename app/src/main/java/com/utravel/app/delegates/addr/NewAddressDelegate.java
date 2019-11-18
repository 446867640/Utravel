package com.utravel.app.delegates.addr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import com.utravel.app.R;
import com.utravel.app.activities.proxy.LoginActivity;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.delegates.order.SureOrderDelegate;
import com.utravel.app.entity.AddressBean;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class NewAddressDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
    private AppCompatImageView iv_back;
    private AppCompatTextView tv_title;
    private EditText etUsername,etPhone,etStreet,etYoubian;
    private LinearLayout llAddress,ll_ismoren;
    private RadioGroup rg_btn;
    private TextView tvAddress,tvOk;

    public static final int REQ_MODIFY_FRAGMENT = 100;
    private boolean is_default = true;
    private int area_id = 0;

    @Override
    public boolean setIsDark() { return true; }

    @Override
    public Object setLayout() { return R.layout.delegate_new_addr; }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewsParams();
        initListener();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        loadProcess();
        getAddressData();
    }

    private void initViews(View rootView) {
        status_bar = (View) this.rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView) this.rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);

        etUsername = (EditText)rootView.findViewById( R.id.et_username );
        etPhone = (EditText)rootView.findViewById( R.id.et_phone );
        llAddress = (LinearLayout)rootView.findViewById( R.id.ll_address );
        ll_ismoren = (LinearLayout)rootView.findViewById( R.id.ll_ismoren );
        tvAddress = (TextView)rootView.findViewById( R.id.tv_address );
        etStreet = (EditText)rootView.findViewById( R.id.et_street );
        etYoubian = (EditText)rootView.findViewById( R.id.et_youbian );
        tvOk = (TextView)rootView.findViewById( R.id.tv_ok );
        rg_btn = (RadioGroup) rootView.findViewById(R.id.rg_btn);
    }

    private void initViewsParams() {
        tv_title.setText(getResources().getString(R.string.add_addr_title));

        rg_btn.check(R.id.rb_btn1);

        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        llAddress.setOnClickListener(this);
        tvOk.setOnClickListener(this);
        rg_btn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_btn1:
                        is_default = true;
                        break;
                    case R.id.rb_btn2:
                        is_default = false;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) { //返回
            pop();
        }else if (v == tvOk) {
            if (TextUtils.isEmpty(etUsername.getText())) {
                show(getResources().getString(R.string.edit_addr_shouhuoren));
                return;
            }
            if (TextUtils.isEmpty(etPhone.getText())) {
                show(getResources().getString(R.string.edit_addr_mobile));
                return;
            }
            if (area_id == 0) {
                show(getResources().getString(R.string.edit_addr_area));
                return;
            }
            if (TextUtils.isEmpty(etStreet.getText())) {
                show(getResources().getString(R.string.edit_addr_addr));
                return;
            }
            loadProcess();
            getAddAddressData(etUsername.getText().toString(),etPhone.getText().toString(),area_id+"",etStreet.getText().toString());
        }else if (v == llAddress) {
            startForResult(SelectAddrDelegate.newInstance("RegisterActivity"), REQ_MODIFY_FRAGMENT);
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == REQ_MODIFY_FRAGMENT && resultCode == RESULT_OK && data != null) {
            String name = data.getString("name");
            area_id = data.getInt("area_id", 0);
            tvAddress.setText(name);
        }
    }

    private void getAddressData() {
        String url = Config.ADDRESS_INFO;
        String apiName = "地址列表接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("page", 1 + "");
        addParams.put("size", 2 + "");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url,addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processAddressData(arg0);
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

    private void processAddressData(String result) {
        try {
            JSONObject json = new JSONObject(result);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            if (parseAdrJson(result).getData() != null && parseAdrJson(result).getData().size() > 0) {
                ll_ismoren.setVisibility(View.VISIBLE);
            }else {
                ll_ismoren.setVisibility(View.GONE);
            }
        } catch (Exception e) {}
    }

    private AddressBean parseAdrJson(String result) {
        return new Gson().fromJson(result, AddressBean.class);
    }

    private void getAddAddressData(String str1, String str2, String str3, String str4) {
        String url = Config.ADDRESS_INFO;
        String apiName = "添加地址接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("contact_name", str1);
        addParams.put("mobile", str2);
        addParams.put("district_id", str3);
        addParams.put("address_id", str3);
        addParams.put("street", str4);
        addParams.put("default", is_default + "");
        NetConnectionNew.post(apiName, getContext(), url,addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        dismissLoadProcess();
                        try {
                            JSONObject json = new JSONObject(arg0);
                            if (json.has("error")) {
                                show(json.optString("error",""));
                                return;
                            }
                            addOk();
                        } catch (Exception e) {
                            addOk();
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

    private void addOk() {
        if (SharedPreferencesUtil.getBoolean(getContext(), "NoAddrisFromGoods")) {
            getSupportDelegate().start(SureOrderDelegate.newInstances( //跳转确认订单页面
                    SharedPreferencesUtil.getString(getContext(), "NoAddrGoodsQuantity"),
                    SharedPreferencesUtil.getString(getContext(), "NoAddrGoodsProductId")));

            ((MyAddressDelegate)getParentFragment()).pop(); //退出MyAddressDelegate
        }
        //退出NewAddressActivity
        show("添加成功");
        pop();
        SharedPreferencesUtil.putBoolean(getContext(), "NoAddrisFromGoods", false);
        SharedPreferencesUtil.remove(getContext(), "NoAddrGoodsQuantity");
        SharedPreferencesUtil.remove(getContext(), "NoAddrGoodsProductId");
    }
}
