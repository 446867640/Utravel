package com.utravel.app.delegates.search;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.utravel.app.R;
import com.utravel.app.activities.proxy.LoginActivity;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.delegates.detail.GoodsInfoDelegate;
import com.utravel.app.entity.NewProductSearchBean;
import com.utravel.app.ui.MyGridView;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.GlidePartCornerTransform;
import com.utravel.app.utils.GlideRoundTransform;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class HomeSearchDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
    private ImageView iv_back;
    private ImageView iv_search;
    private EditText et_search;
    private ImageView iv_pailie;
    private SmartRefreshLayout mSmartRefreshLayout;
    private MyGridView gv_goods;

    private List<NewProductSearchBean.DataBean> searchDatas = new ArrayList<NewProductSearchBean.DataBean>();
    private CommonAdapter<NewProductSearchBean.DataBean> searchAdapter1;
    private CommonAdapter<NewProductSearchBean.DataBean> searchAdapter2;

    private int pageNo = 1;
    private boolean isNoMore = false;
    public boolean isOneNum = false; //是否是单排
    public String et_content = null; //搜索内容

    @Override
    public boolean setIsDark() {return true;}

    @Override
    public Object setLayout() {return R.layout.delegate_search_home;}

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewsParams();
        initAdapter();
        initListener();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        loadProcess();
        getGoodsSearchData();
    }

    private void initViews(View rootView) {
        status_bar = (View) this.rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView) this.rootView.findViewById(R.id.iv_back);
        iv_search = (ImageView) rootView.findViewById(R.id.iv_search);
        et_search = (EditText) rootView.findViewById(R.id.et_search);
        iv_pailie = (ImageView)rootView.findViewById( R.id.iv_pailie );
        mSmartRefreshLayout = (SmartRefreshLayout)rootView.findViewById( R.id.refresh );
        gv_goods = (MyGridView) rootView.findViewById( R.id.gv_goods );
    }

    private void initViewsParams() {
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
    }

    private void initAdapter() {
        searchAdapter1 = new CommonAdapter<NewProductSearchBean.DataBean>(getContext(),searchDatas,R.layout.item_shouye_pro1) {
            @Override
            public void convert(BaseViewHolder holder, NewProductSearchBean.DataBean t) {
                final ImageView iv1 = (ImageView)holder.getView(R.id.iv1);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                params.width  = DensityUtil.dp2px(getContext(), 100);
                params.height = DensityUtil.dp2px(getContext(), 100);
                iv1.setLayoutParams(params);
                holder.setText(R.id.tv1, t.getName());
                holder.setText(R.id.tv2, "￥" + t.getPrice());
                Glide.with(getContext())
                        .load(t.getImage_url())
                        .transform(new GlideRoundTransform(getContext(),5))
                        .into(new SimpleTarget<GlideDrawable>() { // 加上这段代码 可以解决
                            @Override
                            public void onResourceReady(GlideDrawable arg0, GlideAnimation<? super GlideDrawable> arg1) {
                                iv1.setImageDrawable(arg0); //显示图片
                            }
                        });
            }
        };
        searchAdapter2 = new CommonAdapter<NewProductSearchBean.DataBean>(getContext(),searchDatas,R.layout.item_shouye_pro) {
            @Override
            public void convert(BaseViewHolder holder, NewProductSearchBean.DataBean t) {
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
        gv_goods.setAdapter(searchAdapter1);//默认1
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        iv_pailie.setOnClickListener(this);
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKey(et_search);
                    iv_search.performClick();
                }
                return false;
            }
        });
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                searchDatas.clear();
                pageNo = 1;
                isNoMore = false;
                getGoodsSearchData();
            }
        });
        mSmartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (isNoMore) {
                    show(getResources().getString(R.string.no_data));
                    refreshLayout.finishLoadMore();
                    return;
                }
                pageNo += 1;
                getGoodsSearchData();
            }
        });
        gv_goods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getSupportDelegate().start(GoodsInfoDelegate.newInstances(searchDatas.get(position).getId()+""));
            }
        });
    }

    @Override
    public void onClick(View v) {
        hideKey(et_search);
        if (v==iv_back) { //返回
            pop();
        }else if (v==iv_search) {
            searchDatas.clear();
            pageNo = 1;
            isNoMore = false;
            et_content = et_search.getText().toString();
            getGoodsSearchData();
        }else if (v==iv_pailie) {
            if (isOneNum) { //单排
                isOneNum = !isOneNum;
                iv_pailie.setImageResource(R.drawable.pailie1);
                gv_goods.setNumColumns(1);
                gv_goods.setVerticalSpacing(DensityUtil.dp2px(getContext(), 2));
                gv_goods.setHorizontalSpacing(1);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(0,DensityUtil.dp2px(getContext(), 10),0,0);
                gv_goods.setLayoutParams(params);
                gv_goods.setAdapter(searchAdapter1);
                searchAdapter1.refreshDatas(searchDatas);
            }else {		    //双排
                isOneNum = !isOneNum;
                iv_pailie.setImageResource(R.drawable.pailie2);
                gv_goods.setNumColumns(2);
                gv_goods.setVerticalSpacing(DensityUtil.dp2px(getContext(), 8));
                gv_goods.setHorizontalSpacing(DensityUtil.dp2px(getContext(), 8));
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(DensityUtil.dp2px(getContext(), 10), DensityUtil.dp2px(getContext(), 10), DensityUtil.dp2px(getContext(), 10), DensityUtil.dp2px(getContext(), 10));
                gv_goods.setLayoutParams(params);
                gv_goods.setAdapter(searchAdapter2);
                searchAdapter2.refreshDatas(searchDatas);
            }
        }
    }

    public void getGoodsSearchData(){
        String url = Config.GOODS_SEARCH;
        String apiName = "搜索商品接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        if (!TextUtils.isEmpty(et_content)) {
            addParams.put("key", et_content);
        }
        addParams.put("page", pageNo+"");
        addParams.put("size", "20");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    mSmartRefreshLayout.finishRefresh();
                    mSmartRefreshLayout.finishLoadMore();
                    processGoodsSearchData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    mSmartRefreshLayout.finishRefresh();
                    mSmartRefreshLayout.finishLoadMore();
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

    private void processGoodsSearchData(String arg0) {
        try {
            JSONObject json = new JSONObject(arg0);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            if (parcessGoodsSearchJson(arg0).getData().size()==0) {
                show("没有更多数据了");
                isNoMore = true;
                searchAdapter1.refreshDatas(searchDatas);
                searchAdapter2.refreshDatas(searchDatas);
                return;
            }
            searchDatas.addAll(parcessGoodsSearchJson(arg0).getData());
            searchAdapter1.refreshDatas(searchDatas);
            searchAdapter2.refreshDatas(searchDatas);
        } catch (Exception e) {}
    }

    private NewProductSearchBean parcessGoodsSearchJson(String arg0) {
        return new Gson().fromJson(arg0, NewProductSearchBean.class);
    }
}
