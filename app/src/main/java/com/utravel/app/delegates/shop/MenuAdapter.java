package com.utravel.app.delegates.shop;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.utravel.app.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YoKeyword on 16/2/10.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<String> mItems = new ArrayList<>();

    private SparseBooleanArray mBooleanArray;

    private OnItemClickListener mClickListener;

    private int mLastCheckedPosition = -1;

    public MenuAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    public void setDatas(List<String> items) {
        mItems.clear();
        mItems.addAll(items);

        mBooleanArray = new SparseBooleanArray(mItems.size());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_sort_left, parent, false);
        final MyViewHolder holder = new MyViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (mClickListener != null) {
                    mClickListener.onItemClick(position, v);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (!mBooleanArray.get(position)) {
            holder.viewLine.setVisibility(View.INVISIBLE);
            holder.itemView.setBackgroundResource(R.color.delegate_red);
            holder.tvName.setTextColor(Color.BLACK);
        } else {
            holder.viewLine.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.text_color_black));
        }
        holder.tvName.setText(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setItemChecked(int position) {
        mBooleanArray.put(position, true);

        if (mLastCheckedPosition > -1) {
            mBooleanArray.put(mLastCheckedPosition, false);
            notifyItemChanged(mLastCheckedPosition);
        }
        notifyDataSetChanged();

        mLastCheckedPosition = position;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        View viewLine;
        AppCompatTextView tvName;

        public MyViewHolder(View itemView) {
            super(itemView);
            viewLine = itemView.findViewById(R.id.view_line);
            tvName = (AppCompatTextView) itemView.findViewById(R.id.tv_vertical_item_name);
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
