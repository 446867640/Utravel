package com.utravel.app.wechat;

import android.app.Activity;

import com.utravel.app.config.Config;
import com.utravel.app.latte.ConfigType;
import com.utravel.app.latte.Latte;
import com.utravel.app.wechat.callbacks.IWeChatSignInCallback;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class LatteWeChat {

    public static final String APP_ID = Config.WE_CHAT_APP_ID;
    public static final String APP_SECRET = (String) Latte.getConfigurator().getConfiguration(ConfigType.WE_CHAT_APP_SECRET);
    private final IWXAPI WXAPI;
    private IWeChatSignInCallback mSignInCallback;

    private static final class Holder {
        private static final LatteWeChat INSTANCE = new LatteWeChat();
    }

    public static LatteWeChat getInstance() {
        return Holder.INSTANCE;
    }

    //微信登录初始化
    private LatteWeChat() {
        final Activity activity = Latte.getConfigurator().getConfiguration(ConfigType.ACTIVITY);
        WXAPI = WXAPIFactory.createWXAPI(activity, APP_ID, true);
        WXAPI.registerApp(APP_ID);
    }

    //向微信客户端发送请求，通知其，我们的设备端登录了，获取api
    public final IWXAPI getWXAPI() {
        return WXAPI;
    }

    public LatteWeChat onSignSuccess(IWeChatSignInCallback callback) {
        this.mSignInCallback = callback;
        return this;
    }

    public IWeChatSignInCallback getSignInCallback() {
        return mSignInCallback;
    }

    //微信点击登录按钮的事件
    public final void signIn() {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo"; //微信的认证信息
        req.state = "random_state"; //认证信息:原来作者项目中的
        WXAPI.sendReq(req);
    }
}
