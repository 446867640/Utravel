package com.utravel.app.delegates.main.shouye;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.gongwen.marqueen.SimpleMF;
import com.gongwen.marqueen.SimpleMarqueeView;
import com.gongwen.marqueen.util.OnItemClickListener;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.utravel.app.R;
import com.utravel.app.activities.base.NewsActivity;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.addr.MyAddressDelegate;
import com.utravel.app.delegates.detail.GoodsInfoDelegate;
import com.utravel.app.delegates.main.MainDelegate;
import com.utravel.app.delegates.main.OnBackDelegate;
import com.utravel.app.delegates.order.SureOrderDelegate;
import com.utravel.app.delegates.search.HomeSearchDelegate;
import com.utravel.app.entity.AddressBean;
import com.utravel.app.entity.GetSystemNotificationsBean;
import com.utravel.app.entity.GoodsDetailBean1;
import com.utravel.app.entity.HomeBannerBean1;
import com.utravel.app.entity.NewShouYeProListBean;
import com.utravel.app.photoview.NewsPictureDelegate;
import com.utravel.app.ui.MyGridView;
import com.utravel.app.ui.MyScrollView;
import com.utravel.app.ui.zxing.activity.CaptureActivity1;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.GlidePartCornerTransform;
import com.utravel.app.utils.GlideRoundTransform;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import com.utravel.app.utils.callback.CallbackManager;
import com.utravel.app.utils.callback.CallbackType;
import com.utravel.app.utils.callback.IGlobalCallback;
import com.utravel.app.web.WebDelegateImpl;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ezy.ui.view.BannerView;
import okhttp3.Call;

public class ShouyeDelegate extends OnBackDelegate implements View.OnClickListener {
    private RelativeLayout part_goodslist; //商品列表部分
    private RelativeLayout part_goodsdetail; //商品详情部分
    //商品列表
    private SmartRefreshLayout refresh;
    private RelativeLayout rl_search_part;
    private ImageView iv_serach;
    private ImageView iv_yaoqingzuan;
    private ImageView saoyisao;
    private RelativeLayout rl_bannerbg;//banner1背景部分
    private BannerView banner1;// 轮播图
    private LinearLayout ll_news;// 点击跳转系统消息
    private SimpleMarqueeView<String> marqueeview;//垂直走马灯
    private MyGridView pro_gridview;//商品列表Gridview

    private List<HomeBannerBean1.DataBean> bannerListData1;//banner1轮播图数据
    private List<GetSystemNotificationsBean.DataBean> nolistdata = new ArrayList<>(); //系统消息
    private List<NewShouYeProListBean.DataBean> productsDatas = new ArrayList<NewShouYeProListBean.DataBean>();//商城商品
    private CommonAdapter<NewShouYeProListBean.DataBean> proAdapter;//商城商品

    private int pageNo = 1;
    private boolean isNoMore = false;

    //商品详情
    private MyScrollView sv;
    private BannerView banner;
    private TextView tv_price;
    private TextView tv_name;
    private WebView tv_des;
    private LinearLayout ll_buy;
    private ImageView iv_totop;

    private GoodsDetailBean1.DataBean detaildata;
    private List<GoodsDetailBean1.DataBean.SpecificationsBean> specifications;
    private List<GoodsDetailBean1.DataBean.StockKeepingUnitsBean> stock_keeping_units;
    private List<GoodsDetailBean1.DataBean.ImagesBean> adimgDatas;
    private List<String> imageDatas = new ArrayList<String>();

    @Override
    public Object setLayout() {return R.layout.delegate_shouye;}

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        Util.setStatusBarMode(_mActivity,true);
        onPageVisible();
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

