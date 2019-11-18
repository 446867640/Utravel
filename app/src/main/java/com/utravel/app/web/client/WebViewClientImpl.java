package com.utravel.app.web.client;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.web.WebDelegate;
import com.utravel.app.web.listener.IPageLoadListener;
import com.utravel.app.web.route.Router;

public class WebViewClientImpl extends WebViewClient { //9

    private final WebDelegate DELEGATE;
    private IPageLoadListener mIPageLoadListener = null;

    public void setPageLoadListener(IPageLoadListener listener){
        this.mIPageLoadListener = listener;
    }

    public WebViewClientImpl(WebDelegate delegate) {
        this.DELEGATE = delegate;
    }

//    @Override
//    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//        return super.shouldOverrideUrlLoading(view, request);
//    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) { //11 两个该方法，为了兼用旧版本，用这个
        LatteLogger.e("shouldOverrideUrlLoading", url);
        return Router.getInstance().handleWebUrl(DELEGATE, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if(mIPageLoadListener!=null){
            mIPageLoadListener.onLoadStart();
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if(mIPageLoadListener!=null){
            mIPageLoadListener.onLoadEnd();
        }
    }
}
