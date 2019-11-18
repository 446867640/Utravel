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
import com.utravel.app.delegates.main.my.ShareGoodsDelegate;
import com.utravel.app.entity.TaoBaoGoodsBean1;
import com.utravel.app.entity.TaoBaoItemBean;
import com.utravel.app.ui.GlideImageLoader;
import com.utravel.app.ui.MyGridView;
import com.utravel.app.ui.MyScrollViewWithBack;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.GlidePartCornerTransform;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.Util;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Request;

public class TaoBaoDetailDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {

    private AppCompatImageView iv_back;
    private SmartRefreshLayout refresh;
    private MyScrollViewWithBack sv;
    private Banner banner1;
    private LinearLayoutCompat ll_quan_btn, ll_shouye, ll_shoucang, ll_share, ll_buy;
    private AppCompatTextView tv_price, tv_old_price, tv_fanli, tv_goodsname, tv_quan, tv_quan_time, tv_share, tv_buy;
    private MyGridView gv_goods;

    private TaoBaoGoodsBean1.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean itemBeanAll;
    private List<String> bannerDatas = new ArrayList<>();

    private static final String BUNDLE_TAG = "BUNDLE_TAG";
    private String share_url;
    private String item_url;

    // 淘客api相关
    public Map<String, String> params = new HashMap<String, String>();
    public String content = null;
    public DecimalFormat df = new DecimalFormat("#.00");
    // 淘宝客-推广者-物料搜索
    public List<TaoBaoGoodsBean1.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean> taoBaoGoodDatas = new ArrayList<>();
    public CommonAdapter<TaoBaoGoodsBean1.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean> taoBaoApapter;
    // 淘宝客-推广者-物料搜索
    String method = "taobao.tbk.dg.material.optional";
    String format = "json";
    String v = "2.0";
    String sign_method = "hmac";
    boolean is_tmall = false; //是否是天猫商品
    private int pageNo = 1;
    private boolean isNoMore = false;
    private String keyword = null;
    private String[] defaultKeyword = {
            "品牌男装","个护美妆","鞋袜箱包","配饰精品","家装家具","眼镜墨镜","帽子丝巾",
            "皮带腰带","品牌手表","潮流女装","上衣","裤装","儿童母婴","汽车汽修" };

    @Override
    public boolean setIsDark() { return true; }

    @Override
    public Object setLayout() { return R.layout.delegate_detail; }

