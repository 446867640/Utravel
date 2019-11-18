package com.utravel.app.delegates.order;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.delegates.login.LoginChoiceDelegate;
import com.utravel.app.entity.PinduoduoOrderBean;
import com.utravel.app.ui.MyGridView;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.GlidePartCornerTransform;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class TBOrderDelegate extends LatterDelegate implements View.OnClickListener {
    private SmartRefreshLayout refresh;
    private AppCompatEditText et_search;
    private AppCompatImageView iv_search;
    private MyGridView gv_order_tabbar;
    private GridView gv_order;
    private AppCompatImageView iv_empty;

    private List<String> gv_orderTabDatas = new ArrayList<>();
    private CommonAdapter<String> gv_orderTabAdapter;
    private List<PinduoduoOrderBean.DataBean> gv_orderDatas = new ArrayList<>();
    private CommonAdapter<PinduoduoOrderBean.DataBean> gv_orderAdapter;

    private int pageNo = 1;
    private boolean isNoMore = false;
    private int mCurrentTabbarPosition = 0; //当前tabbar位置
    private String state = null;

    @Nullable
    @Override
    public Object setLayout() { return R.layout.delegate_order_basechild; }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initAdapter();
        initListener();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        getOrderTabbarData();
        loadProcess();
        getOrderData();
    }

    private void initViews(View rootView) {
        refresh = (SmartRefreshLayout) rootView.findViewById(R.id.refresh);
        et_search = (AppCompatEditText) rootView.findViewById(R.id.et_search);
        iv_search = (AppCompatImageView) rootView.findViewById(R.id.iv_search);
//        gv_order_tabbar = (MyGridView) rootView.findViewById(R.id.gv_order_tabbar);
        gv_order = (GridView) rootView.findViewById(R.id.gv_order);
        iv_empty = (AppCompatImageView) rootView.findViewById(R.id.iv_empty);
    }

    private void initAdapter() {
        if (gv_orderTabAdapter == null) {//tabbar适配器
            gv_orderTabAdapter = new CommonAdapter<String>(getContext(), gv_orderTabDatas, R.layout.item_order_tabbar) {
                @Override
                public void convert(BaseViewHolder holder, String s) {
                    AppCompatTextView tv1 = holder.getView(R.id.tv1);
                    View v_line = holder.getView(R.id.v_line);
                    tv1.setText(s);
                    if (mCurrentTabbarPosition == holder.getPosition()) {
                        tv1.setTextColor(ContextCompat.getColor(getContext(), R.color.delegate_red));
                        v_line.setVisibility(View.VISIBLE);
                    }else{
                        tv1.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color_gray));
                        v_line.setVisibility(View.GONE);
                    }
                }
            };
            gv_order_tabbar.setAdapter(gv_orderTabAdapter);
        }
        if (gv_orderAdapter == null) {//订单适配器
            gv_orderAdapter = new CommonAdapter<PinduoduoOrderBean.DataBean>(getContext(), gv_orderDatas, R.layout.item_order_pinduoduo) {
                @Override
                public void convert(BaseViewHolder holder, PinduoduoOrderBean.DataBean t) {
                    final AppCompatTextView tv2 = holder.getView(R.id.tv2);
                    final MyGridView gv_goods = holder.getView(R.id.gv_goods);
                    holder.setText(R.id.tv1, t.getNumber());
                    if (!TextUtils.isEmpty(t.getState_zh_cn())) {
                        tv2.setText(t.getState_zh_cn());
                    }
                    if (t.getState().equals("paid")) { //待返佣
                        tv2.setTextColor(ContextCompat.getColor(getContext(), R.color.color_orange_FF6325));
                        tv2.setBackgroundResource(R.drawable.line_orange_5_daojiao);
                    }else if (t.getState().equals("finished")) { //已到账
                        tv2.setTextColor(ContextCompat.getColor(getContext(), R.color.delegate_red));
                        tv2.setBackgroundResource(R.drawable.line_red_5_daojiao);
                    }else if (t.getState().equals("settled")) { //已失效
                        tv2.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color_black));
                        tv2.setBackgroundResource(R.drawable.line_black_5_daojiao);
                    }
//                    holder.setText(R.id.tv_ordered_at, t.getOrdered_at());
                    holder.setText(R.id.tv_total, "总计：¥" + t.getTotal_paid());
                    if (t.getItems() != null && t.getItems().size() > 0) {
                        final List<PinduoduoOrderBean.DataBean.ItemsBean> productDatas = new ArrayList<PinduoduoOrderBean.DataBean.ItemsBean>();
                        productDatas.addAll(t.getItems());
                        //设置订单商品gv
                        gv_goods.setAdapter(new CommonAdapter<PinduoduoOrderBean.DataBean.ItemsBean>(getContext(), productDatas, R.layout.item_order_pinduoduo_itemgoods) {
                            @Override
                            public void convert(BaseViewHolder helder, PinduoduoOrderBean.DataBean.ItemsBean bean) {
                                final AppCompatImageView iv1_item = helder.getView(R.id.iv1_item);
                                helder.setText(R.id.tv_goodsname, bean.getProduct_name());
//                                helder.setText(R.id.tv_goodsprice, "￥" + bean.getUnit_price());
//                                helder.setText(R.id.tv_rewarded_balance, "预估返" + bean.getRewarded_balance());
                                GlidePartCornerTransform transformation = new GlidePartCornerTransform(getContext(), DensityUtil.dp2px(getContext(), 2));
                                //只是绘制左上角和右上角圆角
                                transformation.setExceptCorner(false, false, false, false);
                                Glide.with(getContext())
                                    .load(bean.getImage_url())
                                    .asBitmap()
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .transform(transformation)
                                    .into(iv1_item);
                            }
                        });
                    }
                }
            };
            gv_order.setAdapter(gv_orderAdapter);
        }
    }

    private void initListener() {
        iv_search.setOnClickListener(this);
        gv_order_tabbar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ( mCurrentTabbarPosition == position){
                    return;
                }
                mCurrentTabbarPosition = position;
                gv_orderTabAdapter.refreshDatas(gv_orderTabDatas);
                if (position==0){ //全部
                    state = null;
                }else if (position==1) { //待返佣
                    state = "paid";
                }else if (position==2) { //已到账
                    state = "finished";
                }else if (position==3) { //已失效
                    state = "settled";
                }
                pageNo = 1;
                isNoMore = false;
                gv_orderDatas.clear();
                loadProcess();
                getOrderData();
            }
        });
        refresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (isNoMore) {
                    show("没有更多数据了");
                    refreshLayout.finishLoadMore();
                    return;
                }
                pageNo += 1;
                getOrderData();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==iv_search) {

        }
    }

    private void getOrderTabbarData() {
        if (gv_orderTabDatas.size() == 0) {
            gv_orderTabDatas.add(_mActivity.getResources().getString(R.string.order_tabbar_pinduoduo_all));
            gv_orderTabDatas.add(_mActivity.getResources().getString(R.string.order_tabbar_pinduoduo_waiting));
            gv_orderTabDatas.add(_mActivity.getResources().getString(R.string.order_tabbar_pinduoduo_account));
            gv_orderTabDatas.add(_mActivity.getResources().getString(R.string.order_tabbar_pinduoduo_expired));
            gv_orderTabAdapter.refreshDatas(gv_orderTabDatas);
        }
    }

    private void getOrderData() {
        String url = Config.PRODUCT_PDD_ORDER; //接口URL
        String apiName = "拼多多订单列表接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        if (!TextUtils.isEmpty(state)){
            addParams.put("state", state);
        }
        addParams.put("page", pageNo + "");
        addParams.put("size", 10 + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    refresh.finishLoadMore();
                    processOrderData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    refresh.finishLoadMore();
                    dismissLoadProcess();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            SharedPreferencesUtil.clearlogin(getContext());
                            ((MyOrderDelegate)getParentFragment()).getSupportDelegate().start(new LoginChoiceDelegate());
                            ((MyOrderDelegate)getParentFragment()).getSupportDelegate().pop();
                        }
                    }
                }
            });
    }

    private void processOrderData(String result) {
        if (parseOrderData(result).getData().size()==0) {
            if (gv_orderDatas.size()==0) {
                iv_empty.setVisibility(View.VISIBLE);
                gv_order.setVisibility(View.GONE);
            }
            show("没有更多数据了");
            isNoMore = true;
            gv_orderAdapter.refreshDatas(gv_orderDatas);
            return;
        }
        iv_empty.setVisibility(View.GONE);
        gv_order.setVisibility(View.VISIBLE);
        gv_orderDatas.addAll(parseOrderData(result).getData());
        gv_orderAdapter.refreshDatas(gv_orderDatas);
    }

    private PinduoduoOrderBean parseOrderData(String arg0) {
        return new Gson().fromJson(arg0, PinduoduoOrderBean.class);
    }
}
