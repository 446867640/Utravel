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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import com.utravel.app.R;
import com.utravel.app.activities.proxy.LoginActivity;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.entity.AddressBean;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class EditAddDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
    private AppCompatImageView iv_back;
    private AppCompatTextView tv_title;

    private EditText etUsername, etPhone, etAddress, etZipcode;
    private LinearLayout llArea, ll_ismoren;
    private RadioGroup rgBtn;
    private RadioButton rbBtn1, rbBtn2;
    private TextView etArea, tvBtnOk;

    public static final int REQ_MODIFY_FRAGMENT = 100;
    public static final String BUNDLE_ID = "BUNDLE_ID";
    public static final String BUNDLE_CONTACT_NAME = "BUNDLE_CONTACT_NAME";
    public static final String BUNDLE_MOBILE = "BUNDLE_MOBILE";
    public static final String BUNDLE_ZIPCODE = "BUNDLE_ZIPCODE";
    private String value_id = null;
    private String value_contact_name = null;
    private String value_mobile = null;
    private String value_zipcode = null;
    private boolean is_default = true;
    private int area_id = 0;


    @Override
    public boolean setIsDark() { return true; }

    @Override
    public Object setLayout() { return R.layout.delegate_addaddr; }

    public static EditAddDelegate newInstance(String id, String contact_name, String mobile, String zipcode) {
        EditAddDelegate fragment = new EditAddDelegate();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_ID, id);
        bundle.putString(BUNDLE_CONTACT_NAME, contact_name);
        bundle.putString(BUNDLE_MOBILE, mobile);
        bundle.putString(BUNDLE_ZIPCODE, zipcode);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            value_id = bundle.getString(BUNDLE_ID);
            value_contact_name = bundle.getString(BUNDLE_CONTACT_NAME);
            value_mobile = bundle.getString(BUNDLE_MOBILE);
            value_zipcode = bundle.getString(BUNDLE_ZIPCODE);
        }
    }

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
        getAddressListData();
    }

    private void initViews(View rootView) {
        status_bar = (View) this.rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView) this.rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);

        etUsername = (EditText) rootView.findViewById(R.id.et_username);
        etPhone = (EditText) rootView.findViewById(R.id.et_phone);
        llArea = (LinearLayout) rootView.findViewById(R.id.ll_area);
        etArea = (TextView) rootView.findViewById(R.id.et_area);
        etAddress = (EditText) rootView.findViewById(R.id.et_address);
        etZipcode = (EditText) rootView.findViewById(R.id.et_zipcode);
        rgBtn = (RadioGroup) rootView.findViewById(R.id.rg_btn);
        rbBtn1 = (RadioButton) rootView.findViewById(R.id.rb_btn1);
        rbBtn2 = (RadioButton) rootView.findViewById(R.id.rb_btn2);
        ll_ismoren = (LinearLayout) rootView.findViewById(R.id.ll_ismoren);
        tvBtnOk = (TextView) rootView.findViewById(R.id.tv_btn_ok);
    }

    private void initViewsParams() {
        tv_title.setText(getResources().getString(R.string.edit_addr_title));

        etUsername.setText(value_contact_name);
        etPhone.setText(value_mobile);
        etUsername.setText(value_contact_name);
        etZipcode.setText(value_zipcode);

        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);

        rgBtn.check(R.id.rb_btn1);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        llArea.setOnClickListener(this);
        tvBtnOk.setOnClickListener(this);
        rgBtn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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
        }else if (v==llArea) {
            startForResult(SelectAddrDelegate.newInstance("RegisterActivity"), REQ_MODIFY_FRAGMENT);
        } else if (v==tvBtnOk) {
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
            if (TextUtils.isEmpty(etAddress.getText())) {
                show(getResources().getString(R.string.edit_addr_addr));
                return;
            }
            loadProcess();
            editAddressData(etUsername.getText().toString(),etPhone.getText().toString(),area_id + "", etAddress.getText().toString());
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == REQ_MODIFY_FRAGMENT && resultCode == RESULT_OK && data != null) {
            String name = data.getString("name");
            area_id = data.getInt("area_id", 0);
            etArea.setText(name);
        }
    }

    private void editAddressData(String s1, String s2, String s3, String s4) {
        String url = Config.SET_MOREN_ADDRESS + value_id;
        String apiName = "编辑地址接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Content-Type", "application/x-www-form-urlencoded");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("default",is_default + "");
        addParams.put("contact_name", s1);
        addParams.put("mobile", s2);
        addParams.put("district_id", s3);
        addParams.put("address_id", s3);
        addParams.put("street", s4);
        NetConnectionNew.patch(apiName, getContext(), url,addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        dismissLoadProcess();
                        processEditAddressData(arg0);
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

    private void processEditAddressData(String arg0) {
        try {
            JSONObject json = new JSONObject(arg0);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            show("修改成功");
            pop();
        } catch (Exception e) {
            show("修改成功");
            pop();
        }
    }

    private void getAddressListData() {
        String url = Config.ADDRESS_INFO;
        String apiName = "收货地址接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("page", 1 + "");
        addParams.put("size", 100 + "");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url,addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        dismissLoadProcess();
                        processAddressListData(arg0);
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

    private void processAddressListData(String result) {
        try {
            JSONObject json = new JSONObject(result);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            if (parseAdrJson(result).getData() != null) {
                if (parseAdrJson(result).getData().size()>0) {
                    if (parseAdrJson(result).getData().size()==1) {
                        ll_ismoren.setVisibility(View.GONE);
                    }else {
                        ll_ismoren.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception e) {}
    }

    private AddressBean parseAdrJson(String result) {
        return new Gson().fromJson(result, AddressBean.class);
    }
}
