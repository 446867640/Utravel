package com.utravel.app.web.route;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.webkit.URLUtil;
import android.webkit.WebView;

import com.utravel.app.R;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.utils.Util;
import com.utravel.app.web.WebDelegate;
import com.utravel.app.web.WebDelegateImpl;

public class Router { //10

    private Router() {

    }

    private static class Holder {
        private static final Router INSTANCE = new Router();
    }

    public static Router getInstance() {
        return Holder.INSTANCE;
    }

    public final boolean handleWebUrl(WebDelegate delegate, String url) { //12
        // 如果是电话协议
        if (url.contains("tel:")) {
            callPhone(delegate.getContext(), url);
            return true;
        }
        if(url.contains("pinduoduo://")){
            String host = delegate.getContext().getResources().getString(R.string.pinduoduo_host);
            if(Util.checkHasInstalledApp(delegate.getContext(), host)){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                delegate.getContext().startActivity(intent);
                delegate.pop();
            }
            return true;
        }else {
            final LatterDelegate topDelegate = delegate.getTopDelegate();
            final WebDelegateImpl webDelegate = WebDelegateImpl.creat(url);
            topDelegate.start(webDelegate);
        }
        return true; //返回true,表示已经接管了该url；
    }

    private void loadWebPage(WebView webView, String url) { //12
        if (webView != null) {
            webView.loadUrl(url);
        } else {
            throw new NullPointerException("webview is NULL(Router)");
        }
    }

    private void loadLocalPage(WebView webView, String url) { //12
        //20190917 改
//        loadWebPage(webView, "file:///android_asset/" + url);
        loadWebPage(webView, url);
    }

    private void loadPage(WebView webView, String url) { //12
        if (URLUtil.isNetworkUrl(url) || URLUtil.isAssetUrl(url)) {
            loadWebPage(webView, url);
        } else {
            loadLocalPage(webView, url);
        }
    }

    public final void loadPage(WebDelegate delegate, String url) { //12
        loadPage(delegate.getWebvView(), url);
    }

    private void callPhone(Context context, String url) { //12 跳到拨打电话页面，让用户选择是否进行拨打
        final Intent intent = new Intent(Intent.ACTION_DIAL);
        final Uri data = Uri.parse(url);
        intent.setData(data);
        ContextCompat.startActivity(context, intent, null);
    }
}
