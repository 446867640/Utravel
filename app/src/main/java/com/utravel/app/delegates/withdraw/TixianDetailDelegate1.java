package com.utravel.app.delegates.withdraw;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.delegates.login.LoginChoiceDelegate;
import com.utravel.app.entity.TiXianDetailBean;
import com.utravel.app.ui.MyGridView;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class TixianDetailDelegate1 extends LatterDelegate implements View.OnClickListener {
    private View status_bar;
    private AppCompatImageView iv_back;
    private AppCompatTextView tv_title,tv_total_tixian;
    private SmartRefreshLayout refresh;
    private MyGridView gv_detail;
    private AppCompatImageView iv_empty;

    private CommonAdapter<TiXianDetailBean.DataBean.DetailBean> gvAdapter;
    private List<TiXianDetailBean.DataBean.DetailBean> gvDatas = new ArrayList<>();

    private static final String DETAIL_TYPE = "DETAIL_TYPE";
    private String detailType = null;
    private int pageNo = 1;
    private boolean isNoMore = false;
    private boolean isPullDownRefresh = false;

    public static TixianDetailDelegate1 newInstance(String type) {
        TixianDetailDelegate1 fragment = new TixianDetailDelegate1();
        Bundle bundle = new Bundle();
        bundle.putString(DETAIL_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            detailType = bundle.getString(DETAIL_TYPE);
        }
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        Util.setStatusBarMode(_mActivity,true);
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_my_tixian_detail;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewParams();
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
        iv_back = (AppCompatImageView)rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);
        tv_total_tixian = (AppCompatTextView) rootView.findViewById(R.id.tv_total_tixian);
        refresh = (SmartRefreshLayout) rootView.findViewById(R.id.refresh);
        gv_detail = (MyGridView) rootView.findViewById(R.id.gv_detail);
        iv_empty = (AppCompatImageView) rootView.findViewById(R.id.iv_empty);
    }

    private void initViewParams() {
        tv_title.setText(_mActivity.getResources().getString(R.string.my_tixianjilu));
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
    }

    private void initAdapter() {
        gvAdapter = new CommonAdapter<TiXianDetailBean.DataBean.DetailBean>(getContext(), gvDatas, R.layout.item_my_tixian_detail) {
            @Override
            public void convert(BaseViewHolder holder, TiXianDetailBean.DataBean.DetailBean t) {
                TextView tv2 = holder.getView(R.id.tv_2);
                TextView tv4 = holder.getView(R.id.tv_4);
                //时间字符串所有内容
                String time = Util.timeToYYMMDDHHmmss(t.getCreated_at());
                holder.setText(R.id.tv_1, "提现到" + t.getChannel_zh_cn() + "（T + " + t.getArrival_periods() + "）"); //提现到支付宝
                holder.setText(R.id.tv_2, t.getAmount_of_arrival()); //提现金额
                holder.setText(R.id.tv_3, time); //提现时间
                holder.setText(R.id.tv_4, t.getState_zh_cn()); //提现状态
                if (t.getState().equals("accepted")) {
                    //已通过(绿色)
                    tv4.setTextColor(ContextCompat.getColor(getContext(), R.color.color_lvse_06B704));
                    tv4.setBackgroundResource(R.drawable.line_green_8_daojiao);
                }else if (t.getState().equals("rejected")) {
                    //已拒绝(红色)
                    tv4.setTextColor(ContextCompat.getColor(getContext(), R.color.delegate_red));
                    tv4.setBackgroundResource(R.drawable.line_red_8_daojiao);
                }else if (t.getState().equals("successful")) {
                    //已到账(橙色)
                    tv4.setTextColor(ContextCompat.getColor(getContext(), R.color.color_orange_FF6325));
                    tv4.setBackgroundResource(R.drawable.line_orange_8_daojiao);
                }else {
                    //审核中
                    tv4.setTextColor(ContextCompat.getColor(getContext(), R.color.color_huise_808080));
                    tv4.setBackgroundResource(R.drawable.line_gray_8_daojiao);
                }
            }
        };
        gv_detail.setAdapter(gvAdapter);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        gv_detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (gvDatas.size()>0) {
                    isPullDownRefresh = true;
                }
                gvDatas.clear();
                pageNo = 1;
                isNoMore = false;
                getDetailData();
            }
        });
        refresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                isPullDownRefresh = false;
                if (isNoMore) {
                    show("没有更多数据了");
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
        if (v==iv_back) {
            pop();
            _mActivity.finish();
        }
    }

    private void getDetailData() {
        String url = Config.WITHDRAWALS; //接口url
        String apiName = "提现明细接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("page", pageNo + "");
        addParams.put("size", 10 + "");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    refresh.finishLoadMore();
                    refresh.finishRefresh();
                    processDetailData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    refresh.finishLoadMore();
                    refresh.finishRefresh();
                    iv_empty.setVisibility(View.VISIBLE);
                    gv_detail.setVisibility(View.GONE);
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            showMsg401();
                        }
                    }
                }
            });
    }

    private void processDetailData(String result) {
        if (parseDetailData(result).getData().getTotal()!=null) {
            tv_total_tixian.setText(parseDetailData(result).getData().getTotal());
        }
        if (parseDetailData(result).getData().getDetail().size()==0) {
            show(getResources().getString(R.string.no_data));
            isNoMore = true;
            gvAdapter.refreshDatas(gvDatas);
            if (isPullDownRefresh) {
                return;
            }
            if (gvDatas.size()==0){
                iv_empty.setVisibility(View.VISIBLE);
                gv_detail.setVisibility(View.GONE);
            }
            return;
        }
        iv_empty.setVisibility(View.GONE);
        gv_detail.setVisibility(View.VISIBLE);
        gvDatas.addAll(parseDetailData(result).getData().getDetail());
        gvAdapter.refreshDatas(gvDatas);
    }

    private TiXianDetailBean parseDetailData(String result) {
        return new Gson().fromJson(result, TiXianDetailBean.class);
    }
}
