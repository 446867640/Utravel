package com.utravel.app.web;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public interface IWebViewInitializer { //2

    WebView initWebView(WebView webView);
    //针对webview的浏览器本身的行为；
    WebViewClient initWebViewClient();
    //针对webview的浏览器内部页面的控制行为；
    WebChromeClient initWebChromeClient();
}
