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
import android.widget.ImageView;
import android.widget.TextView;

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
import com.utravel.app.delegates.detail.JingDongDetailDelegate;
import com.utravel.app.delegates.login.LoginChoiceDelegate;
import com.utravel.app.entity.CategoryBean;
import com.utravel.app.entity.JDBankuanBean;
import com.utravel.app.entity.TaoBaoGoodsBean;
import com.utravel.app.ui.MyGridView;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.GlideRoundTransform;
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

public class JDSearchDelegateTest extends LatterSwipeBackDelegate implements View.OnClickListener {

    private View status_bar;
    private AppCompatTextView tv_title;
    private AppCompatImageView iv_back, iv_search;
    private AppCompatEditText et_search;
    private SmartRefreshLayout refresh;
    private MyGridView gv_goods;

    public List<JDBankuanBean> JDGoodsDatas = new ArrayList<>();
    public CommonAdapter<JDBankuanBean> JDGoodsAdapter;
    private String[] defaultKeyword = {
            "品牌男装","个护美妆","鞋袜箱包","配饰精品","家装家具","眼镜墨镜","帽子丝巾",
            "皮带腰带","品牌手表","潮流女装","上衣","裤装","儿童母婴","汽车汽修" };
    private final List<String> TAB_TITLES = new ArrayList<>();
    private int pageNo = 1;
    private boolean isNoMore = false;
    private String keyword = null;

    @Override
    public boolean setIsDark() { return true; }

    @Override
    public Object setLayout() { return R.layout.delegate_search_jd_test; }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initKeywordData();
        initViews(rootView);
        initViewParams();
        initAdapter();
        initListener();
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

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        if (keyword!=null){
            JDGoodsDatas.clear();
            pageNo = 1;
            isNoMore = false;
            loadProcess();
            getGoodsData(keyword);
        }
    }

    private void initViews(View rootView) {
        status_bar = (View) rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView) rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);
        iv_search = (AppCompatImageView) rootView.findViewById(R.id.iv_search);
        et_search = (AppCompatEditText) rootView.findViewById(R.id.et_search);
        refresh = (SmartRefreshLayout) rootView.findViewById(R.id.refresh);
        gv_goods = (MyGridView) rootView.findViewById(R.id.gv_goods);
    }

    private void initViewParams() {
        if (keyword!=null){
            et_search.setText(keyword);
        }
        tv_title.setText(getResources().getString(R.string.jingdong));

        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        params.height = Util.getStatusBarHeight(getContext());
        status_bar.setLayoutParams(params);
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
            JDGoodsDatas.clear();
            pageNo = 1;
            isNoMore = false;
            keyword = et_search.getText().toString();
            getGoodsData(keyword);
        }
    }

    private void getGoodsData(String value) {
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
                            getSupportDelegate().start(new LoginChoiceDelegate());
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
