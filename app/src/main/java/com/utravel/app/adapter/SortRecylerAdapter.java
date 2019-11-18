package com.utravel.app.adapter;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.utravel.app.R;
import com.utravel.app.config.ItemType;
import com.utravel.app.delegates.sort.SortDelegate;
import com.utravel.app.latte.Latte;
import com.utravel.app.recycler.MultipleFields;
import com.utravel.app.recycler.MultipleItemEntity;
import com.utravel.app.recycler.MultipleRecyclerAdapter;
import com.utravel.app.recycler.MultipleViewHolder;
import com.utravel.app.utils.GlideRoundTransform;

import java.util.List;

public class SortRecylerAdapter extends MultipleRecyclerAdapter {

    private SortDelegate DELEGATE;
    private int mPrePosition = 0;

    public SortRecylerAdapter(List<MultipleItemEntity> data, SortDelegate delegate) {
        super(data);
        this.DELEGATE = delegate;
        addItemType(ItemType.SORT_LEFT, R.layout.item_sort_left);
        addItemType(ItemType.SORT_RIGHT, R.layout.item_shouye_icon);
    }

    @Override
    protected void convert(MultipleViewHolder helper, MultipleItemEntity entity) {
        super.convert(helper, entity);
        switch (helper.getItemViewType()){
            case ItemType.SORT_LEFT:
                final String text = entity.getField(MultipleFields.TEXT);
                final boolean isClicked = entity.getField(MultipleFields.TAG);
                final AppCompatTextView name = helper.getView(R.id.tv_vertical_item_name);
                final View line = helper.getView(R.id.view_line);
                final View leftItemView = helper.itemView;
                if (!isClicked) {
                    line.setVisibility(View.INVISIBLE);
                    name.setTextColor(ContextCompat.getColor(mContext,R.color.text_color_black));
                    name.setTextSize(14);
                    name.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    leftItemView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.delegate_bg_ebebeb));
                }else {
                    line.setVisibility(View.VISIBLE);
                    line.setBackgroundColor(ContextCompat.getColor(mContext,R.color.delegate_red));
                    name.setTextColor(ContextCompat.getColor(mContext,R.color.delegate_red));
                    name.setTextSize(16);
                    name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    leftItemView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.delegate_white));
                }
                helper.setText(R.id.tv_vertical_item_name,text);
                break;
            case ItemType.SORT_RIGHT:
                final String mGoodsName = entity.getField(MultipleFields.TEXT);
                final String mGoodsImage = entity.getField(MultipleFields.IMAGE_URL);
                final int id = entity.getField(MultipleFields.ID);
                final TextView tv1 = helper.getView(R.id.tv1);
                final ImageView iv1 = helper.getView(R.id.iv1);
                helper.setText(R.id.tv1, mGoodsName);
                Glide.with(Latte.getApplicationContext())
                    .load(mGoodsImage)
                    .transform(new GlideRoundTransform(Latte.getApplicationContext(),5))
                    .into(new SimpleTarget<GlideDrawable>() { //加上这段代码 可以解决
                        @Override
                        public void onResourceReady(GlideDrawable arg0, GlideAnimation<? super GlideDrawable> arg1) {
                            iv1.setImageDrawable(arg0); //显示图片
                        }
                    });
                break;
        }
    }
}
