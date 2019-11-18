package com.utravel.app.delegates.sort;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.utravel.app.R;
import com.utravel.app.adapter.SortRecylerAdapter;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.recycler.MultipleFields;
import com.utravel.app.recycler.MultipleItemEntity;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class SortLeftDelegate extends LatterDelegate {
    RecyclerView mRecyclerView;

    private int mPrePosition = 0;    //之前点击位置
    private int mCurrentPosition = 0;//现在点击位置
    private static final String SORT_LEFT_POSITION = "SORT_LEFT_POSITION";

    @Override
    public Object setLayout() {
        return R.layout.delegate_sort_left;
    }

    public static SortLeftDelegate newInstance(int position) {
        SortLeftDelegate fragment = new SortLeftDelegate();
        Bundle bundle = new Bundle();
        bundle.putInt(SORT_LEFT_POSITION , position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCurrentPosition = bundle.getInt(SORT_LEFT_POSITION);
            mPrePosition = bundle.getInt(SORT_LEFT_POSITION);
        }
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        mRecyclerView = rootView.findViewById(R.id.rv_vertical_menu_list);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        if (TextUtils.isEmpty(SharedPreferencesUtil.getString(getContext(), Config.PRODUCT_CATEGORIES))){
            getSortLeftData();
        }else{
            processSortLeftData(SharedPreferencesUtil.getString(getContext(), Config.PRODUCT_CATEGORIES));
        }
    }

    private void getSortLeftData() {
        String url = Config.PRODUCT_CATEGORIES; //接口URL
        String apiName = "商品分类接口--取左边"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        dismissLoadProcess();
                        SharedPreferencesUtil.putString(getContext(), Config.PRODUCT_CATEGORIES, arg0);
                        processSortLeftData(arg0);
                    }
                },
                new NetConnectionNew.FailCallback() {
                    @Override
                    public void onFail(Call arg0, Exception arg1, int arg2) {  dismissLoadProcess(); }
                });
    }

    private void processSortLeftData(String json) {
        final SortDelegate delegate = new SortDelegate();
        final List<MultipleItemEntity> datas = new SortLeftDataConverter(mCurrentPosition).setJsonData(json).convert();
        final SortRecylerAdapter adapter = new SortRecylerAdapter(datas,delegate);
        mRecyclerView.setAdapter(adapter);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setItemAnimator(null);//屏蔽动画效果
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                final int currentPostion = position;
                if (currentPostion!=mPrePosition) {
                    //还原上一个
                    datas.get(mPrePosition).setField(MultipleFields.TAG,false);
                    adapter.notifyItemChanged(mPrePosition);
                    //更新选中item
                    datas.get(currentPostion).setField(MultipleFields.TAG,true);
                    adapter.notifyItemChanged(currentPostion);
                    mPrePosition = currentPostion;
                    showContent(position);
                }
            }
        });
    }

    private void showContent(int position){
        SortRightDelegate delegate = SortRightDelegate.newInstance(position);
        ((SortDelegate) getParentFragment()).switchContentFragment(delegate);
    }
}
