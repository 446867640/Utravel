package com.utravel.app.recycler;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据的转化处理类
 */
public abstract class DataConverter {

    protected final List<MultipleItemEntity> ENTITIES = new ArrayList<MultipleItemEntity>();
    private String mJsonData=null;

    public abstract ArrayList<MultipleItemEntity> convert();

    public DataConverter setJsonData(String json){
        this.mJsonData = json;
        return this;
    }

    protected String getJsonData(){
        if (mJsonData == null || mJsonData.isEmpty()) {
            throw new NullPointerException("DATA IS NULL!");
        }
        return mJsonData;
    }
}
