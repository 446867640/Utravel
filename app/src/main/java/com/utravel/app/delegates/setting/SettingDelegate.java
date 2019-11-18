package com.utravel.app.delegates.setting;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.delegates.login.LoginChoiceDelegate;
import com.utravel.app.delegates.main.MainDelegate;
import com.utravel.app.dialog.NameDialog;
import com.utravel.app.dialog.PhotoDialog;
import com.utravel.app.entity.MemeberEntity;
import com.utravel.app.latte.Latte;
import com.utravel.app.utils.GlideCircleTransform;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import com.kyleduo.switchbutton.SwitchButton;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.yokeyword.eventbusactivityscope.EventBusActivityScope;
import okhttp3.Call;

public class SettingDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
    private SwitchButton sb_xiaoxi;
    private AppCompatImageView iv_back, ivTouxiang;
    private AppCompatTextView tv_title, tv_nicheng, tvTaobao, tvWeixin, tvHuancen, tvExit;
    private LinearLayoutCompat llTouxiang, llNicheng, llTaobao, llWeixin, llPhone, llMima, llXiaoxi, llHuancun;

    private MemeberEntity.DataBean memeberData;
    String mobile = null;
    int gender = 0;
    int district_id = 0;
    int avatar_id;
    String avatar_url;

    public static SettingDelegate newInstance(){
        final Bundle args = new Bundle();
        final SettingDelegate delegate = new SettingDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    public void setPicData(String type, Intent data) {
        SharedPreferencesUtil.removeKey(Latte.getApplicationContext(),"PHOTO");
        if (type.equals("take_photo")) { //拍照
            LatteLogger.e("进入拍照"+data.getExtras(), "进入拍照"+data.getExtras());
            Bundle bundle = data.getExtras();
            uploadimg(Util.getCameraImage(bundle));
        }else if (type.equals("pick_photo")) { //相册
//            LatteLogger.e("进入相册"+data.getData(), "进入相册"+data.getData());
            try {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    uploadimg(urlToStr(imageUri));
                }
            } catch (Exception e) {}
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBusActivityScope.getDefault(_mActivity).register(this);
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_setting;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewParams();
        initListener();
    }

    private void initViews(View rootView) {
        status_bar = (View) rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView)rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);
        tv_title.setText("设置");
        llTouxiang = (LinearLayoutCompat)rootView.findViewById( R.id.ll_touxiang );
        ivTouxiang = (AppCompatImageView)rootView.findViewById( R.id.iv_touxiang );
        llNicheng = (LinearLayoutCompat)rootView.findViewById( R.id.ll_nicheng );
        tv_nicheng = (AppCompatTextView) rootView.findViewById( R.id.tv_nicheng );
        llTaobao = (LinearLayoutCompat)rootView.findViewById( R.id.ll_taobao );
        tvTaobao = (AppCompatTextView)rootView.findViewById( R.id.tv_taobao );
        llWeixin = (LinearLayoutCompat)rootView.findViewById( R.id.ll_weixin );
        tvWeixin = (AppCompatTextView)rootView.findViewById( R.id.tv_weixin );
        llPhone = (LinearLayoutCompat)rootView.findViewById( R.id.ll_phone );
        llMima = (LinearLayoutCompat)rootView.findViewById( R.id.ll_mima );
        llXiaoxi = (LinearLayoutCompat)rootView.findViewById( R.id.ll_xiaoxi );
        sb_xiaoxi = (SwitchButton) rootView.findViewById( R.id.sb_xiaoxi );
        llHuancun = (LinearLayoutCompat)rootView.findViewById( R.id.ll_huancun );
        tvHuancen = (AppCompatTextView)rootView.findViewById( R.id.tv_huancen );
        tvExit = (AppCompatTextView)rootView.findViewById( R.id.tv_exit );
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        llTouxiang.setOnClickListener(this);
        llNicheng.setOnClickListener(this);
        llTaobao.setOnClickListener(this);
        llWeixin.setOnClickListener(this);
        llPhone.setOnClickListener(this);
        llMima.setOnClickListener(this);
        tvHuancen.setOnClickListener(this);
        tvExit.setOnClickListener(this);
        sb_xiaoxi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(_mActivity,"打开",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(_mActivity,"关闭",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initViewParams() {
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        getMemeberData();
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) {//返回
            pop();
        }else if (v==llTouxiang) {//设置头像
            PhotoDialog mPhotoDialog = PhotoDialog.newInstance();
            mPhotoDialog.show(getFragmentManager(), getClass().getSimpleName());
        }else if (v==llNicheng) {//设置昵称
            NameDialog mNameDialog = NameDialog.newInstance();
            mNameDialog.show(getFragmentManager(), getClass().getSimpleName());
        }else if (v==llTaobao) {//淘宝授权

        }else if (v==llWeixin) {//微信绑定

        }else if (v==llPhone) {//修改手机号
            if (mobile != null) {
                getSupportDelegate().start(ChangePhoneOneDelegate.newInstance(mobile));
            }
        }else if (v==llMima) {//修改密码
            getSupportDelegate().start(new ChangeLoginMMDelegate());
        }else if (v==tvHuancen) {//清除缓存

        }else if (v==tvExit) {//退出登陆
            loadProcess();
            delExitLogin();
        }
    }

    @Override
    public boolean setIsDark() {
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBusActivityScope.getDefault(_mActivity).unregister(this);
    }

    @Subscribe
    public void getEvent(String event) {//方法名随意，反正不调用
        if (!event.isEmpty()) {
            patchMemeberData(event);
        }
    }

    public String urlToStr(Uri uri) {
        String path = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            Cursor cursor = _mActivity.getContentResolver().query(
                    uri, new String[] { MediaStore.Images.Media.DATA }, null,
                    null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
            return path;
        } else {
            path = uri.getPath();
            return path;
        }
    }

    private void patchMemeberData(String name) {
        String url = Config.REGISTER_URL; //接口url
        String apiName = "修改个人信息接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Content-Type", "application/x-www-form-urlencoded");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("name", name);
        addParams.put("gender", gender + "");
        addParams.put("district_id", district_id + "");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.patch(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processPatchMemeberData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            getSupportDelegate().startWithPop(new LoginChoiceDelegate());
                            SharedPreferencesUtil.clearlogin(getContext());
                        }
                    }
                }
            });
    }

    private void processPatchMemeberData (String arg0){
        JSONObject json = JSON.parseObject(arg0);
        if (json.containsKey("error")) {
            show(json.getString("error"));
            return;
        }
        String name = json.getJSONObject("data").getString("name");
        tv_nicheng.setText(name);
    }

    private void getMemeberData() {
        String url = Config.REGISTER_URL; //接口url
        String apiName = "查看用户信息接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processMemeberData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            show(Config.ERROR401);
                            SharedPreferencesUtil.clearlogin(getContext());
                            ((MainDelegate)getParentFragment()).getSupportDelegate().startWithPop(new LoginChoiceDelegate());
                        }
                    }
                }
            });
    }

    private void processMemeberData (String arg0){
        JSONObject json = JSON.parseObject(arg0);
        if (json.containsKey("error")) {
            show(json.getString("error"));
            return;
        }
        memeberData = parseMemeberData(arg0).getData();
        String avatar = memeberData.getAvatar()==null? null:memeberData.getAvatar().getUrl();
        String name = memeberData.getName();
        mobile = memeberData.getMobile();
        //头像
        Glide.with(getContext())
            .load(avatar)
            .error(R.mipmap.touxiang_gray)
            .transform(new GlideCircleTransform(getContext()))
            .into(ivTouxiang);
        //昵称
        if (name != null) {
            tv_nicheng.setText(name);
        }
    }

    private MemeberEntity parseMemeberData(String arg0) {
        return new Gson().fromJson(arg0, MemeberEntity.class);
    }

    private void delExitLogin() {
        String url = Config.AUTH_TOKEN; //接口url
        String apiName = ""; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.delete(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    JSONObject json = JSON.parseObject(arg0);
                    if (json!=null && json.containsKey("error")) {
                        show(json.getString("error"));
                        return;
                    }
                    SharedPreferencesUtil.clearlogin(getContext());
                    getSupportDelegate().startWithPop(new LoginChoiceDelegate());
                    show("退出登录成功");
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            show(Config.ERROR401);
                            SharedPreferencesUtil.clearlogin(getContext());
                            getSupportDelegate().startWithPop(new LoginChoiceDelegate());
                        }
                    }
                }
            });
    }

    private void uploadimg(final String filePath) {
        String url = Config.UPLOAD_IMG_URL;
        String apiName = "上传图片接口";
        String interfaceName = "file";
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        List<String> filePaths = new ArrayList<>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("type", "avatar");
        addParams.put("time", System.currentTimeMillis() + "");
        filePaths.add(filePath);
        NetConnectionNew.postUpLoad(apiName,getContext(),url,addHeader,addParams,filePaths,interfaceName,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String result, int arg1) {
                    parseUploadImg(result);
                }
            }, new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            show(Config.ERROR401);
                            SharedPreferencesUtil.clearlogin(getContext());
                            getSupportDelegate().startWithPop(new LoginChoiceDelegate());
                        }
                    }
                }
            });
    }

    private void parseUploadImg(String result) {
        JSONObject json = JSON.parseObject(result);
        if (json!=null && json.containsKey("error")) {
            if (json.containsKey("error")) {
                show(json.getString("error"));
                return;
            }
            JSONObject data = json.getJSONObject("data");
            if (data.getInteger("id")!=0) {
                avatar_id = data.getInteger("id");
                if (Config.BASE.equals("https://staging.wanteyun.com/api/v1/")) {
                    LatteLogger.e("图片id", avatar_id+"");
                }
            }
            if (data.getString("url")!=null) {
                avatar_url = data.getString("url");
                Glide.with(getContext())
                        .load(avatar_url)
                        .transform(new GlideCircleTransform(getContext()))
                        .into(ivTouxiang);
            }
            putChangeAvatar(avatar_id);
        }
    }

    public void putChangeAvatar(int id){
        String url = Config.AVATAR; //请求接口url
        String apiName = "单独保存图片接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addHeader.put("Content-Type", "application/x-www-form-urlencoded");
        addParams.put("avatar_id", id + "");
        addParams.put("time", System.currentTimeMillis() + "");

        NetConnectionNew.put(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    JSONObject json = JSON.parseObject(arg0);
                    if (json!=null ) {
                        if (json.containsKey("error") ) {
                            show(json.getString("error"));
                            return;
                        }
                        show("修改成功");
                    }else {
                        show("修改成功");
                    }
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            show(Config.ERROR401);
                            SharedPreferencesUtil.clearlogin(getContext());
                            getSupportDelegate().startWithPop(new LoginChoiceDelegate());
                        }
                    }
                }
            });
    }
}
