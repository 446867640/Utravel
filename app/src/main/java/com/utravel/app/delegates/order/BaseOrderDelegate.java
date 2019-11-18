package com.utravel.app.delegates.order;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.utravel.app.R;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.alipay.PayDelegate;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.entity.OrderListBean;
import com.utravel.app.ui.MyGridView;
import com.utravel.app.utils.GlideRoundTransform;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;

public class BaseOrderDelegate extends LatterDelegate {
    private SmartRefreshLayout refresh;
    private MyGridView gv_order;
    private AppCompatImageView iv_empty;

    private List<OrderListBean.DataBean> gv_orderDatas = new ArrayList<>();
    private CommonAdapter<OrderListBean.DataBean> gv_orderAdapter;

    private int pageNo = 1;
    private boolean isNoMore = false;
    private boolean isPullDownRefresh = false;
    private static final String BUNDLE_STATE = "BUNDLE_STATE";
    private String state = null;

    public static BaseOrderDelegate newInstance(String value) {
        BaseOrderDelegate fragment = new BaseOrderDelegate();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_STATE, value);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            state = bundle.getString(BUNDLE_STATE);
        }
    }

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
        loadProcess();
        getOrderData();
    }

    private void initViews(View rootView) {
        refresh = (SmartRefreshLayout) rootView.findViewById(R.id.refresh);
        gv_order = (MyGridView) rootView.findViewById(R.id.gv_order);
        iv_empty = (AppCompatImageView) rootView.findViewById(R.id.iv_empty);
    }

    private void initAdapter() {
        if (gv_orderAdapter == null) {//订单适配器
            gv_orderAdapter = new CommonAdapter<OrderListBean.DataBean>(getContext(), gv_orderDatas, R.layout.item_order) {
                @Override
                public void convert(BaseViewHolder holder, final OrderListBean.DataBean t) {
                    final AppCompatTextView tv2 = holder.getView(R.id.tv2);
                    final AppCompatTextView tv_btn1 = holder.getView(R.id.tv_btn1);
                    final AppCompatTextView tv_btn2 = holder.getView(R.id.tv_btn2);
                    final MyGridView gv_goods = holder.getView(R.id.gv_goods);
                    holder.setText(R.id.tv1, t.getNumber());
                    if (!TextUtils.isEmpty(t.getState_zh_cn())) {
                        tv2.setText(t.getState_zh_cn());
                    }
                    tv_btn1.setVisibility(View.GONE);
                    tv_btn2.setVisibility(View.GONE);
                    tv_btn1.setEnabled(false);
                    tv_btn2.setEnabled(false);
                    holder.setText(R.id.tv_total, "总计：¥" + t.getTotal_payment_amount());
                    if (t.getState().equals("unpaid")) { //待付款
                        tv2.setTextColor(ContextCompat.getColor(getContext(), R.color.delegate_red));
                        tv2.setBackgroundResource(R.drawable.line_red_40_daojiao);
                        tv_btn1.setText("取消订单");
                        tv_btn2.setText("立即付款");
                        tv_btn1.setBackgroundResource(R.drawable.line_black_5_daojiao);
                        tv_btn2.setBackgroundResource(R.drawable.line_black_5_daojiao);
                        tv_btn1.setVisibility(View.VISIBLE);
                        tv_btn2.setVisibility(View.VISIBLE);
                        tv_btn1.setEnabled(true);
                        tv_btn2.setEnabled(true);
                    }else if (t.getState().equals("paid")) { //待发货
                        tv2.setTextColor(ContextCompat.getColor(getContext(), R.color.color_orange_FF6325));
                        tv2.setBackgroundResource(R.drawable.line_chengse_bg_baise_40_daojiao);
                        tv_btn1.setText("");
                        tv_btn2.setText("待发货");
                        tv_btn1.setVisibility(View.GONE);
                        tv_btn2.setVisibility(View.VISIBLE);
                        tv_btn1.setEnabled(false);
                        tv_btn2.setEnabled(false);
                    }else if (t.getState().equals("shipped")) { //待收货
                        tv2.setTextColor(ContextCompat.getColor(getContext(), R.color.color_orange_FF6325));
                        tv2.setBackgroundResource(R.drawable.line_chengse_bg_baise_40_daojiao);
                        tv_btn1.setText("");
                        tv_btn2.setText("确认收货");
                        tv_btn2.setBackgroundResource(R.drawable.line_black_5_daojiao);
                        tv_btn1.setVisibility(View.GONE);
                        tv_btn2.setVisibility(View.VISIBLE);
                        tv_btn1.setEnabled(false);
                        tv_btn2.setEnabled(true);
                    }else if (t.getState().equals("received")) { //已完成
                        tv2.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color_black));
                        tv2.setBackgroundResource(R.drawable.line_black_40_daojiao);
                        tv_btn1.setText("");
                        tv_btn2.setText("已完成");
                        tv_btn1.setVisibility(View.GONE);
                        tv_btn2.setVisibility(View.VISIBLE);
                        tv_btn1.setEnabled(false);
                        tv_btn2.setEnabled(false);
                    }
                    View.OnClickListener listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v==tv_btn1) {
                                if (t.getState().equals("unpaid")) { //待付款--取消订单
                                    loadProcess();
                                    putCancelDingDanData(t.getId(),6);
                                }
                            }else if(v==tv_btn2){
                                if (t.getState().equals("unpaid")) { //待付款--立即付款
                                    ((MyOrderDelegate)getParentFragment()).getSupportDelegate().start(PayDelegate.newInstances(
                                            t.getId() + "",
                                            "order",
                                            t.getItems().get(0).getStock_keeping_unit_id() + ""
                                    ));
                                }else if (t.getState().equals("shipped")) { //待收货--确认收货
                                    loadProcess();
								    putCancelDingDanData(t.getId(),9);
                                }
                            }
                        }
                    };
                    tv_btn1.setOnClickListener(listener);
                    tv_btn2.setOnClickListener(listener);
                    if (t.getItems() != null && t.getItems().size() > 0) {
                        final List<OrderListBean.DataBean.ItemsBean> productDatas = t.getItems();
                        //设置订单商品gv
                        gv_goods.setAdapter(new CommonAdapter<OrderListBean.DataBean.ItemsBean>(getContext(), productDatas, R.layout.item_order_itemgoods) {
                            @Override
                            public void convert(BaseViewHolder holder, OrderListBean.DataBean.ItemsBean bean) {
                                ImageView iv_avartor = holder.getView(R.id.iv_avartor);
                                TextView tv_sku = holder.getView(R.id.tv_sku);
                                if (bean.getSpecification_values().size()==0) {
                                    tv_sku.setVisibility(View.GONE);
                                }else {
                                    String strSpecValues = "";
                                    for (int i = 0; i < bean.getSpecification_values().size(); i++) {
                                        if (i==0) {
                                            strSpecValues = bean.getSpecification_values().get(i).getContent();
                                        }else {
                                            strSpecValues = strSpecValues + "，" + bean.getSpecification_values().get(i).getContent();
                                        }
                                    }
                                    holder.setText(R.id.tv_sku, strSpecValues);
                                    tv_sku.setVisibility(View.VISIBLE);
                                }
                                holder.setText(R.id.tv_goodsname, bean.getProduct_name());
                                holder.setText(R.id.tv_price, "¥" + bean.getPrice());
                                holder.setText(R.id.tv_count, "X" + bean.getQuantity());

                                if (!TextUtils.isEmpty(bean.getImage_url())) {
                                    Glide.with(getContext())
                                            .load(bean.getImage_url())
                                            .transform(new GlideRoundTransform(getContext(),5))
                                            .into(iv_avartor);
                                }
                            }
                        });
                        gv_goods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                                ((MyOrderDelegate)getParentFragment()).getSupportDelegate().start(DDItemDetailDelegate.newInstance(t.getId() + ""));
                            }
                        });
                    }
                }
            };
            gv_order.setAdapter(gv_orderAdapter);
        }
    }

    private void initListener() {
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (gv_orderDatas.size()>0) {
                    isPullDownRefresh = true;
                }
                gv_orderDatas.clear();
                pageNo = 1;
                isNoMore = false;
                getOrderData();
            }
        });
        refresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                isPullDownRefresh = false;
                if (isNoMore) {
                    show(getResources().getString(R.string.no_data));
                    refreshLayout.finishLoadMore();
                    return;
                }
                pageNo += 1;
                getOrderData();
            }
        });
    }

    private void getOrderData() {
        String url = Config.DING_DAN_LIST; //接口URL
        String apiName = "自营订单列表接口"; //接口名
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
                    refresh.finishRefresh();
                    processOrderData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    refresh.finishLoadMore();
                    refresh.finishRefresh();
                    dismissLoadProcess();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            showMsg401();
                        }
                    }
                }
            });
    }

    private void processOrderData(String result) {
        try {
            JSONObject json = new JSONObject(result);
            if (json!=null) {
                if ( json.has("error") ) {
                    show(json.optString("error",""));
                    return;
                }
                if (parseOrderData(result).getData().size()==0) {
                    show(getResources().getString(R.string.no_data));
                    isNoMore = true;
                    gv_orderAdapter.refreshDatas(gv_orderDatas);
                    if (isPullDownRefresh) {
                        return;
                    }
                    if (gv_orderDatas.size()==0) {
                        iv_empty.setVisibility(View.VISIBLE);
                        gv_order.setVisibility(View.GONE);
                    }
                    return;
                }
                iv_empty.setVisibility(View.GONE);
                gv_order.setVisibility(View.VISIBLE);
                gv_orderDatas.addAll(parseOrderData(result).getData());
                gv_orderAdapter.refreshDatas(gv_orderDatas);
            }
        } catch (JSONException e) {
            LatteLogger.e("订单列表接口异常", e.getMessage());
        }
    }

    private OrderListBean parseOrderData(String arg0) {
        return new Gson().fromJson(arg0, OrderListBean.class);
    }

    private void putCancelDingDanData(int id, final int flag) {
        String url = Config.PUT_DINGDAN;
        if (flag==6) {
            url = url + id + "/cancel";
        }else if (flag==9) {
            url = url + id + "/receive";
        }
        final String apiName = "取消、确认订单接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.put(apiName,getContext(),url,addHeader,addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processCancelDingDanData(arg0,flag);
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
                            show(Config.ERROR404);
                        }
                    }
                }
            });
    }

    private void processCancelDingDanData(String arg0, int flag) {
        try {
            JSONObject json = new JSONObject(arg0);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            parseCancelDingDanData(flag);
        } catch (Exception e) {
            parseCancelDingDanData(flag);
        }
    }

    private void parseCancelDingDanData(int flag) {
        if (flag == 6) {
            show("订单取消成功");
            choiceView();
        }else if (flag == 9) {
            show("已确认收货");
            //跳转已完成
            ((MyOrderDelegate)getParentFragment()).setViewPagerItem(4);
        }
    }

    private void choiceView() {
        gv_orderDatas.clear();
        pageNo = 1;
        isNoMore = false;
        getOrderData();
    }
}
