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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.delegates.login.LoginChoiceDelegate;
import com.utravel.app.delegates.main.MainDelegate;
import com.utravel.app.delegates.main.my.ShareGoodsDelegate;
import com.utravel.app.entity.JDBankuanBean;
import com.utravel.app.entity.TaoBaoGoodsBean;
import com.utravel.app.entity.TaoBaoItemBean;
import com.utravel.app.ui.GlideImageLoader;
import com.utravel.app.ui.MyGridView;
import com.utravel.app.ui.MyScrollViewWithBack;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.GlideRoundTransform;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import okhttp3.Call;

public class JingDongDetailDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {

    private AppCompatImageView iv_back;
    private SmartRefreshLayout refresh;
    private MyScrollViewWithBack sv;
    private Banner banner1;
    private LinearLayoutCompat ll_quan_btn, ll_shouye, ll_shoucang, ll_share, ll_buy;
    private AppCompatTextView tv_price, tv_old_price, tv_fanli, tv_goodsname, tv_quan, tv_quan_time, tv_share, tv_buy;
    private MyGridView gv_goods;

    private JDBankuanBean itemBeanAll;
    private List<String> bannerDatas = new ArrayList<>();
    private List<JDBankuanBean> JDGoodsDatas = new ArrayList<>();
    private CommonAdapter<JDBankuanBean> JDGoodsAdapter;

    private static final String BUNDLE_TAG = "BUNDLE_TAG";
    private String share_url;
    private String skuId;

    private String[] defaultKeyword = {
            "品牌男装","个护美妆","鞋袜箱包","配饰精品","家装家具","眼镜墨镜","帽子丝巾",
            "皮带腰带","品牌手表","潮流女装","上衣","裤装","儿童母婴","汽车汽修" };
    private int pageNo = 1;
    private boolean isNoMore = false;
    private String keyword = null;

    @Override
    public boolean setIsDark() { return true; }

    @Override
    public Object setLayout() { return R.layout.delegate_detail; }

