package com.utravel.app.delegates.addr;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.utravel.app.R;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.entity.LocationBean;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;

public class SelectAddrDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
    private AppCompatImageView iv_back;
    private AppCompatTextView tv_title;
    private ListView listView;
    private List<LocationBean> locations = new ArrayList<>();
    private LocationAdapter locationAdapter;
    private int area_id = 0;
    private String name = "";
    private int parent_id = 0;
    private static final String BUNDLE_TAG = "BUNDLE_TAG";
    private String flag;

    @Override
    public boolean setIsDark() { return true; }

    @Override
    public Object setLayout() { return R.layout.delegate_select_addr; }

    public static SelectAddrDelegate newInstance(String value) {
        SelectAddrDelegate fragment = new SelectAddrDelegate();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TAG, value);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            flag = bundle.getString(BUNDLE_TAG);
        }
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewsParams();
        initAdapter();
        initListener();
        loadProcess();
        getAreaList(parent_id);
    }

    private void initAdapter() {
        locationAdapter = new LocationAdapter(getContext(), locations,R.layout.item_city);
        listView.setAdapter(locationAdapter);
    }

    private void initViews(View rootView) {
        status_bar = (View) this.rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView) this.rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);
        listView = (ListView) rootView.findViewById(R.id.listview);
    }

    private void initViewsParams() {
        tv_title.setText(getResources().getString(R.string.select_addr_title));
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                name = locations.get(position).name;
                area_id = locations.get(position).id;
                loadProcess();
                getAreaList(locations.get(position).id);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) { //返回
            pop();
        }
    }

    private class LocationAdapter extends CommonAdapter<LocationBean> {
        public LocationAdapter(Context context, List<LocationBean> datas, int layoutId) {
            super(context, datas, layoutId);
        }
        @Override
        public void convert(BaseViewHolder holder, final LocationBean bean) {
            holder.setText(R.id.tv_cityname, bean.name);
        }
    }

    private void getAreaList(int parent_id) {
        String url = Config.AREAS_URL;
        String apiName = "注册所在地接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addParams.put("time", System.currentTimeMillis() + "");
        if (parent_id != 0) {
            addParams.put("parent_id", parent_id + "");
        }
        NetConnectionNew.get(apiName, getContext(), url,addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    parseAreas(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                }
            });
    }

    private void parseAreas(String result) {
        try {
            JSONObject json = new JSONObject(result);
            if (json.has("error")) {
                show(json.optString("error"));
                return;
            }
            JSONArray list = json.getJSONArray("data");
            if (list.length() > 0) {
                locations.clear();
                for (int i = 0; i < list.length(); i++) {
                    JSONObject tmp = list.getJSONObject(i);
                    LocationBean fb = new LocationBean();
                    fb.id = tmp.optInt("id");
                    fb.name = tmp.optString("name");
                    locations.add(fb);
                }
                locationAdapter.refreshDatas(locations);
            } else {
                if(flag.equals("RegisterActivity")){
                    Bundle bundle = new Bundle();
                    bundle.putInt("area_id", area_id);
                    bundle.putString("name", name);
                    setFragmentResult(RESULT_OK, bundle);
                }
                pop();
            }
        } catch (JSONException e) {}
    }
}
