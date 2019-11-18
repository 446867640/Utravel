package com.utravel.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.utravel.app.R;
import com.utravel.app.entity.ImgTvBean;

import java.util.ArrayList;
import java.util.List;

public class IconVPGVAdpter extends BaseAdapter {
    private Context context;
    private List<ImgTvBean> IconDatas;// 数据源
    private int mIndex; // 页数下标，标示第几页，从0开始
    private int mPargerSize;// 每页显示的最大的数量

    public IconVPGVAdpter(
            Context context,
            ArrayList<ImgTvBean> IconDatas,
            int mIndex,
            int mPargerSize) {
        this.context = context;
        this.IconDatas = IconDatas;
        this.mIndex = mIndex;
        this.mPargerSize = mPargerSize;
    }

    /**
     * 先判断数据及的大小是否显示满本页lists.size() > (mIndex + 1)*mPagerSize
     * 如果满足，则此页就显示最大数量lists的个数 如果不够显示每页的最大数量，那么剩下几个就显示几个
     */
    @Override
    public int getCount() {
        return IconDatas.size() > (mIndex + 1) * mPargerSize ?
                mPargerSize : (IconDatas.size() - mIndex*mPargerSize);
    }
    @Override
    public ImgTvBean getItem(int arg0) {
        return IconDatas.get(arg0 + mIndex * mPargerSize);
    }
    @Override
    public long getItemId(int arg0) {
        return arg0 + mIndex * mPargerSize;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_shouye_icon, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv1);
            holder.iv_image = (ImageView) convertView.findViewById(R.id.iv1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 重新确定position因为拿到的总是数据源，数据源是分页加载到每页的GridView上的
        int pos = position + mIndex * mPargerSize;
        // 假设mPagerSize=8，假如点击的是第二页（即mIndex=1）上的第二个位置item(position=1),那么这个item的实际位置就是pos=9
        holder.tv_name.setText(IconDatas.get(pos).getName());

        if (TextUtils.isEmpty(IconDatas.get(pos).getImageUrl())) {
            Glide.with(context).load(IconDatas.get(pos).getImageResource()).into(holder.iv_image);
        }else {
            Glide.with(context).load(IconDatas.get(pos).getImageUrl()).into(holder.iv_image);
        }
        // 添加item监听
        // convertView.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View arg0) {
        // // TODO Auto-generated method stub
        // Toast.makeText(context, "您点击了" + lists.get(pos).getName(),
        // Toast.LENGTH_SHORT).show();
        // }
        // });
        return convertView;
    }

    static class ViewHolder {
        private TextView tv_name;
        private ImageView iv_image;
    }
}

