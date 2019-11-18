package com.utravel.app.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.utravel.app.config.Config;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.NetConnectionNew;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public abstract class BaseWXEntryActivity extends BaseWXActivity {
    //用户登录成功后回调
    public abstract void onSignInSuccess(String userInfo);
    //微信发送请求到第三方应用后的回调
    @Override
    public void onReq(BaseReq baseReq) {}
    //第三方应用发送请求到微信后的回调
    @Override
    public void onResp(BaseResp baseResp) {
        final String code = ((SendAuth.Resp)baseResp).code;
        LatteLogger.e("微信code="+code, "微信code="+code);
        final StringBuffer authUrl = new StringBuffer();
        authUrl  //地址的拼接：官方文档上有
                .append("https://api.weixin.qq.com/sns/oauth2/access_token?appid=")
                .append(LatteWeChat.APP_ID)
                .append("&secret=")
                .append(LatteWeChat.APP_SECRET)
                .append("&code=")
                .append(code)
                .append("&grant_type=authorization_code");
        LatteLogger.d("authUrl", authUrl.toString());
        getOAuthData(code);
//        getAuth(authUrl.toString());
    }

    private void getOAuthData(String code) {
        String url = Config.OAUTH_WECHAT_LOGIN; //接口url
        String apiName = "微信授权登陆接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addParams.put("code", code);
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, this, url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    onSignInSuccess(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) { }
            });
    }

    private void getAuth(String authUrl){
        String apiName = "获取微信用户access_token, openid"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        NetConnectionNew.get(apiName, this, authUrl, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    final JSONObject authObj = JSON.parseObject(arg0);
                    final String accessToken = authObj.getString("access_token");
                    final String openId = authObj.getString("openid");
                    final StringBuffer userInfoUrl = new StringBuffer();
                    userInfoUrl  //获取用户名字、地址、位置等个人信息
                            .append("https://api.weixin.qq.com/sns/userinfo?access_token=")
                            .append(accessToken)
                            .append("&openid=")
                            .append(openId)
                            .append("&lang=") //语言设置：使用中文返回
                            .append("zh_CN");
                    LatteLogger.d("userInfoUrl", userInfoUrl.toString());
                    getUserInfo(userInfoUrl.toString());
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {}
            });
    }

    private void getUserInfo(String userInfoUrl){
        String apiName = "获取微信用户名字、地址、位置等个人信息"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        NetConnectionNew.get(apiName, this, userInfoUrl, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    onSignInSuccess(arg0);
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {}
            });
    }
}
