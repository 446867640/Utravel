package com.utravel.app.delegates.main.shequ;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.main.MainDelegate;
import com.utravel.app.delegates.main.OnBackDelegate;
import com.utravel.app.delegates.main.my.SharePicDelegate;
import com.utravel.app.delegates.search.HomeSearchDelegate;
import com.utravel.app.entity.TreeNodesBean1;
import com.utravel.app.entity.TreesGoodsListBean;
import com.utravel.app.entity.YaoQingWoderenBean;
import com.utravel.app.utils.GlideCircleTransform;
import com.utravel.app.utils.GlideRoundTransform;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;

public class ShequDelegate extends OnBackDelegate implements View.OnClickListener {
    private ImageView iv_bg;
    private ImageView iv_myyaoqing, iv_yaoqingren, iv_share, iv_change_goods, iv_shopgoods;
    private ImageView iv_person1, iv_person2, iv_person3, iv_person4, iv_person5, iv_person6, iv_person7;
    private ImageView iv_person_bg1, iv_person_bg2, iv_person_bg3, iv_person_bg4, iv_person_bg5, iv_person_bg6, iv_person_bg7;
    private TextView tv_phone1, tv_phone2, tv_phone3, tv_phone4, tv_phone5, tv_phone6, tv_phone7;

    private List<ImageView> iv_avators = new ArrayList<ImageView>();
    private List<ImageView> iv_backgrounds = new ArrayList<ImageView>();
    private List<TextView> tv_phones = new ArrayList<TextView>();

    private YaoQingWoderenBean.DataBean parentData;
    private TreeNodesBean1.DataBean treeNodesBean;
    private List<TreesGoodsListBean.DataBean> listGoodsData;
    private List<TreeNodesBean1.DataBean.ChildrenBeanX> children23;
    private List<TreeNodesBean1.DataBean.ChildrenBeanX.ChildrenBean> children45;
    private List<TreeNodesBean1.DataBean.ChildrenBeanX.ChildrenBean> children67;

    private int size23=-1;
    private int size45=-1;
    private int size67=-1;
    private int goods_id = -1;

