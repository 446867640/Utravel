package com.utravel.app.delegates.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.activities.base.YiJianActivity;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.alipay.PayDelegate;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.entity.OrderDetailBean1;
import com.utravel.app.ui.MyListView;
import com.utravel.app.utils.GlideRoundTransform;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;

public class DDItemDetailDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
    private AppCompatImageView iv_back;
    private AppCompatTextView tv_title;
    private LinearLayout llAddress;
    private LinearLayout ll_kefu;
    private TextView tv0,tv1,tv2,tv3;
    private TextView tv_s1,tv_s2;
    private TextView tvP1,tvP2,tvP3,tvP4;
    private TextView tvD1,tvD2,tvD3,tvD4,tvD5;
    private TextView tv_btn1,tv_btn2;
    private MyListView listview;
    private LinearLayout ll_dec_total_money;//商品总金额部分
    private TextView tv_dec_total_money;//商品总金额
    private LinearLayout ll_dec_postage_money;//运费部分
    private TextView tv_dec_postage_money;//运费金额
    private LinearLayout ll_dec_conpon_money;//抵扣金部分
    private TextView tv_dec_conpon_money;//抵扣金金额
    private LinearLayout ll_dec_conpon_point;//抵扣积分部分
    private TextView tv_dec_conpon_point;//抵扣积分金额
    private TextView tv_real_money;//实付款
    private LinearLayout ll_reward_point;//赠送积分部分
    private TextView tv_reward_point;//赠送积分
    private ImageView iv_kefu;

    private OrderDetailBean1 orderDetailData;
    private List<OrderDetailBean1.ItemsBean> line_items;
    private CommonAdapter<OrderDetailBean1.ItemsBean> adapter;

    private double postage_price;//邮费价格
    private double total_amount;//商品总价格
    private String id;
    private String detailState = "";
    private double total_payment_amount;//实付款
    private double coupon_price;//最高优惠抵扣金额
    private double coupon_point_price;//积分转化的最高优惠抵扣金额
    private double coupon_price_final;//最高优惠抵扣金额
    private double coupon_point_price_final;//积分转化的最高优惠抵扣金额

    private static final String BUNDLE_ID = "BUNDLE_ID";
    private String value_id = null;

    public static DDItemDetailDelegate newInstance(String id) {
        DDItemDetailDelegate fragment = new DDItemDetailDelegate();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            value_id = bundle.getString(BUNDLE_ID);
        }
    }

    @Override
    public boolean setIsDark() { return true; }

    @Override
    public Object setLayout() { return R.layout.delegate_dditem_detail; }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewsParams();
        initListener();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        if (value_id != null) {
            loadProcess();
            getOrderDetailData(value_id);
        }else {
            show("订单详情信息获取失败");
            tv_btn1.setEnabled(false);
            tv_btn2.setEnabled(false);
        }
    }

    private void initViews(View rootView) {
        status_bar = (View) this.rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView) this.rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);
        tv0 = (TextView) rootView.findViewById( R.id.tv0 );
        ll_kefu = (LinearLayout) rootView.findViewById( R.id.ll_kefu );
        llAddress = (LinearLayout) rootView.findViewById( R.id.ll_address );
        tv1 = (TextView) rootView.findViewById( R.id.tv1 );
        tv2 = (TextView) rootView.findViewById( R.id.tv2 );
        tv3 = (TextView) rootView.findViewById( R.id.tv3 );
        listview = (MyListView) rootView.findViewById( R.id.listview );
        tv_s1 = (TextView) rootView.findViewById( R.id.tv_s1 );
        tv_s2 = (TextView) rootView.findViewById( R.id.tv_s2 );
        tvP1 = (TextView) rootView.findViewById( R.id.tv_p1 );
        tvP2 = (TextView) rootView.findViewById( R.id.tv_p2 );
        tvP3 = (TextView) rootView.findViewById( R.id.tv_p3 );
        tvP4 = (TextView) rootView.findViewById( R.id.tv_p4 );
        tvD1 = (TextView) rootView.findViewById( R.id.tv_d1 );
        tvD2 = (TextView) rootView.findViewById( R.id.tv_d2 );
        tvD3 = (TextView) rootView.findViewById( R.id.tv_d3 );
        tvD4 = (TextView) rootView.findViewById( R.id.tv_d4 );
        tvD5 = (TextView) rootView.findViewById( R.id.tv_d5 );
        tv_btn1 = (TextView) rootView.findViewById( R.id.tv_btn1 );
        tv_btn2 = (TextView) rootView.findViewById( R.id.tv_btn2 );
        iv_kefu = (ImageView) rootView.findViewById( R.id.iv_kefu );
        ll_dec_total_money = (LinearLayout) rootView.findViewById(R.id.ll_dec_total_money);
        tv_dec_total_money = (TextView) rootView.findViewById(R.id.tv_dec_total_money);
        ll_dec_postage_money = (LinearLayout) rootView.findViewById(R.id.ll_dec_postage_money);
        tv_dec_postage_money = (TextView) rootView.findViewById(R.id.tv_dec_postage_money);
        ll_dec_conpon_money = (LinearLayout) rootView.findViewById(R.id.ll_dec_conpon_money);
        tv_dec_conpon_money = (TextView) rootView.findViewById(R.id.tv_dec_conpon_money);
        ll_dec_conpon_point = (LinearLayout) rootView.findViewById(R.id.ll_dec_conpon_point);
        tv_dec_conpon_point = (TextView) rootView.findViewById(R.id.tv_dec_conpon_point);
        tv_real_money = (TextView) rootView.findViewById(R.id.tv_real_money);
        ll_reward_point = (LinearLayout) rootView.findViewById(R.id.ll_reward_point);
        tv_reward_point = (TextView) rootView.findViewById(R.id.tv_reward_point);
    }

    private void initViewsParams() {
        tv_title.setText(getResources().getString(R.string.order_detail_title));
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        ll_kefu.setOnClickListener(this);
        tv_btn1.setOnClickListener(this);
        tv_btn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) { //返回
            pop();
        }else if (v==tv_btn1) {
            if (orderDetailData.getState().equals("unpaid")) {//待付款
                loadProcess();
                putCancelDingDanData(orderDetailData.getId(), 0);
            }else{//paid、shipped、received、closed、canceled
                goToTuiHuanAct(orderDetailData.getRefund_id(), orderDetailData.getState());
            }
        }else if (v==tv_btn2) {
            if (orderDetailData.getState().equals("unpaid")) {
                //待付款
                getSupportDelegate().startWithPop(PayDelegate.newInstances(
                        orderDetailData.getId()+"",
                        "order",
                        null
                ));
            }else if (orderDetailData.getState().equals("shipped")){
                //待收货
				loadProcess();
				putCancelDingDanData(orderDetailData.getId(), 1);
            }
        }else if (v==ll_kefu) {
            goToNextAty(YiJianActivity.class);
        }
    }

    private void goToTuiHuanAct(int refoundId, String state1) {
//        if (refoundId!=0) {//已申请退款服务
//            Intent intent = new Intent(DDItemDetailActivity.this,TuiKuanDetailActivity.class);
//            intent.putExtra("object_type","order");
//            intent.putExtra("state",state1);
//            intent.putExtra("id", refoundId+"");
//            startActivity(intent);
//        }else{//未申请退款服务
//            Intent intent = new Intent(DDItemDetailActivity.this,TuiHuanServiceActivity.class);
//            intent.putExtra("object_type","order");
//            intent.putExtra("state",state1);
//            intent.putExtra("id", orderDetailData.getId()+"");
//            startActivity(intent);
//        }
    }

    private void getOrderDetailData(String id2) {
        String url = Config.DING_DAN_LIST + "/" + id2;
        final String apiName = "订单详情接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url,addHeader, addParams,
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
                            tv_btn1.setEnabled(true);
                            tv_btn2.setEnabled(true);
                            processOrderDetailData(arg0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new NetConnectionNew.FailCallback() {
                    @Override
                    public void onFail(Call arg0, Exception arg1, int arg2) {
                        dismissLoadProcess();
                        tv_btn1.setEnabled(false);
                        tv_btn2.setEnabled(false);
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

    private void processOrderDetailData(String json) {
        try {
            JSONObject jsonStr = new JSONObject(json);
            if (jsonStr.has("error")) {
                show(jsonStr.optString("error",""));
                return;
            }
            orderDetailData = parseOrderDetailData(json);
            String[] addr = orderDetailData.getAddress().split("\\s+");
            //时间字符串所有内容
            String created_at = orderDetailData.getCreated_at();
            String updated_at = orderDetailData.getUpdated_at();
            tv0.setText(orderDetailData.getState_zh_cn());//订单状态
            tv1.setText(addr[0]);//收货人
            tv2.setText(addr[1]);//联系电话
            tv3.setText(addr[2]);//收货地址
            tvD1.setText("订单编号：" + orderDetailData.getNumber());//订单编号
            if (created_at!=null) {//创建时间
                tvD3.setVisibility(View.VISIBLE);
                int c = created_at.indexOf("T");
                int c1 = created_at.indexOf(".");
                created_at.substring(c+1, c1);
                tvD3.setText("创建时间：" + created_at.substring(0, c) + " " + created_at.substring(c+1, c1));
            }else {
                tvD3.setVisibility(View.GONE);
            }
            if (orderDetailData.getState().equals("shipped")) {
                if (updated_at!=null) {
                    int u = updated_at.indexOf("T");
                    int u1 = updated_at.indexOf(".");
                    updated_at.substring(u+1, u1);
                    tvD5.setText("发货时间：" + updated_at.substring(0, u) + " " + updated_at.substring(u+1, u1));//发货时间
                    tvD5.setVisibility(View.VISIBLE);
                }
            }else {
                tvD5.setVisibility(View.GONE);
            }
            //商品总价格
            if (!TextUtils.isEmpty(orderDetailData.getTotal_amount())) {
                total_amount = Double.parseDouble(orderDetailData.getTotal_amount());
                tv_dec_total_money.setText("￥" + total_amount);
            }
            if (!TextUtils.isEmpty(orderDetailData.getSubtotal())) {
                total_amount = Double.parseDouble(orderDetailData.getSubtotal());
                tv_dec_total_money.setText("￥" + total_amount);
            }
            //邮费
            if (!TextUtils.isEmpty(orderDetailData.getShipping_fees())) {
                postage_price = Double.parseDouble(orderDetailData.getShipping_fees());
                if (orderDetailData.getShipping_fees().equals("0.0")) {
                    ll_dec_postage_money.setVisibility(View.GONE);
                }else {
                    tv_dec_postage_money.setText("+￥" + postage_price);
                    ll_dec_postage_money.setVisibility(View.VISIBLE);
                }
            }
            if (!TextUtils.isEmpty(orderDetailData.getTotal_payment_amount())) {
                tv_real_money.setText("￥" + orderDetailData.getTotal_payment_amount());
            }
            if (!TextUtils.isEmpty(orderDetailData.getTotal_reward_points())) {
                tv_reward_point.setText(orderDetailData.getTotal_reward_points());
            }
            if (orderDetailData.getState().equals("unpaid")) {
                detailState  = "1";
                //待付款
                tv_btn1.setVisibility(View.VISIBLE);
                tv_btn2.setVisibility(View.VISIBLE);
                tv_btn1.setText("取消订单");
                tv_btn2.setText("立即付款");
                tv_btn1.setEnabled(true);
                tv_btn2.setEnabled(true);
            }else if (orderDetailData.getState().equals("paid")){
                detailState  = "2";
                //待发货
                tv_btn1.setVisibility(View.GONE);
                tv_btn2.setVisibility(View.GONE);
                if (orderDetailData.getRefund_id()!=0) {//有记录
                    tv_btn1.setText("退换详情");
                }else {
                    tv_btn1.setText("退换服务");
                }
                tv_btn2.setText("待发货");
                tv_btn1.setEnabled(true);
                tv_btn2.setEnabled(false);
            }else if (orderDetailData.getState().equals("shipped")){
                detailState  = "3";
                //待收货
                tv_btn1.setVisibility(View.GONE);
                tv_btn2.setVisibility(View.VISIBLE);
                if (orderDetailData.getRefund_id()!=0) {//有记录
                    tv_btn1.setText("退换详情");
                }else {
                    tv_btn1.setText("退换服务");
                }
                tv_btn2.setText("已完成");
                tv_btn1.setEnabled(true);
                tv_btn2.setEnabled(false);
            }else if (orderDetailData.getState().equals("received")){
                detailState  = "5";
                //已完成
                tv_btn1.setVisibility(View.GONE);
                tv_btn2.setVisibility(View.VISIBLE);
                if (orderDetailData.getRefund_id()!=0) {//有记录
                    tv_btn1.setText("退换详情");
                }else {
                    tv_btn1.setText("退换服务");
                }
                tv_btn2.setText("已完成");
                tv_btn1.setEnabled(true);
                tv_btn2.setEnabled(false);
            }if (orderDetailData.getState().equals("canceled")){
                detailState  = "4";
                //已取消订单
                if (orderDetailData.getRefund_id()!=0) {//有记录
                    tv_btn1.setVisibility(View.GONE);
//					tv_btn1.setVisibility(View.VISIBLE);
                    tv_btn1.setText("退换详情");
                    tv_btn1.setEnabled(true);
                }else {
                    tv_btn1.setVisibility(View.GONE);
                    tv_btn1.setText("");
                    tv_btn1.setEnabled(false);
                }
                tv_btn2.setVisibility(View.VISIBLE);
                tv_btn2.setText("订单已取消");
                tv_btn2.setEnabled(false);
            }else if (orderDetailData.getState().equals("closed")) {
                detailState  = "10";
                //订单已关闭
                if (orderDetailData.getRefund_id()!=0) {//有记录
                    tv_btn1.setVisibility(View.GONE);
//					tv_btn1.setVisibility(View.VISIBLE);
                    tv_btn1.setText("退换详情");
                    tv_btn1.setEnabled(true);
                }else {
                    tv_btn1.setVisibility(View.GONE);
                    tv_btn1.setText("");
                    tv_btn1.setEnabled(false);
                }
                tv_btn2.setVisibility(View.VISIBLE);
                tv_btn2.setEnabled(false);
                tv_btn2.setText("订单已关闭");
            }

            if (orderDetailData.getItems()!=null && orderDetailData.getItems().size() > 0) {
                line_items = orderDetailData.getItems();
                adapter = new CommonAdapter<OrderDetailBean1.ItemsBean>(getContext(),line_items,R.layout.item_dd_detail) {
                    @Override
                    public void convert(BaseViewHolder holder, OrderDetailBean1.ItemsBean t) {
                        holder.setText(R.id.tv1, t.getProduct_name());
                        holder.setText(R.id.tv2, "￥" + t.getPrice());
                        holder.setText(R.id.tv4, "×" + t.getQuantity());
                        TextView tv3 = holder.getView(R.id.tv3);
                        if (t.getSpecification_values().size()==0) {
                            tv3.setVisibility(View.GONE);
                        }else {
                            String strSpecValues = "";
                            for (int i = 0; i < t.getSpecification_values().size(); i++) {
                                if (i==0) {
                                    strSpecValues = t.getSpecification_values().get(i).getContent();
                                }else {
                                    strSpecValues = strSpecValues + "，" + t.getSpecification_values().get(i).getContent();
                                }
                            }
                            tv3.setVisibility(View.VISIBLE);
                            holder.setText(R.id.tv3, strSpecValues);
                        }
                        //加载图片
                        if (t.getImage_url()!=null && !t.getImage_url().equals("")) {
                            Glide.with(getContext())
                                .load(t.getImage_url())
                                .transform(new GlideRoundTransform(getContext(),5))
                                .into((ImageView)holder.getView(R.id.iv1));
                        }
                    }
                };
                listview.setAdapter(adapter);
            }
        } catch (Exception e) {}
    }

    private OrderDetailBean1 parseOrderDetailData(String json) {
        return new Gson().fromJson(json, OrderDetailBean1.class);
    }

    private void putCancelDingDanData(int id, final int flag) {
        String url="";
        if (flag==0) {
            url = Config.PUT_DINGDAN + id + "/cancel";
        }else if (flag==1) {
            url = Config.PUT_DINGDAN + id + "/receive";
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
        if (flag == 0) {
            show("订单已取消");
            pop();
        }else if (flag == 1) {
            show("已确认收货");
            //跳转已完成
            getSupportDelegate().startWithPop(MyOrderDelegate.newInstance(4));
        }
    }
}
