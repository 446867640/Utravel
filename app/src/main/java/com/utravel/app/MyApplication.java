package com.utravel.app;

import android.app.Application;
import android.os.Environment;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;
import com.utravel.app.activities.base.SplashActivity;
import com.utravel.app.config.Config;
import com.utravel.app.latte.Latte;
import com.utravel.app.ui.zxing.activity.ZXingLibrary;
import com.utravel.app.web.event.TestEvent;
import java.util.concurrent.TimeUnit;
import cn.jpush.android.api.JPushInterface;
import es.dmoral.toasty.Toasty;
import me.yokeyword.fragmentation.Fragmentation;
import okhttp3.OkHttpClient;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Latte.init(this)
            .withApiHost(Config.BASE)
            .withWeChatAppId(Config.WE_CHAT_APP_ID)
            .withWeChatAppSecret(Config.WE_CHAT_APP_SECRET)
            .withJavascriptInterface("latte")
            .withEvaluateJavascript("toweb();")
            .withWebEvent("test", new TestEvent())
            .configure();
        initLogger();
        initToast();
        initFragmention();
        initOkHttp();
        initBugly();
        initJpush();
        initUUZUCHE();
    }
    private void initUUZUCHE() { //初始化uuzuche二维码
        ZXingLibrary.initDisplayOpinion(this);
    }

    private void initJpush() {
        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
    }

    private void initToast() {
        Toasty.Config.getInstance().tintIcon(true).allowQueue(true).apply();
    }

    private void initLogger() {
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    private void initFragmention() { //显示悬浮球; 其他Mode:SHAKE: 摇一摇唤出;NONE：隐藏
        Fragmentation.builder().stackViewMode(Config.ISDEBUG ? Fragmentation.BUBBLE : Fragmentation.NONE).debug(BuildConfig.DEBUG).install();
    }

    private void initBugly() {
        Beta.autoInit = false;
        Beta.autoCheckUpgrade = false; //true表示初始化时自动检查升级; false表示不会自动检查升级,需要手动调用Beta.checkUpgrade()方法;
        Beta.upgradeCheckPeriod = 2 * 1000; //设置升级检查周期为60s(默认检查周期为0s)，60s内SDK不重复向后台请求策略);
        Beta.initDelay = 1 * 1000;  //设置启动延时为1s（默认延时3s），APP启动1s后初始化SDK，避免影响APP启动速度;
        Beta.largeIconId = R.mipmap.logo; //设置通知栏大图标，largeIconId为项目中的图片资源;
        Beta.smallIconId = R.mipmap.logo; //设置状态栏小图标，smallIconId为项目中的图片资源Id;
        Beta.defaultBannerId = R.mipmap.logo; //设置更新弹窗默认展示的banner，defaultBannerId为项目中的图片资源Id;当后台配置的banner拉取失败时显示此banner，默认不设置则展示“loading“;
        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); //设置sd卡的Download为更新资源保存目录; 后续更新资源会保存在此目录，需要在manifest中添加WRITE_EXTERNAL_STORAGE权限;
        Beta.showInterruptedStrategy = true; //已经确认过的弹窗在APP下次启动自动检查更新时会再次显示;
        Beta.canShowUpgradeActs.add(SplashActivity.class); //只允许在MainActivity上显示更新弹窗，其他activity上不显示弹窗; 不设置会默认所有activity都可以显示弹窗;
        Bugly.init(this, Config.BUGLY_APP_ID, Config.ISDEBUG); //统一初始化Bugly产品，包含Beta/
    }

    private void initOkHttp() {
//        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null); //设置可访问所有的https网站（默认）
//        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(证书的inputstream, null, null, null, null); //设置具体的证书
//        HttpsUtils.getSslSocketFactory(证书的inputstream, 本地证书的inputstream, 本地证书的密码); //设置双向认证
        /**
         * 对于Cookie(包含Session)
         *  PersistentCookieStore //持久化cookie
         *  SerializableHttpCookie //持久化cookie
         *  MemoryCookieStore //cookie信息存在内存中
         */
        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // .addInterceptor(new LoggerInterceptor("TAG"))
                // .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .connectTimeout(10000000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000000L, TimeUnit.MILLISECONDS)
                .cookieJar(cookieJar)
                .build();
    }
}
