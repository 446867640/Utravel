package com.utravel.app.activities.proxy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.utravel.app.activities.base.NewsActivity;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.delegates.main.MainDelegate;
import com.utravel.app.latte.Latte;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.SharedPreferencesUtil;

public class MainActivity extends ProxyActivity {

    @Override
    public LatterDelegate setRootDelegate() {  return new MainDelegate();  }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Latte.getConfigurator().withActivity(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //极光推送在APP退出之后通知跳转
        if (SharedPreferencesUtil.getBoolean(MainActivity.this, "isJPush")) {
            LatteLogger.e("进入推送isJPush==" + SharedPreferencesUtil.getBoolean(MainActivity.this, "isJPush"), "----");
            SharedPreferencesUtil.putBoolean(MainActivity.this, "isJPush",false);
            if (SharedPreferencesUtil.getBoolean(MainActivity.this, "isJPush_announcement")) {
                //系统公告
                Intent anno = new Intent(this, NewsActivity.class);
                anno.putExtra("news", 0);
                startActivity(anno);
                LatteLogger.e("进入推送isJPush==" + SharedPreferencesUtil.getBoolean(MainActivity.this, "isJPush_announcement"), "----");
                SharedPreferencesUtil.putBoolean(MainActivity.this, "isJPush_announcement",false);
            }else if(SharedPreferencesUtil.getBoolean(MainActivity.this, "isJPush_notice")){
                //消息列表
                Intent anno = new Intent(this, NewsActivity.class);
                anno.putExtra("news", 1);
                startActivity(anno);
                LatteLogger.e("进入推送isJPush==" + SharedPreferencesUtil.getBoolean(MainActivity.this, "isJPush_notice"), "----");
                SharedPreferencesUtil.putBoolean(MainActivity.this, "isJPush_notice",false);
            }
        }else {
            LatteLogger.e("不进入推送isJPush==", "不进入推送isJPush==");
        }
    }
}