package com.utravel.app.delegates.main.my;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.delegates.login.LoginChoiceDelegate;
import com.utravel.app.dialog.MyYaoQingRenDialog;
import com.utravel.app.entity.MyTeamBean;
import com.utravel.app.utils.GlideCircleTransform;
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

public class MyFansDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
    private AppCompatImageView iv_back;
    private AppCompatTextView tv_title;
    private AppCompatEditText et_search;
    private AppCompatImageView iv_search;
    private AppCompatTextView tv_myparent;
    private SmartRefreshLayout mSmartRefreshLayout;
    private GridView gv_fans;
    private AppCompatTextView tv_fansnums;
    private AppCompatImageView iv_empty;

    private List<MyTeamBean.DataBean> gvDatas = new ArrayList<>();
    private CommonAdapter<MyTeamBean.DataBean> gvAdapter;

    private int pageNo = 1;
    private boolean isNoMore = false;
    public boolean isSearch = false;
    private String content_et = null;

    @Override
    public boolean setIsDark() {
        return false;
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_my_fans;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewParams();
        initAdapter();
        initListener();
    }

    private void initViews(View rootView) {
        status_bar = (View) this.rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView) this.rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);
        tv_myparent = (AppCompatTextView) rootView.findViewById(R.id.tv_myparent);
        et_search = (AppCompatEditText) rootView.findViewById(R.id.et_search);
        iv_search = (AppCompatImageView) rootView.findViewById(R.id.iv_search);
        mSmartRefreshLayout = (SmartRefreshLayout)rootView.findViewById(R.id.refresh);
        gv_fans = (GridView) rootView.findViewById(R.id.gv_fans);
        tv_fansnums = (AppCompatTextView) rootView.findViewById(R.id.tv_fansnums);
        iv_empty = (AppCompatImageView) rootView.findViewById(R.id.iv_empty);

        tv_title.setText("我的粉丝");
        et_search.setHint("搜索个人粉丝用户");
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        loadProcess();
        getTeamDataFromNet();
    }

    private void initViewParams() {
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
    }

    private void initAdapter() {
        if (gvAdapter == null) {
            gvAdapter = new CommonAdapter<MyTeamBean.DataBean>(getContext(), gvDatas, R.layout.item_myfans_zhuanshu) {
                @Override
                public void convert(BaseViewHolder holder, MyTeamBean.DataBean t) {
                    Glide.with(getContext())
                            .load(t.getAvatar().getUrl())
                            .transform(new GlideCircleTransform(getContext()))
                            .into((ImageView)holder.getView(R.id.iv_avartor));
                    holder.setText(R.id.tv_name, t.getName());
                    if ( !TextUtils.isEmpty(SharedPreferencesUtil.getString(getContext(), "mobile"))) {
                        holder.setText(R.id.tv_phone, "账号" + SharedPreferencesUtil.getString(getContext(), "mobile"));
                    }
                    holder.setText(R.id.tv_time, Util.timeToYYMMDD(t.getCreated_at()));
                }
            };
            gv_fans.setAdapter(gvAdapter);
        }
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        tv_myparent.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId,KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    iv_search.performClick();
                }
                return false;
            }
        });
        mSmartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (isSearch) {
                    mSmartRefreshLayout.finishLoadMore();
                    return;
                }
                if (isNoMore) {
                    show(getResources().getString(R.string.no_data));
                    mSmartRefreshLayout.finishLoadMore();
                    return;
                }
                pageNo += 1;
                getTeamDataFromNet();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) { //返回
            pop();
        }else if (v==tv_myparent) { //查看我的邀请人
            MyYaoQingRenDialog dialog = MyYaoQingRenDialog.newInstance();
            dialog.show(getFragmentManager(), getClass().getSimpleName());
        }else if (v==iv_search) {
            if (TextUtils.isEmpty(et_search.getText())) {
                show("手机号不能为空");
                return;
            }
            isSearch = true;
            gvDatas.clear();
            pageNo = 1;
            isNoMore = false;
            getTeamDataFromNet1();
        }
    }

    private void getTeamDataFromNet() {
        String url = Config.GET_MY_TEAM;
        String apiName = "我的团队列表接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("page", pageNo+"");
        addParams.put("size", "10");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    mSmartRefreshLayout.finishLoadMore();
                    processMyTeamData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    mSmartRefreshLayout.finishLoadMore();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            showMsg401();
                        }else if (arg1.getMessage().contains("404")){
                            show(Config.ERROR404);
                        }
                    }
                }
            });
    }

    private void processMyTeamData(String result) {
        JSONObject json = JSON.parseObject(result);
        if (json != null) {
            if (json.containsKey("error")) {
                show(json.getString("error"));
                return;
            }
            tv_fansnums.setText("专属粉丝：" + parseTeamJson(result).getCounts());
            if (parseTeamJson(result).getData().size()==0) {
                if (gvDatas.size()>0) {
                    //上拉下拉listData会加载数据，如果到n组数据后,getData().size()==0不足以判断空，需要listdata作为依据
                    gv_fans.setVisibility(View.VISIBLE);
                    iv_empty.setVisibility(View.GONE);
                    tv_fansnums.setVisibility(View.VISIBLE);
                }else {
                    gv_fans.setVisibility(View.GONE);
                    iv_empty.setVisibility(View.VISIBLE);
                    tv_fansnums.setVisibility(View.GONE);
                }
                show("没有更多数据了");
                isNoMore = true;
                mSmartRefreshLayout.finishLoadMore();
                return;
            }
            gvDatas.addAll(parseTeamJson(result).getData());
            gvAdapter.refreshDatas(gvDatas);
            gv_fans.setVisibility(View.VISIBLE);
            iv_empty.setVisibility(View.GONE);
            tv_fansnums.setVisibility(View.VISIBLE);
        }
    }

    private void getTeamDataFromNet1() {
        String url = Config.GET_MY_TEAM_SEARCH;
        String apiName = "我的团队搜索接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("mobile", et_search.getText().toString());
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processMyTeamData1(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            showMsg401();
                        }else if (arg1.getMessage().contains("404")){
                            show(Config.TEAM_SEARCH_ERROR404);
                        }
                    }
                }
            });
    }

    private void processMyTeamData1(String arg0) {
        JSONObject json = JSON.parseObject(arg0);
        if (json != null) {
            if (json.containsKey("error")) {
                show(json.getString("error"));
                return;
            }
            gvDatas.addAll(parseTeamJson(arg0).getData());
            gvAdapter.refreshDatas(gvDatas);
            if (gvDatas.size()>0) {
                gv_fans.setVisibility(View.VISIBLE);
                iv_empty.setVisibility(View.GONE);
                tv_fansnums.setVisibility(View.VISIBLE);
            }else {
                gv_fans.setVisibility(View.GONE);
                iv_empty.setVisibility(View.VISIBLE);
                tv_fansnums.setVisibility(View.GONE);
            }
        }
    }

    private MyTeamBean parseTeamJson(String result) {
        return new Gson().fromJson(result, MyTeamBean.class);
    }
}
