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
import com.utravel.app.delegates.detail.TaoBaoDetailDelegate1;
import com.utravel.app.entity.TaoBaoGoodsBean;
import com.utravel.app.ui.MyGridView;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.GlidePartCornerTransform;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.Util;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;

public class TBSearchDelagateTest1 extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
    private AppCompatImageView iv_back, iv_search;
    private AppCompatTextView tv_title;
    private AppCompatEditText et_search;
    private SmartRefreshLayout refresh;
    private MyGridView gv_goods;

    private List<TaoBaoGoodsBean.ResultListBean> taoBaoGoodDatas = new ArrayList<>();
    private CommonAdapter<TaoBaoGoodsBean.ResultListBean> taoBaoApapter;

    private static final String KEY = "KEY";
    private static final String IS_TMALL = "IS_TMALL";
    private int pageNo = 1;
    private boolean isNoMore = false;
    private boolean is_tmall = false; //是否是天猫商品
    private String keyword = null;

    private String[] defaultKeyword = {
            "品牌男装","个护美妆","鞋袜箱包","配饰精品","家装家具","眼镜墨镜","帽子丝巾",
            "皮带腰带","品牌手表","潮流女装","上衣","裤装","儿童母婴","汽车汽修" };

    @Override
    public boolean setIsDark() { return true; }

    @Override
    public Object setLayout() { return R.layout.delegate_search_jd_test; }

    public static TBSearchDelagateTest1 newInstance(String key) {
        TBSearchDelagateTest1 fragment = new TBSearchDelagateTest1();
        Bundle bundle = new Bundle();
        bundle.putString(KEY, key);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static TBSearchDelagateTest1 newInstance(boolean key) {
        TBSearchDelagateTest1 fragment = new TBSearchDelagateTest1();
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

    private void initKeywordData() {
        final int min=0;
        final int max=defaultKeyword.length-1;
        Random random = new Random();
        final int randomNum = random.nextInt(max)%(max-min+1) + min;
        keyword = defaultKeyword[randomNum];
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
            taoBaoApapter = new CommonAdapter<TaoBaoGoodsBean.ResultListBean>(getContext(),taoBaoGoodDatas,R.layout.item_shouye_goods) {
                @Override
                public void convert(BaseViewHolder holder, TaoBaoGoodsBean.ResultListBean t) {
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
                    holder.setText(R.id.tv_fan, getResources().getString(R.string.yugusheng) + t.getEstimated_commission());

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
                getSupportDelegate().start(TaoBaoDetailDelegate1.newInstance1(taoBaoGoodDatas.get(position)));
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
        String url = Config.TAOBAO_SEARCH; //接口url
        String apiName = "淘宝商品搜素接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addParams.put("q", value);
        addParams.put("page_no", pageNo + "");
        addParams.put("page_size", 10 + "");
        addParams.put("is_tmall",is_tmall+"");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    refresh.finishLoadMore();
                    processTaoBaoGoodsData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    refresh.finishLoadMore();
                }
            });
    }

    public void processTaoBaoGoodsData(String arg0) {
        JSONObject json = JSON.parseObject(arg0);
        if (json != null) {
            if (json.containsKey("error")) {
                show(json.getString("error"));
                return;
            }
            if (parcessGoodsSearchJson(arg0).getResult_list() != null) {
                if (parcessGoodsSearchJson(arg0).getResult_list().size()==0) {
                    show(getResources().getString(R.string.no_data));
                    isNoMore = true;
                    taoBaoApapter.refreshDatas(taoBaoGoodDatas);
                    return;
                }
                taoBaoGoodDatas.addAll(parcessGoodsSearchJson(arg0).getResult_list());
                taoBaoApapter.refreshDatas(taoBaoGoodDatas);
            }else {
                if (json.containsKey("error_response")) {
                    JSONObject error_response = json.getJSONObject("error_response");
                    if (error_response.containsKey("sub_msg")) {
                        show(error_response.getString("sub_msg"));
                    }
                }
            }
        }
    }

    public TaoBaoGoodsBean parcessGoodsSearchJson(String arg0) {
        return new Gson().fromJson(arg0, TaoBaoGoodsBean.class);
    }
}
