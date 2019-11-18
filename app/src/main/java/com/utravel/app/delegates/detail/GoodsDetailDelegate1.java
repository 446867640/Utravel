package com.utravel.app.delegates.detail;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.delegates.login.LoginChoiceDelegate;
import com.utravel.app.entity.GoodsPromotionUrlBean;
import com.utravel.app.entity.PinduoduoGoodsBean;
import com.utravel.app.entity.PinduoduoGoodsDetailBean;
import com.utravel.app.entity.PinduoduoGoodsTopBean;
import com.utravel.app.photoview.NewsPictureDelegate;
import com.utravel.app.ui.GlideImageLoader;
import com.utravel.app.ui.MyGridView;
import com.utravel.app.ui.MyScrollViewWithBack;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.GlidePartCornerTransform;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class GoodsDetailDelegate1 extends LatterSwipeBackDelegate implements View.OnClickListener {
    private SmartRefreshLayout refresh;
    private MyScrollViewWithBack sv;
    private Banner banner1;
    private AppCompatImageView iv_back;
    private LinearLayoutCompat ll_quan_btn, ll_shouye, ll_shoucang, ll_share, ll_buy;
    private AppCompatTextView tv_price, tv_old_price, tv_fanli, tv_goodsname, tv_quan, tv_quan_time, tv_share, tv_buy;
    private MyGridView gv_goods;

    private PinduoduoGoodsDetailBean.DataBean goodsDetailBean;
    private GoodsPromotionUrlBean.DataBean goodsPromotionUrlBean;
    private List<String> bannerDatas = new ArrayList<>();
    private List<PinduoduoGoodsBean.DataBean.GoodsListBean> gv_goodsDatas = new ArrayList<>();
    private CommonAdapter<PinduoduoGoodsBean.DataBean.GoodsListBean> gv_goodsAdapter;
    private List<PinduoduoGoodsTopBean.DataBean.ListBean> gv_topgoodsDatas = new ArrayList<>();
    private CommonAdapter<PinduoduoGoodsTopBean.DataBean.ListBean> gv_topgoodsAdapter;

    private static final String GOODS_ID = "GOODS_ID";
    private static final String KEY = "KEY";
    private long mGoodsId = -1;
    private String keyword = null;
    private int pageNo = 1;
    private boolean isNoMore = false;

    @Override
    public boolean setIsDark() { return true; }
    @Override
    public Object setLayout() { return R.layout.delegate_detail; }

    public static GoodsDetailDelegate1 newInstance(long id, String key) {
        GoodsDetailDelegate1 fragment = new GoodsDetailDelegate1();
        Bundle bundle = new Bundle();
        bundle.putLong(GOODS_ID, id);
        bundle.putString(KEY, key);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mGoodsId = bundle.getLong(GOODS_ID);
            keyword = bundle.getString(KEY);
        }
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initBanner();
        initViewParams();
        initAdapter();
        initListener();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        loadProcess();
        getGoodsCommissionInfoData();
        if (keyword!=null){
            getGoodsData(keyword);
        }else{
            getTopGoodsData();
        }
    }

    private void initViews(View rootView) {
        refresh = (SmartRefreshLayout) rootView.findViewById(R.id.refresh);
        sv = (MyScrollViewWithBack) this.rootView.findViewById(R.id.sv);
        banner1 = (Banner) rootView.findViewById(R.id.banner1);
        iv_back = (AppCompatImageView) this.rootView.findViewById(R.id.iv_back);
        tv_price = (AppCompatTextView) this.rootView.findViewById(R.id.tv_price);
        tv_old_price = (AppCompatTextView) this.rootView.findViewById(R.id.tv_old_price);
        tv_fanli = (AppCompatTextView) this.rootView.findViewById(R.id.tv_fanli);
        tv_goodsname = (AppCompatTextView) this.rootView.findViewById(R.id.tv_goodsname);
        ll_quan_btn = (LinearLayoutCompat) this.rootView.findViewById(R.id.ll_quan_btn);
        ll_shouye = (LinearLayoutCompat) this.rootView.findViewById(R.id.ll_shouye);
        ll_shoucang = (LinearLayoutCompat) this.rootView.findViewById(R.id.ll_shoucang);
        ll_share = (LinearLayoutCompat) this.rootView.findViewById(R.id.ll_share);
        tv_share = (AppCompatTextView) this.rootView.findViewById(R.id.tv_share);
        ll_buy = (LinearLayoutCompat) this.rootView.findViewById(R.id.ll_buy);
        tv_buy = (AppCompatTextView) this.rootView.findViewById(R.id.tv_buy);
        tv_quan = (AppCompatTextView) this.rootView.findViewById(R.id.tv_quan);
        tv_quan_time = (AppCompatTextView) this.rootView.findViewById(R.id.tv_quan_time);
        gv_goods = (MyGridView) this.rootView.findViewById(R.id.gv_goods);
    }

    private void initViewParams() {
        tv_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        gv_goods.setFocusable(false);
        sv.fullScroll(ScrollView.FOCUS_UP);
        RelativeLayout.LayoutParams iv_back_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        iv_back_params.topMargin = Util.getStatusBarHeight(_mActivity);
        iv_back_params.leftMargin = DensityUtil.dp2px(_mActivity,5);
        iv_back_params.height = DensityUtil.dp2px(_mActivity,44);
        iv_back_params.width = DensityUtil.dp2px(_mActivity,44);
        iv_back.setLayoutParams(iv_back_params);
    }

    public void initBanner() {
        banner1.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);//设置banner样式
