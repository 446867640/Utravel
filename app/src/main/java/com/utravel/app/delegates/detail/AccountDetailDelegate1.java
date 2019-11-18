package com.utravel.app.delegates.detail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.activities.proxy.LoginActivity;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.entity.AccountLogBean;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class AccountDetailDelegate1 extends LatterDelegate implements View.OnClickListener {
    private View status_bar;
    private AppCompatImageView iv_back;
    private AppCompatTextView tv_title;
    private SmartRefreshLayout refresh;
    private GridView gv_account;
    private AppCompatImageView iv_empty;

    private List<AccountLogBean.DataBean> gv_detaiDatas = new ArrayList<AccountLogBean.DataBean>();
    private CommonAdapter<AccountLogBean.DataBean> gv_detaiAdapter;

    private int pageNo = 1;
    private boolean isNoMore = false;
    private static final String DELEGATE_TAG = "AccountDetailDelegate";
    private String type = "balance";

    @Nullable
    @Override
    public Object setLayout() { return R.layout.delegate_detail_account; }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        Util.setStatusBarMode(_mActivity,true);
    }

    public static AccountDetailDelegate1 newInstance(String value) {
        AccountDetailDelegate1 fragment = new AccountDetailDelegate1();
        Bundle bundle = new Bundle();
        bundle.putString(DELEGATE_TAG, value);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getString(DELEGATE_TAG);
        }
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewsParams();
        initAdapter();
        initListener();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        loadProcess();
        getDetailData();
    }

    private void initViews(View rootView) {
        status_bar = (View) this.rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView) this.rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);
        refresh = (SmartRefreshLayout) rootView.findViewById(R.id.refresh);
        gv_account = (GridView) rootView.findViewById(R.id.gv_account);
        iv_empty = (AppCompatImageView) rootView.findViewById(R.id.iv_empty);
    }

    private void initViewsParams() {
        if (type.equals("exchangeable_points")) {
            tv_title.setText("消费券明细");
        }else if (type.equals("balance")) {
            tv_title.setText("余额明细");
        }else {
            tv_title.setText("余额明细");
        }
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
    }

    private void initAdapter() {
        gv_detaiAdapter = new CommonAdapter<AccountLogBean.DataBean>(getContext(), gv_detaiDatas, R.layout.item_detail_account) {
            @Override
            public void convert(BaseViewHolder holder, AccountLogBean.DataBean t) {
                String time = Util.timeToYYMMDDHHmmss(t.getCreated_at());
                holder.setText(R.id.tv_1, t.getDetail());
                holder.setText(R.id.tv_2, time);
                holder.setText(R.id.tv_3, t.getAmount());
            }
        };
        gv_account.setAdapter(gv_detaiAdapter);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        refresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (isNoMore) {
                    show(_mActivity.getResources().getString(R.string.no_data));
                    refreshLayout.finishLoadMore();
                    return;
                }
                pageNo += 1;
                getDetailData();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) { //返回
            pop();
            _mActivity.finish();
        }
    }

    private void getDetailData() {
        String url = Config.GET_IN_OUT_DETAIL;
        String apiName = "收支明细接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("page", pageNo + "");
        addParams.put("size", 15 + "");
        addParams.put("type", type);
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url,addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    refresh.finishLoadMore();
                    processDetailData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    refresh.finishLoadMore();
                    iv_empty.setVisibility(View.VISIBLE);
                    gv_account.setVisibility(View.GONE);
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            showMsg401();
                        }
                    }
                }
            });
    }

    private void processDetailData(String result) {
        if (parseDetailData(result).getData().size()==0) {
            if (gv_detaiDatas.size()==0){
                iv_empty.setVisibility(View.VISIBLE);
                gv_account.setVisibility(View.GONE);
            }
            show(_mActivity.getResources().getString(R.string.no_data));
            isNoMore = true;
            gv_detaiAdapter.refreshDatas(gv_detaiDatas);
            return;
        }
        iv_empty.setVisibility(View.GONE);
        gv_account.setVisibility(View.VISIBLE);
        gv_detaiDatas.addAll(parseDetailData(result).getData());
        gv_detaiAdapter.refreshDatas(gv_detaiDatas);
    }

    private AccountLogBean parseDetailData(String result) {
        return new Gson().fromJson(result, AccountLogBean.class);
    }
}
