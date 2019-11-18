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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.delegates.detail.GoodsDetailDelegate;
import com.utravel.app.entity.CategoryBean;
import com.utravel.app.entity.PinduoduoGoodsBean;
import com.utravel.app.ui.MyGridView;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.GlidePartCornerTransform;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
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

public class SearchDelegateTest extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
//    private LinearLayoutCompat ll_toolbar;
    private AppCompatImageView iv_back, iv_search;
    private AppCompatEditText et_search;
    private AppCompatTextView tv_title;
    private SmartRefreshLayout refresh;
    private MyGridView gv_goods;

    private List<PinduoduoGoodsBean.DataBean.GoodsListBean> gv_goodsDatas = new ArrayList<>();
    private CommonAdapter<PinduoduoGoodsBean.DataBean.GoodsListBean> gv_goodsAdapter;

    private String[] defaultKeyword = {
            "品牌男装","个护美妆","鞋袜箱包","配饰精品","家装家具","眼镜墨镜","帽子丝巾",
            "皮带腰带","品牌手表","潮流女装","上衣","裤装","儿童母婴","汽车汽修" };
    private final List<String> TAB_TITLES = new ArrayList<>();

    private static final String KEY = "KEY";
    private int pageNo = 1;
    private boolean isNoMore = false;
    private String keyword = null;

    @Override
    public boolean setIsDark() { return true; }

    @Override
    public Object setLayout() { return R.layout.delegate_search_jd_test; }

    public static SearchDelegateTest newInstance(String key) {
        SearchDelegateTest fragment = new SearchDelegateTest();
        Bundle bundle = new Bundle();
        bundle.putString(KEY, key);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            keyword = bundle.getString(KEY);
        }
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewParams();
        initAdapter();
        initListener();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        if (keyword==null){
            initKeywordData();
        }
        loadProcess();
        getGoodsData(keyword);
        et_search.setText(keyword);
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
        tv_title.setText(getResources().getString(R.string.pinduoduo));
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        params.height = Util.getStatusBarHeight(getContext());
        status_bar.setLayoutParams(params);
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
                    holder.setText(R.id.tv_oldprice, "¥" + (double)t.getMin_normal_price()/100+"");
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
            gv_goods.setAdapter(gv_goodsAdapter);
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
                getSupportDelegate().start(GoodsDetailDelegate.newInstance(gv_goodsDatas.get(position).getGoods_id(), keyword));
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
            gv_goodsDatas.clear();
            pageNo = 1;
            isNoMore = false;
            keyword = et_search.getText().toString();
            getGoodsData(keyword);
        }
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
                        dismissLoadProcess();
                        refresh.finishLoadMore();
                        processGoodsData(arg0);
                    }
                },
                new NetConnectionNew.FailCallback() {
                    @Override
                    public void onFail(Call arg0, Exception arg1, int arg2) {
                        refresh.finishLoadMore();
                        dismissLoadProcess();
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
}
