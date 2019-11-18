package com.utravel.app.delegates.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.alipay.PayDelegate;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.delegates.addr.ChoiceAddressDelegate;
import com.utravel.app.delegates.addr.SelectAddrDelegate;
import com.utravel.app.entity.AddressBean;
import com.utravel.app.entity.DingDanDitailBean1;
import com.utravel.app.ui.MyListView;
import com.utravel.app.utils.GlideRoundTransform;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;

public class SureOrderDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
    private AppCompatImageView iv_back;
    private AppCompatTextView tv_title;

    private static final String BUNDLE_QUANTITY = "BUNDLE_QUANTITY";
    private static final String BUNDLE_PRODUCT_ID = "BUNDLE_PRODUCT_ID";
    private static final String BUNDLE_CODE = "BUNDLE_CODE";
    private String value_quantity = null;
    private String value_product_id = null;
    private String value_code = null;

    private LinearLayout ll_choice_address;//空地址部分
    private LinearLayout ll_address;//非空地址部分
    private TextView tv1;//收货人
    private TextView tv2;//收货人联系电话
    private TextView tv3;//收货人地址
    private MyListView listview;//自营商品
    private TextView tv_b1;//共多少件商品
    private EditText et_remark;//买家留言
    private TextView tv_real_money;//实付款
    private LinearLayout ll_jiesuan;//提交部分
    private TextView tv_ok;//提交订单

    private List<AddressBean.DataBean> addrData;
    private DingDanDitailBean1.DataBean data1;
    private List<DingDanDitailBean1.DataBean.ItemsBean> dingDanDitailListData1 = new ArrayList<DingDanDitailBean1.DataBean.ItemsBean>();
    private CommonAdapter<DingDanDitailBean1.DataBean.ItemsBean> adapter1;

    private boolean isCheck_coupon_price;
    private boolean isCheck_coupon_point_price;
    private int address_id;//区id
    public static final int REQ_SS_FRAGMENT = 2323;

    @Override
    public boolean setIsDark() { return true; }

    @Override
    public Object setLayout() { return R.layout.delegate_order_sure; }

    public static SureOrderDelegate newInstance(String code) {
        SureOrderDelegate fragment = new SureOrderDelegate();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_CODE, code);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static SureOrderDelegate newInstances(String quantity, String product_id) {
        SureOrderDelegate fragment = new SureOrderDelegate();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_QUANTITY, quantity);
        bundle.putString(BUNDLE_PRODUCT_ID, product_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            value_quantity = bundle.getString(BUNDLE_QUANTITY);
            value_product_id = bundle.getString(BUNDLE_PRODUCT_ID);
            value_code = bundle.getString(BUNDLE_CODE);
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
        getAddressData();//地址管理接口
    }

    private void initViews(View rootView) {
        status_bar = (View) this.rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView) this.rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);
        ll_choice_address = (LinearLayout) rootView.findViewById(R.id.ll_choice_address);
        ll_address = (LinearLayout)rootView.findViewById(R.id.ll_address);
        tv1 = (TextView)rootView.findViewById(R.id.tv1);
        tv2 = (TextView)rootView.findViewById(R.id.tv2);
        tv3 = (TextView)rootView.findViewById(R.id.tv3);
        listview = (MyListView)rootView.findViewById(R.id.listview);
        tv_b1 = (TextView)rootView.findViewById(R.id.tv_b1);
        et_remark = (EditText)rootView.findViewById(R.id.et_remark);
        tv_real_money = (TextView)rootView.findViewById(R.id.tv_real_money);
        ll_jiesuan = (LinearLayout)rootView.findViewById(R.id.ll_jiesuan);
        tv_ok = (TextView)rootView.findViewById(R.id.tv_ok);
    }

    private void initViewsParams() {
        tv_title.setText(getResources().getString(R.string.order_sure_title));
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
    }

    private void initAdapter() {
        adapter1 = new CommonAdapter<DingDanDitailBean1.DataBean.ItemsBean>(getContext(),dingDanDitailListData1,R.layout.item_dingdan_ditail) {
            @Override
            public void convert(BaseViewHolder holder, DingDanDitailBean1.DataBean.ItemsBean t) {
                LinearLayout ll_sx = holder.getView(R.id.ll_sx);

                if (t.getSpecification_values().size()==0) {
                    ll_sx.setVisibility(View.GONE);
                }else {
                    String sxStr = "";
                    for (int i = 0; i < t.getSpecification_values().size(); i++) {
                        if (i==0) {
                            sxStr = t.getSpecification_values().get(i).getContent();
                        }else {
                            sxStr = sxStr + "+" + t.getSpecification_values().get(i).getContent();
                        }
                    }
                    holder.setText(R.id.tv_sx1, sxStr);
                    ll_sx.setVisibility(View.VISIBLE);
                }
                holder.setText(R.id.tv_1, t.getProduct_name());
                holder.setText(R.id.tv_2, "￥" + t.getPrice());
                holder.setText(R.id.tv_3, "×" + t.getQuantity());
                ImageView iv_1 = (ImageView)holder.getView(R.id.iv_1);
                if (t.getImage_url()!=null && t.getImage_url() != "") {
                    Glide.with(getContext())
                        .load(t.getImage_url())
                        .transform(new GlideRoundTransform(getContext(),5))
                        .into(iv_1);
                }
            }
        };
        listview.setAdapter(adapter1);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        ll_choice_address.setOnClickListener(this);
        ll_address.setOnClickListener(this);
        tv_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == iv_back) {
            pop();
        } else if (v == ll_choice_address) {
            startForResult(new ChoiceAddressDelegate(), REQ_SS_FRAGMENT);
        } else if (v==ll_address) {//选择收货地址
            ll_choice_address.performClick();
        }else if (v == tv_ok) {//提交订单
            if (address_id==0) {
                show("请选择收货地址");
                return;
            }
            //调用结算接口
            loadProcess();
            postDingDanDataFromNet();
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == REQ_SS_FRAGMENT && resultCode == RESULT_OK && data != null) {
            ll_choice_address.setVisibility(View.GONE);
            ll_address.setVisibility(View.VISIBLE);
            tv1.setText(data.getString("name"));
            tv2.setText(data.getString("mobile"));
            tv3.setText(data.getString("address"));
            address_id = Integer.parseInt(data.getString("id"));
        }
    }

    private void getAddressData() {
        String apiName = "地址管理接口";
        String url = Config.ADDRESS_INFO;
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("page", 1 + "");
        addParams.put("size", 1 + "");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        dismissLoadProcess();
                        processAddressData(arg0);
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

    private void processAddressData(String result) {
        try {
            JSONObject json = new JSONObject(result);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            addrData = parseAdrJson(result).getData();
            if (addrData != null && addrData.size() > 0) {
                ll_choice_address.setVisibility(View.GONE);
                ll_address.setVisibility(View.VISIBLE);
                ll_jiesuan.setVisibility(View.VISIBLE);
                tv1.setText(parseAdrJson(result).getData().get(0).getContact_name());
                tv2.setText(parseAdrJson(result).getData().get(0).getMobile());
                tv3.setText(parseAdrJson(result).getData().get(0).getAddress());
                address_id = parseAdrJson(result).getData().get(0).getId();
                //调用订单详情页面接口数据
                loadProcess();
                getOrdersData(address_id);
            }
        } catch (Exception e) {}
    }

    private AddressBean parseAdrJson(String result) {
        return new Gson().fromJson(result, AddressBean.class);
    }

    private void getOrdersData(final int address_id2) {
        String url = Config.DING_DAN_DETAIL;
        final String apiName = "调用订单详情页面接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("address_id",address_id2+"");
        addParams.put("time", System.currentTimeMillis() + "");
        if (value_product_id != null) {
            addParams.put("stock_keeping_unit_id", value_product_id);
        }
        if (value_quantity !=null) {
            addParams.put("quantity",value_quantity);
        }
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processOrdersData(arg0);
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

    private void processOrdersData(String result) {
        try {
            JSONObject json = new JSONObject(result);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            };
            if (parseOrdersData(result).getData()!=null) {
                data1 = parseOrdersData(result).getData();
                if (data1.getItems()!=null && data1.getItems().size()>0) {
                    listview.setVisibility(View.VISIBLE);
                    dingDanDitailListData1.addAll(data1.getItems());
                    adapter1.refreshDatas(dingDanDitailListData1);
                }else {
                    listview.setVisibility(View.GONE);
                }
                //共多少件商品
                tv_b1.setText("共" + dingDanDitailListData1.size() + "种商品");
                //商品总金额
                if (!TextUtils.isEmpty(data1.getTotal_amount())) {
                    tv_real_money.setText("￥ " + data1.getTotal_amount());
                }
            }
        } catch (Exception e) {}
    }

    private DingDanDitailBean1 parseOrdersData(String result) {
        return new Gson().fromJson(result, DingDanDitailBean1.class);
    }

    private void postDingDanDataFromNet() {
        String url = Config.DING_DAN_LIST;
        String apiName = "创建订单接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("address_id", address_id + "");
//		addParams.put("used_cash_coupons", coupon_price + "");
//		addParams.put("used_points_coupons", coupon_point_price + "");
        addParams.put("used_cash_coupons", isCheck_coupon_price + "");
        addParams.put("used_points_coupons", isCheck_coupon_point_price + "");
        addParams.put("time", System.currentTimeMillis()+"");
        if (value_product_id!=null) {
            addParams.put("stock_keeping_unit_id", value_product_id);
            LatteLogger.e("address_id",address_id + "");
            LatteLogger.e("stock_keeping_unit_id",value_product_id);
        }
        if (value_quantity!=null) {
            addParams.put("quantity",value_quantity);
            LatteLogger.e("quantity",value_quantity);
        }
        if (!et_remark.getText().toString().equals("")) {
            addParams.put("remark",et_remark.getText().toString());
            LatteLogger.e("remark",et_remark.getText().toString());
        }
        NetConnectionNew.post(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processPostDingDan(arg0);
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

    private void processPostDingDan(String arg0) {
        try {
            JSONObject json = new JSONObject(arg0);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            show("订单创建成功");
            if (json.has("data")) {
                if (json.optJSONArray("data").length()>1) {
                    getSupportDelegate().startWithPop(MyOrderDelegate.newInstance(1));
                }else {
                    if (json.optJSONArray("data").getJSONObject(0).optInt("id",0)!=0) {
                        getSupportDelegate().startWithPop(PayDelegate.newInstances(
                                json.optJSONArray("data").getJSONObject(0).optInt("id",0) + "",
                                "order",
                                value_product_id
                        ));
                    }
                }
            }else {
                if (json.optInt("id",0)!=0) {
                    getSupportDelegate().startWithPop(PayDelegate.newInstances(
                            json.optInt("id",0) + "",
                            "order",
                            value_product_id
                    ));
                }
            }
        } catch (JSONException e) {}
    }
}
