package com.utravel.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter{
    protected Context mContext = null;
    protected LayoutInflater layoutInflater = null;
    public List<T> mDatas = null;
    protected int layoutId;
    protected int mStart, mEnd;
    public static String[] URLS;
    public CommonAdapter(Context context, List<T> datas, int layoutId) {
        this.mContext = context;
        this.mDatas = datas;
        this.layoutId = layoutId;
        layoutInflater = LayoutInflater.from(context);
    }

    public void refreshDatas(List<T> datas) {
        this.mDatas = datas;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return mDatas != null ? mDatas.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        BaseViewHolder holder = BaseViewHolder.get(mContext, convertView, parent, layoutId, position);
        convert(holder, getItem(position));
        return holder.getConvertView();
    }

    public abstract void convert(BaseViewHolder holder, T t);

}