    @Override
    public Object setLayout() { return R.layout.delegate_dianzhu; }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        Util.setStatusBarMode(_mActivity,true);
        if (SharedPreferencesUtil.getBoolean(getContext(), Config.IS_INIT_TREE_ID)) {
            goods_id = -1;
            SharedPreferencesUtil.putBoolean(getContext(), Config.IS_INIT_TREE_ID, false);
        }
        setAllGone();
        loadProcess();
        getTreesGoodsListData();
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        iniViewsParmas();
        initListener();
    }

    private void initViews(View view) {
        iv_bg = (ImageView) view.findViewById(R.id.iv_bg);
        iv_myyaoqing = (ImageView) view.findViewById(R.id.iv_myyaoqing);
        iv_yaoqingren = (ImageView) view.findViewById(R.id.iv_yaoqingren);
        iv_share = (ImageView) view.findViewById(R.id.iv_share);
        iv_change_goods = (ImageView) view.findViewById(R.id.iv_change_goods);
        iv_shopgoods = (ImageView) view.findViewById(R.id.iv_shopgoods);
        iv_person1 = (ImageView) view.findViewById(R.id.iv_person1);
        iv_person2 = (ImageView) view.findViewById(R.id.iv_person2);
        iv_person3 = (ImageView) view.findViewById(R.id.iv_person3);
        iv_person4 = (ImageView) view.findViewById(R.id.iv_person4);
        iv_person5 = (ImageView) view.findViewById(R.id.iv_person5);
        iv_person6 = (ImageView) view.findViewById(R.id.iv_person6);
        iv_person7 = (ImageView) view.findViewById(R.id.iv_person7);
        iv_person_bg1 = (ImageView) view.findViewById(R.id.iv_person_bg1);
        iv_person_bg2 = (ImageView) view.findViewById(R.id.iv_person_bg2);
        iv_person_bg3 = (ImageView) view.findViewById(R.id.iv_person_bg3);
        iv_person_bg4 = (ImageView) view.findViewById(R.id.iv_person_bg4);
        iv_person_bg5 = (ImageView) view.findViewById(R.id.iv_person_bg5);
        iv_person_bg6 = (ImageView) view.findViewById(R.id.iv_person_bg6);
        iv_person_bg7 = (ImageView) view.findViewById(R.id.iv_person_bg7);
        tv_phone1 = (TextView) view.findViewById(R.id.tv_phone1);
        tv_phone2 = (TextView) view.findViewById(R.id.tv_phone2);
        tv_phone3 = (TextView) view.findViewById(R.id.tv_phone3);
        tv_phone4 = (TextView) view.findViewById(R.id.tv_phone4);
        tv_phone5 = (TextView) view.findViewById(R.id.tv_phone5);
        tv_phone6 = (TextView) view.findViewById(R.id.tv_phone6);
        tv_phone7 = (TextView) view.findViewById(R.id.tv_phone7);
    }

    private void iniViewsParmas() {
        //头像
        iv_avators.add(iv_person1);
        iv_avators.add(iv_person2);
        iv_avators.add(iv_person3);
        iv_avators.add(iv_person4);
        iv_avators.add(iv_person5);
        iv_avators.add(iv_person6);
        iv_avators.add(iv_person7);
        //背景
        iv_backgrounds.add(iv_person_bg1);
        iv_backgrounds.add(iv_person_bg2);
        iv_backgrounds.add(iv_person_bg3);
        iv_backgrounds.add(iv_person_bg4);
        iv_backgrounds.add(iv_person_bg5);
        iv_backgrounds.add(iv_person_bg6);
        iv_backgrounds.add(iv_person_bg7);
        //手机号
        tv_phones.add(tv_phone1);
        tv_phones.add(tv_phone2);
        tv_phones.add(tv_phone3);
        tv_phones.add(tv_phone4);
        tv_phones.add(tv_phone5);
        tv_phones.add(tv_phone6);
        tv_phones.add(tv_phone7);
        //店主背景
        Glide.with(getContext())
                .load(R.drawable.dianzhubg)
                .asGif().dontAnimate() //去掉显示动画
                .diskCacheStrategy(DiskCacheStrategy.SOURCE) //DiskCacheStrategy.NONE
                .into(iv_bg);
    }

    private void initListener() {
        for (int i = 0; i < iv_avators.size(); i++) {
            iv_avators.get(i).setOnClickListener(this);
        }
        iv_myyaoqing.setOnClickListener(this);
        iv_yaoqingren.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        iv_change_goods.setOnClickListener(this);
        iv_shopgoods.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!Util.isToken(getContext())) {
            showMsg401();
            goods_id=-1;
            return;
        }
        if (v==iv_yaoqingren) { //查看我的邀请人
            if (goods_id!=-1) {
                loadProcess();
                getMyParentData();
            }else {
                show(getResources().getString(R.string.dz_no_goods_change));
            }
        }else if (v==iv_myyaoqing) { //我的邀请
            if (goods_id!=-1) {
                ((MainDelegate)getParentFragment()).getSupportDelegate().start(new MyYaoQingDelegate());
            }else {
                show(getResources().getString(R.string.dz_please_buy));
            }
        }else if (v==iv_person1) { //自己立即激活
            ((MainDelegate)getParentFragment()).getSupportDelegate().start(new HomeSearchDelegate());
        }else if (v==iv_person2) { //立即邀请
            if (goods_id!=-1) {
                ((MainDelegate)getParentFragment()).getSupportDelegate().start(SharePicDelegate.newInstance(goods_id+""));
            }else {
                show(getResources().getString(R.string.dz_please_buy));

            }
        }else if (v==iv_person3) { //立即邀请
            iv_person2.performClick();
        }else if (v==iv_person4) { //立即邀请
            iv_person2.performClick();
        }else if (v==iv_person5) { //立即邀请
            iv_person2.performClick();
        }else if (v==iv_person6) { //立即邀请
            iv_person2.performClick();
        }else if (v==iv_person7) { //立即邀请
            iv_person2.performClick();
        }else if (v==iv_share) { //分享图标
            iv_person2.performClick();
        }else if (v==iv_change_goods) {
            if (listGoodsData!=null && listGoodsData.size()>0) {
                showGoodsListPopwindow(listGoodsData);
            }else {
                show(getResources().getString(R.string.dz_no_goods_change));
            }
        }
    }

    private void showYaoQingRenPopwindow(YaoQingWoderenBean.DataBean bean) {
        View parent = ((ViewGroup) ((Activity) _mActivity).findViewById(android.R.id.content)).getChildAt(0);
        View popView = View.inflate(getContext(), R.layout.pop_wodeyaoqingren, null);
        final ImageView iv1 = (ImageView) popView.findViewById(R.id.iv1);
        final TextView tv1 = (TextView) popView.findViewById(R.id.tv1);
        final TextView tv2 = (TextView) popView.findViewById(R.id.tv2);
        if (bean!=null) {
            if (!TextUtils.isEmpty(bean.getMobile())) {
                tv1.setText("账号：" + bean.getMobile());
            }
            if (bean.getAvatar()!=null && !TextUtils.isEmpty(bean.getAvatar().getUrl())) {
                Glide.with(getContext())
                        .load(bean.getAvatar().getUrl())
                        .transform(new GlideCircleTransform(getContext()))
                        .into(iv1);
            }
        }
        final TextView tv_cancel = (TextView) popView.findViewById(R.id.tv_cancel);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        final PopupWindow popWindow = new PopupWindow(popView, width, height);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);// 设置同意在外点击消失
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                if (v==tv_cancel) {
                    popWindow.dismiss();
                }
            }
        };
        tv_cancel.setOnClickListener(listener);
        ColorDrawable dw = new ColorDrawable(0x50000000);
        popWindow.setBackgroundDrawable(dw);
        popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    private void showGoodsListPopwindow(final List<TreesGoodsListBean.DataBean> listData) {
        View parent = ((ViewGroup) ((Activity) _mActivity).findViewById(android.R.id.content)).getChildAt(0);
        View popView = View.inflate(getContext(), R.layout.pop_tree_goodslist, null);
        final TextView tv_cancel = (TextView) popView.findViewById(R.id.tv_cancel);
        final LinearLayout ll_pop = (LinearLayout) popView.findViewById(R.id.ll_pop);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        params.width = screenWidth;
        params.height = screenHeight*3/5;
        ll_pop.setLayoutParams(params);
        final GridView gv_goods = (GridView) popView.findViewById(R.id.gv_goods);

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        gv_goods.setAdapter(new CommonAdapter<TreesGoodsListBean.DataBean>(getContext(), listData, R.layout.item_tree_goodslist) {
            @Override
            public void convert(final BaseViewHolder holder, TreesGoodsListBean.DataBean t) {
                holder.setText(R.id.tv_name, t.getName());
                holder.setText(R.id.tv_price, "¥" + t.getPrice());
                final ImageView iv1 = holder.getView(R.id.iv_avartor);
                Glide.with(getContext())
                        .load(t.getImage_url())
                        .transform(new GlideRoundTransform(getContext(),5))
                        .error(R.mipmap.ic_launcher)
                        .into(new SimpleTarget<GlideDrawable>() { // 加上这段代码 可以解决
                            @Override
                            public void onResourceReady(GlideDrawable arg0, GlideAnimation<? super GlideDrawable> arg1) {
                                iv1.setImageDrawable(arg0); //显示图片
                            }
                        });
            }
        });
        final PopupWindow popWindow = new PopupWindow(popView, width, height);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);// 设置同意在外点击消失
        gv_goods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popWindow.dismiss();
                goods_id = listGoodsData.get(position).getId();
                Glide.with(getContext())
                        .load(listData.get(position).getImage_url())
                        .transform(new GlideRoundTransform(getContext(),5))
                        .into(new SimpleTarget<GlideDrawable>() { // 加上这段代码 可以解决
                            @Override
                            public void onResourceReady(GlideDrawable arg0, GlideAnimation<? super GlideDrawable> arg1) {
                                iv_shopgoods.setImageDrawable(arg0); //显示图片
                            }
                        });
                setAllGone();
                loadProcess();
                getTreeNodesData();
            }
        });
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                if (v==tv_cancel) {
                    popWindow.dismiss();
                }
            }
        };
        tv_cancel.setOnClickListener(listener);
        ColorDrawable dw = new ColorDrawable(0x50000000);
        popWindow.setBackgroundDrawable(dw);
        popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    private void getTreeNodesData(){
        String url = Config.TREES_ID + goods_id; //接口url
        String apiName = "七人组树节点接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("id", goods_id + "");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processTreeNodesData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            showMsg401();
                            goods_id=-1;
                        }else if (arg1.getMessage().contains("404")) {
                            dismissLoadProcess();
                            if (arg1.getMessage()!=null) {
                                if (arg1.getMessage().contains("401")) {
                                    showMsg401();
                                    goods_id=-1;
                                }else if (arg1.getMessage().contains("404")) {
                                    //自己未激活
                                    setAllGone();
                                }
                            }
                        }
                    }
                }
            });
    }

    private void processTreeNodesData(String arg0) {
        try {
            JSONObject json = new JSONObject(arg0);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            treeNodesBean = parseTreeNodesData(arg0).getData();
            String mobileStr = null;
            String imageUrlStr = null;
            if (treeNodesBean!=null && treeNodesBean.getMember_mobile()!=null) {
                mobileStr = treeNodesBean.getMember_mobile();
            }
            if (treeNodesBean!=null && treeNodesBean.getAvatar()!=null && treeNodesBean.getAvatar().getUrl()!=null) {
                imageUrlStr = treeNodesBean.getAvatar().getUrl();
            }
            if (mobileStr!=null && imageUrlStr!=null) {
                setSevenShow(imageUrlStr,mobileStr);
            }
        } catch (Exception e) {}
    }

    private TreeNodesBean1 parseTreeNodesData(String arg0) {
        return new Gson().fromJson(arg0, TreeNodesBean1.class);
    }

    private void setSevenShow(String imageUrl, String mobile) {
        //设置自己已开通
        tv_phones.get(0).setText(mobile); //电话号码
        iv_avators.get(0).setEnabled(false); //能否点击
        Glide.with(getContext())
                .load(imageUrl) //头像
                .transform(new GlideCircleTransform(getContext()))
                .into(iv_avators.get(0));
        iv_backgrounds.get(0).setImageResource(R.drawable.ziquan); //背景框
        //其余
        children23 = treeNodesBean.getChildren();
        size23 = children23.size();
        if (size23==0) { //6个立即邀请
            setLiJiKaiTong(1);
        }else if(size23==1) { //2个已开通，5个立即邀请
            //2已开通
            setYiJingKaiTong(1);
            //3,6,7是立即邀请
            setLiJiKaiTong(2);
            setLiJiKaiTong(5);
            setLiJiKaiTong(6);
            //45情况
            children45 = children23.get(0).getChildren();
            size45 = children45.size();
            if (size45==0) {//4,5立即邀请
                setLiJiKaiTong(3);
                setLiJiKaiTong(4);
            }else if (size45==1) {  //4已开通，5立即邀请
                setYiJingKaiTong(3);
                setLiJiKaiTong(4);
            }else if (size45==2) { //4,5已开通
                setYiJingKaiTong(3);
                setYiJingKaiTong(4);
            }
        }else if(size23==2) {
            //2,3已开通
            setYiJingKaiTong(1);
            setYiJingKaiTong(2);
            //45
            children45 = children23.get(0).getChildren();
            size45 = children45.size();
            if (size45==0) {//4,5个立即邀请
                setLiJiKaiTong(3);
                setLiJiKaiTong(4);
            }else if (size45==1) {  //4已开通，5个立即邀请
                setYiJingKaiTong(3);
                setLiJiKaiTong(4);
            }else if (size45==2) { //4,5已开通
                setYiJingKaiTong(3);
                setYiJingKaiTong(4);
            }
            //67
            children67 = children23.get(1).getChildren();
            size67 = children67.size();
            if (size67==0) { //6,7立即邀请
                setLiJiKaiTong(5);
                setLiJiKaiTong(6);
            }else if(size67==1){ //6已开通，7立即邀请
                setYiJingKaiTong(5);
                setLiJiKaiTong(6);
            }else if(size67==2){  //6，7已开通
                setYiJingKaiTong(5);
                setYiJingKaiTong(6);
            }
        }
    }

    private void setLiJiKaiTong(int position) { //立即邀请
        tv_phones.get(position).setText(getResources().getString(R.string.lijiyaoqing));//电话号码
        iv_avators.get(position).setEnabled(true); //能否点击
        iv_avators.get(position).setImageResource(R.drawable.dianzhu_share);  //头像
        iv_backgrounds.get(position).setImageResource(R.drawable.ziquan);  //背景框
    }

    private void setYiJingKaiTong(int i) { //已开通
        String inviter = null;
        String imageUrl = null;
        if (i==1) {
            inviter = children23.get(0).getInviter();
            imageUrl = children23.get(0).getAvatar().getUrl();
            tv_phones.get(i).setText(treeNodesBean.getChildren().get(0).getMember_mobile());
        }else if (i==2) {
            inviter = children23.get(1).getInviter();
            imageUrl = children23.get(1).getAvatar().getUrl();
            tv_phones.get(i).setText(treeNodesBean.getChildren().get(1).getMember_mobile());
        }else if (i==3) {
            inviter = children45.get(0).getInviter();
            imageUrl = children45.get(0).getAvatar().getUrl();
            tv_phones.get(i).setText(treeNodesBean.getChildren().get(0).getChildren().get(0).getMember_mobile());
        }else if (i==4) {
            inviter = children45.get(1).getInviter();
            imageUrl = children45.get(1).getAvatar().getUrl();
            tv_phones.get(i).setText(treeNodesBean.getChildren().get(0).getChildren().get(1).getMember_mobile());
        }else if (i==5) {
            inviter = children67.get(0).getInviter();
            imageUrl = children67.get(0).getAvatar().getUrl();
            tv_phones.get(i).setText(treeNodesBean.getChildren().get(1).getChildren().get(0).getMember_mobile());
        }else if (i==6) {
            inviter = children67.get(1).getInviter();
            imageUrl = children67.get(1).getAvatar().getUrl();
            tv_phones.get(i).setText(treeNodesBean.getChildren().get(1).getChildren().get(1).getMember_mobile());
        }
        if (inviter==null) {
            iv_backgrounds.get(i).setImageResource(R.drawable.lanquan);
        }else {
            if(inviter.equals("root")) {
                iv_backgrounds.get(i).setImageResource(R.drawable.hongquan);
            }else if(inviter.equals("children")) {
                iv_backgrounds.get(i).setImageResource(R.drawable.chengseyh);
            }
        }
        Glide.with(getContext())
                .load(imageUrl) //头像
                .transform(new GlideCircleTransform(getContext()))
                .into(iv_avators.get(i));
        iv_avators.get(i).setEnabled(false);
    }

    private void setAllGone() {
        for (int i = 0; i < iv_backgrounds.size(); i++) {
            if (i==0) {
                tv_phones.get(i).setText(getResources().getString(R.string.lijikaitong)); //电话号码
            }else {
                tv_phones.get(i).setText(getResources().getString(R.string.lijiyaoqing));//电话号码
            }
            iv_avators.get(i).setImageResource(R.drawable.dianzhu_share);//头像
            iv_avators.get(i).setEnabled(true); //能否点击
            iv_backgrounds.get(i).setImageResource(R.drawable.ziquan); //背景框
        }
    }

    private void getMyParentData() {
        String url = Config.TREES_ID + goods_id + "/inviter"; //接口url
        String apiName = "邀请我的人信息接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("id", goods_id + "");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processMyParentData(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            showMsg401();
                            goods_id=-1;
                        }else if (arg1.getMessage().contains("404")){
                            show(Config.YAOQINF_CODE_ERROR404);
                        }
                    }
                }
            });
    }

    private void processMyParentData(String arg0) {
        try {
            JSONObject json = new JSONObject(arg0);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            parentData = parseMyParentData(arg0).getData();
            if (parentData!=null) {
                showYaoQingRenPopwindow(parentData);
            }else {
                show("没有邀请人");
            }
        } catch (Exception e) {}
    }

    private YaoQingWoderenBean parseMyParentData(String arg0) {
        return new Gson().fromJson(arg0, YaoQingWoderenBean.class);
    }

    private void getTreesGoodsListData() {
        String url = Config.TREES; //请求接口url
        String apiName = "树状图商品列表接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, getContext(), url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String result, int arg1) {
                    dismissLoadProcess();
                    processTreesGoodsListData(result);
                }
            },
            new NetConnectionNew.FailCallback(){
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            showMsg401();
                            goods_id=-1;
                        }
                    }
                }
            });
    }

    private void processTreesGoodsListData(String result) {
        try {
            JSONObject json = new JSONObject(result);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            listGoodsData = parseTreesGoodsListData(result).getData();
            if (listGoodsData.size()>0) {
                if (goods_id == -1) {
                    goods_id = listGoodsData.get(0).getId();
                    Glide.with(getContext())
                            .load(listGoodsData.get(0).getImage_url())
                            .transform(new GlideRoundTransform(getContext(),5))
                            .into(new SimpleTarget<GlideDrawable>() { // 加上这段代码 可以解决
                                @Override
                                public void onResourceReady(GlideDrawable arg0, GlideAnimation<? super GlideDrawable> arg1) {
                                    iv_shopgoods.setImageDrawable(arg0); //显示图片
                                }
                            });
                }
                getTreeNodesData();
            }else {
                //自己未激活
                setAllGone();
                iv_shopgoods.setVisibility(View.GONE);
            }
        } catch (JSONException e) {}
    }

    private TreesGoodsListBean parseTreesGoodsListData(String result) {
        return new Gson().fromJson(result, TreesGoodsListBean.class);
    }
}