    private void initViews(View rootView) {
        part_goodslist = (RelativeLayout)rootView.findViewById(R.id.part_goodslist);
        part_goodsdetail = (RelativeLayout)rootView.findViewById(R.id.part_goodsdetail);
        //商品列表
        refresh = (SmartRefreshLayout) rootView.findViewById(R.id.refresh);
        rl_search_part = (RelativeLayout) rootView.findViewById(R.id.rl_search_part);
        iv_serach = (ImageView) rootView.findViewById(R.id.iv_serach);
        saoyisao = (ImageView) rootView.findViewById(R.id.saoyisao);
        iv_yaoqingzuan = (ImageView) rootView.findViewById(R.id.iv_yaoqingzuan);
        rl_bannerbg = (RelativeLayout) rootView.findViewById(R.id.rl_bannerbg);
        banner1 = (BannerView) rootView.findViewById(R.id.banner1);
        ll_news = (LinearLayout) rootView.findViewById(R.id.ll_news);
        marqueeview = (SimpleMarqueeView<String>) rootView.findViewById(R.id.marqueeview);
        pro_gridview = (MyGridView) rootView.findViewById(R.id.pro_gridview);
        //商品详情
        sv = (MyScrollView)rootView.findViewById(R.id.sv);
        banner = (BannerView) rootView.findViewById(R.id.banner);
        tv_price = (TextView) rootView.findViewById(R.id.tv_price);
        tv_name = (TextView) rootView.findViewById(R.id.tv_name);
        tv_des = (WebView) rootView.findViewById(R.id.tv_des);
        ll_buy = (LinearLayout) rootView.findViewById(R.id.ll_buy);
        iv_totop = (ImageView) rootView.findViewById(R.id.iv_totop);
    }

    private void initViewParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        params.width = screenWidth;
        params.height = Util.getStatusBarHeight(getContext()) + DensityUtil.dp2px(getContext(), 44);
        rl_search_part.setLayoutParams(params);

