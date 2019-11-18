package com.utravel.app.activities.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.activities.proxy.AccountLogActivity;
import com.utravel.app.activities.proxy.DDItemDetailActivity;
import com.utravel.app.activities.proxy.LoginActivity;
import com.utravel.app.activities.proxy.TixianDetailActivity;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.order.DDItemDetailDelegate;
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

public class NewsActivity extends BaseActivity implements View.OnClickListener {
    private View status_bar;
    private AppCompatImageView iv_back, iv_empty;
    private AppCompatTextView tv_title;
    private GridView gv_newbar, gv_news;
    private SmartRefreshLayout refresh;

    private List<String> tab_titlesData = new ArrayList<>();
    private List<GetSystemNotificationsBean.DataBean> gv_newsDatas1 = new ArrayList<GetSystemNotificationsBean.DataBean>();
    private List<GetPersonNotificationsBean.DataBean> gv_newsDatas2 = new ArrayList<GetPersonNotificationsBean.DataBean>();
    private CommonAdapter<String> tab_titlesAdapter;
    private CommonAdapter<GetSystemNotificationsBean.DataBean> gv_newsAdapter1;
    private CommonAdapter<GetPersonNotificationsBean.DataBean> gv_newsAdapter2;

    private int pageNo = 1;
    private boolean isNoMore = false;
    private int tag_value = 0;
    private boolean isFirstNews1 = true;
    private boolean isFirstNews2 = true;

    @Override
    public boolean setIsDark() { return true; }

    @Override
    protected int getLayoutId() { return R.layout.activity_news_main; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tag_value = getIntent().getIntExtra(Config.NEWS_KEY,0);
    }

    @Override
    protected void initParams() {
        initViews();
        initViewsParams();
        initAdapter();
        initListener();
        getTabTitleData();
        if (tag_value==0) { //系统消息
            isFirstNews1 = false;
            isFirstNews2 = true;
            gv_newsDatas1.clear();
            loadProcess();
            getSystemNotificationsData();
        }
    }

