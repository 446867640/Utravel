package com.utravel.app.delegates.search;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.TextView;

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
import com.utravel.app.delegates.detail.TaoBaoDetailDelegate;
import com.utravel.app.entity.CategoryBean;
import com.utravel.app.entity.TaoBaoGoodsBean1;
import com.utravel.app.ui.MyGridView;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.GlidePartCornerTransform;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
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

public class TBSearchDelagateTest extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
    private AppCompatImageView iv_back, iv_search;
    private AppCompatTextView tv_title;
    private AppCompatEditText et_search;
    private SmartRefreshLayout refresh;
    private MyGridView gv_goods;

    private static final String KEY = "KEY";
    private static final String IS_TMALL = "IS_TMALL";
    private int pageNo = 1;
    private boolean isNoMore = false;
    private String keyword = null;

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
    private boolean isFirst = true;

    private final List<String> TAB_TITLES = new ArrayList<>();
    private String[] defaultKeyword = {
            "品牌男装","个护美妆","鞋袜箱包","配饰精品","家装家具","眼镜墨镜","帽子丝巾",
            "皮带腰带","品牌手表","潮流女装","上衣","裤装","儿童母婴","汽车汽修" };

    @Override
    public boolean setIsDark() { return true; }

    @Override
    public Object setLayout() { return R.layout.delegate_search_jd_test; }

    public static TBSearchDelagateTest newInstance(String key) {
        TBSearchDelagateTest fragment = new TBSearchDelagateTest();
        Bundle bundle = new Bundle();
        bundle.putString(KEY, key);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static TBSearchDelagateTest newInstance(boolean key) {
        TBSearchDelagateTest fragment = new TBSearchDelagateTest();
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_TMALL, key);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            keyword = bundle.getString(KEY);
            is_tmall = bundle.getBoolean(IS_TMALL);
        }
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initKeywordData();
        initViews(rootView);
        initViewParams();
        initAdapter();
        initListener();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        if (keyword!=null){
            loadProcess();
            getGoodsData(keyword);
        }
    }

    private void initViews(View rootView) {
        status_bar = (View) rootView.findViewById(R.id.status_bar);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);
        iv_back = (AppCompatImageView) rootView.findViewById(R.id.iv_back);
        iv_search = (AppCompatImageView) rootView.findViewById(R.id.iv_search);
        et_search = (AppCompatEditText) rootView.findViewById(R.id.et_search);
        refresh = (SmartRefreshLayout) rootView.findViewById(R.id.refresh);
        gv_goods = (MyGridView) rootView.findViewById(R.id.gv_goods);
    }

    private void initKeywordData() {
        final int min=0;
        Random random = new Random();
        if (!TextUtils.isEmpty(SharedPreferencesUtil.getString(getContext(), Config.PRODUCT_CATEGORIES))){
            List<CategoryBean.DataBean> sortData = parseSortData(SharedPreferencesUtil.getString(getContext(), Config.PRODUCT_CATEGORIES)).getData();
            if (sortData!=null && sortData.size()>0 ){
                final int max=sortData.size()-1;
                final int randomNum = random.nextInt(max)%(max-min+1) + min;
                for (int i = 0;i < sortData.size();i++){
                    TAB_TITLES.add(sortData.get(i).getName());
                }
                keyword = TAB_TITLES.get(randomNum);
            }else {
                final int max=defaultKeyword.length-1;
                final int randomNum = random.nextInt(max)%(max-min+1) + min;
                keyword = defaultKeyword[randomNum];
            }
        }else {
            final int max=defaultKeyword.length-1;
            final int randomNum = random.nextInt(max)%(max-min+1) + min;
            keyword = defaultKeyword[randomNum];
        }
    }

    private CategoryBean parseSortData(String json) {
        return new Gson().fromJson(json, CategoryBean.class);
    }

    private void initViewParams() {
        if (keyword!=null){
            et_search.setText(keyword);
        }
        if (is_tmall) {
            tv_title.setText(getResources().getString(R.string.tianmao));
        }else {
            tv_title.setText(getResources().getString(R.string.taobao));
        }
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        params.height = Util.getStatusBarHeight(getContext());
        status_bar.setLayoutParams(params);
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
                    //预估返利算法
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
        iv_search.setOnClickListener(this);
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    iv_search.performClick();
                }
                return false;
            }
        });
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
                    getGoodsData(keyword);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back){ //返回
            pop();
        }else if(v==iv_search){ //搜索
            if (TextUtils.isEmpty(et_search.getText().toString())){
                String msg = _mActivity.getResources().getString(R.string.empty_search);
                show(msg);
                return;
            }
            taoBaoGoodDatas.clear();
            pageNo = 1;
            isNoMore = false;
            keyword = et_search.getText().toString();
            getGoodsData(keyword);
        }
    }

    private void getGoodsData(String value) {
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
            if ( isFirst ) {
                isFirst = false;
                SharedPreferencesUtil.putString(getContext(), Config.SEARCH_DATA_KEY, arg0);
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