//        banner1.setBannerTitles(titles);//设置标题集合（当banner样式有显示title时）
        banner1.setImageLoader(new GlideImageLoader());//设置图片加载器
//        banner1.setImages(bannerDatas);//设置图片集合,size必须大于0，常在获取到数据之后，才设置
        banner1.setBannerAnimation(Transformer.Default);//设置banner动画效果
        banner1.isAutoPlay(true);//设置自动轮播，默认为true
        banner1.setDelayTime(3000);//设置轮播时间
        banner1.setIndicatorGravity(BannerConfig.CENTER);//设置指示器位置（当banner模式中有指示器时）
    }

    private void initAdapter() {
        if (gv_goodsAdapter == null) {//商品适配器
            gv_goodsAdapter = new CommonAdapter<PinduoduoGoodsBean.DataBean.GoodsListBean>(getContext(), gv_goodsDatas, R.layout.item_shouye_goods) {
                @Override
                public void convert(BaseViewHolder holder, PinduoduoGoodsBean.DataBean.GoodsListBean t) {
                    //外层
                    LinearLayoutCompat ll_gridview_item = holder.getView(R.id.ll_gridview_item);
                    LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(
                            (screenWidth - DensityUtil.dp2px(getContext(), 24)) / 2,
                            (screenWidth - DensityUtil.dp2px(getContext(), 24)) / 2 + DensityUtil.dp2px(getContext(), 113));
                    ll_gridview_item.setLayoutParams(layoutParams);
                    //内层
                    final AppCompatImageView iv_goods = holder.getView(R.id.iv_goods);
                    LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    params.width = (screenWidth - DensityUtil.dp2px(getContext(), 24)) / 2;
                    params.height = (screenWidth - DensityUtil.dp2px(getContext(), 24)) / 2;
                    iv_goods.setLayoutParams(params);
                    iv_goods.setScaleType(AppCompatImageView.ScaleType.FIT_XY);
                    AppCompatTextView tv_oldprice = holder.getView(R.id.tv_oldprice);
                    tv_oldprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

                    holder.setText(R.id.tv_name, t.getGoods_name());
                    holder.setText(R.id.tv_price, (double)t.getMin_group_price()/100+"");
                    holder.setText(R.id.tv_oldprice, (double)t.getMin_normal_price()/100+"");
                    holder.setText(R.id.tv_quan, (double)t.getCoupon_discount()/100 + "元券");
                    holder.setText(R.id.tv_fan, getResources().getString(R.string.fanli_text) + t.getEstimated_commission());

                    GlidePartCornerTransform transformation = new GlidePartCornerTransform(_mActivity, DensityUtil.dp2px(_mActivity, 5));
                    transformation.setExceptCorner(false, false, true, true);
                    Glide.with(getContext())
                            .load(t.getGoods_thumbnail_url())
                            .asBitmap()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .transform(transformation)
                            .into(iv_goods);
                }
            };
        }
        if (gv_topgoodsAdapter == null) {//商品适配器
            gv_topgoodsAdapter = new CommonAdapter<PinduoduoGoodsTopBean.DataBean.ListBean>(getContext(), gv_topgoodsDatas, R.layout.item_shouye_goods) {
                @Override
                public void convert(BaseViewHolder holder, PinduoduoGoodsTopBean.DataBean.ListBean t) {
                    //外层
                    LinearLayoutCompat ll_gridview_item = holder.getView(R.id.ll_gridview_item);
                    LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(
                            (screenWidth - DensityUtil.dp2px(getContext(), 24)) / 2,
                            (screenWidth - DensityUtil.dp2px(getContext(), 24)) / 2 + DensityUtil.dp2px(getContext(), 113));
                    ll_gridview_item.setLayoutParams(layoutParams);
                    //内层
                    final AppCompatImageView iv_goods = holder.getView(R.id.iv_goods);
                    LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    params.width = (screenWidth - DensityUtil.dp2px(getContext(), 24)) / 2;
                    params.height = (screenWidth - DensityUtil.dp2px(getContext(), 24)) / 2;
                    iv_goods.setLayoutParams(params);
                    iv_goods.setScaleType(AppCompatImageView.ScaleType.FIT_XY);
                    AppCompatTextView tv_oldprice = holder.getView(R.id.tv_oldprice);
                    tv_oldprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

                    holder.setText(R.id.tv_name, t.getGoods_name());
                    holder.setText(R.id.tv_price, (double)t.getMin_group_price()/100+"");
                    holder.setText(R.id.tv_oldprice,  "¥" + (double)t.getMin_normal_price()/100+"");
                    holder.setText(R.id.tv_quan, (double)t.getCoupon_discount()/100 + "元券");
                    holder.setText(R.id.tv_fan, getResources().getString(R.string.fanli_text) + t.getEstimated_commission());

                    GlidePartCornerTransform transformation = new GlidePartCornerTransform(_mActivity, DensityUtil.dp2px(_mActivity, 5));
                    transformation.setExceptCorner(false, false, true, true);
                    Glide.with(getContext())
                            .load(t.getGoods_thumbnail_url())
                            .asBitmap()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .transform(transformation)
                            .into(iv_goods);
                }
            };
        }
        if (keyword!=null){
            gv_goods.setAdapter(gv_goodsAdapter);
        }else{
            gv_goods.setAdapter(gv_topgoodsAdapter);
        }
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        ll_quan_btn.setOnClickListener(this);
        ll_shouye.setOnClickListener(this);
        ll_shoucang.setOnClickListener(this);
        ll_share.setOnClickListener(this);
        ll_buy.setOnClickListener(this);
        banner1.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                getSupportDelegate().start(NewsPictureDelegate.newInstance(position, bannerDatas));
            }
        });
        gv_goods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (keyword!=null){
                    getSupportDelegate().start(GoodsDetailDelegate1.newInstance(gv_goodsDatas.get(position).getGoods_id(), keyword));
                }else{
                    getSupportDelegate().start(GoodsDetailDelegate1.newInstance(gv_topgoodsDatas.get(position).getGoods_id(), null));
                }
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
                if (keyword!=null){
                    getGoodsData(keyword);
                }else{
                    getTopGoodsData();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == iv_back) { //返回
            pop();
        }else if (v==ll_quan_btn) {//立即领券
            ll_buy.performClick();
        }else if (v==ll_shouye) {//首页
            pop();
        }else if (v==ll_shoucang) {//收藏
            show("敬请期待");
        }else if (v==ll_share) {//分享赚
            show("敬请期待");
        }else if (v==ll_buy) {//购买省
            loadProcess();
            getGoodsPromotionUrl();
        }
    }

    public void settBannerData() {
        //设置图片集合
        banner1.setImages(bannerDatas);
        banner1.start();
    }

    private void getGoodsCommissionInfoData() {
        String url = Config.PRODUCT_PDD_COMMISSION_INFO; //接口URL
        String apiName = "拼多多商品预付返利接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addParams.put("goods_id", mGoodsId + "");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    processGoodsCommissionInfoData(arg0);
                    getGoodsDetailData();
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    tv_fanli.setVisibility(View.GONE);
                    getGoodsDetailData();
                }
            });
    }

    private void processGoodsCommissionInfoData(String arg0) {
       JSONObject json = JSON.parseObject(arg0);
       if (json!=null){
           if (json.containsKey("error")){
               show(json.getString("error"));
               tv_fanli.setVisibility(View.GONE);
               return;
           }
           JSONObject data = json.getJSONObject("data");
           final double estimated_commission = data.getDouble("estimated_commission");
           tv_fanli.setText(getResources().getString(R.string.fanli_text) + estimated_commission);
           tv_share.setText("¥" + estimated_commission);
       }
    }

    private void getGoodsDetailData() {
        String url = Config.PRODUCT_PDD_DETAIL; //接口URL
        String apiName = "拼多多商品详情接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addParams.put("goods_id", mGoodsId + "");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processGoodsDetailData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                }
            });
    }

    private void processGoodsDetailData(String arg0) {
        goodsDetailBean = parseGoodsDetailData(arg0).getData();
        //banner轮播图片
        if (goodsDetailBean!=null){
            bannerDatas.addAll(goodsDetailBean.getGoods_gallery_urls());
            settBannerData();
        }
        tv_price.setText((double)goodsDetailBean.getMin_group_price()/100+""); //团购价
        tv_old_price.setText("原价￥" + (double)goodsDetailBean.getMin_normal_price()/100+""); //指导价
        tv_goodsname.setText(goodsDetailBean.getGoods_name());
        if (goodsDetailBean.getCoupon_discount()==0){
            ll_quan_btn.setVisibility(View.GONE);
            tv_buy.setVisibility(View.GONE);
        }else{
            tv_quan.setText((double)goodsDetailBean.getCoupon_discount()/100+"");
            tv_buy.setText("¥" + (double)goodsDetailBean.getCoupon_discount()/100);
            ll_quan_btn.setVisibility(View.VISIBLE);
            tv_buy.setVisibility(View.VISIBLE);
        }
    }

    private PinduoduoGoodsDetailBean parseGoodsDetailData(String arg0) {
        return new Gson().fromJson(arg0, PinduoduoGoodsDetailBean.class);
    }

    private void getGoodsData(String value) {
        String url = Config.PRODUCT_PDD_SEARCH; //接口URL
        String apiName = "拼多多商品搜索接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        if (!TextUtils.isEmpty(value)){
            addParams.put("keyword", value);
        }
        addParams.put("page", pageNo + "");
        addParams.put("size", 10 + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        refresh.finishLoadMore();
                        processGoodsData(arg0);
                    }
                },
                new NetConnectionNew.FailCallback() {
                    @Override
                    public void onFail(Call arg0, Exception arg1, int arg2) {
                        refresh.finishLoadMore();
                    }
                });
    }

    private void processGoodsData(String result) {
        if (parseGoodsData(result).getData()==null || parseGoodsData(result).getData().getGoods_list()==null){
            show("数据异常");
            return;
        }
        if (parseGoodsData(result).getData().getGoods_list().size()==0) {
            show("没有更多数据了");
            isNoMore = true;
            gv_goodsAdapter.refreshDatas(gv_goodsDatas);
            return;
        }
        gv_goodsDatas.addAll(parseGoodsData(result).getData().getGoods_list());
        gv_goodsAdapter.refreshDatas(gv_goodsDatas);
    }

    private PinduoduoGoodsBean parseGoodsData(String arg0) {
        return new Gson().fromJson(arg0, PinduoduoGoodsBean.class);
    }

    private void getGoodsPromotionUrl() {
        String url = Config.PRODUCT_PDD_COMMISSION_URL; //接口URL
        String apiName = "拼多多商品id转推广URL接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("goods_id", mGoodsId + "");
        addParams.put("generate_we_app", true + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processGoodsPromotionUrl(arg0);
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

    private void processGoodsPromotionUrl(String json) {
        goodsPromotionUrlBean = parseGoodsPromotionUrl(json).getData();
//        String mobile_url = goodsPromotionUrlBean.getMobile_url();
//        getSupportDelegate().start(WebRootDelegate.newInstance(mobile_url));
        String page_path = goodsPromotionUrlBean.getWe_app_info().getPage_path();
        Util.goToMinWeChat(Config.PINDUODUO_USER_NAME, page_path);
    }

    private GoodsPromotionUrlBean parseGoodsPromotionUrl(String json) {
        return new Gson().fromJson(json, GoodsPromotionUrlBean.class);
    }

    private void getTopGoodsData() {
        String url = Config.PRODUCT_PDD_TOP; //接口URL
        String apiName = "拼多多热销榜商品接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addParams.put("sort_type", 1 + "");
        addParams.put("page", pageNo + "");
        addParams.put("size", 10 + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    refresh.finishLoadMore();
                    processTopGoodsData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    refresh.finishLoadMore();
                }
            });
    }

    private void processTopGoodsData(String result) {
        if (parseTopGoodsData(result).getData()==null || parseTopGoodsData(result).getData().getList()==null){
            show("数据异常");
            return;
        }
        if (parseTopGoodsData(result).getData().getList().size()==0) {
            show("没有更多数据了");
            isNoMore = true;
            gv_topgoodsAdapter.refreshDatas(gv_topgoodsDatas);
            return;
        }
        gv_topgoodsDatas.addAll(parseTopGoodsData(result).getData().getList());
        gv_topgoodsAdapter.refreshDatas(gv_topgoodsDatas);
    }

    private PinduoduoGoodsTopBean parseTopGoodsData(String arg0) {
        return new Gson().fromJson(arg0, PinduoduoGoodsTopBean.class);
    }
}
