package com.utravel.app.delegates.main.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;

import com.utravel.app.R;
import com.utravel.app.activities.base.NewsActivity;
import com.utravel.app.activities.base.PersonRzActivity;
import com.utravel.app.activities.base.SettingActivity;
import com.utravel.app.activities.base.YiJianActivity;
import com.utravel.app.activities.proxy.LoginActivity;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.addr.MyAddressDelegate;
import com.utravel.app.delegates.bankcard.MyBankCardDelegate;
import com.utravel.app.delegates.detail.AccountDetailDelegate;
import com.utravel.app.delegates.detail.GoodsInfoDelegate;
import com.utravel.app.delegates.main.MainDelegate;
import com.utravel.app.delegates.main.OnBackDelegate;
import com.utravel.app.delegates.main.shequ.MyYaoQingDelegate;
import com.utravel.app.delegates.order.MyOrderDelegate;
import com.utravel.app.delegates.withdraw.TiXianDelegate;
import com.utravel.app.delegates.withdraw.TixianDetailDelegate;
import com.utravel.app.entity.ImgTvBean;
import com.utravel.app.entity.IntegerIconBean;
import com.utravel.app.ui.MyGridView;
import com.utravel.app.ui.zxing.activity.CaptureActivity1;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.GlideCircleTransform;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import com.utravel.app.utils.callback.CallbackManager;
import com.utravel.app.utils.callback.CallbackType;
import com.utravel.app.utils.callback.IGlobalCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class MyDelegate extends OnBackDelegate implements View.OnClickListener {
    private ImageView iv_xiaoxi, iv_erweima, iv_setting, iv_header, iv_yaoqing, iv_tixian;
    private TextView tv_name, tv_total_tixian, tv_total_zichan;
    private MyGridView gv_order, gv_tool, gv_service;
    private LinearLayout ll_tixian;

    private List<IntegerIconBean> orderDatas = new ArrayList<IntegerIconBean>();
    private CommonAdapter<IntegerIconBean> orderAdapter;
    private List<IntegerIconBean> toolDatas = new ArrayList<IntegerIconBean>();
    private CommonAdapter<IntegerIconBean> toolAdapter;
    private List<ImgTvBean> serviceDatas = new ArrayList<ImgTvBean>();
    private CommonAdapter<ImgTvBean> serviceAdapter;

    private int newsKey = 1;

    @Override
    public Object setLayout() {
        return R.layout.delegate_my;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initAdapter();
        initListener();
        CallbackManager.getInstance().addCallback(CallbackType.ON_SCAN, new IGlobalCallback<String>() {
            @Override
            public void executeCallback(String args) {
                LatteLogger.e("扫码信息", args);
                if (!TextUtils.isEmpty(args)) {
                    String code = Util.analyzingCode(args);
                    ((MainDelegate) getParentFragment()).getSupportDelegate().start(GoodsInfoDelegate.newInstance(code));
                }
            }
        });
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        getOrderData();
        getToolData();
        getServiceData();
    }

    private void initViews(View rootView) {
        iv_xiaoxi = (ImageView) rootView.findViewById(R.id.iv_xiaoxi);
        iv_erweima = (ImageView) rootView.findViewById(R.id.iv_erweima);
        iv_setting = (ImageView) rootView.findViewById(R.id.iv_setting);
        iv_header = (ImageView) rootView.findViewById(R.id.iv_header);
        tv_name = (TextView) rootView.findViewById(R.id.tv_name);
        ll_tixian = (LinearLayout) rootView.findViewById(R.id.ll_tixian);
        iv_tixian = (ImageView) rootView.findViewById(R.id.iv_tixian);
        tv_total_tixian = (TextView) rootView.findViewById(R.id.tv_total_tixian);
        tv_total_zichan = (TextView) rootView.findViewById(R.id.tv_total_zichan);
        iv_yaoqing = (ImageView) rootView.findViewById(R.id.iv_yaoqing);
        gv_order = (MyGridView) rootView.findViewById(R.id.gv_order);
        gv_tool = (MyGridView) rootView.findViewById(R.id.gv_tool);
        gv_service = (MyGridView) rootView.findViewById(R.id.gv_service);
    }

    private void initAdapter() {
        orderAdapter = new CommonAdapter<IntegerIconBean>(getContext(), orderDatas, R.layout.item_my_order) {
            @Override
            public void convert(BaseViewHolder holder, IntegerIconBean t) {
                ImageView iv1 = holder.getView(R.id.iv1);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                params.width = (screenWidth - DensityUtil.dp2px(getContext(), 176)) / 4;
                params.height = (screenWidth - DensityUtil.dp2px(getContext(), 176)) / 4;
                iv1.setLayoutParams(params);
                iv1.setScaleType(ImageView.ScaleType.FIT_XY);
                iv1.setImageResource(t.getIconImage());
            }
        };
        gv_order.setAdapter(orderAdapter);
        toolAdapter = new CommonAdapter<IntegerIconBean>(getContext(), toolDatas, R.layout.item_my_order) {
            @Override
            public void convert(BaseViewHolder holder, IntegerIconBean t) {
                ImageView iv1 = holder.getView(R.id.iv1);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                params.width = (screenWidth - DensityUtil.dp2px(getContext(), 164)) / 3;
                params.height = (screenWidth - DensityUtil.dp2px(getContext(), 164)) / 3;
                iv1.setLayoutParams(params);
                iv1.setScaleType(ImageView.ScaleType.FIT_XY);
                iv1.setImageResource(t.getIconImage());
            }
        };
        gv_tool.setAdapter(toolAdapter);
        serviceAdapter = new CommonAdapter<ImgTvBean>(getContext(), serviceDatas, R.layout.item_my_service) {
            @Override
            public void convert(BaseViewHolder holder, ImgTvBean t) {
                ImageView iv1 = holder.getView(R.id.iv_1);
                TextView tv1 = holder.getView(R.id.tv_1);
                tv1.setText(t.getName());
                iv1.setImageResource(t.getImageResource());
            }
        };
        gv_service.setAdapter(serviceAdapter);
    }

    private void initListener() {
        iv_header.setOnClickListener(this);
        tv_name.setOnClickListener(this);
        iv_erweima.setOnClickListener(this);
        iv_xiaoxi.setOnClickListener(this);
        iv_setting.setOnClickListener(this);
        iv_tixian.setOnClickListener(this);
        ll_tixian.setOnClickListener(this);
        tv_total_tixian.setOnClickListener(this);
        gv_order.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!Util.isToken(getContext())) {
                    showMsg401();
                    return;
                }
                ((MainDelegate) getParentFragment()).getSupportDelegate().start(MyOrderDelegate.newInstance(position));
            }
        });
        gv_tool.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!Util.isToken(getContext())) {
                    showMsg401();
                    return;
                }
                if (toolDatas.get(position).getId() == 0) {// 实名认证
                    goToNextAty(PersonRzActivity.class);
                } else if (toolDatas.get(position).getId() == 1) {// 收货地址
                    ((MainDelegate) getParentFragment()).getSupportDelegate().start(new MyAddressDelegate());
                } else if (toolDatas.get(position).getId() == 2) {// 我的卡包
                    ((MainDelegate) getParentFragment()).getSupportDelegate().start(new MyBankCardDelegate());
                }
            }
        });
        gv_service.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!Util.isToken(getContext())) {
                    showMsg401();
                    return;
                }
                if (serviceDatas.get(position).getId() == 3) {// 我的邀请
                    ((MainDelegate) getParentFragment()).getSupportDelegate().start(new MyYaoQingDelegate());
                } else if (serviceDatas.get(position).getId() == 4) {// 余额明细
                    ll_tixian.performClick();
                } else if (serviceDatas.get(position).getId() == 5) {// 提现记录
                    ((MainDelegate) getParentFragment()).getSupportDelegate().start(new TixianDetailDelegate());
                } else if (serviceDatas.get(position).getId() == 6) {// 意见反馈
                    goToNextAty(YiJianActivity.class);
                } else if (serviceDatas.get(position).getId() == 7) {// 客服中心
                    show(getResources().getString(R.string.please_waiting));
                }
            }
        });



        gv_order.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (SharedPreferencesUtil.getString(getContext(), "token").equals("")) {
                    SharedPreferencesUtil.clearlogin(getContext());
                    goToNextAty(LoginActivity.class);
                    return;
                }
                ((MainDelegate) getParentFragment()).getSupportDelegate().start(MyOrderDelegate.newInstance(position));
            }
        });
        gv_service.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (SharedPreferencesUtil.getString(getContext(), "token").equals("")) {
                    SharedPreferencesUtil.clearlogin(getContext());
                    goToNextAty(LoginActivity.class);
                    return;
                }
                if (serviceDatas.get(position).getId() == 0) {// 实名认证
                    goToNextAty(PersonRzActivity.class);
                } else if (serviceDatas.get(position).getId() == 1) {// 收货地址
                    ((MainDelegate) getParentFragment()).getSupportDelegate().start(new MyAddressDelegate());
                } else if (serviceDatas.get(position).getId() == 2) {// 我的卡包
                    ((MainDelegate) getParentFragment()).getSupportDelegate().start(new MyBankCardDelegate());
                } else if (serviceDatas.get(position).getId() == 3) {// 余额明细
                    ll_tixian.performClick();
                } else if (serviceDatas.get(position).getId() == 4) {// 我的邀请
                    ((MainDelegate) getParentFragment()).getSupportDelegate().start(new MyYaoQingDelegate());
                } else if (serviceDatas.get(position).getId() == 5) {// 提现记录
                    ((MainDelegate) getParentFragment()).getSupportDelegate().start(new TixianDetailDelegate());
                } else if (serviceDatas.get(position).getId() == 6) {// 意见反馈
                    goToNextAty(YiJianActivity.class);
                } else if (serviceDatas.get(position).getId() == 7) {// 客服中心
                    show("即将开通");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!Util.isToken(_mActivity)) {
            SharedPreferencesUtil.clearlogin(getContext());
            goToNextAty(LoginActivity.class);
            return;
        }
        if (v == iv_xiaoxi) { // 消息
            Intent intent = new Intent(getContext(), NewsActivity.class);
            intent.putExtra(Config.NEWS_KEY, newsKey);
            getContext().startActivity(intent);
        } else if (v == iv_header) { // 头像
            iv_setting.performClick();
        } else if (v == tv_name) { // 昵称
            iv_setting.performClick();
        } else if (v == iv_erweima) { // 二维码
//            ((MainDelegate) getParentFragment()).getSupportDelegate().startForResult(new ScannerDelegate(), RequestCodes.SCAN);
            goToNextAty(CaptureActivity1.class);
        } else if (v == iv_setting) { // 设置
            goToNextAty(SettingActivity.class);
        } else if (v == ll_tixian) { // 余额明细
            ((MainDelegate) getParentFragment()).getSupportDelegate().start(AccountDetailDelegate.newInstance("balance"));
        } else if (v == tv_total_tixian) { // 跳转提现明细
            ((MainDelegate) getParentFragment()).getSupportDelegate().start(new TixianDetailDelegate());
        } else if (v == iv_tixian) { // 提现页面
            ((MainDelegate) getParentFragment()).getSupportDelegate().start(new TiXianDelegate());
        }
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        Util.setStatusBarMode(_mActivity, false);
        onPageVisible();
    }

    private void onPageVisible() {
        loadProcess();
        getMemberData();
        getUnreadData();
    }

    private void getOrderData() {// 我的订单数据
        if (orderDatas.size() == 0) {
            orderDatas.add(new IntegerIconBean(R.drawable.quanbudingdan, 0));
            orderDatas.add(new IntegerIconBean(R.drawable.daifukuan, 1));
            orderDatas.add(new IntegerIconBean(R.drawable.daifahuo, 2));
            orderDatas.add(new IntegerIconBean(R.drawable.yiwancheng, 3));
            orderAdapter.refreshDatas(orderDatas);
        }
    }

    private void getToolData() {// 常用工具数据
        if (toolDatas.size() == 0) {
            toolDatas.add(new IntegerIconBean(R.drawable.shiming, 0)); // 实名认证
            toolDatas.add(new IntegerIconBean(R.drawable.shouhuo, 1)); // 我的地址
            toolDatas.add(new IntegerIconBean(R.drawable.kabao, 2)); // 我的卡包
            toolAdapter.refreshDatas(toolDatas);
        }
    }

    private void getServiceData() {// 服务中心数据
        if (serviceDatas.size() == 0) {
            serviceDatas.add(new ImgTvBean(R.drawable.wode_yuemingxi_icon, getResources().getString(R.string.yuemingxi), 4)); // 余额明细
            serviceDatas.add(new ImgTvBean(R.drawable.wode_tixianjilu_icon, getResources().getString(R.string.tixianjilu), 5)); // 提现记录
            serviceDatas.add(new ImgTvBean(R.drawable.wode_fankui_icon, getResources().getString(R.string.yijianfankui), 6)); // 意见反馈
            serviceDatas.add(new ImgTvBean(R.drawable.wode_kefu_icon, getResources().getString(R.string.kefuzhongxin), 7)); // 客服中心
            serviceAdapter.refreshDatas(serviceDatas);
        }
    }

    private void getMemberData() {
        String url = Config.DASHBOARD; // 请求接口url
        String apiName = "个人中心接口（dashboard）"; // 接口名
        Map<String, String> addHeader = new HashMap<String, String>(); // 请求头
        Map<String, String> addParams = new HashMap<String, String>(); // 请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String result, int arg1) {
                    dismissLoadProcess();
                    parseGRZXData(result);
                }
            }, new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    if (arg1.getMessage() != null) {
                        if (arg1.getMessage().contains("401")) {
                            showMsg401();
                        }
                    }
                }
            });
    }

    private void parseGRZXData(String result) {
        try {
            JSONObject json = new JSONObject(result);
            if (json.has("error")) {
                show(json.optString("error", ""));
                return;
            }
            JSONObject data = json.optJSONObject("data");
            // 用户头像
            if (data.optJSONObject("avatar") != null) {
                JSONObject avatar = data.optJSONObject("avatar");
                if (!avatar.optString("url", "").equals("")) {
                    String avatar_url = avatar.optString("url", "");
                    Glide.with(getContext()).load(avatar_url).transform(new GlideCircleTransform(getContext())).into(iv_header);
                }
            }
            // 用户名
            if (!data.optString("name", "").equals("")) {
                tv_name.setText(data.optString("name", ""));
                SharedPreferencesUtil.putString(getContext(), "name", data.optString("name", ""));
            }
            // 我的资产
            if (!TextUtils.isEmpty(data.optString("balance", ""))) {
                String balance = data.optString("balance", "");
                tv_total_zichan.setText(balance);
            }
            // 累计提现
            if (!TextUtils.isEmpty(data.optString("withdrawal_count", ""))) {
                String withdrawal_count = data.optString("withdrawal_count", "");
                tv_total_tixian.setText("累计提现￥" + withdrawal_count);
            }
        } catch (JSONException e) {
        }
    }

    private void getUnreadData() {
        String url = Config.GET_NOTIFICATIONS_UNREAD; //接口url
        String apiName = "未读通知数接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        dismissLoadProcess();
                        processNoReadNotificationsData(arg0);
                    }
                },
                new NetConnectionNew.FailCallback() {
                    @Override
                    public void onFail(Call arg0, Exception arg1, int arg2) {
                        dismissLoadProcess();
                        if (arg1.getMessage() != null) {
                            if (arg1.getMessage().contains("401")) {
                                showMsg401();
                            }
                        }
                    }
                });
    }

    private void processNoReadNotificationsData(String arg0) {
        com.alibaba.fastjson.JSONObject json = JSON.parseObject(arg0);
        if (json.containsKey("error")) {
            show(json.getString("error"));
            return;
        }
        int count = json.getJSONObject("data").getInteger("count");
        if (count > 0) {
            iv_xiaoxi.setImageResource(R.mipmap.xiaoxi_on_fff);
            newsKey = 1;
        } else {
            iv_xiaoxi.setImageResource(R.mipmap.xiaoxi_fff);
            newsKey = 0;
        }
    }
}
