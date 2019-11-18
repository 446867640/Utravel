package com.utravel.app.delegates.sort;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.utravel.app.config.ItemType;
import com.utravel.app.recycler.DataConverter;
import com.utravel.app.recycler.MultipleFields;
import com.utravel.app.recycler.MultipleItemEntity;

import java.util.ArrayList;

public class SortRightDataConverter extends DataConverter {

    private int mCurrentPosition;

    public SortRightDataConverter(int mCurrentPosition) {
        super();
        this.mCurrentPosition = mCurrentPosition;
    }

    @Override
    public ArrayList<MultipleItemEntity> convert() {
        JSONArray data = JSON.parseObject(getJsonData()).getJSONArray("data");
        ArrayList<MultipleItemEntity> dataList = new ArrayList<MultipleItemEntity>();
        JSONArray secondary_categories = data.getJSONObject(mCurrentPosition).getJSONArray("secondary_categories");
        for (int j = 0; j < secondary_categories.size(); j++){
            JSONObject secondary = secondary_categories.getJSONObject(j);
            final int id = secondary.getInteger("id");
            final String name = secondary.getString("name");
            final String imageurl = secondary.getString("image_url");
            final MultipleItemEntity entity = (MultipleItemEntity) MultipleItemEntity.builer()
                    .setItemType(ItemType.SORT_RIGHT)
                    .setField(MultipleFields.ID,id)
                    .setField(MultipleFields.TEXT,name)
                    .setField(MultipleFields.IMAGE_URL,imageurl)
                    .build();
            dataList.add(entity);
        }

        return dataList;
    }
}
