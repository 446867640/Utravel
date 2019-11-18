package com.utravel.app.web;

import android.webkit.JavascriptInterface;

import com.alibaba.fastjson.JSON;
import com.utravel.app.web.event.Event;
import com.utravel.app.web.event.EventManager;

public class LatteWebInterface { //6
    private final WebDelegate DELEGATE;

    private LatteWebInterface(WebDelegate DELEGATE) {
        this.DELEGATE = DELEGATE;
    }

    static LatteWebInterface creat(WebDelegate delegate){
        return new LatteWebInterface(delegate);
    }

    @JavascriptInterface
    public String event(String params){
        final String action = JSON.parseObject(params).getString("action");
        final Event event = EventManager.getInstance().creatEvent(action);
        if(event !=null){
            event.setAction(action);
            event.setDelegate(DELEGATE);
            event.setContext(DELEGATE.getContext());
            event.setUrl(DELEGATE.getUrl());
            return event.execute(params);
        }
        return null; //js返回的事件
    }
}
