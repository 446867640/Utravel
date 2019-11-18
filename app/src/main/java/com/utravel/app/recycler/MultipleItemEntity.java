package com.utravel.app.recycler;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.WeakHashMap;

public class MultipleItemEntity implements MultiItemEntity {

    private final LinkedHashMap<Object,Object> MULTIPLE_FIELDS = new LinkedHashMap<Object,Object>();
    private final ReferenceQueue<LinkedHashMap<Object,Object>> ITEM_QUEUE = new ReferenceQueue<LinkedHashMap<Object,Object>>();
    private final SoftReference<LinkedHashMap<Object,Object>> FIELDS_REFERENCE = new SoftReference<>(MULTIPLE_FIELDS,ITEM_QUEUE);

    MultipleItemEntity(WeakHashMap<Object,Object> fields){
        FIELDS_REFERENCE.get().putAll(fields);
    }

    public static MultiItemEntityBuilder builer(){
        return new MultiItemEntityBuilder();
    }

    @Override
    public int getItemType() {
        return (int)FIELDS_REFERENCE.get().get(MultipleFields.ITEM_TYPE);
    }

    @SuppressWarnings("unchecked")
    public final <T> T getField(Object key){
        return (T) FIELDS_REFERENCE.get().get(key);
    }

    public final LinkedHashMap<?,?> getFields(){
        return FIELDS_REFERENCE.get();
    }

    public final MultipleItemEntity setField(Object key, Object value){
        FIELDS_REFERENCE.get().put(key,value);
        return this;
    }
}
