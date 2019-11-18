package com.utravel.app.delegates.main.shequ;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.utravel.app.R;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.entity.WoYaoQingDeRenBean1;
import com.utravel.app.ui.MyGridView;
import com.utravel.app.utils.GlideCircleTransform;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;

public class MyYaoQingDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
    private AppCompatImageView iv_back;
    private AppCompatTextView tv_title;
    private MyGridView gv_tabbar;
    private SmartRefreshLayout refresh;
    private GridView gv_team;
    private RelativeLayout rl_empty;
    private AppCompatTextView tv_team_count;

    private List<String> tabbarDatas = new ArrayList<String>();
    private CommonAdapter<String> adapter_tabbar;
    private WoYaoQingDeRenBean1 teamBean;
    private WoYaoQingDeRenBean1 teamNonBean;
    private List<WoYaoQingDeRenBean1.DataBean> teamDatas = new ArrayList<WoYaoQingDeRenBean1.DataBean>();
    private CommonAdapter<WoYaoQingDeRenBean1.DataBean> adapter;
    private List<WoYaoQingDeRenBean1.DataBean> teamDatas_non = new ArrayList<WoYaoQingDeRenBean1.DataBean>();
    private CommonAdapter<WoYaoQingDeRenBean1.DataBean> adapter_non;

    private int tabbarPosition=0;
    private int pageNo = 1;
    private int pageNo_non = 1;
    private boolean isNoMore = false;
    private boolean isNoMore_non = false;
    private boolean isOn = true; //是否是
    private boolean isFirst_non = true; //未激活是否是第一次加载数据
    private int teamCount;
    private int teamNonCount;

    @Override
    public boolean setIsDark() { return true; }

    @Override
    public Object setLayout() { return R.layout.delegate_my_yaoqing; }

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
        getTabbarData();
        loadProcess();
        getTeamData();
    }

    private void initViews(View rootView) {
        status_bar = (View) this.rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView) this.rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);
        refresh = (SmartRefreshLayout) rootView.findViewById(R.id.refresh);
        gv_tabbar = (MyGridView) rootView.findViewById(R.id.gv_tabbar);
        gv_team = (GridView) rootView.findViewById(R.id.gv_team);
        rl_empty = (RelativeLayout) rootView.findViewById(R.id.rl_empty);
        tv_team_count = (AppCompatTextView) rootView.findViewById(R.id.tv_team_count);
    }

    private void initViewsParams() {
        tv_title.setText("我的邀请");
        gv_team.setVisibility(View.VISIBLE);
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
    }

    private void initAdapter() {
        adapter_tabbar = new CommonAdapter<String>(getContext(), tabbarDatas, R.layout.item_myyaoqing_tabbar) {
            @Override
            public void convert(BaseViewHolder holder, String t) {
                TextView tv1 = holder.getView(R.id.tv1);
                View v_line = holder.getView(R.id.v_line);
                if (tabbarPosition == holder.getPosition()) {
                    tv1.setTextColor(Color.parseColor("#3796F9"));
                    tv1 .setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    v_line.setVisibility(View.VISIBLE);
                }else {
                    tv1.setTextColor(Color.parseColor("#666666"));
                    tv1 .setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    v_line.setVisibility(View.GONE);
                }
                tv1.setText(t);
            }
        };
        gv_tabbar.setAdapter(adapter_tabbar);
        adapter = new CommonAdapter<WoYaoQingDeRenBean1.DataBean>(getContext(), teamDatas, R.layout.item_myyaoqing) {
            @Override
            public void convert(BaseViewHolder holder, WoYaoQingDeRenBean1.DataBean t) {
                final ImageView iv_avartor = holder.getView(R.id.iv_avartor);
                holder.setText(R.id.tv_name, t.getName());
                holder.setText(R.id.tv_phone, "账号：" + t.getMobile());
                holder.setText(R.id.tv_time, Util.timeToYYMMDD(t.getCreated_at()));
                if (t.getAvatar()!=null) {
                    Glide.with(getContext())
                            .load(t.getAvatar().getUrl())
                            .error(R.mipmap.ic_launcher)
                            .transform(new GlideCircleTransform(getContext()))
                            .into(iv_avartor);
                }
            }
        };
        adapter_non = new CommonAdapter<WoYaoQingDeRenBean1.DataBean>(getContext(), teamDatas_non, R.layout.item_myyaoqing) {
            @Override
            public void convert(BaseViewHolder holder, WoYaoQingDeRenBean1.DataBean t) {
                final ImageView iv_avartor = holder.getView(R.id.iv_avartor);
                holder.setText(R.id.tv_name, t.getName());
                holder.setText(R.id.tv_phone, "账号：" + t.getMobile());
                holder.setText(R.id.tv_time, Util.timeToYYMMDD(t.getCreated_at()));
                if (t.getAvatar()!=null) {
                    Glide.with(getContext())
                            .load(t.getAvatar().getUrl())
                            .error(R.mipmap.ic_launcher)
                            .transform(new GlideCircleTransform(getContext()))
                            .into(iv_avartor);
                }
            }
        };
        gv_team.setAdapter(adapter);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        gv_tabbar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0) {
                    isOn = true;
                    gv_team.setAdapter(adapter);
                    if (teamDatas.size()>0) {
                        tv_team_count.setText("已激活成员总数：" + teamCount);
                        tv_team_count.setVisibility(View.VISIBLE);
                        gv_team.setVisibility(View.VISIBLE);
                        rl_empty.setVisibility(View.GONE);
                    }else {
                        tv_team_count.setVisibility(View.GONE);
                        rl_empty.setVisibility(View.VISIBLE);
                        gv_team.setVisibility(View.GONE);
                    }
                }else {
                    isOn = false;
                    gv_team.setAdapter(adapter_non);
                    if (isFirst_non) {
                        loadProcess();
                        getTeamData();
                        isFirst_non = false;
                    }
                    if (teamDatas_non.size()>0) {
                        tv_team_count.setText("未激活成员总数：" + teamNonCount);
                        tv_team_count.setVisibility(View.VISIBLE);
                        gv_team.setVisibility(View.VISIBLE);
                        rl_empty.setVisibility(View.GONE);
                    }else {
                        tv_team_count.setVisibility(View.GONE);
                        rl_empty.setVisibility(View.VISIBLE);
                        gv_team.setVisibility(View.GONE);
                    }
                }
                tabbarPosition = position;
                adapter_tabbar.refreshDatas(tabbarDatas);
            }
        });
        refresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (isOn) {
                    teamDatas.clear();
                    pageNo = 1;
                    isNoMore = false;
                }else {
                    teamDatas_non.clear();
                    pageNo_non = 1;
                    isNoMore_non = false;
                }
                getTeamData();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) { //返回
            pop();
        }
    }

    private void getTabbarData() {
        if (tabbarDatas.size()==0) {
            tabbarDatas.add("已激活成员");
            tabbarDatas.add("未激活成员");
            adapter_tabbar.refreshDatas(tabbarDatas);
        }
    }

    private void getTeamData(){
        String url = Config.INVITATIONS; //接口url
        String apiName = "我邀请的人接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("activated", isOn + "");
        addParams.put("size", 20 + "");
        if (isOn) {
            addParams.put("page", pageNo + "");
        }else {
            addParams.put("page", pageNo_non + "");
        }
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    refresh.finishLoadMore();
                    if (isOn) {
                        processTeamData(arg0);
                    }else {
                        processTeamNonData(arg0);
                    }
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    refresh.finishLoadMore();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            showMsg401();
                        }
                    }
                }
            });
    }

    private void processTeamData(String arg0) {
        try {
            JSONObject json = new JSONObject(arg0);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            teamBean = parseTeamData(arg0);
            teamCount = teamBean.getTotal_count();
            if (parseTeamData(arg0).getData().size()==0) {
                if (teamDatas.size()==0) {
                    rl_empty.setVisibility(View.VISIBLE);
                    tv_team_count.setVisibility(View.GONE);
                    gv_team.setVisibility(View.GONE);
                }
                show("没有更多数据了");
                isNoMore = true;
                adapter.refreshDatas(teamDatas);
                return;
            }
            teamDatas.addAll(parseTeamData(arg0).getData());
            adapter.refreshDatas(teamDatas);
            rl_empty.setVisibility(View.GONE);
            tv_team_count.setVisibility(View.VISIBLE);
            gv_team.setVisibility(View.VISIBLE);
            tv_team_count.setText("已激活成员总数：" + teamCount);
        } catch (Exception e) {}
    }

    private void processTeamNonData(String arg0) {
        try {
            JSONObject json = new JSONObject(arg0);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            teamNonBean = parseTeamData(arg0);
            teamNonCount = teamNonBean.getTotal_count();
            if (parseTeamData(arg0).getData().size()==0) {
                if (teamDatas_non.size()==0) {
                    rl_empty.setVisibility(View.VISIBLE);
                    tv_team_count.setVisibility(View.GONE);
                    gv_team.setVisibility(View.GONE);
                }
                show("没有更多数据了");
                isNoMore = true;
                adapter_non.refreshDatas(teamDatas_non);
                return;
            }
            teamDatas_non.addAll(parseTeamData(arg0).getData());
            adapter_non.refreshDatas(teamDatas_non);
            rl_empty.setVisibility(View.GONE);
            tv_team_count.setVisibility(View.VISIBLE);
            gv_team.setVisibility(View.VISIBLE);
            tv_team_count.setText("未激活成员总数：" + parseTeamData(arg0).getTotal_count());
        } catch (Exception e) {}
    }

    private WoYaoQingDeRenBean1 parseTeamData(String arg0) {
        return new Gson().fromJson(arg0, WoYaoQingDeRenBean1.class);
    }

}
