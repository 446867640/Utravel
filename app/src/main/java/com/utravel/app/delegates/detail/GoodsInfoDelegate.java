package com.utravel.app.delegates.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.delegates.addr.MyAddressDelegate;
import com.utravel.app.delegates.order.SureOrderDelegate;
import com.utravel.app.entity.AddressBean;
import com.utravel.app.entity.GoodsDetailBean1;
import com.utravel.app.photoview.NewsPictureDelegate;
import com.utravel.app.ui.MyScrollView;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ezy.ui.view.BannerView;
import okhttp3.Call;

public class GoodsInfoDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private MyScrollView sv;
    private ImageView iv_back;//返回
    private LinearLayout ll_buy;//立即购买
    private ImageView iv_totop;//顶置
    private BannerView banner;//轮播图
    private TextView tv_price;//价格
    private TextView tv_name;//商品名称
    private WebView tv_des;//详情webview

    private GoodsDetailBean1.DataBean detaildata;
    private List<GoodsDetailBean1.DataBean.SpecificationsBean> specifications;
    private List<GoodsDetailBean1.DataBean.StockKeepingUnitsBean> stock_keeping_units;
    private List<GoodsDetailBean1.DataBean.ImagesBean> adimgDatas;
    private List<String> imageDatas = new ArrayList<String>();

    private static final String BUNDLE_PRODUCT_ID = "BUNDLE_PRODUCT_ID";
    private static final String BUNDLE_PRODUCT_CODE = "BUNDLE_PRODUCT_CODE";

    private String product_id;//商品详情id
    private String product_code;//商品详情code
    private String code;//商品详情code

    public static GoodsInfoDelegate newInstance(String code) {
        GoodsInfoDelegate fragment = new GoodsInfoDelegate();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_PRODUCT_CODE, code);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static GoodsInfoDelegate newInstances(String id) {
        GoodsInfoDelegate fragment = new GoodsInfoDelegate();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_PRODUCT_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            product_id = bundle.getString(BUNDLE_PRODUCT_ID);
            product_code = bundle.getString(BUNDLE_PRODUCT_CODE);
        }
    }

    @Override
    public boolean setIsDark() { return true; }

    @Override
    public Object setLayout() { return R.layout.delegate_goods_info; }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initListener();
        initViewParams();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        if (product_id==null && product_code==null) {
            show("获取商品信息失败");
        }else {
            loadProcess();
            if (product_id!=null) {
                getGoodsDetailData(product_id);
            }else if (product_code!=null) {
                if (product_code.contains("%3D")) {
                    code = product_code.replace("%3D", "=");
                }else {
                    code = product_code;
                }
                getGoodsDetailCodeData(code);
            }
        }
    }

    private void initViews(View rootView) {
        iv_back = (ImageView) rootView.findViewById(R.id.iv_back);
        sv = (MyScrollView) rootView.findViewById(R.id.sv);
        banner = (BannerView) rootView.findViewById(R.id.banner);
        tv_price = (TextView) rootView.findViewById(R.id.tv_price);
        tv_name = (TextView) rootView.findViewById(R.id.tv_name);
        tv_des = (WebView) rootView.findViewById(R.id.tv_des);
        ll_buy = (LinearLayout) rootView.findViewById(R.id.ll_buy);
        iv_totop = (ImageView) rootView.findViewById(R.id.iv_totop);
    }

    private void initViewParams() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        params.width = DensityUtil.dp2px(getContext(), 40);
        params.height = DensityUtil.dp2px(getContext(), 40);
        params.topMargin = Util.getStatusBarHeight(getContext());
        params.leftMargin = DensityUtil.dp2px(getContext(), 10);
        iv_back.setLayoutParams(params);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        iv_totop.setOnClickListener(this);
        ll_buy.setOnClickListener(this);
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
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) {//返回
            pop();
        }else if(v== iv_totop){//置顶
            sv.smoothScrollTo(0,0);
        }else if (v==ll_buy) {//立即购买
            if (!Util.isToken(getContext())) {
                show401();
                return;
            }
            loadProcess();
            getAddressData(stock_keeping_units.get(0).getId(),1);
        }
    }

    private void showFont(WebView webView, String content) {
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        setting.setDefaultTextEncodingName("UTF-8");
        webView.setScrollBarStyle(View.SCROLLBAR_POSITION_DEFAULT);
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<meta charset='utf-8' />");
        sb.append("<meta name='apple-mobile-web-app-capable' content='yes'>");
        sb.append("<meta name='apple-mobile-web-app-status-bar-style' content='black'>");
        sb.append("<meta name='viewport' content='width=device-width,initial-scale=1, minimum-scale=1.0, maximum-scale=1, user-scalable=no'>");
        sb.append("<meta name='viewport' content='width=device-width, initial-scale=1.0,user-scalable=no,maximum-scale=1.0'>");
        sb.append("<style>");
        sb.append("html,body {padding:0;margin:0;}");
        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body id='cont' >");
        sb.append(content);
        sb.append("</body>");
        sb.append("</html>");
        sb.append("<script type='text/javascript'>");
        sb.append("window.onload=function(){");
        sb.append("var src=document.getElementsByTagName('img');");
        sb.append("for (var i=0; i<src.length; i++) {");
        sb.append("url = src[i].getAttribute('src');");
        sb.append("link = url;");
        sb.append("src[i].setAttribute('src',link);");
        sb.append("if(document.body.clientWidth < src[i].naturalWidth){");
        sb.append("src[i].setAttribute('width','100%');");
        sb.append("}");
        sb.append("src[i].setAttribute('height','auto');");
        sb.append("src[i].setAttribute('style','margin-top:0px;');}}");
        sb.append("</script>");
        // if (!"".equals(content)) {
        // content = "<font size='4' color='#6a6a6a'>" + content + "</font>";
        // } else {
        // content = "<br/>";
        // }
        webView.setBackgroundResource(R.drawable.et_shape);
        // webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.loadDataWithBaseURL(null, sb.toString(), "text/html", "utf-8", null);
    }
    
    private void initBannerView() {
        banner.setViewFactory(new BannerViewFactory());
        banner.setDataList(adimgDatas);
        banner.start();
    }

    public class BannerViewFactory implements BannerView.ViewFactory<GoodsDetailBean1.DataBean.ImagesBean> {
        @Override
        public View create(final GoodsDetailBean1.DataBean.ImagesBean item, final int position, ViewGroup container) {
            ImageView iv = new ImageView(container.getContext());
            Glide.with(getContext())
                    .load(item.getUrl())
                    .into(iv);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (adimgDatas!=null && adimgDatas.size()>0) {
                        imageDatas.clear();
                        for (int i = 0; i < adimgDatas.size(); i++) {
                            imageDatas.add(adimgDatas.get(i).getUrl());
                        }
                        getSupportDelegate().start(NewsPictureDelegate.newInstance(position, imageDatas));
                    }
                }
            });
            return iv;
        }
    }

    public void getGoodsDetailData(String id){
        String url = Config.GET_PRODUCTS_ID + id;
        String apiName = "商品详情接口通过id";
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
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                }
            });
    }

    public void getGoodsDetailCodeData(String str){
        String url = Config.GET_PRODUCTS_CODE;
        String apiName = "商品详情接口通过code";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addParams.put("code", str);
        NetConnectionNew.post(apiName, getContext(),url, addHeader, addParams,
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
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("404")){
                            show(Config.CODE_ERROR404);
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
                showFont(tv_des, parseGoodsDetailJson1(arg0).getData().getDescription());
                initBannerView();
            }
        } catch (Exception e) {
            show("异常");
        }
    }

    private GoodsDetailBean1 parseGoodsDetailJson1(String arg0) {
        return new Gson().fromJson(arg0, GoodsDetailBean1.class);
    }

    private void getAddressData(final int skuid2, final int num) {
        final String apiName = "地址管理接口";
        String url = Config.ADDRESS_INFO;
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
                    getSupportDelegate().start(new MyAddressDelegate());
                    SharedPreferencesUtil.putBoolean(getContext(), "NoAddrisFromGoods", true);
                    SharedPreferencesUtil.putString(getContext(), "NoAddrGoodsQuantity", quantity+"");
                    SharedPreferencesUtil.putString(getContext(), "NoAddrGoodsProductId", skuid2 + "");
                }else {
                    if (product_code!=null) {
                        getSupportDelegate().start(SureOrderDelegate.newInstance(code + ""));
                    }else {
                        getSupportDelegate().start(SureOrderDelegate.newInstances(quantity+"",skuid2 + ""));
                    }
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
}
