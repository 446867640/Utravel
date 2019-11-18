package com.utravel.app.delegates.addr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.entity.AddressBean;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;

public class ChoiceAddressDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
    private ImageView iv_back;
    private TextView tv_title;
    private ListView listview;
    private LinearLayout llEmptyAddress;
    private TextView tvNew;
    private List<AddressBean.DataBean> listData = new ArrayList<AddressBean.DataBean>();
    private CommonAdapter<AddressBean.DataBean> listAdapter;

    @Override
    public boolean setIsDark() { return true; }

    @Override
    public Object setLayout() { return R.layout.delegate_choice_addr; }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewsParams();
        initAdapter();
        initListener();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        listData.clear();
        loadProcess();
        getAddressData();
    }

    private void initViews(View rootView) {
        status_bar = (View) this.rootView.findViewById(R.id.status_bar);
        iv_back = (ImageView) this.rootView.findViewById(R.id.iv_back);
        tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        llEmptyAddress = (LinearLayout) rootView.findViewById(R.id.ll_empty_address);
        listview = (ListView) rootView.findViewById(R.id.listview);
        tvNew = (TextView) rootView.findViewById(R.id.tv_new);
    }

    private void initViewsParams() {
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
    }

    private void initAdapter() {
        listAdapter = new CommonAdapter<AddressBean.DataBean>(getContext(),listData,R.layout.item_add_address1) {
            @Override
            public void convert(BaseViewHolder holder, final AddressBean.DataBean t) {
                holder.setText(R.id.tv_username, t.getContact_name());
                holder.setText(R.id.tv_phone, t.getMobile());
                holder.setText(R.id.tv_address, t.getAddress());
                View v1 = holder.getView(R.id.v1);
                View v2 = holder.getView(R.id.v2);
                if (holder.getPosition()==0) {
                    v1.setVisibility(View.VISIBLE);
                    v2.setVisibility(View.VISIBLE);
                }else {
                    v1.setVisibility(View.GONE);
                    v2.setVisibility(View.GONE);
                }
            }
        };
        listview.setAdapter(listAdapter);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        tvNew.setOnClickListener(this);
        tv_title.setOnClickListener(this);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("id", listData.get(position).getId()+"");
                bundle.putString("name", listData.get(position).getContact_name());
                bundle.putString("mobile", listData.get(position).getMobile());
                bundle.putString("address", listData.get(position).getAddress());
                setFragmentResult(RESULT_OK, bundle);
                SharedPreferencesUtil.putString(getContext(), "uplevel_addressId", listData.get(position).getId()+"");
                SharedPreferencesUtil.putString(getContext(), "uplevel_name", listData.get(position).getContact_name());
                SharedPreferencesUtil.putString(getContext(), "uplevel_mobile", listData.get(position).getMobile());
                SharedPreferencesUtil.putString(getContext(), "uplevel_address", listData.get(position).getAddress());
                pop();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == iv_back) {
            pop();
        } else if (v == tvNew) {
            getSupportDelegate().start(new NewAddressDelegate());
        } else if (v == tv_title) {
            getSupportDelegate().start(new MyAddressDelegate());
        }
    }

    private void getAddressData() {
        final String apiName = "收货地址接口";
        String url = Config.ADDRESS_INFO;
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("page", 1 + "");
        addParams.put("size", 100 + "");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url,addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        dismissLoadProcess();
                        processAddressData(arg0);
                    }
                },
                new NetConnectionNew.FailCallback() {
                    @Override
                    public void onFail(Call arg0, Exception arg1, int arg2) {
                        dismissLoadProcess();
                        if (arg1.getMessage()!=null) {
                            if (arg1.getMessage().contains("401")) {
                                showMsg401();
                            }
                        }
                    }
                });
    }

    private void processAddressData(String result) {
        try {
            JSONObject json = new JSONObject(result);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            if (parseAdrJson(result).getData()!=null && parseAdrJson(result).getData().size()>0) {
                llEmptyAddress.setVisibility(View.GONE);
                listview.setVisibility(View.VISIBLE);
                listData.addAll(parseAdrJson(result).getData());
                listAdapter.refreshDatas(listData);
            }else {
                llEmptyAddress.setVisibility(View.VISIBLE);
                listview.setVisibility(View.GONE);
            }
        } catch (Exception e) {}
    }

    private AddressBean parseAdrJson(String result) {
        return new Gson().fromJson(result, AddressBean.class);
    }
}
