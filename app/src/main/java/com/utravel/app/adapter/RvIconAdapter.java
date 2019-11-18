package com.utravel.app.adapter;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.utravel.app.R;
import com.utravel.app.entity.ImgTvBean;
import java.util.List;


public class RvIconAdapter extends BaseQuickAdapter<ImgTvBean, BaseViewHolder> {
    public RvIconAdapter(List<ImgTvBean> datas) {
        super(R.layout.item_shouye_icon, datas);
    }

    @Override
    protected void convert(BaseViewHolder helper, ImgTvBean t) {
        helper.setText(R.id.tv1, t.getName());
        ImageView iv1 = (ImageView) helper.getView(R.id.iv1);
        iv1.setImageResource(t.getImageResource());
    }
}

