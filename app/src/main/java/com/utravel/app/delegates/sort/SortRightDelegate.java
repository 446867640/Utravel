package com.utravel.app.delegates.sort;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.adapter.SortRecylerAdapter;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.delegates.search.SearchDelegateTest;
import com.utravel.app.entity.CategoryBean;
import com.utravel.app.recycler.MultipleFields;
import com.utravel.app.recycler.MultipleItemEntity;
import com.utravel.app.utils.GlideRoundTransform;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class SortRightDelegate extends LatterDelegate {

    private RecyclerView mRecyclerView;

    private static final String ARG_CONTENT_ID = "CONTENT_ID";
    private int mCurrentPosition = 0;
    private List<CategoryBean.DataBean> dataBean;
    private List<CategoryBean.DataBean.SecondaryCategoriesBean> secondaryDatas;

    public static SortRightDelegate newInstance(int position) {
        SortRightDelegate fragment = new SortRightDelegate();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_CONTENT_ID, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCurrentPosition = bundle.getInt(ARG_CONTENT_ID);
        }
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_sort_right;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.rv_list_content);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        if (TextUtils.isEmpty(SharedPreferencesUtil.getString(getContext(), Config.PRODUCT_CATEGORIES))){
            getSortRightData();
        }else{
            processSortRightData(SharedPreferencesUtil.getString(getContext(), Config.PRODUCT_CATEGORIES));
        }
    }

    private void getSortRightData() {
        String url = Config.PRODUCT_CATEGORIES; //接口URL
        String apiName = "商品分类接口--取右边"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    SharedPreferencesUtil.putString(getContext(), Config.PRODUCT_CATEGORIES, arg0);
                    processSortRightData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {  dismissLoadProcess(); }
            });
    }

    private void processSortRightData(String json) {
        dataBean = parseSortRightData(json).getData();
        if (dataBean!=null && dataBean.size()>0){
            final String main_image_url = dataBean.get(mCurrentPosition).getMain_image_url();
            final String name = dataBean.get(mCurrentPosition).getName();
            final ImageView headIv = new ImageView(getContext());
            Glide.with(getContext())
                    .load(main_image_url)
                    .transform(new GlideRoundTransform(getContext(),5))
                    .into(new SimpleTarget<GlideDrawable>() { // 加上这段代码 可以解决
                        @Override
                        public void onResourceReady(GlideDrawable arg0, GlideAnimation<? super GlideDrawable> arg1) {
                            headIv.setImageDrawable(arg0); //显示图片
                        }
                    });
            final SortDelegate delegate = new SortDelegate();
            final List<MultipleItemEntity> datas = new SortRightDataConverter(mCurrentPosition).setJsonData(json).convert();
            final SortRecylerAdapter adapter = new SortRecylerAdapter(datas,delegate);
            adapter.addHeaderView(headIv);
            mRecyclerView.setAdapter(adapter);
            StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(manager);
            adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    ((SortDelegate)getParentFragment()).getSupportDelegate().start(
                            SearchDelegateTest.newInstance((String) datas.get(position).getField(MultipleFields.TEXT)));
                }
            });
        }
    }

    private CategoryBean parseSortRightData(String json) {
        return new Gson().fromJson(json, CategoryBean.class);
    }
}