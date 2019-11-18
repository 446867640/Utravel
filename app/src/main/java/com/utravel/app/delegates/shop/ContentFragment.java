package com.utravel.app.delegates.shop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.utravel.app.R;
import com.utravel.app.adapter.SortRecylerAdapter;
import com.utravel.app.delegates.login.LoginChoiceDelegate;
import com.utravel.app.delegates.sort.SortDelegate;
import com.utravel.app.delegates.sort.SortRightDataConverter;
import com.utravel.app.recycler.MultipleItemEntity;

import java.util.List;

import me.yokeyword.fragmentation.anim.DefaultNoAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * Created by YoKeyword on 16/2/9.
 */
public class ContentFragment extends MySupportFragment {

    RecyclerView mRecyclerView;

    private static final String ARG_MENU = "arg_menu";
    private String mMenu;

    public static ContentFragment newInstance(String menu) {
        Bundle args = new Bundle();
        args.putString(ARG_MENU, menu);
        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mMenu = args.getString(ARG_MENU);
        }
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultNoAnimator();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delegate_sort_right, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list_content);
        final SortDelegate delegate = new SortDelegate();
        final List<MultipleItemEntity> datas = new SortRightDataConverter(0).convert();
        final SortRecylerAdapter adapter = new SortRecylerAdapter(datas, delegate);
        mRecyclerView.setAdapter(adapter);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ((SortDelegate) getParentFragment()).getSupportDelegate().start(new LoginChoiceDelegate());
            }
        });
    }

    @Override
    public boolean onBackPressedSupport() {
        // ContentFragment是ShopFragment的栈顶子Fragment,可以在此处理返回按键事件
        return super.onBackPressedSupport();
    }
}
