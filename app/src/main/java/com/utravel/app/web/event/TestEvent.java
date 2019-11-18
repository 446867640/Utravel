package com.utravel.app.web.event;

import android.webkit.WebView;
import android.widget.Toast;

import com.utravel.app.latte.ConfigType;
import com.utravel.app.latte.Latte;

public class TestEvent extends Event {
    @Override
    public String execute(String params) {
        Toast.makeText(getContext(), getAction(), Toast.LENGTH_SHORT).show();
        if(getAction().equals("pinduoduo")){

        }else if (getAction().equals("taobao")){

        }else if (getAction().equals("tianmao")){

        }
        else if (getAction().equals("jingdong")){

        }
        if (getAction().equals("pinduoduo")){
            final WebView webView = getWebView();
            webView.post(new Runnable() {
                @Override
                public void run() {
                    final String method = Latte.getConfigurator().getConfiguration(ConfigType.EVALUATE_JAVASCRIPT);
                    webView.evaluateJavascript(method, null);
                }
            });
        }
        return null;
    }
}
