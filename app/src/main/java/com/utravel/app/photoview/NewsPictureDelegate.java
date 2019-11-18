package com.utravel.app.photoview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.utravel.app.R;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.Util;
import java.io.Serializable;
import java.util.List;

public class NewsPictureDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    private HackyViewPager picture_vp;
    private ImageView btn_back;
    private TextView tv_index;

    private List<String> imageDatas;
    private int imagePosition = -1;

    private static final String BUNDLE_POSITION_TAG = "position";
    private static final String BUNDLE_IMAGEDATA_TAG = "imageDatas";

    @Override
    public boolean setIsDark() { return false; }

    @Override
    public Object setLayout() { return R.layout.delegate_news_picture; }

    public static NewsPictureDelegate newInstance(int position, List<String> imageDatas) {
        NewsPictureDelegate fragment = new NewsPictureDelegate();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_POSITION_TAG, position);
        bundle.putSerializable(BUNDLE_IMAGEDATA_TAG, (Serializable) imageDatas);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            imagePosition = bundle.getInt(BUNDLE_POSITION_TAG);
            imageDatas = (List<String>)bundle.getSerializable(BUNDLE_IMAGEDATA_TAG);
        }
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewPager();
        initViewsParams();
        initListener();
    }

    private void initViewsParams() {
        FrameLayout.LayoutParams back_params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        back_params.width = DensityUtil.dp2px(getContext(), 36);
        back_params.height = DensityUtil.dp2px(getContext(), 24);
        back_params.topMargin = Util.getStatusBarHeight(getContext()) + DensityUtil.dp2px(getContext(), 5);
        back_params.leftMargin = DensityUtil.dp2px(getContext(), 10);
        btn_back.setLayoutParams(back_params);
    }

    private void initViews(View rootView) {
        picture_vp = (HackyViewPager) rootView.findViewById(R.id.picture_vp);
        btn_back = (ImageView) rootView.findViewById(R.id.btn_back);
        tv_index = (TextView) rootView.findViewById(R.id.tv_index);
    }

    private void initViewPager() {
        picture_vp.setAdapter(new ViewPagerAdapter());
        picture_vp.addOnPageChangeListener(new ViewPagerChangeListener());
        if (imagePosition != -1) {
            picture_vp.setCurrentItem(imagePosition);
        }else {
            picture_vp.setCurrentItem(0);
        }
    }

    private void initListener() {
        btn_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v==btn_back) {
            pop();
        }
    }

    private class ViewPagerAdapter extends PagerAdapter { // 查看大图viewpager适配器
        @SuppressLint("InflateParams")
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = getLayoutInflater().inflate(R.layout.item_news_picture,null);
            final PhotoView picture_iv_item = (PhotoView) view.findViewById(R.id.picture_iv_item);
            // 给imageview设置一个tag，保证异步加载图片时不会乱序
            picture_iv_item.setTag(imageDatas.get(position));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.width  = screenWidth;
            params.height = screenWidth*3/2;
            //这个是真实比例，不能用这个，因为定死真实高度之后，只能在这个高度缩放，所以把高度定义得大些，
            //像现在设置为1.5倍，不影响宽高比，还能在这个高度缩放
//			params.height = screenWidth*281/375;
            picture_iv_item.setLayoutParams(params);
//			picture_iv_item.setScaleType(ScaleType.FIT_XY);
            Glide.with(getContext())
                    .load(imageDatas.get(position))
                    .into(new SimpleTarget<GlideDrawable>() { // 加上这段代码 可以解决
                        @Override
                        public void onResourceReady(GlideDrawable arg0, GlideAnimation<? super GlideDrawable> arg1) {
                            picture_iv_item.setImageDrawable(arg0); //显示图片
                        }
                    });
            container.addView(view);
            return view;
        }
        @Override
        public int getCount() {return imageDatas.size();}
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {return arg0 == arg1;}
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {container.removeView((View) object);}
    }

    // viewpager切换监听器
    private class ViewPagerChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {}
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {}
        @Override
        public void onPageSelected(int arg0) {
            tv_index.setText((arg0 + 1) + "/" + imageDatas.size());
        }
    }
}

