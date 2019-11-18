package com.utravel.app.delegates.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.utravel.app.R;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.delegates.login.LoginChoiceDelegate;
import com.utravel.app.delegates.main.my.MyDelegate;
import com.utravel.app.delegates.main.shequ.ShequDelegate;
import com.utravel.app.delegates.main.shequ.SortDelegate1;
import com.utravel.app.delegates.main.shouye.ShouyeDelegate;
import com.utravel.app.entity.MainBottomBarBean;
import com.utravel.app.ui.MyGridView;
import com.utravel.app.utils.Util;

import java.util.ArrayList;

import me.yokeyword.fragmentation.SupportFragment;

public class MainDelegate extends LatterDelegate {
    MyGridView mMyGridView;

    CommonAdapter<MainBottomBarBean> gv_adapter;
    ArrayList<MainBottomBarBean> gv_data = new ArrayList<>();
    SupportFragment[] delegateArray;
    private final ArrayList<OnBackDelegate> ITEM_DELEGATES = new ArrayList<>();

    int prePosition = 0;
    int delegatePosition = 0;

    @Override
    public Object setLayout() {
        return R.layout.delegate_main;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initDelegates();
    }

    public static MainDelegate newInstance() {
        Bundle args = new Bundle();
        MainDelegate fragment = new MainDelegate();
        fragment.setArguments(args);
        return fragment;
    }

    private void initDelegates() {
        ITEM_DELEGATES.clear();
        ITEM_DELEGATES.add(new ShouyeDelegate());
        ITEM_DELEGATES.add(new ShequDelegate());
        ITEM_DELEGATES.add(new MyDelegate());
        delegateArray = ITEM_DELEGATES.toArray(new SupportFragment[3]);
        loadMultipleRootFragment(R.id.fl_tab_container, 0, delegateArray);
    }

    private void initViews(View rootView) {
        mMyGridView = (MyGridView) rootView.findViewById(R.id.gv_bottomBar);
        gv_data.clear();
        int shouye_not = getContext().getResources().getIdentifier("shouye_not", "mipmap", getContext().getPackageName());
        int shouye_on = getContext().getResources().getIdentifier("shouye_on", "mipmap", getContext().getPackageName());
        int shequ_not = getContext().getResources().getIdentifier("shequ_not", "mipmap", getContext().getPackageName());
        int shequ_on = getContext().getResources().getIdentifier("shequ_on", "mipmap", getContext().getPackageName());
        int wode_not = getContext().getResources().getIdentifier("wode_not", "mipmap", getContext().getPackageName());
        int wode_on = getContext().getResources().getIdentifier("wode_on", "mipmap", getContext().getPackageName());
        gv_data.add(new MainBottomBarBean(shouye_not,shouye_on,getResources().getString(R.string.shouye),0));
        gv_data.add(new MainBottomBarBean(shequ_not,shequ_on,getResources().getString(R.string.shequ),1));
        gv_data.add(new MainBottomBarBean(wode_not,wode_on,getResources().getString(R.string.wode),2));
        gv_adapter = new CommonAdapter<MainBottomBarBean>(_mActivity,gv_data,R.layout.item_main_bottombar) {
            @Override
            public void convert(BaseViewHolder holder, MainBottomBarBean t) {
                TextView tv1 = holder.getView(R.id.tv1);
                ImageView iv1 = holder.getView(R.id.iv1);
                tv1.setText(t.getName());
                if (holder.getPosition()==prePosition) {
                    tv1.setTextColor(Color.parseColor("#F62341"));
                    iv1.setImageResource(t.getImage_on());
                }else{
                    tv1.setTextColor(Color.parseColor("#666666"));
                    iv1.setImageResource(t.getImage_non());
                }
            }
        };
        mMyGridView.setAdapter(gv_adapter);
        mMyGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (gv_data.get(position).getId() != 0) { //我的页面
                    if (!Util.isToken(_mActivity)) {
                        getSupportDelegate().start(new LoginChoiceDelegate());
                        return;
                    }
                }
                prePosition = position;
                gv_adapter.refreshDatas(gv_data);

                showHideFragment(delegateArray[position], delegateArray[delegatePosition]);
                delegatePosition = position;
//                EventBusActivityScope.getDefault(_mActivity).post(new TabSelectedEvent(position));
            }
        });
    }
}
