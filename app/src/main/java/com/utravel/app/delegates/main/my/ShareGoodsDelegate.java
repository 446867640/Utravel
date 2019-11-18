package com.utravel.app.delegates.main.my;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.utravel.app.R;
import com.utravel.app.adapter.BaseViewHolder;
import com.utravel.app.adapter.CommonAdapter;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.entity.ImgTvBean;
import com.utravel.app.ui.MyGridView;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.Util;
import com.utravel.app.utils.WXShareUtil;
import com.utravel.app.utils.ZXingUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShareGoodsDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {

    private AppCompatImageView iv_back, iv_img, iv_erweima;
    private LinearLayout ll_share_pic;
    private AppCompatTextView tv_goodsname, tv_price, tv_old_price, tv_quan;
    private MyGridView gv_share;

    private List<ImgTvBean> shareDatas = new ArrayList<ImgTvBean>();
    private CommonAdapter<ImgTvBean> shareAdapter;
    private WXShareUtil wxShare = new WXShareUtil(getContext());
    private ClipboardManager cm;// 剪切板

    private static final String IMAGEURL = "IMAGEURL";
    private static final String GOODSNAME = "GOODSNAME";
    private static final String GOODSPRICE = "GOODSPRICE";
    private static final String GOODSOLDPRICE = "GOODSOLDPRICE";
    private static final String GOODSQUAN = "GOODSQUAN";
    private static final String SHAREURL = "SHAREURL";
    private String imageUrl = null;
    private String goodsName = null;
    private String goodsPrice;
    private String goodsOldPrice;
    private String goodsQuan;
    private String shareUrl = null;

    @Override
    public boolean setIsDark() { return false; }

    @Override
    public Object setLayout() { return R.layout.delegate_share_goods; }

    public static ShareGoodsDelegate newInstance(
            String imageUrl1,
            String goodsName2,
            String goodsPrice3,
            String goodsOldPrice4,
            String goodsQuan5,
            String shareUrl6) {
        ShareGoodsDelegate fragment = new ShareGoodsDelegate();
        Bundle bundle = new Bundle();
        bundle.putString(IMAGEURL, imageUrl1);
        bundle.putString(GOODSNAME, goodsName2);
        bundle.putString(GOODSPRICE, goodsPrice3);
        bundle.putString(GOODSOLDPRICE, goodsOldPrice4);
        bundle.putString(GOODSQUAN, goodsQuan5);
        bundle.putString(SHAREURL, shareUrl6);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            imageUrl = bundle.getString(IMAGEURL);
            goodsName = bundle.getString(GOODSNAME);
            goodsPrice = bundle.getString(GOODSPRICE);
            goodsOldPrice = bundle.getString(GOODSOLDPRICE);
            goodsQuan = bundle.getString(GOODSQUAN);
            shareUrl = bundle.getString(SHAREURL);
        }
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewsParams();
        initAdapter();
        initListener();
        setData();
        getSharaIconData();
    }

    private void initViews(View rootView) {
        iv_back = (AppCompatImageView) this.rootView.findViewById(R.id.iv_back);
        ll_share_pic = (LinearLayout) this.rootView.findViewById(R.id.ll_share_pic);
        iv_img = (AppCompatImageView) rootView.findViewById(R.id.iv_img);
        iv_erweima = (AppCompatImageView) rootView.findViewById(R.id.iv_erweima);
        tv_goodsname = (AppCompatTextView) this.rootView.findViewById(R.id.tv_goodsname);
        tv_price = (AppCompatTextView) this.rootView.findViewById(R.id.tv_price);
        tv_old_price = (AppCompatTextView) this.rootView.findViewById(R.id.tv_old_price);
        tv_quan = (AppCompatTextView) this.rootView.findViewById(R.id.tv_quan);
        gv_share = (MyGridView) this.rootView.findViewById(R.id.gv_share);
        tv_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    private void initViewsParams() {
        cm = (ClipboardManager) _mActivity.getSystemService(Context.CLIPBOARD_SERVICE);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = DensityUtil.dp2px(getContext(), 40);
        params.width = DensityUtil.dp2px(getContext(), 40);
        params.topMargin = Util.getStatusBarHeight(_mActivity);
        iv_back.setLayoutParams(params);
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
                    loadProcess();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 保存截图为bitmap,并保存到本地
//                            Bitmap bitmap = getCacheBitmapFromView(ll_share_pic);
                            Bitmap bitmap = getLinearLayoutBitmap(ll_share_pic);
//                            saveMyBitmap(bitmap);
                            dismissLoadProcess();
                            if (shareDatas.get(position).getId() == 0) {// 微信好友
                                wxShare.sharePicWithBitmap(0,bitmap);
//                                if (WXShareUtil.fileIsExists(Environment.getExternalStorageDirectory() + "/share_goods.png")) {
//                                    wxShare.sharePicWithPath(0, Environment.getExternalStorageDirectory() + "/share_goods.png");
//                                }
                            } else if (shareDatas.get(position).getId() == 1) {// 朋友圈
                                wxShare.sharePicWithBitmap(1,bitmap);
//                                if (WXShareUtil.fileIsExists(Environment.getExternalStorageDirectory() + "/share_goods.png")) {
//                                    wxShare.sharePicWithPath(1, Environment.getExternalStorageDirectory() + "/share_goods.png");
//                                }
                            }
                        }
                    }, 2000);
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

    public static Bitmap getRelativeLayoutBitmap(RelativeLayout relativeLayout) {
        int h = 0;
        Bitmap bitmap;
        for (int i = 0; i < relativeLayout.getChildCount(); i++) {
            h += relativeLayout.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(relativeLayout.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        relativeLayout.draw(canvas);
        return bitmap;
    }

    /**
     * 截取LinearLayout
     **/
    public static Bitmap getLinearLayoutBitmap(LinearLayout linearLayout) {
        int h = 0;
        Bitmap bitmap;
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            h += linearLayout.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(linearLayout.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        linearLayout.draw(canvas);
        return bitmap;
    }

    @SuppressLint("SdCardPath")
    public String saveMyBitmap(Bitmap mBitmap) { // 保存bitmap到SD卡
        File f = new File(Environment.getExternalStorageDirectory() + "/share_goods.png");

        try {
            f.createNewFile();
        } catch (IOException e) {
            System.out.println("在保存图片时出错：" + e.toString());
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            mBitmap.compress(Bitmap.CompressFormat.PNG, 90, fOut);
        } catch (Exception e) {
            return "create_bitmap_error";
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Environment.getExternalStorageDirectory() + "/share_goods.png";
    }

    public static Bitmap getCacheBitmapFromView(View view) { // 获取一个 View 的缓存视图
        final boolean drawingCacheEnabled = true;
        view.setDrawingCacheEnabled(drawingCacheEnabled);
        view.buildDrawingCache(drawingCacheEnabled);
        final Bitmap drawingCache = view.getDrawingCache();
        Bitmap bitmap;
        if (drawingCache != null) {
            bitmap = Bitmap.createBitmap(drawingCache);
            view.setDrawingCacheEnabled(false);
        } else {
            bitmap = null;
        }
        return bitmap;
    }

    private void setData() {
        if (imageUrl!=null){
            loadProcess();
            Glide.with(getContext())
                .load(imageUrl)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> arg1) {
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.width = screenWidth;
                        params.height = screenWidth;
                        iv_img.setLayoutParams(params);
                        iv_img.setImageBitmap(bitmap); //显示图片
                        dismissLoadProcess();
                    }
                });
        }
        tv_price.setText(goodsPrice); //团购价
        tv_old_price.setText("原价￥" + goodsOldPrice); //指导价
        tv_goodsname.setText(goodsName);
        if (goodsQuan == null ||goodsQuan.equals("0")){
            tv_quan.setVisibility(View.GONE);
        }else{
            tv_quan.setText(goodsQuan);
            tv_quan.setVisibility(View.VISIBLE);
        }
        int widthAndHeight = DensityUtil.dp2px(getContext(), 120);
        Bitmap qrcode;
        try {
            qrcode = ZXingUtils.createQRCode(shareUrl, widthAndHeight, widthAndHeight);
            iv_erweima.setImageBitmap(qrcode);
        } catch (Exception e) {}
    }

    private void getSharaIconData() {
        shareDatas.add(new ImgTvBean(R.mipmap.sharewxhy, getResources().getString(R.string.wxhy), 0));
        shareDatas.add(new ImgTvBean(R.mipmap.sharewxpyq, getResources().getString(R.string.wxpyq), 1));
        shareDatas.add(new ImgTvBean(R.mipmap.sharelink, getResources().getString(R.string.copy_link), 2));
        gv_share.setNumColumns(shareDatas.size());
        shareAdapter.refreshDatas(shareDatas);
    }
}
