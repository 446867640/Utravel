package com.utravel.app.delegates.bankcard;

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
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.entity.BankCardsBean;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;

public class EditBankCardDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
    private AppCompatImageView iv_back;
    private AppCompatTextView tv_title;
    private EditText et1, et2, et3, et4, et5, et6;
    private LinearLayout llIsmoren;
    private RadioGroup rgBtn;
    private RadioButton rbBtn1,rbBtn2;
    private TextView tvOk;

    private static final String BUNDLE_ID = "BUNDLE_ID";
    private static final String BUNDLE_BANK_NAME = "BUNDLE_BANK_NAME";
    private static final String BUNDLE_ACCOUNT_NUMBER = "BUNDLE_ACCOUNT_NUMBER";
    private static final String BUNDLE_IS_DEFAULT = "BUNDLE_IS_DEFAULT";
    private String value_id = null;
    private String value_bank_name = null;
    private String value_account_number = null;
    private boolean value_is_default = false;

    public static EditBankCardDelegate newInstance(String id, String bank_name, String account_number, boolean is_default) {
        EditBankCardDelegate fragment = new EditBankCardDelegate();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_ID, id);
        bundle.putString(BUNDLE_BANK_NAME, bank_name);
        bundle.putString(BUNDLE_ACCOUNT_NUMBER, account_number);
        bundle.putBoolean(BUNDLE_IS_DEFAULT, is_default);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            value_id = bundle.getString(BUNDLE_ID);
            value_bank_name = bundle.getString(BUNDLE_BANK_NAME);
            value_account_number = bundle.getString(BUNDLE_ACCOUNT_NUMBER);
            value_is_default = bundle.getBoolean(BUNDLE_IS_DEFAULT);
        }
    }

    @Override
    public boolean setIsDark() { return true; }

    @Override
    public Object setLayout() { return R.layout.delegate_edit_bank_card; }

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
        getBankCardsList();
    }

    private void initViews(View rootView) {
        status_bar = (View) this.rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView) this.rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);
        et1 = (EditText) rootView.findViewById( R.id.et_1 );
        et2 = (EditText) rootView.findViewById( R.id.et_2 );
        et3 = (EditText) rootView.findViewById( R.id.et_3 );
        et4 = (EditText) rootView.findViewById( R.id.et_4 );
        et5 = (EditText) rootView.findViewById( R.id.et_5 );
        et6 = (EditText) rootView.findViewById( R.id.et_6 );
        llIsmoren = (LinearLayout) rootView.findViewById( R.id.ll_ismoren );
        rgBtn = (RadioGroup) rootView.findViewById( R.id.rg_btn );
        rbBtn1 = (RadioButton) rootView.findViewById( R.id.rb_btn1 );
        rbBtn2 = (RadioButton) rootView.findViewById( R.id.rb_btn2 );
        tvOk = (TextView) rootView.findViewById( R.id.tv_ok );
    }

    private void initViewsParams() {
        tv_title.setText(getResources().getString(R.string.my_edit_bankcark_title));
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        rbBtn1.setOnClickListener( this );
        rbBtn2.setOnClickListener( this );
        tvOk.setOnClickListener(this);
        rgBtn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_btn1:
                        value_is_default = true;
                        break;
                    case R.id.rb_btn2:
                        value_is_default = false;
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
        }else if (v==tvOk) {
            if (TextUtils.isEmpty(et3.getText())) {
                show(getResources().getString(R.string.edit_bankcark_bank));
                return;
            }
            if (TextUtils.isEmpty(et4.getText())) {
                show(getResources().getString(R.string.edit_bankcark_cardnum));
                return;
            }
            if (TextUtils.isEmpty(et5.getText())) {
                show(getResources().getString(R.string.edit_bankcark_addr));
                return;
            }
            if (TextUtils.isEmpty(et6.getText())) {
                show(getResources().getString(R.string.edit_bankcark_loginpwd));
                return;
            }
            loadProcess();
            editBankCardData(
                    et3.getText().toString(), //银行名称
                    et4.getText().toString(), //银行卡号
                    et5.getText().toString(), //银行地址
                    et6.getText().toString());//登录密码
        }
    }

    private void editBankCardData(String bank_name, String account_number, String bank_address,  String password) {
        String url = Config.SET_BANK_CARD + value_id;
        String apiName = "编辑银行卡接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("name", bank_name);//银行名称3
        addParams.put("account_number", account_number);//银行卡号4
        addParams.put("branch", bank_address);//银行地址5
        addParams.put("password",password);//登录密码6
        addParams.put("default",value_is_default + "");
        NetConnectionNew.patch(apiName, getContext(), url,addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processEditBankCardData(arg0);
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

    private void processEditBankCardData(String arg0) {
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

    public void getBankCardsList(){
        String url = Config.GET_BANK_CARD;
        String apiName = "银行卡列表接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("page", 1 + "");
        addParams.put("size", 100 + "");
        addParams.put("time", System.currentTimeMillis()+"");
        NetConnectionNew.get(apiName, getContext(), url,addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processBankCardListData(arg0);
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

    public void processBankCardListData(String result) {
        try {
            JSONObject json = new JSONObject(result);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            if (parseBankCardListData(result).getData() != null) {
                if (parseBankCardListData(result).getData().size()>0) {
                    if (parseBankCardListData(result).getData().size()==1) {
                        llIsmoren.setVisibility(View.GONE);
                    }else {
                        llIsmoren.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception e) {}
    }

    public BankCardsBean parseBankCardListData(String result) {
        return new Gson().fromJson(result, BankCardsBean.class);
    }
}
