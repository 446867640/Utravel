package com.utravel.app.delegates.addr;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;

public class MyAddressDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
    private ImageView iv_back;
    private LinearLayout ll_del;
    private ImageView iv_del;
    private TextView tv_over;
    private LinearLayout llEmptyAddress;
    private TextView tvNew;
    //记录删除状态
    private boolean isDel = false;
    private ListView listview;
    private List<AddressBean.DataBean> addresslistData;
    private CommonAdapter<AddressBean.DataBean> addressListAdapter;

    @Override
    public boolean setIsDark() { return true; }

    @Override
    public Object setLayout() {
        return R.layout.delegate_my_address;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewParams();
        initAdapter();
        initListener();
    }

    private void initViews(View rootView) {
        status_bar = (View) rootView.findViewById(R.id.status_bar);
        iv_back = (ImageView) rootView.findViewById(R.id.iv_back);
        ll_del = (LinearLayout) rootView.findViewById(R.id.ll_del);
        iv_del = (ImageView) rootView.findViewById( R.id.iv_del );
        tv_over = (TextView) rootView.findViewById( R.id.tv_over );
        llEmptyAddress = (LinearLayout) rootView.findViewById( R.id.ll_empty_address );
        listview = (ListView) rootView.findViewById( R.id.listview );
        tvNew = (TextView) rootView.findViewById( R.id.tv_new );
    }

    private void initViewParams() {
        iv_del.setColorFilter(getResources().getColor(R.color.app_theme_color));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(getContext());
        status_bar.setLayoutParams(params);
    }

    private void initAdapter() {
        addressListAdapter = new CommonAdapter<AddressBean.DataBean>(getContext(),addresslistData,R.layout.item_add_address) {
            @Override
            public void convert(BaseViewHolder holder, final AddressBean.DataBean t) {
                holder.setText(R.id.tv_username, t.getContact_name());
                holder.setText(R.id.tv_phone, t.getMobile());
                holder.setText(R.id.tv_address, t.getAddress());
                final LinearLayout ll_edit = holder.getView(R.id.ll_edit);
                final View v1 = holder.getView(R.id.v1);
                final View v2 = holder.getView(R.id.v2);
                final ImageView iv_btn1 = holder.getView(R.id.iv_btn1);
                final TextView tv_btn1 = holder.getView(R.id.tv_btn1);
                final TextView tv_btn2 = holder.getView(R.id.tv_btn2);
                final TextView tv_btn3 = holder.getView(R.id.tv_btn3);
                if (holder.getPosition()==0) {
                    v1.setVisibility(View.VISIBLE);
                    v2.setVisibility(View.VISIBLE);
                }else {
                    v1.setVisibility(View.GONE);
                    v2.setVisibility(View.GONE);
                }
                if (isDel) {
                    ll_edit.setVisibility(View.VISIBLE);
                }else {
                    ll_edit.setVisibility(View.GONE);
                }
                if (t.isIs_default()) {
                    iv_btn1.setBackgroundResource(R.drawable.qi_xuanze);
                    tv_btn1.setTextColor(Color.parseColor("#FF3A66"));
                }else {
                    iv_btn1.setBackgroundResource(R.drawable.qb_weixuan);
                    tv_btn1.setTextColor(Color.parseColor("#000000"));
                }
                final LinearLayout ll_btn1 = holder.getView(R.id.ll_btn1);
                ll_btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (t.isIs_default()) {
                            show("已是默认状态");
                        }else {
                            //调用设为默认的接口
                            loadProcess();
                            setMoRenAddressData(t.getId());
                        }
                    }
                });
                //编辑按钮
                tv_btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转activity
                        getSupportDelegate().start(EditAddDelegate.newInstance(
                                t.getId()+"",
                                t.getContact_name(),
                                t.getMobile(),
                                t.getZipcode()));
                        //触发隐藏编辑部分
                        ll_del.performClick();
                    }
                });
                //删除按钮
                tv_btn3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //弹窗确认是否删除
                        showSureCancelPopwindow(t.getId()+"");
                    }
                });
            }
        };
        listview.setAdapter(addressListAdapter);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        ll_del.setOnClickListener(this);
        tvNew.setOnClickListener(this);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isDel) {
                    //跳转activity
                    getSupportDelegate().start(EditAddDelegate.newInstance(
                            addresslistData.get(position).getId()+"",
                            addresslistData.get(position).getContact_name(),
                            addresslistData.get(position).getMobile(),
                            addresslistData.get(position).getZipcode()
                    ));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) {
            pop();
        }else if (v==ll_del) {
            addressListAdapter.notifyDataSetChanged();
            if (isDel) {
                iv_del.setVisibility(View.VISIBLE);
                tv_over.setVisibility(View.GONE);
            }else {
                iv_del.setVisibility(View.GONE);
                tv_over.setVisibility(View.VISIBLE);
            }
            isDel = !isDel;
        }else if (v==tvNew) {
            getSupportDelegate().start(new NewAddressDelegate());
        }
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        isDel = false;
        iv_del.setVisibility(View.VISIBLE);
        tv_over.setVisibility(View.GONE);
        loadProcess();
        getAddressData();
    }

    private void showSureCancelPopwindow(final String id) { //展示是否确认删除弹窗
        View parent = ((ViewGroup) _mActivity.findViewById(android.R.id.content)).getChildAt(0);
        View popView = View.inflate(_mActivity, R.layout.pop_sure_cancel, null);
        TextView et_name = (TextView) popView.findViewById(R.id.et_name);
        et_name.setText("是否确认删除？");
        Button btn_cancel = (Button) popView.findViewById(R.id.btn_cancel);
        Button btn_ok = (Button) popView.findViewById(R.id.btn_ok);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        final PopupWindow popWindow = new PopupWindow(popView, width, height);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);// 设置同意在外点击消失
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_cancel:
                        break;
                    case R.id.btn_ok:
                        loadProcess();
                        delAddressData(id);
                        break;
                }
                popWindow.dismiss();
            }
        };
        btn_cancel.setOnClickListener(listener);
        btn_ok.setOnClickListener(listener);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        popWindow.setBackgroundDrawable(dw);
        popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    private void getAddressData() {
        String url = Config.ADDRESS_INFO;
        String apiName = "收货地址接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("page", 1 + "");
        addParams.put("size", 100 + "");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
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
            if (parseAdrJson(result).getData() != null) {
                if (parseAdrJson(result).getData().size()>0) {
                    llEmptyAddress.setVisibility(View.GONE);
                    listview.setVisibility(View.VISIBLE);
                    addresslistData = parseAdrJson(result).getData();
                    addressListAdapter.refreshDatas(addresslistData);
                }else {
                    llEmptyAddress.setVisibility(View.VISIBLE);
                    listview.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {}
    }

    private AddressBean parseAdrJson(String result) {
        return new Gson().fromJson(result, AddressBean.class);
    }

    private void setMoRenAddressData(int id) {
        String url = Config.SET_MOREN_ADDRESS + id + "/set_as_default";
        String apiName = "设置默认地址接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("aaa",id+"");
        addParams.put("time",System.currentTimeMillis() + "");
        NetConnectionNew.patch(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processMoRenAddressData(arg0);
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

    private void processMoRenAddressData(String arg0) {
        try {
            JSONObject json = new JSONObject(arg0);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            loadProcess();
            getAddressData();
        } catch (Exception e) {
            loadProcess();
            getAddressData();
        }
    }

    private void delAddressData(String id) {
        String url = Config.SET_MOREN_ADDRESS + id;
        String apiName = "删除地址接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("time", System.currentTimeMillis()+"");
        NetConnectionNew.delete(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processDelAddressData(arg0);
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

    private void processDelAddressData(String arg0) {
        try {
            JSONObject json = new JSONObject(arg0);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            show("删除成功");
            loadProcess();
            getAddressData();
        } catch (Exception e) {
            show("删除成功");
            loadProcess();
            getAddressData();
        }
    }
}