        RelativeLayout.LayoutParams banner1_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        banner1_params.topMargin = Util.getStatusBarHeight(getContext()) + DensityUtil.dp2px(getContext(), 50);
        banner1_params.bottomMargin = DensityUtil.dp2px(getContext(), 6);
        banner1_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, rl_bannerbg.getId());
        banner1.setLayoutParams(banner1_params);
    }

    private void initAdapter() {
        proAdapter = new CommonAdapter<NewShouYeProListBean.DataBean>(getContext(),productsDatas, R.layout.item_shouye_pro) {
            @Override
            public void convert(BaseViewHolder holder, NewShouYeProListBean.DataBean t) {
                //外层
                LinearLayout ll_gridview_item = holder.getView(R.id.ll_gridview_item);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((screenWidth-DensityUtil.dp2px(getContext(), 28))/2,(screenWidth-DensityUtil.dp2px(getContext(), 28))/2+DensityUtil.dp2px(getContext(), 80));
                ll_gridview_item.setLayoutParams(layoutParams);
                //内层
                final ImageView iv1 = holder.getView(R.id.iv1);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                params.width = (screenWidth-DensityUtil.dp2px(getContext(), 30))/2;
                params.height = (screenWidth-DensityUtil.dp2px(getContext(), 30))/2;
                iv1.setLayoutParams(params);
                holder.setText(R.id.tv1, t.getName());
                holder.setText(R.id.tv2, "￥" + t.getPrice());
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
        pro_gridview.setAdapter(proAdapter);
    }

    private void initListener() {
        //商品详情
        iv_totop.setOnClickListener(this);
        ll_buy.setOnClickListener(this);
        saoyisao.setOnClickListener(this);
        sv.setScrollViewListener(new MyScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(MyScrollView scrollView, int x, int y,int oldx, int oldy) {
                if (y>0) {
                    iv_totop.setVisibility(View.VISIBLE);
                }else {
                    iv_totop.setVisibility(View.GONE);
                }
            }
        });
        //商品列表
        iv_serach.setOnClickListener(this);
        ll_news.setOnClickListener(this);
        marqueeview.setOnItemClickListener(new OnItemClickListener<TextView, String>() {
            @Override
            public void onItemClickListener(TextView mView, String mData, int mPosition) {
                goToNextAty(NewsActivity.class, Config.NEWS_KEY, 0);
            }
        });
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                productsDatas.clear();
                pageNo = 1;
                isNoMore = false;
                getProductsData();
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
                pageNo += 1;
                getProductsData();
            }
        });
        pro_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainDelegate) getParentFragment()).getSupportDelegate().start(GoodsInfoDelegate.newInstances(productsDatas.get(position).getId() + ""));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v== iv_totop){//置顶
            sv.smoothScrollTo(0,0);
        }else if (v==ll_buy) {//立即购买
            if (!Util.isToken(getContext())) {
                show401();
                return;
            }
            loadProcess();
            getAddressData(stock_keeping_units.get(0).getId(),1);
        }else if (v==iv_serach) { //搜索
            ((MainDelegate) getParentFragment()).getSupportDelegate().start(new HomeSearchDelegate());
        }else if (v==ll_news) { //系统公告
            goToNextAty(NewsActivity.class);
        }else if (v==saoyisao) {
            goToNextAty(CaptureActivity1.class);
//            ((MainDelegate) getParentFragment()).getSupportDelegate().startForResult(new ScannerDelegate(), RequestCodes.SCAN);
        }
    }

    private void onPageVisible() {
        loadProcess();
        getProductsData();//商品列表接口
    }

    public class BannerViewFactory1 implements BannerView.ViewFactory<HomeBannerBean1.DataBean> {
        @Override
        public View create(final HomeBannerBean1.DataBean t, int position, final ViewGroup container) {
            final ImageView iv = new ImageView(container.getContext());
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (t.getUrl().contains("http://") || t.getUrl().contains("https://")) {
                        ((MainDelegate) getParentFragment()).getSupportDelegate().start( WebDelegateImpl.creat(t.getUrl()));
                    }else {
                        ((MainDelegate) getParentFragment()).getSupportDelegate().start( WebDelegateImpl.creat("http://" + t.getUrl()));
                    }
                }
            });
            Glide.with(container.getContext().getApplicationContext())
                    .load(t.getImage_url())
                    .transform(new GlideRoundTransform(getContext(), 5))
                    .into(new SimpleTarget<GlideDrawable>() { // 加上这段代码 可以解决
                        @Override
                        public void onResourceReady(GlideDrawable arg0, GlideAnimation<? super GlideDrawable> arg1) {
                            iv.setImageDrawable(arg0); //显示图片,比例347/902
                        }
                    });
            return iv;
        }
    }

    public class BannerViewFactory implements BannerView.ViewFactory<GoodsDetailBean1.DataBean.ImagesBean> {
        @Override
        public View create(final GoodsDetailBean1.DataBean.ImagesBean item, final int position, ViewGroup container) {
            ImageView iv = new ImageView(container.getContext());
            Glide.with(getContext()).load(item.getUrl()).into(iv);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (adimgDatas!=null && adimgDatas.size()>0) {
                        imageDatas.clear();
                        for (int i = 0; i < adimgDatas.size(); i++) {
                            imageDatas.add(adimgDatas.get(i).getUrl());
                        }
                        ((MainDelegate)getParentFragment()).getSupportDelegate().start(NewsPictureDelegate.newInstance(position, imageDatas));
                    }
                }
            });
            return iv;
        }
    }

    private void getAddressData(final int skuid2, final int num) {
        String url = Config.ADDRESS_INFO;
        String apiName = "地址管理接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("page", 1 + "");
        addParams.put("size", 2 + "");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processAddressData(arg0,skuid2,num);
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

    private void processAddressData(String result, int skuid2, int quantity) {
        try {
            JSONObject json = new JSONObject(result);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            if (parseAdrJson(result).getData() != null) {
                if (parseAdrJson(result).getData().size() == 0) {
                    ((MainDelegate) getParentFragment()).getSupportDelegate().start(new MyAddressDelegate());
                    SharedPreferencesUtil.putBoolean(getContext(), "NoAddrisFromGoods", true);
                    SharedPreferencesUtil.putString(getContext(), "NoAddrGoodsQuantity", quantity+"");
                    SharedPreferencesUtil.putString(getContext(), "NoAddrGoodsProductId", skuid2 + "");
                }else {
                    ((MainDelegate) getParentFragment()).getSupportDelegate().start(SureOrderDelegate.newInstances(
                            quantity+"",
                            skuid2+""
                    ));
                    SharedPreferencesUtil.putBoolean(getContext(), "NoAddrisFromGoods", false);
                    SharedPreferencesUtil.remove(getContext(), "NoAddrGoodsQuantity");
                    SharedPreferencesUtil.remove(getContext(), "NoAddrGoodsProductId");
                }
            }
        } catch (Exception e) {}
    }

    private AddressBean parseAdrJson(String result) {
        return new Gson().fromJson(result, AddressBean.class);
    }

    private void getProductsData() {
        String url = Config.GET_PRODUCTS; // 接口url
        String apiName = "首页商品列表接口"; // 接口名
        Map<String, String> addHeader = new HashMap<String, String>(); // 请求头
        Map<String, String> addParams = new HashMap<String, String>(); // 请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("page", "1");
        addParams.put("size", "2");
        addParams.put("time", System.currentTimeMillis()+"");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processProductsData(arg0);
                }
            }, new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    if (arg1.getMessage().contains("401")) {
                        showMsg401();
                    }else if (arg1.getMessage().contains("404")){
                        show(Config.ERROR404);
                    }
                }
            });
    }

    private void processProductsData(String arg0) {
        try {
            JSONObject json = new JSONObject(arg0);
            if (json.has("error")) {
                show(json.optString("error", ""));
                part_goodslist.setVisibility(View.GONE);
                part_goodsdetail.setVisibility(View.GONE);
                return;
            }
            if (parseProductsData(arg0).getData().size() == 0) {
                part_goodslist.setVisibility(View.GONE);
                part_goodsdetail.setVisibility(View.GONE);
                return;
            }
            if (parseProductsData(arg0).getData().size() >1) { //商品列表
                part_goodslist.setVisibility(View.VISIBLE);
                part_goodsdetail.setVisibility(View.GONE);

                pageNo = 1;
                isNoMore = false;
                productsDatas.clear();

                initViewParams();
                getBannerDataFromNet();//banner1轮播图
                getSystemNotificationsData();//系统公告，走马灯
                loadProcess();
                getGoodsSearchData();
            }else { //商品详情
                loadProcess();
                getGoodsDetailData(parseProductsData(arg0).getData().get(0).getId()+"");
            }
        } catch (Exception e) {}
    }

    private NewShouYeProListBean parseProductsData(String arg0) {
        return new Gson().fromJson(arg0, NewShouYeProListBean.class);
    }

    public void getGoodsDetailData(String id){
        String url = Config.GET_PRODUCTS_ID + id;
        String apiName = "商品详情接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(),url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processGoodsDetailData(arg0);

                    part_goodslist.setVisibility(View.GONE);
                    part_goodsdetail.setVisibility(View.VISIBLE);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("404")){
                            show(Config.ERROR404);
                        }
                    }
                }
            });
    }

    private void processGoodsDetailData(String arg0) {
        try {
            JSONObject json = new JSONObject(arg0);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            if (parseGoodsDetailJson1(arg0).getData()!=null) {
                detaildata = parseGoodsDetailJson1(arg0).getData();
                specifications = detaildata.getSpecifications();
                stock_keeping_units = detaildata.getStock_keeping_units();
                if (detaildata.getImages()!=null) {//轮播图列表数据
                    adimgDatas = detaildata.getImages();
                }
                LatteLogger.e("产品id", detaildata.getId()+"");
                //商品价格
                if (!TextUtils.isEmpty(stock_keeping_units.get(0).getPrice())) {
                    tv_price.setText("￥" + stock_keeping_units.get(0).getPrice());
                }
                //商品名称
                if (!TextUtils.isEmpty(detaildata.getName())) {
                    tv_name.setText(detaildata.getName());
                }
                Util.showFont(tv_des, parseGoodsDetailJson1(arg0).getData().getDescription());
                initBannerView();
            }
        } catch (Exception e) {
            show("数据异常");
        }
    }

    private GoodsDetailBean1 parseGoodsDetailJson1(String arg0) {
        return new Gson().fromJson(arg0, GoodsDetailBean1.class);
    }

    private void initBannerView() {
        banner.setViewFactory(new BannerViewFactory());
        banner.setDataList(adimgDatas);
        banner.start();
    }

    public void getGoodsSearchData(){
        String url = Config.GOODS_SEARCH;
        String apiName = "搜索商品接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("page", pageNo+"");
        addParams.put("size", "20");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        dismissLoadProcess();
                        refresh.finishLoadMore();
                        refresh.finishRefresh();
                        processGoodsSearchData(arg0);
                    }
                },
                new NetConnectionNew.FailCallback() {
                    @Override
                    public void onFail(Call arg0, Exception arg1, int arg2) {
                        dismissLoadProcess();
                        refresh.finishLoadMore();
                        refresh.finishRefresh();
                        if (arg1.getMessage().contains("401")) {
                            showMsg401();
                        }else if (arg1.getMessage().contains("404")){
                            show(Config.ERROR404);
                        }
                    }
                });
    }

    private void processGoodsSearchData(String arg0) {
        try {
            JSONObject json = new JSONObject(arg0);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            if (parseProductsData(arg0).getData().size()==0) {
                show(getResources().getString(R.string.no_data));
                isNoMore = true;
                proAdapter.refreshDatas(productsDatas);
                return;
            }
            productsDatas.addAll(parseProductsData(arg0).getData());
            proAdapter.refreshDatas(productsDatas);
        } catch (Exception e) {}
    }

    private void getBannerDataFromNet() {
        String url = Config.BANNER_DATA; // 接口url
        String apiName = "首页轮播图接口"; // 接口名
        Map<String, String> addHeader = new HashMap<String, String>(); // 请求头
        Map<String, String> addParams = new HashMap<String, String>(); // 请求体
        addHeader.put("Accept", "application/json");
        addParams.put("tag", "home");
        addParams.put("page", "1");
        addParams.put("size", "10");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    porcessBannerData(arg0);
                }
            }, new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                }
            });
    }

    private void porcessBannerData(String arg0) {
        try {
            JSONObject json = new JSONObject(arg0);
            if (json.has("error")) {
                show(json.optString("error", ""));
                return;
            }
            if (parcessBannerJson(arg0).getData() != null) {
                bannerListData1 = parcessBannerJson(arg0).getData();
                banner1.setViewFactory(new BannerViewFactory1());
                banner1.setDataList(bannerListData1);
                banner1.start();
            }
        } catch (Exception e) {}
    }

    private HomeBannerBean1 parcessBannerJson(String arg0) {
        return new Gson().fromJson(arg0, HomeBannerBean1.class);
    }

    private void getSystemNotificationsData() {
        String url = Config.GET_SYSTEM_NOTIFICATIONS_LIST; // 接口url
        String apiName = "系统公告列表接口"; // 接口名
        Map<String, String> addHeader = new HashMap<String, String>(); // 请求头
        Map<String, String> addParams = new HashMap<String, String>(); // 请求体
        addHeader.put("Accept", "application/json");
        addParams.put("page", "1");
        addParams.put("size", "10");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    procesSystemNotificationsData(arg0);
                }
            }, new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {}
            });
    }

    private void procesSystemNotificationsData(String json) {
        com.alibaba.fastjson.JSONObject jsonStr = JSON.parseObject(json);
        if (jsonStr!=null) {
            if ( jsonStr.containsKey("error") ) {
                show(jsonStr.getString("error"));
                return;
            }
            if (parseSystemNotificationsData(json).getData().size() > 0) {
                nolistdata = parseSystemNotificationsData(json).getData();
                if (nolistdata!=null) {
                    final List<String> datas = new ArrayList<>();
                    for(int i = 0; i < nolistdata.size(); i++){
                        datas.add(nolistdata.get(i).getTitle());
                    }
                    SimpleMF<String> marqueeFactory = new SimpleMF(getContext());
                    marqueeFactory.setData(datas);
                    marqueeview.setMarqueeFactory(marqueeFactory);
                    marqueeview.startFlipping();
                }
            }
        }
    }

    private GetSystemNotificationsBean parseSystemNotificationsData(String json) {
        return new Gson().fromJson(json, GetSystemNotificationsBean.class);
    }
}
