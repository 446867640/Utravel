package com.utravel.app.web.event;

import android.support.annotation.NonNull;

import java.util.HashMap;

public class EventManager {
    //存储event的名字和事件的map
    private static final HashMap<String, Event> EVENTS = new HashMap<>();

    private EventManager() {
    }

    //简单工厂方法单例模式
    private static class Holder {
        private static final EventManager INSTRANCE = new EventManager();
    }

    public static EventManager getInstance() {
        return Holder.INSTRANCE;
    }

    //增加map中的数据方法
    public EventManager addEvent(@NonNull String name, @NonNull Event event) {
        EVENTS.put(name, event);
        return this;
    }

    //事件的创建
    public Event creatEvent(@NonNull String action){
        final Event event = EVENTS.get(action);
        if(event==null){ //创建一个未定义的event
            return new UndefindEvent();
        }
        return event;
    }
}
