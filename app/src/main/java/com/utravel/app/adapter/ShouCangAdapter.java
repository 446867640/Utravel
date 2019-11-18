package com.utravel.app.adapter;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.utravel.app.R;
import com.utravel.app.entity.ShouCangEntity;
import java.util.List;

public class ShouCangAdapter extends BaseQuickAdapter<ShouCangEntity, BaseViewHolder> {

    public ShouCangAdapter(List<ShouCangEntity> shoucangDatas) {
        super(R.layout.item_my_shoucang, shoucangDatas);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ShouCangEntity item) {
        TextView tv_oldprice = helper.getView(R.id.tv_oldprice);
        final ImageView iv1 = helper.getView(R.id.iv1);
        tv_oldprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

    }
}