    public static JingDongDetailDelegate newInstance1(JDBankuanBean bean) {
        JingDongDetailDelegate fragment = new JingDongDetailDelegate();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_TAG, (Serializable) bean);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            itemBeanAll = (JDBankuanBean)bundle.getSerializable(BUNDLE_TAG);
        }
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initKeywordData();
        initViews(rootView);
        initViewsParams();
        initBanner();
        initAdapter();
        initListener();
        setGoodsItemData1();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        if (keyword!=null){
            JDGoodsDatas.clear();
            pageNo = 1;
            isNoMore = false;
            loadProcess();
            getJDGoodsData(keyword);
        }
    }

    private void initKeywordData() {
        final int min=0;
        final int max=13;
        Random random = new Random();
        final int randomNum = random.nextInt(max)%(max-min+1) + min;
        keyword = defaultKeyword[randomNum];
    }

    private void initViews(View rootView) {
        iv_back = (AppCompatImageView) this.rootView.findViewById(R.id.iv_back);
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

    private void initViewsParams() {
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
        if (JDGoodsAdapter == null) {//商品适配器
            JDGoodsAdapter = new CommonAdapter<JDBankuanBean>(getContext(),JDGoodsDatas,R.layout.item_shouye_goods) {
                @Override
                public void convert(BaseViewHolder holder, JDBankuanBean t) {
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

                    holder.setText(R.id.tv_name, t.getWareName()); //商品名
                    holder.setText(R.id.tv_price, t.getPrice()); //价格
                    holder.setText(R.id.tv_fan, getResources().getString(R.string.yugusheng) + t.getEstimated_commission()); //返利
                    if (t.getImageUrl() != null && !t.getImageUrl().equals("")) { //图片
                        Glide.with(getContext())
                                .load("http://img10.360buyimg.com/n1/"+ t.getImageUrl())
                                .transform(new GlideRoundTransform(getContext(), 5))
                                .into((ImageView) holder.getView(R.id.iv_goods));
                    }
                    AppCompatTextView tv_quan = holder.getView(R.id.tv_quan);
                    tv_quan.setVisibility(View.GONE);
                }
            };
            gv_goods.setAdapter(JDGoodsAdapter);
        }
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        ll_quan_btn.setOnClickListener(this);
        ll_shouye.setOnClickListener(this);
        ll_shoucang.setOnClickListener(this);
        ll_share.setOnClickListener(this);
        ll_buy.setOnClickListener(this);
        gv_goods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //进入商品详情
                getSupportDelegate().start(JingDongDetailDelegate.newInstance1(JDGoodsDatas.get(position)));

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
                if (keyword!=null){
                    pageNo += 1;
                    getJDGoodsData(keyword);
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
            if (!TextUtils.isEmpty(skuId)) {
                loadProcess();
                getJDPromotionUrlData(Util.getJDUrlwithSkuId(skuId));
            }
        }else if (v==ll_buy) {//购买省

        }
    }

    private void setGoodsItemData1 (){
        //banner轮播图片
        if (itemBeanAll!=null){
            bannerDatas.add(Config.JD_PIC_URL + itemBeanAll.getImageUrl());
            settBannerData();
        }
        tv_goodsname.setText(itemBeanAll.getWareName());
        tv_price.setText(itemBeanAll.getPrice()); //团购价
        tv_old_price.setVisibility(View.GONE);
        tv_fanli.setText(getResources().getString(R.string.share_zhuan) + "¥" + itemBeanAll.getEstimated_commission()); //购买省钱数
        tv_share.setText( "¥" + itemBeanAll.getEstimated_commission());
        skuId = itemBeanAll.getSkuId(); //商品id
        ll_quan_btn.setVisibility(View.GONE); //领券布局
        tv_buy.setVisibility(View.GONE); //购买省钱数
    }

    public void settBannerData() { //设置图片集合
        banner1.setImages(bannerDatas);
        banner1.start();
    }

    private TaoBaoItemBean parseGoodsItemData(String arg0) {
        return new Gson().fromJson(arg0, TaoBaoItemBean.class);
    }

    private void getJDPromotionUrlData(String original_url) {
        String url = Config.JD_PROMOTION_URL; //接口url
        String apiName = "京东链接转换接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("original_url", original_url);
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        dismissLoadProcess();
                        processJDPromotionUrlData(arg0);
                    }
                },
                new NetConnectionNew.FailCallback() {
                    @Override
                    public void onFail(Call arg0, Exception arg1, int arg2) {
                        dismissLoadProcess();
                        if (arg1.getMessage()!=null) {
                            if (arg1.getMessage().contains("401")) {
                                show(Config.ERROR401);
                                ((MainDelegate)getParentFragment().getParentFragment()).getSupportDelegate().start(new LoginChoiceDelegate());
                                SharedPreferencesUtil.clearlogin(getContext());
                            }
                        }
                    }
                });
    }

    private void processJDPromotionUrlData (String arg0){
        JSONObject json = JSON.parseObject(arg0);
        if (json != null) {
            if (json.containsKey("error")) {
                show(json.getString("error"));
                return;
            }
            String click_url = json.getJSONObject("data").getString("click_url");
            getSupportDelegate().start(ShareGoodsDelegate.newInstance(
                    Config.JD_PIC_URL + itemBeanAll.getImageUrl(),
                    itemBeanAll.getWareName(),
                    itemBeanAll.getPrice(),
                    null,
                    "0",
                    click_url));
        }
    }

    private void getJDGoodsData(String value) {
        String url = Config.PRODUCT_JD_SEARCH; //接口url
        String apiName = "京东商品搜索接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("keyword", value);
        addParams.put("page", pageNo + "");
        addParams.put("page_size", "10");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        dismissLoadProcess();
                        refresh.finishLoadMore();
                        processJDGoodsData(arg0);
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
                                ((MainDelegate)getParentFragment()).getSupportDelegate().start(new LoginChoiceDelegate());
                                SharedPreferencesUtil.clearlogin(getContext());
                            }
                        }
                    }
                });
    }

    private void processJDGoodsData (String arg0){
        JSONArray jsonArray = JSON.parseArray(arg0);
        if (jsonArray.size() == 0) {
            show("没有更多数据了");
            refresh.finishLoadMore();
            isNoMore = true;
            return;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JDBankuanBean bean = new JDBankuanBean();
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            bean.setSkuId(jsonObject.getString("skuId"));// 商品skuid
            bean.setImageUrl(jsonObject.getString("imageUrl"));// 商品图片
            bean.setWareName(jsonObject.getString("wareName"));// 商品名称
            bean.setPrice(jsonObject.getString("price"));// 商品价格
            bean.setEstimated_commission(jsonObject.getString("estimated_commission"));// 省..
            JDGoodsDatas.add(bean);
        }
        JDGoodsAdapter.refreshDatas(JDGoodsDatas);
    }

    public TaoBaoGoodsBean parcessGoodsSearchJson(String arg0) {
        return new Gson().fromJson(arg0, TaoBaoGoodsBean.class);
    }
}
