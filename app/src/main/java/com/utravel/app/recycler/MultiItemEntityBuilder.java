package com.utravel.app.recycler;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.WeakHashMap;

public class MultiItemEntityBuilder {

    private static final WeakHashMap<Object,Object> FIELDS = new WeakHashMap<Object,Object>();

    public MultiItemEntityBuilder(){
        //先清除之前的数据
        FIELDS.clear();
    }

    public final MultiItemEntityBuilder setItemType(int itemType){
        FIELDS.put(MultipleFields.ITEM_TYPE, itemType);
        return this;
    }

    public final MultiItemEntityBuilder setField(Object key, Object value){
        FIELDS.put(key,value);
        return this;
    }

    public final MultiItemEntityBuilder setFields(WeakHashMap<?,?> map){
        FIELDS.putAll(map);
        return this;
    }

    public final MultiItemEntity build(){
        return new MultipleItemEntity(FIELDS);
    }
}
