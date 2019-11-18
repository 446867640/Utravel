package com.utravel.app.delegates.news;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.delegates.login.LoginChoiceDelegate;
import com.utravel.app.delegates.withdraw.TixianDetailDelegate;
import com.utravel.app.entity.GetPersonNotificationsBean;
import com.utravel.app.entity.GetSystemNotificationsBean;
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

public class NewsChildDelegate1 extends LatterDelegate {

    private SmartRefreshLayout mSmartRefreshLayout;
    private GridView gv_news;
    private AppCompatImageView iv_empty;

    private List<GetSystemNotificationsBean.DataBean> gv_newsDatas1 = new ArrayList<GetSystemNotificationsBean.DataBean>();
    private List<GetPersonNotificationsBean.DataBean> gv_newsDatas2 = new ArrayList<GetPersonNotificationsBean.DataBean>();
    private CommonAdapter<GetSystemNotificationsBean.DataBean> gv_newsAdapter1;
    private CommonAdapter<GetPersonNotificationsBean.DataBean> gv_newsAdapter2;

    private int pageNo = 1;
    private boolean isNoMore = false;
    private static final String BUNDLE_TAG = "NewsChildDelegate1";
    private int tag_value = 0;

    public static NewsChildDelegate1 newInstance(int value) {
        NewsChildDelegate1 fragment = new NewsChildDelegate1();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_TAG, value);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            tag_value = bundle.getInt(BUNDLE_TAG);
        }
    }

    @Nullable
    @Override
    public Object setLayout() {
        return R.layout.delegate_news_child1;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initAdapter();
        initListener();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        loadProcess();
        if (tag_value==0) { //系统消息
            getSystemNotificationsData();
        }else { //个人消息
            getPersonNotificationsData();
        }
    }

    private void initViews(View rootView) {
        mSmartRefreshLayout = (SmartRefreshLayout)rootView.findViewById(R.id.refresh);
        gv_news = (GridView) rootView.findViewById(R.id.gv_news);
        iv_empty = (AppCompatImageView) rootView.findViewById(R.id.iv_empty);
    }

    private void initAdapter() {
        if (gv_newsAdapter1 == null) {
            gv_newsAdapter1 = new CommonAdapter<GetSystemNotificationsBean.DataBean>(getContext(), gv_newsDatas1, R.layout.item_news_system) {
                @Override
                public void convert(BaseViewHolder holder, GetSystemNotificationsBean.DataBean t) {
//                    ImageView iv1 = holder.getView(R.id.iv1);
                    String time = t.getCreated_at();
                    String tv1_time = Util.timeToYYMMDDHHmmss(time); //年月日 时分秒
                    String tv5_time = Util.timeToYYMMDD(time); //年月日
                    holder.setText(R.id.tv1, tv1_time);
                    holder.setText(R.id.tv2, t.getTitle());
                    holder.setText(R.id.tv3, t.getContent());
                    holder.setText(R.id.tv4, t.getSignature());
                    holder.setText(R.id.tv5, tv5_time);
                }
            };
        }
        if (gv_newsAdapter2 == null) {
            gv_newsAdapter2 = new CommonAdapter<GetPersonNotificationsBean.DataBean>(getContext(), gv_newsDatas2, R.layout.item_news_person) {
                @Override
                public void convert(BaseViewHolder holder, GetPersonNotificationsBean.DataBean t) {
                    ImageView iv1 = holder.getView(R.id.iv1); //不同类型同时设置不同图片
                    ImageView ivp = holder.getView(R.id.ivp); //小红点
                    String time = t.getCreated_at();
                    String tv2_time = Util.timeToYYMMDDHHmmss(time);

                    holder.setText(R.id.tv2, tv2_time);
                    holder.setText(R.id.tv3, t.getContent());

                    if (t.isRead()) {
                        ivp.setVisibility(View.GONE);
                    }else {
                        ivp.setVisibility(View.VISIBLE);
                    }
                    if(t.getTag().equals("downline_upgrade")){
                        holder.setText(R.id.tv1, "团队成员升级");
                    }else if (t.getTag().equals("points_arrival")) {
                        holder.setText(R.id.tv1, "积分到账");
                    }else if (t.getTag().equals("withdrawal_arrival")) {
                        holder.setText(R.id.tv1, "提现到账");
                    }else if (t.getTag().equals("points_purchase_rejected")) {
                        holder.setText(R.id.tv1, "爆款批发审核");
                    }else if (t.getTag().equals("balance_arrival")) {
                        holder.setText(R.id.tv1, "T宝到账");
                    }else if (t.getTag().equals("exchangeable_points_arrival")) {
                        holder.setText(R.id.tv1, "消费券到账");
                    }else if (t.getTag().equals("feed_back_reply")) {
                        holder.setText(R.id.tv1, "客服反馈");
                    }else if(t.getTag().equals("identity_certification")){
                        holder.setText(R.id.tv1, "认证结果");
                    }else if(t.getTag().equals("order")){
                        holder.setText(R.id.tv1, "订单信息");
                    }else if(t.getTag().equals("lucky_draw_finished")){
                        holder.setText(R.id.tv1, "T宝夺宝");
                    }else if(t.getTag().equals("national_porcelain_artwork_order")){
                        holder.setText(R.id.tv1, "大师作品");
                    }else if(t.getTag().equals("refund_activity")){
                        holder.setText(R.id.tv1, "退款信息");
                    }
                }
            };
        }
        if ( tag_value==0 ) { //系统消息
    	    gv_news.setAdapter(gv_newsAdapter1);
        }else if (tag_value==1) { //个人消息
    	    gv_news.setAdapter(gv_newsAdapter2);
        }
    }

    private void initListener() {
        if ( tag_value==1 ) { //个人消息
            gv_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    loadProcess();
                    patchReadnotifications(gv_newsDatas2.get(position).getId()+"" , position);
                }
            });
        }
        mSmartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (isNoMore) {
                    Toast.makeText(getContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                    refreshLayout.finishLoadMore();
                    return;
                }
                pageNo += 1;
                if ( tag_value==0 ) { //系统消息
                    getSystemNotificationsData();
                }else if ( tag_value==1 ) { //个人消息
                    getPersonNotificationsData();
                }
    	    }
        });
    }

    private void getSystemNotificationsData() {
        String url = Config.GET_SYSTEM_NOTIFICATIONS_LIST;
        String apiName = "系统公告接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addParams.put("page", pageNo + "");
        addParams.put("size", "10");
        NetConnectionNew.get(apiName, getContext(), url,addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    mSmartRefreshLayout.finishLoadMore();
                    procesSystemNotificationsData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    gv_news.setVisibility(View.GONE);
                    iv_empty.setVisibility(View.VISIBLE);
                    mSmartRefreshLayout.finishLoadMore();
                }
            });
    }

    private void procesSystemNotificationsData(String json) {
        JSONObject jsonStr = JSON.parseObject(json);
        if (jsonStr!=null) {
            if ( jsonStr.containsKey("error") ) {
                show(jsonStr.getString("error"));
                return;
            }
            if (parseSystemNotificationsData(json).getData().size()==0) {
                if (gv_newsDatas1.size()==0) {
                    gv_news.setVisibility(View.GONE);
                    iv_empty.setVisibility(View.VISIBLE);
                }
                show("没有更多数据了");
                mSmartRefreshLayout.finishLoadMore();
                isNoMore = true;
                return;
            }
            if (parseSystemNotificationsData(json).getData().size() > 0) {
                gv_news.setVisibility(View.VISIBLE);
                iv_empty.setVisibility(View.GONE);
                gv_newsDatas1.addAll(parseSystemNotificationsData(json).getData());
                gv_newsAdapter1.refreshDatas(gv_newsDatas1);
                mSmartRefreshLayout.finishLoadMore();
            }
        }else {

        }
    }

    private GetSystemNotificationsBean parseSystemNotificationsData(String json) {
        return new Gson().fromJson(json, GetSystemNotificationsBean.class);
    }

    private void getPersonNotificationsData() {
        String url = Config.GET_PERSON_NOTIFICATIONS_LIST;
        String apiName = "通知接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("page", pageNo + "");
        addParams.put("size", "10");
        NetConnectionNew.get(apiName, getContext(), url,addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    mSmartRefreshLayout.finishLoadMore();
                    procesPersonNotificationsData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    mSmartRefreshLayout.finishLoadMore();
                }
            });
    }

    private void procesPersonNotificationsData(String json) {
        JSONObject jsonStr = JSON.parseObject(json);
        if (jsonStr!=null) {
            if ( jsonStr.containsKey("error") ) {
                show(jsonStr.getString("error"));
                return;
            }
            if (parsePersonNotificationsData(json).getData().size()==0) {
                if (gv_newsDatas1.size()==0) {
                    gv_news.setVisibility(View.GONE);
                    iv_empty.setVisibility(View.VISIBLE);
                }
                show("没有更多数据了");
                mSmartRefreshLayout.finishLoadMore();
                isNoMore = true;
                return;
            }
            if (parsePersonNotificationsData(json).getData().size() > 0) {
                gv_news.setVisibility(View.VISIBLE);
                iv_empty.setVisibility(View.GONE);
                gv_newsDatas2.addAll(parsePersonNotificationsData(json).getData());
                gv_newsAdapter2.refreshDatas(gv_newsDatas2);
                mSmartRefreshLayout.finishLoadMore();
            }
        }else {

        }
    }

    private GetPersonNotificationsBean parsePersonNotificationsData(String json) {
        return new Gson().fromJson(json, GetPersonNotificationsBean.class);
    }

    private void patchReadnotifications(String id, final int position) {
        String url = Config.PATCH_READ_NOTIFICATIONS + id + "/read";
        String apiName = "标记已读接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addHeader.put("Content-Type", "application/json");
        addParams.put("time", System.currentTimeMillis()+"");
        NetConnectionNew.patch(apiName, getContext(), url, addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        dismissLoadProcess();
                        if (gv_newsDatas2.get(position).getTag().equals("withdrawal_arrival")) {
                            getSupportDelegate().start(TixianDetailDelegate.newInstance(gv_newsDatas2.get(position).getTag()));
                        }else if(gv_newsDatas2.get(position).getTag().equals("points_arrival")){
                            //我的积分
                        }else if (gv_newsDatas2.get(position).getTag().equals("downline_upgrade")) {
                            //我的等级
                        }else if (gv_newsDatas2.get(position).getTag().equals("points_purchase_rejected")) {
                            //爆款记录
                        }else if (gv_newsDatas2.get(position).getTag().equals("balance_arrival")) {
                            //余额明细
                            //待做
                        }else if (gv_newsDatas2.get(position).getTag().equals("exchangeable_points_arrival")) {
                            //消费券到账
                        }else if (gv_newsDatas2.get(position).getTag().equals("feed_back_reply")) {
                            //客服反馈
                        }else if (gv_newsDatas2.get(position).getTag().equals("identity_certification")) {
                            //个人认证

                        }else if (gv_newsDatas2.get(position).getTag().equals("order")) {
                            //订单信息
                            if (gv_newsDatas2.get(position).getPayload()!=null) {

                            }
                        }else if(gv_newsDatas2.get(position).getTag().equals("lucky_draw_finished")){
                            //T宝夺宝
                        }else if(gv_newsDatas2.get(position).getTag().equals("national_porcelain_artwork_order")){
                            //大师作品
                        }else if(gv_newsDatas2.get(position).getTag().equals("refund_activity")){
                            //退款申请
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

    private void patchAllRead() {
        String url = Config.PATCH_ALLREAD_NOTIFICATIONS;
        String apiName = "消息全部标记为已读接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addHeader.put("Content-Type", "application/json");
        addParams.put("time", System.currentTimeMillis()+"");
        NetConnectionNew.patch(apiName, getContext(), url,  addHeader,  addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    pageNo = 1;
                    isNoMore = false;
                    if (gv_newsDatas2!=null) {
                        gv_newsDatas2.clear();
                    }
                    loadProcess();
                    getPersonNotificationsData();
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
}
