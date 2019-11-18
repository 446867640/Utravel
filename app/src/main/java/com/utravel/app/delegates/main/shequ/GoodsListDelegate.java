package com.utravel.app.delegates.main.shequ;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.utravel.app.R;
import com.utravel.app.activities.proxy.LoginActivity;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.entity.GoodsListBean;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.GlidePartCornerTransform;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;

public class GoodsListDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
    private AppCompatImageView iv_back;
    private AppCompatTextView tv_title;
    private SmartRefreshLayout refresh;
    private GridView gv_goods;

    private List<GoodsListBean.DataBean> goodsDatas = new ArrayList<>();
    private CommonAdapter<GoodsListBean.DataBean> goodsAdapter;

    private int pageNo = 1;
    private boolean isNoMore = false;

    @Override
    public boolean setIsDark() { return true; }

    @Override
    public Object setLayout() { return R.layout.delegate_goodslist; }

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
        getGoodsSearchData();
    }

    private void initViews(View rootView) {
        status_bar = (View) this.rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView) this.rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);
        refresh = (SmartRefreshLayout) rootView.findViewById(R.id.refresh);
        gv_goods = (GridView) rootView.findViewById(R.id.gv_goods);
    }

    private void initViewsParams() {
        tv_title.setText(getResources().getString(R.string.goods));
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
    }

    private void initAdapter() {
        if (goodsAdapter==null) {
            goodsAdapter = new CommonAdapter<GoodsListBean.DataBean>(getContext(), goodsDatas, R.layout.item_goodslist_gv1) {
                @Override
                public void convert(BaseViewHolder holder, GoodsListBean.DataBean t) {
                    final ImageView iv1 = (ImageView)holder.getView(R.id.iv1);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                    params.width  = DensityUtil.dp2px(getContext(), 100);
                    params.height = DensityUtil.dp2px(getContext(), 100);
                    iv1.setLayoutParams(params);
                    holder.setText(R.id.tv1, t.getName());
                    holder.setText(R.id.tv2, "¥" + t.getPrice());
                    GlidePartCornerTransform transformation = new GlidePartCornerTransform(getContext(), DensityUtil.dp2px(getContext(), 5));
                    transformation.setExceptCorner(false, false, true, true); //只是绘制左上角和右上角圆角
                    Glide.with(getContext())
                            .load(t.getImage_url())
                            .asBitmap()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .transform(transformation)
                            .into(iv1);
                }
            };
            gv_goods.setAdapter(goodsAdapter);
        }
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        refresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (isNoMore) {
                    show(getResources().getString(R.string.no_data));
                    refreshLayout.finishLoadMore();
                    return;
                }
                pageNo += 1;
                getGoodsSearchData();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == iv_back) { //返回
            pop();
        }
    }

    private void getGoodsSearchData() {
        String url = Config.GOODS_SEARCH; //接口url
        String apiName = "搜索商品接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addParams.put("page", pageNo+"");
        addParams.put("size", "20");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    refresh.finishLoadMore();
                    processGoodsSearchData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    refresh.finishLoadMore();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            show(Config.ERROR401);
                            SharedPreferencesUtil.clearlogin(getContext());
                            goToNextAty(LoginActivity.class);
                        }
                    }
                }
            });
    }

    private void processGoodsSearchData (String arg0){
        JSONObject json = JSON.parseObject(arg0);
        if (json != null) {
            if (json.containsKey("error")) {
                show(json.getString("error"));
                return;
            }
            if (parseGoodsSearchData(arg0).getData().size()==0) {
                Toast.makeText(getContext(), getResources().getString(R.string.no_data), Toast.LENGTH_SHORT).show();
                isNoMore = true;
                refresh.finishLoadMore();
                return;
            }
            goodsDatas.addAll(parseGoodsSearchData(arg0).getData());
            goodsAdapter.refreshDatas(goodsDatas);
        }
    }

    private GoodsListBean parseGoodsSearchData(String arg0) {
        return new Gson().fromJson(arg0, GoodsListBean.class);
    }
}