    private void initViews() {
        status_bar = (View)findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView)findViewById(R.id.iv_back);
        iv_empty = (AppCompatImageView)findViewById(R.id.iv_empty);
        tv_title = (AppCompatTextView)findViewById(R.id.tv_title);
        gv_newbar = (GridView) findViewById(R.id.gv_newbar);
        gv_news = (GridView) findViewById(R.id.gv_news);
        refresh = (SmartRefreshLayout) findViewById(R.id.refresh);
    }

    private void initViewsParams() {
        tv_title.setText("消息");

        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(this);
        status_bar.setLayoutParams(params);
    }

    private void initAdapter() {
        if (tab_titlesAdapter == null) {
            tab_titlesAdapter = new CommonAdapter<String>(this, tab_titlesData, R.layout.item_news_tabbar) {
                @Override
                public void convert(BaseViewHolder holder, String t) {
                    AppCompatTextView tv1 = holder.getView(R.id.tv1);
                    View v_line = holder.getView(R.id.v_line);
                    tv1.setText(t);
                    if ( tag_value == holder.getPosition() ) {
                        tv1.setTextColor(ContextCompat.getColor(NewsActivity.this, R.color.delegate_red));
                        v_line.setVisibility(View.VISIBLE);

                    }else {
                        tv1.setTextColor(ContextCompat.getColor(NewsActivity.this, R.color.text_color_black));
                        v_line.setVisibility(View.GONE);
                    }
                }
            };
            gv_newbar.setAdapter(tab_titlesAdapter);
        }
        if (gv_newsAdapter1 == null) {
            gv_newsAdapter1 = new CommonAdapter<GetSystemNotificationsBean.DataBean>(this, gv_newsDatas1, R.layout.item_news_system) {
                @Override
                public void convert(BaseViewHolder holder, GetSystemNotificationsBean.DataBean t) {
                    String time = t.getCreated_at();
                    String tv1_time = Util.timeToHHmm(time); //时分
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
            gv_newsAdapter2 = new CommonAdapter<GetPersonNotificationsBean.DataBean>(this, gv_newsDatas2, R.layout.item_news_person) {
                @Override
                public void convert(BaseViewHolder holder, GetPersonNotificationsBean.DataBean t) {
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
                    if (t.getTag().equals("withdrawal_arrival")) {
                        holder.setText(R.id.tv1, "提现到账");
                    }else if(t.getTag().equals("refund_activity")){
                        holder.setText(R.id.tv1, "退款信息");
                    }else if (t.getTag().equals("balance_arrival")) {
                        holder.setText(R.id.tv1, "余额到账");
                    }else if (t.getTag().equals("feed_back_reply")) {
                        holder.setText(R.id.tv1, "客服反馈");
                    }else if(t.getTag().equals("identity_certification")){
                        holder.setText(R.id.tv1, "认证结果");
                    }else if(t.getTag().equals("order")){
                        holder.setText(R.id.tv1, "订单信息");
                    }else if(t.getTag().equals("lucky_draw_finished")){//T宝夺宝
                        holder.setText(R.id.tv1, "");
                    }else if(t.getTag().equals("national_porcelain_artwork_order")){//大师作品
                        holder.setText(R.id.tv1, "");
                    }else if (t.getTag().equals("points_arrival")) { //积分到账
                        holder.setText(R.id.tv1, "");
                    }else if (t.getTag().equals("points_purchase_rejected")) { //爆款批发审核
                        holder.setText(R.id.tv1, "");
                    }else if (t.getTag().equals("exchangeable_points_arrival")) { //消费券到账
                        holder.setText(R.id.tv1, "");
                    }else if(t.getTag().equals("downline_upgrade")){ //团队成员升级
                        holder.setText(R.id.tv1, "");
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
        iv_back.setOnClickListener(this);
        gv_newbar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == tag_value) {//不允许重复点击bar
                    return;
                }
                if (position==1) {
                    if (!Util.isToken(NewsActivity.this) ) {
                        //跳转授权页面
                        show(Config.ERROR401);
                        SharedPreferencesUtil.clearlogin(NewsActivity.this);
                        goToNextAty(LoginActivity.class);
                        return;
                    }
                }

                tag_value = position;
                tab_titlesAdapter.refreshDatas(tab_titlesData);
                if ( tag_value==0 ) { //系统消息
                    gv_news.setAdapter(gv_newsAdapter1);
                    if (gv_newsDatas1.size()==0) {
                        gv_news.setVisibility(View.GONE);
                        iv_empty.setVisibility(View.VISIBLE);
                    }else {
                        gv_news.setVisibility(View.VISIBLE);
                        iv_empty.setVisibility(View.GONE);
                    }
                    if (isFirstNews1) {
                        isFirstNews1 = false;
                        gv_newsDatas1.clear();
                        loadProcess();
                        getSystemNotificationsData();
                    }
                }else if (tag_value==1) { //个人消息
                    gv_news.setAdapter(gv_newsAdapter2);
                    if (gv_newsDatas2.size()==0) {
                        gv_news.setVisibility(View.GONE);
                        iv_empty.setVisibility(View.VISIBLE);
                    }else {
                        gv_news.setVisibility(View.VISIBLE);
                        iv_empty.setVisibility(View.GONE);
                    }
                    if (isFirstNews2) {
                        isFirstNews2 = false;
                        gv_newsDatas2.clear();
                        loadProcess();
                        getPersonNotificationsData();
                    }
                }
            }
        });
        gv_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ( tag_value==1 ) { //个人消息
                    loadProcess();
                    patchReadnotifications(gv_newsDatas2.get(position).getId()+"" , position);
                }
            }
        });
        refresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (isNoMore) {
                    show(getResources().getString(R.string.no_data));
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

    @Override
    public void onClick(View v) {
        if ( v==iv_back ) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tag_value ==1) { //处于个人消息页面
            if (!Util.isToken(this)) {
                show401();
                show(Config.ERROR401);
                return;
            }
            isFirstNews2 = false;
            isFirstNews1 = true;
            gv_newsDatas2.clear();
            loadProcess();
            getPersonNotificationsData();
        }
    }

    private void getTabTitleData() {
        if (tab_titlesData.size()==0) {
            tab_titlesData.add(getResources().getString(R.string.news_tab1));
            tab_titlesData.add(getResources().getString(R.string.news_tab2));
            tab_titlesAdapter.refreshDatas(tab_titlesData);
        }
    }

    private void getSystemNotificationsData() {
        String url = Config.GET_SYSTEM_NOTIFICATIONS_LIST;
        String apiName = "系统公告接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addParams.put("page", pageNo + "");
        addParams.put("size", "10");
        NetConnectionNew.get(apiName, NewsActivity.this, url,addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    refresh.finishLoadMore();
                    procesSystemNotificationsData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    gv_news.setVisibility(View.GONE);
                    iv_empty.setVisibility(View.VISIBLE);
                    refresh.finishLoadMore();
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
                show(getResources().getString(R.string.no_data));
                refresh.finishLoadMore();
                isNoMore = true;
                return;
            }
            if (parseSystemNotificationsData(json).getData().size() > 0) {
                gv_news.setVisibility(View.VISIBLE);
                iv_empty.setVisibility(View.GONE);
                gv_newsDatas1.addAll(parseSystemNotificationsData(json).getData());
                gv_newsAdapter1.refreshDatas(gv_newsDatas1);
                refresh.finishLoadMore();
            }
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
        addHeader.put("Authorization", SharedPreferencesUtil.getString(NewsActivity.this, "Token"));
        addParams.put("page", pageNo + "");
        addParams.put("size", "10");
        NetConnectionNew.get(apiName, NewsActivity.this, url,addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    refresh.finishLoadMore();
                    procesPersonNotificationsData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    refresh.finishLoadMore();
                    gv_news.setVisibility(View.GONE);
                    iv_empty.setVisibility(View.VISIBLE);
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            showMsg401();
                        }
                    }
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
                if (gv_newsDatas2.size()==0) {
                    gv_news.setVisibility(View.GONE);
                    iv_empty.setVisibility(View.VISIBLE);
                }
                show(getResources().getString(R.string.no_data));
                refresh.finishLoadMore();
                isNoMore = true;
                return;
            }
            if (parsePersonNotificationsData(json).getData().size() > 0) {
                gv_news.setVisibility(View.VISIBLE);
                iv_empty.setVisibility(View.GONE);
                gv_newsDatas2.addAll(parsePersonNotificationsData(json).getData());
                gv_newsAdapter2.refreshDatas(gv_newsDatas2);
                refresh.finishLoadMore();
            }
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
        addHeader.put("Authorization", SharedPreferencesUtil.getString(NewsActivity.this, "Token"));
        addHeader.put("Content-Type", "application/json");
        addParams.put("time", System.currentTimeMillis()+"");
        NetConnectionNew.patch(apiName, NewsActivity.this, url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    if (gv_newsDatas2.get(position).getTag().equals("withdrawal_arrival")) {
                        //提现明细
                        goToNextAty(TixianDetailActivity.class);
                    }else if (gv_newsDatas2.get(position).getTag().equals("balance_arrival")) {
                        //余额明细
                        goToNextAty(AccountLogActivity.class, "account_log", "balance");
                    }else if (gv_newsDatas2.get(position).getTag().equals("identity_certification")) {
                        //个人认证
                        goToNextAty(PersonRzActivity.class);
                    }else if(gv_newsDatas2.get(position).getTag().equals("points_arrival")){
                        //我的积分
                    }else if (gv_newsDatas2.get(position).getTag().equals("downline_upgrade")) {
                        //我的等级
                    }else if (gv_newsDatas2.get(position).getTag().equals("points_purchase_rejected")) {
                        //爆款记录
                    }else if (gv_newsDatas2.get(position).getTag().equals("exchangeable_points_arrival")) {
                        //消费券到账
                    }else if (gv_newsDatas2.get(position).getTag().equals("feed_back_reply")) {
                        //客服反馈
                    }else if (gv_newsDatas2.get(position).getTag().equals("order")) {
                        //订单信息
                        if (gv_newsDatas2.get(position).getPayload()!=null) {
                            goToNextAty(DDItemDetailActivity.class, "order_id", gv_newsDatas2.get(position).getPayload().getId()+"");
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
        addHeader.put("Authorization", SharedPreferencesUtil.getString(NewsActivity.this, "Token"));
        addHeader.put("Content-Type", "application/json");
        addParams.put("time", System.currentTimeMillis()+"");
        NetConnectionNew.patch(apiName, NewsActivity.this, url,  addHeader,  addParams,
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
