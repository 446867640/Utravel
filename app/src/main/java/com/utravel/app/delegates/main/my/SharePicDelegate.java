package com.utravel.app.delegates.main.my;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.utravel.app.R;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.delegates.login.LoginChoiceDelegate;
import com.utravel.app.delegates.main.MainDelegate;
import com.utravel.app.delegates.news.NewsChildDelegate2;
import com.utravel.app.entity.ImgTvBean;
import com.utravel.app.ui.MyGridView;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import com.utravel.app.utils.WXShareUtil;
import com.utravel.app.utils.ZXingUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;

public class SharePicDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private View status_bar;
    private AppCompatImageView iv_back;
    private AppCompatTextView tv_title;
    private RelativeLayout rl_img;
    private ImageView iv_img;
    private ImageView iv_erweima;
    private TextView tv_tuijianma;
    private MyGridView gv_share;

    private List<ImgTvBean> shareDatas = new ArrayList<ImgTvBean>();
    private CommonAdapter<ImgTvBean> shareAdapter;

    private WXShareUtil wxShare = new WXShareUtil(getContext());
    private ClipboardManager cm;// 剪切板

    private static final String BUNDLE_ID = "BUNDLE_ID";
    private String shareUrl = null;
    private String tree_id = null;

    public static SharePicDelegate newInstance(String id) {
        SharePicDelegate fragment = new SharePicDelegate();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            tree_id = bundle.getString(BUNDLE_ID);
        }
    }

    @Override
    public boolean setIsDark() { return true; }

    @Override
    public Object setLayout() { return R.layout.delegate_share_pic; }

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
        getSharaIconData();
        if (tree_id!=null) {
            loadProcess();
            getYaoQingUrl();
        }else {
            show("error tree_id");
        }
    }

    private void initViews(View rootView) {
        status_bar = (View) this.rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView) this.rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);
        rl_img = (RelativeLayout) rootView.findViewById(R.id.rl_img);
        iv_img = (ImageView) rootView.findViewById(R.id.iv_img);
        iv_erweima = (ImageView) rootView.findViewById(R.id.iv_erweima);
        gv_share = (MyGridView) rootView.findViewById(R.id.gv_share);
        tv_tuijianma = (TextView) rootView.findViewById(R.id.tv_tuijianma);
    }

    private void initViewsParams() {
        tv_title.setText(getResources().getString(R.string.yaoqingzhuan));

        cm = (ClipboardManager) _mActivity.getSystemService(Context.CLIPBOARD_SERVICE);

        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);

        // 图片外层
        LinearLayout.LayoutParams rliv_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rliv_params.width = (screenWidth - DensityUtil.dp2px(getContext(), 100)); // 495*800
        rliv_params.height = (screenWidth - DensityUtil.dp2px(getContext(), 100)) * 800 / 495;
        rl_img.setLayoutParams(rliv_params);
        // 内层主图
        RelativeLayout.LayoutParams iv_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        iv_params.width = (screenWidth - DensityUtil.dp2px(getContext(), 100)); // 495*800
        iv_params.height = (screenWidth - DensityUtil.dp2px(getContext(), 100)) * 800 / 495;
        iv_params.addRule(RelativeLayout.CENTER_IN_PARENT);
        iv_img.setLayoutParams(iv_params);
        // 内层二维码
        RelativeLayout.LayoutParams iv_erweima_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        iv_erweima_params.width = (screenWidth - DensityUtil.dp2px(getContext(), 100)) * 166 / 495;
        iv_erweima_params.height = (screenWidth - DensityUtil.dp2px(getContext(), 100)) * 166 / 495;
        iv_erweima_params.addRule(RelativeLayout.CENTER_IN_PARENT);
        iv_erweima.setLayoutParams(iv_erweima_params);
    }

    private void initAdapter() {
        shareAdapter = new CommonAdapter<ImgTvBean>(getContext(), shareDatas, R.layout.item_share_icon) {
            @Override
            public void convert(BaseViewHolder holder, ImgTvBean t) {
                final ImageView iv_icon = holder.getView(R.id.iv_icon);
                holder.setText(R.id.tv_iconname, t.getName());
                Glide.with(getContext()).load(t.getImageResource()).into(new SimpleTarget<GlideDrawable>() { // 加上这段代码可以解决
                    @Override
                    public void onResourceReady(GlideDrawable arg0, GlideAnimation<? super GlideDrawable> arg1) {
                        iv_icon.setImageDrawable(arg0); // 显示图片
                    }
                });
            }
        };
        gv_share.setAdapter(shareAdapter);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        gv_share.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (shareUrl == null) {
                    show("分享数据异常");
                    return;
                }
                if (shareDatas.get(position).getId() == 2) {// 复制链接
                    cm.setText(shareUrl);// 将文本内容放到系统剪贴板里。
                    show(getResources().getString(R.string.copy_success));
                }else {
                    // 保存截图为bitmap,并保存到本地
                    Bitmap bitmap = Util.getCacheBitmapFromView(rl_img);
                    if (shareDatas.get(position).getId() == 0) {// 微信好友
                        wxShare.sharePicWithBitmap(0,bitmap);
                    } else if (shareDatas.get(position).getId() == 1) {// 朋友圈
                        wxShare.sharePicWithBitmap(1,bitmap);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) { //返回
            pop();
        }
    }

    @Override
    public synchronized void onDestroy() {
        super.onDestroy();
        if (wxShare != null) {
            wxShare.unregister();
        }
    }

    private void twoZxing(String result) {
        int widthAndHeight = (screenWidth - DensityUtil.dp2px(getContext(), 100))  / 2;  //旧的375 / 667
        Bitmap qrcode;
        try {
            qrcode = ZXingUtils.createQRCode(result, widthAndHeight, widthAndHeight);
            iv_erweima.setImageBitmap(qrcode);
            getSharePicData();
        } catch (Exception e) {}
    }

    private void getSharaIconData() {
        shareDatas.add(new ImgTvBean(R.mipmap.sharewxhy, getResources().getString(R.string.wxhy), 0));
        shareDatas.add(new ImgTvBean(R.mipmap.sharewxpyq, getResources().getString(R.string.wxpyq), 1));
        shareDatas.add(new ImgTvBean(R.mipmap.sharelink, getResources().getString(R.string.copy_link), 2));
        gv_share.setNumColumns(shareDatas.size());
        shareAdapter.refreshDatas(shareDatas);
    }

    private void getYaoQingUrl() {
        String url = Config.YAOQINF_URL;
        String apiName = "获取邀请接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Content-Type", "application/x-www-form-urlencoded");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("tree_id", tree_id  + "");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    parseShareUrlData(arg0);
                }
            }, new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    if (arg1.getMessage() != null) {
                        if (arg1.getMessage().contains("401")) {
                            showMsg401();
                        }else if (arg1.getMessage().contains("404")) {
                            show(Config.YAOQINF_CODE_ERROR404);
                        }
                    }
                }
            });
    }

    private void parseShareUrlData(String result) {
        JSONObject json = JSON.parseObject(result);
        if (json != null) {
            if (json.containsKey("error")) {
                show(json.getString("error"));
                return;
            }
            JSONObject data = json.getJSONObject("data");
            shareUrl = data.getString("url");
            if (!TextUtils.isEmpty(shareUrl) && shareUrl.contains("?")) {
                String[] s1 = shareUrl.split("[?]");
                for (int i = 0; i < s1.length; i++) {
                    if (s1[i].contains("=")) {
                        tv_tuijianma.setText("邀请码：" + s1[i].split("[=]")[1]);
                    }
                }
            }
            // 删除缓存图片
            if (wxShare.fileIsExists(Environment.getExternalStorageDirectory() + "/share.png")) {
                wxShare.deleteSingleFile(getContext(), Environment.getExternalStorageDirectory() + "/share.png");
            }
            // 设置二维码
            twoZxing(shareUrl);
        }
    }

    private void getSharePicData() {
        String url = Config.SHARE_PICTURE;
        final String apiName = "二维码分享背景图接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    processSharePicData(arg0);
                    iv_erweima.setVisibility(View.VISIBLE);
                }
            }, new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    if (arg1.getMessage().contains("401")) {
                        showMsg401();
                    }
                }
            });
    }

    private void processSharePicData(String arg0) {
        JSONObject json = JSON.parseObject(arg0);
        if (json != null) {
            if (json.containsKey("error")) {
                show(json.getString("error"));
                return;
            }
            JSONObject data = json.getJSONObject("data");
            String share_image_url = data.getString("image_url");
            Glide.with(getContext()).load(share_image_url).into(new SimpleTarget<GlideDrawable>() { // 加上这段代码
                @Override
                public void onResourceReady(GlideDrawable arg0, GlideAnimation<? super GlideDrawable> arg1) {
                    iv_img.setImageDrawable(arg0); // 显示图片
                    dismissLoadProcess();
                }
            });
        }
    }
}
