package com.utravel.app.wechat.templates;

import com.utravel.app.wechat.BaseWXEntryActivity;
import com.utravel.app.wechat.LatteWeChat;

public class WXEntryTemplate extends BaseWXEntryActivity {

    @Override
    protected void onResume() {
        super.onResume();
        //在登录后返回的是该页面，不会自动消失
        finish();
        //【方法思路】再次获得焦点的时候，然后不能增加任何动画，并且设置为透明
        overridePendingTransition(0, 0);
    }

    @Override
    public void onSignInSuccess(String userInfo) {
        LatteWeChat.getInstance().getSignInCallback().onSignInSuccess(userInfo);
    }
}
