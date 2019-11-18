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
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;

public class AddBankCardDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
    private AppCompatImageView iv_back;
    private AppCompatTextView tv_title;
    private EditText et1, et2, et3, et4, et5, et6;
    private LinearLayout llIsmoren;
    private RadioGroup rgBtn;
    private RadioButton rbBtn1,rbBtn2;
    private TextView tvOk;
    private boolean is_default = true;

    @Override
    public boolean setIsDark() { return true; }

    @Override
    public Object setLayout() { return R.layout.delegate_add_bank_card; }

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
        llIsmoren = (LinearLayout) rootView.findViewById( R.id.ll_ismoren );
        rgBtn = (RadioGroup) rootView.findViewById( R.id.rg_btn );
        rbBtn1 = (RadioButton) rootView.findViewById( R.id.rb_btn1 );
        rbBtn2 = (RadioButton) rootView.findViewById( R.id.rb_btn2 );
        tvOk = (TextView) rootView.findViewById( R.id.tv_ok );
    }

    private void initViewsParams() {
        tv_title.setText(getResources().getString(R.string.add_bankcark_title));
        rgBtn.check(R.id.rb_btn1);
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
            loadProcess();
            postAddBankCard(
                    et4.getText().toString(),
                    et3.getText().toString(),
                    et5.getText().toString());
        }
    }

    public void postAddBankCard(String account_number, String account_name, String bank_address) {
        String url = Config.GET_BANK_CARD;
        String apiName = "创建新银行卡接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        Map<String, String> addParams = new HashMap<String, String>();
        addParams.put("account_number", account_number);//银行卡号
        addParams.put("name", account_name); //开户行名称
        addParams.put("branch", bank_address);//开户行地址
        addParams.put("default", is_default+"");//是否默认
        NetConnectionNew.post(apiName, getContext(), url,addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        dismissLoadProcess();
                        processAddBankCard(arg0);
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

    private void processAddBankCard(String arg0) {
        try {
            JSONObject json = new JSONObject(arg0);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            show("添加成功");
            pop();
        } catch (JSONException e) {
            show("添加成功");
            pop();
        }
    }

    public void getBankCardsList(){
        String url = Config.GET_BANK_CARD;
        String apiName = "银行卡列表接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        Map<String, String> addParams = new HashMap<String, String>();
        addParams.put("page", 1+"");
        addParams.put("size", 100+"");
        addParams.put("time", System.currentTimeMillis() + "");
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
            if (parseBankCardListData(result).getData() != null && parseBankCardListData(result).getData().size() > 0) {
                llIsmoren.setVisibility(View.VISIBLE);
            }else {
                llIsmoren.setVisibility(View.GONE);
            }
        } catch (JSONException e) {}
    }

    public BankCardsBean parseBankCardListData(String result) {
        return new Gson().fromJson(result, BankCardsBean.class);
    }
}
