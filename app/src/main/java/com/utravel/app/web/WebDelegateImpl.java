package com.utravel.app.web;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.utravel.app.web.chromeclient.WebChromeClientImpl;
import com.utravel.app.web.client.WebViewClientImpl;
import com.utravel.app.web.listener.IPageLoadListener;
import com.utravel.app.web.route.RouteKeys;
import com.utravel.app.web.route.Router;

public class WebDelegateImpl extends WebDelegate { //8、13

    private IPageLoadListener mIPageLoadListene = null;

    public void setPageLoadListene(IPageLoadListener listene){
        this.mIPageLoadListene = listene;
    }

    public static WebDelegateImpl creat(String url) {
        WebDelegateImpl fragment = new WebDelegateImpl();
        Bundle bundle = new Bundle();
        bundle.putString(RouteKeys.URL.name(), url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Object setLayout() {
        return getWebvView();
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        if (getUrl() != null) {
            //用原生的方式模拟webview跳转，需要使用WebViewClient进行拦截
            Router.getInstance().loadPage(this, getUrl()); //13
        }
    }

    @Override
    public IWebViewInitializer setInitializer() {
        return this;
    }

    @Override
    public WebView initWebView(WebView webView) { //15
        return new WebViewInitalizer().createWebView(webView);
    }

    @Override
    public WebViewClient initWebViewClient() {
        final WebViewClientImpl client = new WebViewClientImpl(this);
        client.setPageLoadListener(mIPageLoadListene);
        return client;
    }

    @Override
    public WebChromeClient initWebChromeClient() {
        return new WebChromeClientImpl();
    }
}
