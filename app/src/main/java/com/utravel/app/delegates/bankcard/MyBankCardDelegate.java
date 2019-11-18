package com.utravel.app.delegates.bankcard;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.entity.BankCardsBean;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;

public class MyBankCardDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
    private ImageView iv_back,iv_del,iv_empty;
    private LinearLayout ll_del,ll_empty;
    private TextView tv_over,tvAddBank;
    private ListView bankListview;

    private List<BankCardsBean.DataBean> bankCardListData;
    private CommonAdapter<BankCardsBean.DataBean> bankCardListAdapter;

    private boolean isDel = false; //记录删除状态

    @Override
    public boolean setIsDark() { return true; }

    @Override
    public Object setLayout() { return R.layout.delegate_my_bankcard; }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewsParams();
        initAdapter();
        initListener();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        isDel = false;
        iv_del.setVisibility(View.VISIBLE);
        tv_over.setVisibility(View.GONE);
        loadProcess();
        getBankCardsList();
    }

    private void initViews(View rootView) {
        status_bar = (View) this.rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView) this.rootView.findViewById(R.id.iv_back);
        ll_del = (LinearLayout) rootView.findViewById(R.id.ll_del);
        iv_del = (ImageView)rootView.findViewById( R.id.iv_del );
        tv_over = (TextView)rootView.findViewById( R.id.tv_over );
        ll_empty = (LinearLayout) rootView.findViewById(R.id.ll_empty);
        bankListview = (ListView)rootView.findViewById( R.id.bank_listview );
        tvAddBank = (TextView)rootView.findViewById( R.id.tv_add_bank );
    }

    private void initViewsParams() {
        iv_del.setColorFilter(getResources().getColor(R.color.app_theme_color));
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
    }

    private void initAdapter() {
        bankCardListAdapter = new CommonAdapter<BankCardsBean.DataBean>(getContext(),bankCardListData,R.layout.item_bankcard) {
            @Override
            public void convert(BaseViewHolder holder, final BankCardsBean.DataBean t) {
                holder.setText(R.id.tv1, t.getName());
                holder.setText(R.id.tv3, t.getAccount_number());
                final LinearLayout ll_edit = holder.getView(R.id.ll_edit);
                final ImageView iv_btn1 = holder.getView(R.id.iv_btn1);
                final TextView tv_btn1 = holder.getView(R.id.tv_btn1);
                final TextView tv_btn2 = holder.getView(R.id.tv_btn2);
                final TextView tv_btn3 = holder.getView(R.id.tv_btn3);
                if (isDel) {
                    ll_edit.setVisibility(View.VISIBLE);
                }else {
                    ll_edit.setVisibility(View.GONE);
                }
                if (t.isIs_default()) {
                    iv_btn1.setBackgroundResource(R.drawable.qi_xuanze);
                    tv_btn1.setTextColor(Color.parseColor("#EE7C3E"));
                }else {
                    iv_btn1.setBackgroundResource(R.drawable.qb_weixuan);
                    tv_btn1.setTextColor(Color.parseColor("#333333"));
                }
                final LinearLayout ll_btn1 = holder.getView(R.id.ll_btn1);
                ll_btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (t.isIs_default()) {
                            show("已是默认状态");
                        }else {
                            //调用设为默认的接口
                            loadProcess();
                            setMoRenBankCardData(t.getId());
                        }
                    }
                });
                //编辑按钮
                tv_btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转activity
                        getSupportDelegate().start(EditBankCardDelegate.newInstance(
                                t.getId()+"",
                                t.getName(),
                                t.getAccount_number(),
                                t.isIs_default()));
                        //触发隐藏编辑部分
                        ll_del.performClick();
                    }
                });
                //删除按钮
                tv_btn3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //弹窗确认是否删除
                        showSureCancelPopwindow(t.getId()+"");
                    }
                });
            }
        };
        bankListview.setAdapter(bankCardListAdapter);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        ll_del.setOnClickListener(this);
        tvAddBank.setOnClickListener(this);
        bankListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isDel) {
                    //跳转activity
                    getSupportDelegate().start(EditBankCardDelegate.newInstance(
                            bankCardListData.get(position).getId()+"",
                            bankCardListData.get(position).getName(),
                            bankCardListData.get(position).getAccount_number(),
                            bankCardListData.get(position).isIs_default()));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) {
            pop();
        }else if (v==ll_del) {
            bankCardListAdapter.notifyDataSetChanged();
            if (isDel) {
                iv_del.setVisibility(View.VISIBLE);
                tv_over.setVisibility(View.GONE);
            }else {
                iv_del.setVisibility(View.GONE);
                tv_over.setVisibility(View.VISIBLE);
            }
            isDel = !isDel;
        }else if (v==tvAddBank) {
            getSupportDelegate().start(new AddBankCardDelegate());
        }
    }

    private void showSureCancelPopwindow(final String id) {
        View parent = ((ViewGroup) _mActivity.findViewById(android.R.id.content)).getChildAt(0);
        View popView = View.inflate(getContext(), R.layout.pop_sure_cancel, null);
        TextView et_name = (TextView) popView.findViewById(R.id.et_name);
        et_name.setText("是否确认删除？");
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
                        loadProcess();
                        delBankCardData(id);
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
            if (parseBankCardListData(result).getData()!= null) {
                if (parseBankCardListData(result).getData().size()>0) {
                    bankListview.setVisibility(View.VISIBLE);
                    ll_empty.setVisibility(View.GONE);
                    bankCardListData = parseBankCardListData(result).getData();
                    bankCardListAdapter.refreshDatas(bankCardListData);
                }else {
                    bankListview.setVisibility(View.GONE);
                    ll_empty.setVisibility(View.VISIBLE);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BankCardsBean parseBankCardListData(String result) {
        return new Gson().fromJson(result, BankCardsBean.class);
    }

    private void setMoRenBankCardData(int id) {
        String url = Config.SET_BANK_CARD + id + "/set_as_default";
        String apiName = "设置默认银行卡接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("aaa",id+"");
        addParams.put("time",System.currentTimeMillis() + "");
        NetConnectionNew.patch(apiName, getContext(), url,addHeader, addParams,
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
                        loadProcess();
                        getBankCardsList();
                    } catch (Exception e) {
                        loadProcess();
                        getBankCardsList();
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

    private void delBankCardData(String id) {
        String url = Config.SET_BANK_CARD + id;
        String apiName = "删除银行卡接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("time", System.currentTimeMillis()+"");
        NetConnectionNew.delete(apiName, getContext(), url,addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processDelBankCardData(arg0);
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

    private void processDelBankCardData(String arg0) {
        try {
            JSONObject json = new JSONObject(arg0);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            show("删除成功");
            loadProcess();
            getBankCardsList();
        } catch (Exception e) {
            show("删除成功");
            loadProcess();
            getBankCardsList();
        }
    }
}
