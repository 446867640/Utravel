package com.utravel.app.delegates.sort;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.utravel.app.config.ItemType;
import com.utravel.app.recycler.DataConverter;
import com.utravel.app.recycler.MultipleFields;
import com.utravel.app.recycler.MultipleItemEntity;

import java.util.ArrayList;

public class SortLeftDataConverter extends DataConverter {

    private int mCurrentPosition;

    public SortLeftDataConverter(int postion) {
        this.mCurrentPosition = postion;
    }

    @Override
    public ArrayList<MultipleItemEntity> convert() {
        JSONArray data = JSON.parseObject(getJsonData()).getJSONArray("data");
        ArrayList<MultipleItemEntity> dataList = new ArrayList<MultipleItemEntity>();
        for(int i = 0; i < data.size(); i++){
            JSONObject beanObject = data.getJSONObject(i);
            final int id = beanObject.getInteger("id");
            final String main_image_url = beanObject.getString("main_image_url");
            final String name = beanObject.getString("name");
            final MultipleItemEntity entity = (MultipleItemEntity) MultipleItemEntity.builer()
                    .setField(MultipleFields.ITEM_TYPE, ItemType.SORT_LEFT)
                    .setField(MultipleFields.ID, id)
                    .setField(MultipleFields.IMAGE_URL, main_image_url)
                    .setField(MultipleFields.TEXT, name)
                    .setField(MultipleFields.TAG, false)
                    .build();
            dataList.add(entity);
        }
        //设置第一个为默认
        dataList.get(mCurrentPosition).setField(MultipleFields.TAG,true);
        return dataList;

//        ArrayList<MultipleItemEntity> dataList = new ArrayList<MultipleItemEntity>();
//        final JSONArray dataArray = JSON
//                .parseObject(getJsonData())
//                .getJSONObject("data")
//                .getJSONArray("list");
//        final int size = dataArray.size();
//        for(int i = 0; i < size; i++){
//            final JSONObject data = dataArray.getJSONObject(i);
//            final String name = data.getString("name");
//            final int id = data.getInteger("id");
//
//            final MultipleItemEntity entity = (MultipleItemEntity) MultipleItemEntity.builer()
//                    .setItemType(ItemType.VERTICAL_MENU_LIST)
//                    .setField(MultipleFields.ID,id)
//                    .setField(MultipleFields.TEXT,name)
//                    .setField(MultipleFields.TAG,false)
//                    .build();
//            dataList.add(entity);
//            //设置第一个为默认
//            dataList.get(0).setField(MultipleFields.TAG,true);
//        }
//        return dataList;

    }
}