    public static TaoBaoDetailDelegate newInstance1(TaoBaoGoodsBean1.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean bean) {
        TaoBaoDetailDelegate fragment = new TaoBaoDetailDelegate();
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
            itemBeanAll = (TaoBaoGoodsBean1.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean)bundle.getSerializable(BUNDLE_TAG);
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

    private void initKeywordData() {
        final int min=0;
        final int max=13;
        Random random = new Random();
        final int randomNum = random.nextInt(max)%(max-min+1) + min;
        keyword = defaultKeyword[randomNum];
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        if (keyword!=null){
            loadProcess();
            getTaoBaoGoodsData(keyword);
        }
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
        if (taoBaoApapter == null) {//商品适配器
            taoBaoApapter = new CommonAdapter<TaoBaoGoodsBean1.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean>(getContext(),taoBaoGoodDatas,R.layout.item_shouye_goods) {
                @Override
                public void convert(BaseViewHolder holder, TaoBaoGoodsBean1.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean t) {
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

                    holder.setText(R.id.tv_name, t.getTitle()); //商品名
                    holder.setText(R.id.tv_price, t.getZk_final_price()); //一口价
                    holder.setText(R.id.tv_oldprice, "¥" + t.getReserve_price()); //折后价
                    if (!TextUtils.isEmpty(t.getCoupon_amount())) {
                        holder.getView(R.id.tv_quan).setVisibility(View.VISIBLE);
                        holder.setText(R.id.tv_quan, t.getCoupon_amount() + "元券");  //优惠券信息-优惠券面额。如：满299元减20元
                    }else {
                        holder.getView(R.id.tv_quan).setVisibility(View.GONE);
                    }
                    double zk_final_price = 0.0;
                    double coupon_amount = 0.0;
                    double real_post_fee = 0.0;
                    double commission_rate = 0.0;
                    if (!TextUtils.isEmpty(t.getZk_final_price())) {
                        zk_final_price = Double.valueOf(t.getZk_final_price());
                    }
                    if (!TextUtils.isEmpty(t.getCoupon_amount())) {
                        coupon_amount = Double.valueOf(t.getCoupon_amount());
                    }
                    if (!TextUtils.isEmpty(t.getReal_post_fee())) {
                        real_post_fee = Double.valueOf(t.getReal_post_fee());
                    }
                    if (!TextUtils.isEmpty(t.getCommission_rate())) {
                        commission_rate = Double.valueOf(t.getCommission_rate());
                    }
                    double final_price = (zk_final_price - coupon_amount + real_post_fee) * commission_rate/10000*0.7;
                    holder.setText(R.id.tv_fan, getResources().getString(R.string.yugusheng) + final_price);

                    if (t.getPict_url()!=null) {
                        GlidePartCornerTransform transformation = new GlidePartCornerTransform(_mActivity, DensityUtil.dp2px(_mActivity, 5));
                        transformation.setExceptCorner(false, false, true, true);

                        if (!t.getPict_url().contains("http://") && !t.getPict_url().contains("https://")) {
                            Glide.with(getContext())
                                    .load("http:" + t.getPict_url())
                                    .asBitmap()
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .transform(transformation)
                                    .into(iv_goods);
                        }else {
                            Glide.with(getContext())
                                    .load(t.getPict_url())
                                    .asBitmap()
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .transform(transformation)
                                    .into(iv_goods);
                        }
                    }
                }
            };
            gv_goods.setAdapter(taoBaoApapter);
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
                getSupportDelegate().start(TaoBaoDetailDelegate.newInstance1(taoBaoGoodDatas.get(position)));
//                String url = null;
//                if (TextUtils.isEmpty(taoBaoGoodDatas.get(position).getCoupon_share_url())) {
//                    url = taoBaoGoodDatas.get(position).getItem_url();
//                }else {
//                    if (taoBaoGoodDatas.get(position).getCoupon_share_url().equals("http:") || taoBaoGoodDatas.get(position).getCoupon_share_url().equals("https:")) {
//                        url = taoBaoGoodDatas.get(position).getCoupon_share_url();
//                    }else {
//                        url = "https:" + taoBaoGoodDatas.get(position).getCoupon_share_url();
//                    }
//                }
//                AlibcUtil.openByUrl(_mActivity, url);
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
                    getTaoBaoGoodsData(keyword);
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
            if (!TextUtils.isEmpty(share_url)) {
                getSupportDelegate().start(ShareGoodsDelegate.newInstance(
                        itemBeanAll.getSmall_images().getString().get(0),
                        itemBeanAll.getTitle(),
                        itemBeanAll.getZk_final_price(),
                        itemBeanAll.getReserve_price(),
                        itemBeanAll.getCoupon_amount(),
                        share_url));
            }
        }else if (v==ll_buy) {//购买省
            if (!TextUtils.isEmpty(item_url)) {

            }
        }
    }

    private void setGoodsItemData1 (){
        //banner轮播图片
        if (itemBeanAll!=null && itemBeanAll.getSmall_images()!=null){
            if (itemBeanAll.getSmall_images()!=null) {
                bannerDatas.addAll(itemBeanAll.getSmall_images().getString());
                settBannerData();
            }
        }
        tv_price.setText(itemBeanAll.getZk_final_price()); //团购价
        tv_old_price.setText("原价￥" + itemBeanAll.getReserve_price()); //指导价
        tv_goodsname.setText(itemBeanAll.getTitle());
        //返利算法
        double zk_final_price = 0.0;
        double coupon_amount = 0.0;
        double real_post_fee = 0.0;
        double commission_rate = 0.0;
        if (!TextUtils.isEmpty(itemBeanAll.getZk_final_price())) {
            zk_final_price = Double.valueOf(itemBeanAll.getZk_final_price());
        }
        if (!TextUtils.isEmpty(itemBeanAll.getCoupon_amount())) {
            coupon_amount = Double.valueOf(itemBeanAll.getCoupon_amount());
        }
        if (!TextUtils.isEmpty(itemBeanAll.getReal_post_fee())) {
            real_post_fee = Double.valueOf(itemBeanAll.getReal_post_fee());
        }
        if (!TextUtils.isEmpty(itemBeanAll.getCommission_rate())) {
            commission_rate = Double.valueOf(itemBeanAll.getCommission_rate());
        }
        double final_price = (zk_final_price - coupon_amount + real_post_fee) * commission_rate/10000*0.7;

        tv_fanli.setText(getResources().getString(R.string.share_zhuan) + "¥" + final_price); //购买省钱数
        tv_share.setText( "¥" + final_price);
        if (TextUtils.isEmpty(itemBeanAll.getCoupon_share_url())){
            share_url = itemBeanAll.getItem_url();
            item_url = itemBeanAll.getItem_url();

            ll_quan_btn.setVisibility(View.GONE); //领券布局
            tv_buy.setVisibility(View.GONE); //购买省钱数
            tv_quan_time.setVisibility(View.GONE);
        }else{
            if (itemBeanAll.getCoupon_share_url().equals("http:") || itemBeanAll.getCoupon_share_url().equals("https:")) {
                share_url = itemBeanAll.getCoupon_share_url();
            }else {
                share_url = "https:" + itemBeanAll.getCoupon_share_url();
            }
            item_url = itemBeanAll.getItem_url();
            tv_quan_time.setVisibility(View.VISIBLE);
            tv_quan_time.setText(itemBeanAll.getCoupon_start_time().substring(5) + "至" + itemBeanAll.getCoupon_end_time().substring(5));
            ll_quan_btn.setVisibility(View.VISIBLE); //领券布局
            tv_quan.setText(itemBeanAll.getCoupon_amount());  //领券数
            tv_buy.setText( "¥" + itemBeanAll.getCoupon_amount()); //购买省钱数
            tv_buy.setVisibility(View.VISIBLE); //购买省钱数
        }
    }

    public void settBannerData() { //设置图片集合
        banner1.setImages(bannerDatas);
        banner1.start();
    }

    private TaoBaoItemBean parseGoodsItemData(String arg0) {
        return new Gson().fromJson(arg0, TaoBaoItemBean.class);
    }

    private void getTaoBaoGoodsData(String value) {
        String url = Config.BASE_TAOKE; //接口url
        try {
            PostFormBuilder build = OkHttpUtils
                    .post()
                    .url(url)
                    .addHeader("Host", new URL(url).getHost())
                    .addHeader("Accept", "text/xml,text/javascript")
                    .addHeader("User-Agent", Util.getUserAgent())
                    .addHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + "utf-8")
                    .addParams("sign", initSign());
            if (params != null && params.size() > 0) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    System.out.println(entry.getKey() + ":" + entry.getValue());
                    build = build.addParams(entry.getKey(), entry.getValue());
                }
            }
            build.build().execute(new StringCallback() {
                @Override
                public void onBefore(Request request, int id) {
                    super.onBefore(request, id);
                    LatteLogger.e("淘宝页面商品列表接口url===", request.url().toString());
                }

                @Override
                public void onResponse(String arg0, int arg1) {
                    dismissLoadProcess();
                    refresh.finishLoadMore();
                    LatteLogger.e("淘宝页面商品列表接口成功===", arg0);
                    processTaoBaoGoodsData(arg0);
                }
                @Override
                public void onError(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    refresh.finishLoadMore();
                }
            });
        } catch (IOException e) {}
    }

    public String initSign() throws IOException { // 淘宝客商品查询sign
        // 公共参数
        params.put("method", method);
        params.put("app_key", Util.getAppKey(getContext()));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        params.put("timestamp", df.format(new Date()));
        params.put("format", format);
        params.put("v", v);
        params.put("sign_method", sign_method);
        // 业务参数
        params.put("adzone_id", Util.getAdzoneId(getContext()));
        params.put("start_tk_rate", "1000");// 佣金下限
        params.put("q", keyword);
        params.put("is_tmall", is_tmall+"");
        params.put("page_no", pageNo + "");
        params.put("page_size", "10");
        // 返回签名参数
        return Util.signTopRequest(params, Util.getAppSecret(getContext()), Config.SIGN_METHOD_HMAC);
    }

    public void processTaoBaoGoodsData(String arg0) {
        if (parcessGoodsSearchJson(arg0).getTbk_dg_material_optional_response() != null) {
            if (parcessGoodsSearchJson(arg0).getTbk_dg_material_optional_response().getResult_list().getMap_data().size()==0) {
                show(getResources().getString(R.string.no_data));
                isNoMore = true;
                taoBaoApapter.refreshDatas(taoBaoGoodDatas);
                return;
            }
            taoBaoGoodDatas.addAll(parcessGoodsSearchJson(arg0)
                    .getTbk_dg_material_optional_response()
                    .getResult_list()
                    .getMap_data());
            taoBaoApapter.refreshDatas(taoBaoGoodDatas);
        }else {
            JSONObject json = JSON.parseObject(arg0);
            if (json.containsKey("error_response")) {
                JSONObject error_response = json.getJSONObject("error_response");
                if (error_response.containsKey("sub_msg")) {
                    show(error_response.getString("sub_msg"));
                }
            }
        }
    }

    public TaoBaoGoodsBean1 parcessGoodsSearchJson(String arg0) {
        return new Gson().fromJson(arg0, TaoBaoGoodsBean1.class);
    }
}
