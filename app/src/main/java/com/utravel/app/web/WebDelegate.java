package com.utravel.app.web;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;

import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.latte.ConfigType;
import com.utravel.app.latte.Latte;
import com.utravel.app.web.route.RouteKeys;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public abstract class WebDelegate extends LatterDelegate implements IWebViewInitializer{ //1、3

    private WebView mWebvView = null;
    private final ReferenceQueue<WebView> WEB_VIEW_QUEUE = new ReferenceQueue<>();
    private String mUrl = null;
    private boolean mIsWebViewAbailable = false; //初始化webview的标志，如果没有初始化完成，则无法获取Webview，借鉴旧版源码webviewfragment
    private LatterDelegate mTopDelegate;

    public WebDelegate() { }

    public abstract IWebViewInitializer setInitializer();//3   指定子类必须初始化该接口

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mUrl = bundle.getString(RouteKeys.URL.name());
        }
        initWebView();
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() { //5
        if (mWebvView != null) {
            mWebvView.removeAllViews();
            mWebvView.destroy();
        } else {
            final IWebViewInitializer initializer = setInitializer();
            if (initializer != null) {
                //WebView是new出来的，而不是在xml文件中直接显示的，原因：在xml中或多或少都会内存泄露的问题存在
                final WeakReference<WebView> webViewWeakReference = new WeakReference<>(new WebView(getContext()), WEB_VIEW_QUEUE);
                mWebvView = webViewWeakReference.get();
                mWebvView = initializer.initWebView(mWebvView);//初始化
                mWebvView.setWebViewClient(initializer.initWebViewClient());
                mWebvView.setWebChromeClient(initializer.initWebChromeClient());
                final String name = Latte.getConfigurator().getConfiguration(ConfigType.JAVASCRIPT_INTERFACE);
                mWebvView.addJavascriptInterface(LatteWebInterface.creat(this), name); //7
                mIsWebViewAbailable = true;
            } else {
                throw new NullPointerException("Initializer is Null(Initializer为空)");
            }
        }
    }

    public void setTopDelegate(LatterDelegate delegate){
        mTopDelegate = delegate;
    }

    public LatterDelegate getTopDelegate(){
        //如果是null,则从自身转化为delegate输出
        //可以从本身进行delegate的切换，如果不是，从本身的delegate切换
        if(mTopDelegate == null){
            mTopDelegate = this;
        }
        return mTopDelegate;
    }

    public WebView getWebvView() { //7
        if (mWebvView == null) {
            throw new NullPointerException("WebView is NULL(WebView为空)");
        }
        return mIsWebViewAbailable ? mWebvView : null;
    }

    public String getUrl() { //7
        if (mUrl == null) {
            throw new NullPointerException("WebView is NULL(WebView为空)");
        }
        return mUrl;
    }

    @Override
    public void onPause() { //7
        super.onPause();
        if (mWebvView != null) {
            mWebvView.onPause();
        }
    }

    @Override
    public void onResume() { //7
        super.onResume();
        if (mWebvView != null) {
            mWebvView.onResume();
        }
    }

    @Override
    public void onDestroyView() { //7
        super.onDestroyView();
        mIsWebViewAbailable = false;
    }

    @Override
    public void onDestroy() { //7
        super.onDestroy();
        if (mWebvView != null) {
            mWebvView.removeAllViews();
            mWebvView.destroy();
            mWebvView = null;
        }
    }
}
