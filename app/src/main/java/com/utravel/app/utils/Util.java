package com.utravel.app.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import com.utravel.app.R;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.detail.GoodsInfoDelegate;
import com.utravel.app.delegates.main.MainDelegate;
import com.utravel.app.latte.Latte;
import com.utravel.app.wechat.LatteWeChat;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class Util {
    //用于fragment
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    //用于fragment
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static int isAppAlive(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> listInfos = activityManager.getRunningTasks(20);
        //判断程序是否在栈顶
        if (listInfos.get(0).topActivity.getPackageName().equals(packageName)) {
            return 1;
        } else {
            //判断程序是否在栈里,在后台
            for (ActivityManager.RunningTaskInfo info : listInfos) {
                if (info.topActivity.getPackageName().equals(packageName)) {
                    return 2;
                }
            }
            return 0;//栈里找不到，返回3
        }
    }

    @SuppressLint("SdCardPath")
    public static String saveMyBitmap(Bitmap mBitmap, String pathname) { // 保存bitmap到SD卡
        File f = new File(pathname);

        try {
            f.createNewFile();
        } catch (IOException e) {
            System.out.println("在保存图片时出错：" + e.toString());
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            mBitmap.compress(Bitmap.CompressFormat.PNG, 90, fOut);
        } catch (Exception e) {
            return "create_bitmap_error";
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathname;
    }

    public static Bitmap getCacheBitmapFromView(View view) { // 获取一个 View 的缓存视图
        final boolean drawingCacheEnabled = true;
        view.setDrawingCacheEnabled(drawingCacheEnabled);
        view.buildDrawingCache(drawingCacheEnabled);
        final Bitmap drawingCache = view.getDrawingCache();
        Bitmap bitmap;
        if (drawingCache != null) {
            bitmap = Bitmap.createBitmap(drawingCache);
            view.setDrawingCacheEnabled(false);
        } else {
            bitmap = null;
        }
        return bitmap;
    }

    //设置透明状态栏
    public static void setTranslateStatusBar(Activity activity, int statusBarId) {
        // 4.4以上处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // android
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 状态栏透明
            View status_bar = activity.findViewById(statusBarId);// 标题栏id
            if (status_bar != null) {
                ViewGroup.LayoutParams params = status_bar.getLayoutParams();
                params.height = getStatusBarHeight(activity);
                status_bar.setLayoutParams(params);
            }
        }
        //5.0 以上处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    //获取状态栏高度
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 设置状态栏字体颜色
     * 只有在状态栏全透明的时候才有效
     */
    public static void setStatusBarMode(Activity activity, boolean bDark) {
        //6.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = activity.getWindow().getDecorView();
            if (decorView != null) {
                int vis = decorView.getSystemUiVisibility();
                if (bDark) {
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(vis);
            }
        }
    }

    //跳第三方浏览器
    public static void webUrl(Context packageContext, String url){
        Intent i = new Intent();
        i.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        i.setData(content_url);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        packageContext.startActivity(i);
    }

    public static void slideToUp(View view) {
        Animation slide = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        slide.setDuration(500);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        view.startAnimation(slide);
        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public static void copy(Context context, String content) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(content);
    }

    public static void slideToDown(View view) {
        Animation slide = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f);
        slide.setDuration(500);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        view.startAnimation(slide);
        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    //获取文件后缀名
    public static String getExtension(String filePath) {
        String suffix = "";
        final File file = new File(filePath);
        final String name = file.getName();
        final int idx = name.lastIndexOf(".");
        if (idx > 0) {
            suffix = name.substring(idx + 1);
        }
        return suffix;
    }

    public static String getFileNameByTime(String timeFormatHeader, String extension) {
        return getTimeFormatName(timeFormatHeader) + "." + extension;
    }


    private static String getTimeFormatName(String timeFormatHeader) {
        final Date date = new Date(System.currentTimeMillis());
        //必须要加上单引号
        final SimpleDateFormat dateFormat = new SimpleDateFormat("'" + timeFormatHeader + "'" + "_yyyyMMdd_HHmmss", Locale.getDefault());
        return dateFormat.format(date);
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static File getFileByPath(String realFilePath) {
        return new File(realFilePath);
    }

    public static File creatDir(String sdcardDirName) {
        final String dir = Config.SDCARD_DIR + "/" + sdcardDirName + "/";
        final File fileDir = new File(dir);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        return fileDir;
    }

    public static File createFile(String sdcardDirName, String fileName) {
        return new File(creatDir(sdcardDirName), fileName);
    }

    public static boolean isToken(Context context) {
        if (!TextUtils.isEmpty(SharedPreferencesUtil.getString(context, "token"))) {
            return true;
        }
        return false;
    }

    //获取线程名
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    /** 删除单个文件
     * @param filePath$Name 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteSingleFile(Context context, String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                LatteLogger.e("--Method--", "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
                Toast.makeText(context, "删除单个文件" + filePath$Name + "失败！", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(context, "删除单个文件失败：" + filePath$Name + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static boolean fileIsExists(String strFile){
        try{
            File f=new File(strFile);
            if(!f.exists()){
                return false;
            }
        }
        catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * 根据Bundle获取图片在sd卡的路径
     *
     * @param bundle
     * @return
     */
    public static String getCameraImage(Bundle bundle) {
        String strState = Environment.getExternalStorageState();
        if (!strState.equals(Environment.MEDIA_MOUNTED)) {
            LatteLogger.i("TAG", "SD卡不存在");
        }
        String fileName = Config.APP + System.currentTimeMillis() + ".jpg"; // 此处可以改为时间
        // Bundle bundle = data.getExtras();
        Bitmap bitmap = (Bitmap) bundle.get("data");
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/tu/");
        if (!file.exists()) {
            file.mkdirs();
        }
        fileName = Environment.getExternalStorageDirectory().toString() + "/tu/" + fileName;
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.flush();
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }

    public static String urlToStr(Context context, Uri uri) {
        String path = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            Cursor cursor = context.getContentResolver().query(
                    uri, new String[]{MediaStore.Images.Media.DATA}, null,
                    null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
            return path;
        } else {
            path = uri.getPath();
            return path;
        }
    }

    public static String[] getVersionInfo(Context context) {
        String[] version = new String[2];
        PackageManager packageManager = context.getPackageManager(); // 获取packageManager实例
        String packageName = context.getPackageName(); // 获取包名
        int flag = 0;
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(packageName, flag);
        } catch (Exception e) {
        }
        if (packageInfo != null) {
            version[0] = String.valueOf(packageInfo.versionCode);
            version[1] = packageInfo.versionName;
        }
        return version;
    }

    public static void deleAPKFile(Context context) {
        DeleteFileUtil.deleteDir(getAPKDir(context));
    }

    //获取下载的APK文件目录
    public static String getAPKDir(Context context) {
        String apk_dir = "";
        if (isHasSdcard()) { //保存到SD卡路径下
            apk_dir = Environment.getExternalStorageDirectory().getAbsolutePath() + Config.APK_DIR;
            LatteLogger.e("SD卡路径", apk_dir + "");
        } else { //保存到app的包名路径下
            apk_dir = context.getFilesDir().getAbsolutePath() + Config.APK_DIR;
            LatteLogger.e("app的包名路径", apk_dir + "");
        }
        return apk_dir;
    }

    //获取下载的APK文件目录
    public static String getPhotoDir(Context context) {
        String photo_dir = "";
        if (isHasSdcard()) { //保存到SD卡路径下
            photo_dir = Environment.getExternalStorageDirectory().getAbsolutePath() + Config.PHOTO_DIR;
            LatteLogger.e("SD卡图片路径", photo_dir + "");
        } else { //保存到app的包名路径下
            photo_dir = context.getFilesDir().getAbsolutePath() + Config.PHOTO_DIR;
            LatteLogger.e("app的包名图片路径", photo_dir + "");
        }
        return photo_dir;
    }

    //判断是否插入SD卡
    public static boolean isHasSdcard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkHasInstalledApp(@NonNull Context context, String pkgName) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(pkgName, PackageManager.GET_GIDS);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        } catch (RuntimeException e) {
            app_installed = false;
        }
        return app_installed;
    }

    //年月日时分秒
    public static String timeToYYMMDDHHmmss(String timestamp){
        //2018-08-20T11:04:51.936+08:00
        String time1 = timeToYYMMDD(timestamp);	  //年月日
        String time2 = timeToHHmmss(timestamp);    //时分秒
        return time1 + " " + time2;
    }

    //年月日
    public static String timeToYYMMDD(String timestamp){
        //2018-08-20T11:04:51.936+08:00
        int loc1 = timestamp.indexOf("T");
        String time1 = timestamp.substring(0, loc1);      //年月日
        return time1;        				  			  //2018-08-20
    }

    //月日
    public static String timeToMMDD(String timestamp){
        //2018-08-20T11:04:51.936+08:00
        int loc1 = timestamp.indexOf("T");
        String time1 = timestamp.substring(5, loc1);      //月日
        return time1;									  //08-20
    }

    //时分秒
    public static String timeToHHmmss(String timestamp){
        //2018-08-20T11:04:51.936+08:00
        int loc1 = timestamp.indexOf("T");
        int loc2 = timestamp.indexOf(".");
        String time2 = timestamp.substring(loc1+1, loc2); //时分秒
        return time2;						  	 		  //11:04:51
    }

    //时分
    public static String timeToHHmm(String timestamp){
        //2018-08-20T11:04:51.936+08:00
        int loc1 = timestamp.indexOf("T");
        int loc2 = timestamp.indexOf(".");
        String time2 = timestamp.substring(loc1+1, loc1+6); //时分
        return time2;						  	 		    //11:04
    }

    //format表示日期格式（固定字母写法）："yyyy-MM-dd HH:mm:ss" "MM-dd HH:mm:ss" "HH:mm:ss"等
    //data表示日期字符串：比如"2019-11-01"
    //timeStamp表示时间戳
    //日期格式"yyyy-MM-dd HH:mm:ss" 转化为 时间戳     单位：s
    public static String dateToTimeStamp(String date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date).getTime()/1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //时间戳 转化为 日期格式"yyyy-MM-dd HH:mm:ss"
    public static String timeStampToDate(int timeStamp, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(timeStamp*1000));
    }

    //返回倒计时数字格式
    public static String getCountdownHHmmss(String countdownTime) {
        //1、国际标准时间格式--"yyyy-MM-dd HH:mm:ss"日期格式
        String time1 = timeToYYMMDDHHmmss(countdownTime);
        //2、"yyyy-MM-dd HH:mm:ss"日期格式 转化为 String型时间戳
        String time2 = dateToTimeStamp(time1, "yyyy-MM-dd HH:mm:ss");
        //3、计算倒计时时间段差值，得到新的long型时间戳（结束时间-当前系统时间）
        int dt = (int)(Integer.parseInt(time2) - System.currentTimeMillis()/1000);
        //4、long时间戳  转化为  "HH:mm:ss"日期格式
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String time3 = formatter.format(new Date(dt));
        return time3;
    }

    //返回倒计时数字格式
    public static int getCountdownDt(String countdownTime) {
        //1、国际标准时间格式--"yyyy-MM-dd HH:mm:ss"日期格式
        String time1 = timeToYYMMDDHHmmss(countdownTime);
        //2、"yyyy-MM-dd HH:mm:ss"日期格式 转化为 String型时间戳
        String time2 = dateToTimeStamp(time1, "yyyy-MM-dd HH:mm:ss");
        //3、计算倒计时时间段差值，得到新的long型时间戳（结束时间-当前系统时间）
        int dt = (int)(Integer.parseInt(time2) - System.currentTimeMillis()/1000);
        return dt;
    }

    public static String getssToHHmmss(int second) {
        if (second < 10) {
            return "00 : 00 : 0" + second;
        }
        if (second < 60) {
            return "00 : 00 : " + second;
        }
        if (second < 3600) {
            int minute = second / 60;
            second = second - minute * 60;
            if (minute < 10) {
                if (second < 10) {
                    return "00 : 0" + minute + " : 0" + second;
                }
                return "00 : 0" + minute + " : " + second;
            }
            if (second < 10) {
                return "00 : " + minute + " : 0" + second;
            }
            return "00 : " + minute + " : " + second;
        }
        //691100
        int hour = second / 3600;   //191.97222222222=191
        int minute = (second - hour * 3600) / 60;     //(691100-687600)/60=3500/60=58.3333=58
        second = second - hour * 3600 - minute * 60;  //691100-191*3600-58*60=691100-687600-3480=20
        //小于10h
        if (hour < 10) {
            //小于10h，小于10m
            if (minute < 10) {
                //小于10h，小于10m，小于10s
                if (second < 10) {
                    return "0" + hour + " : 0" + minute + " : 0" + second;
                }
                //小于10h，小于10m，大于10s
                return "0" + hour + " : 0" + minute + " : " + second;
            }
            //小于10h，大于10m，小于10s
            if (second < 10) {//小于10h，大于10m，小于10s
                return "0" + hour + " : " +  minute + " : 0" + second;
            }
            //小于10h，大于10m，大于10s
            return "0" + hour + " : " + minute + " : " + second;
        }
        //大于10h
        if (minute < 10) {
            //大于10h，小于10m，小于10s
            if (second < 10) {
                return hour + " : 0" + minute + " : 0" + second;
            }
            //大于10h，小于10m，大于10s
            return hour + " : 0" + minute + " : " + second;
        }
        //大于10h，大于10m，小于10s
        if (second < 10) {
            return hour + " : " + minute + " : 0" + second;
        }
        //大于10h，大于10m，大于10s
        return hour + " : "  + minute + " : " + second;
    }

    public static void setTagAndAlias(Context context, String id) { //设置标签与别名
        /**
         *这里设置了别名，在这里获取的用户登录的信息
         *并且此时已经获取了用户的userId,然后就可以用用户的userId来设置别名了
         **/
        Set<String> tags = new HashSet<String>();
        if (!TextUtils.isEmpty(id)){
            tags.add(id);//设置tag
        }
        //上下文、别名【Sting行】、标签【Set型】、回调
        // JPushInterface.setAliasAndTags(LoginActivity.this, id, tags,mAliasCallback);
        JPushInterface.setAlias(context, id, mAliasCallback);
        //JPushInterface.setAlias(LoginActivity.this, 0, id);
    }

    public static TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    //这里可以往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    //UserUtils.saveTagAlias(getHoldingActivity(), true);
                    logs = "Set tag and alias success极光推送别名设置成功";
                    LatteLogger.e("TAG", logs);
                    LatteLogger.e("极光推送【Alias】", alias);
                    break;
                case 6002:
                    //极低的可能设置失败 我设置过几百回 出现3次失败 不放心的话可以失败后继续调用上面那个方面 重连3次即可 记得return 不要进入死循环了...
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.极光推送别名设置失败，60秒后重试";
                    LatteLogger.e("TAG", logs);
                    break;
                default:
                    logs = "极光推送设置失败，Failed with errorCode = " + code;
                    LatteLogger.e("TAG", logs);
                    break;
            }
        }
    };


    public static void goToMinWeChat(String userName, String page_path) {
        IWXAPI api = LatteWeChat.getInstance().getWXAPI();
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = userName;              //填小程序原始id
        if (!TextUtils.isEmpty(page_path)) {
            req.path = page_path;                  //拉起小程序页面的可带参路径，不填默认拉起小程序首页
        }
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
        api.sendReq(req);
    }

    public  static String getUserAgent(){
        return "Mozilla/5.0 (iPhone; U; CPU iPhone OS 5_1_1 like Mac OS X; en) AppleWebKit/534.46.0 (KHTML, like Gecko) CriOS/19.0.1084.60 Mobile/9B206 Safari/7534.48.3";
    }

    //获取淘客appkey
    public static String getAppKey(Context context) {
        Properties pro = new Properties();
        try {
            pro.load(context.getAssets().open("app.properties"));
            return pro.getProperty("appKey");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    //获取淘客appSecret
    public static String getAppSecret(Context context) {
        Properties pro = new Properties();
        try {
            pro.load(context.getAssets().open("app.properties"));
            return pro.getProperty("appSecret");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    //获取淘客adzone_id
    public static String getAdzoneId(Context context) {
        Properties pro = new Properties();
        try {
            pro.load(context.getAssets().open("app.properties"));
            return pro.getProperty("adzoneId");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    //获取淘客pid
    public static String getPID(Context context) {
        Properties pro = new Properties();
        try {
            pro.load(context.getAssets().open("app.properties"));
            return pro.getProperty("pid");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    //淘宝客对TOP请求进行签名。
    public static String signTopRequest(Map<String, String> params, String secret, String signMethod) throws IOException {
        // 第一步：检查参数是否已经排序
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        if (Config.SIGN_METHOD_MD5.equals(signMethod)) {
            query.append(secret);
        }
        for (String key : keys) {
            String value = params.get(key);
            if (isNotEmpty(key) && isNotEmpty(value)) {
                query.append(key).append(value);
            }
        }
        // 第三步：使用MD5/HMAC加密
        byte[] bytes;
        if (Config.SIGN_METHOD_HMAC.equals(signMethod)) {
            bytes = encryptHMAC(query.toString(), secret);
        } else {
            query.append(secret);
            bytes = encryptMD5(query.toString());
        }
        // 第四步：把二进制转化为大写的十六进制
        return byte2hex(bytes);
    }

    //把字节流转换为十六进制表示方式。
    public static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

    //对字节流进行HMAC_MD5摘要。
    public static byte[] encryptHMAC(String data, String secret) throws IOException {
        byte[] bytes = null;
        try {
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(Config.CHARSET_UTF8), "HmacMD5");
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            bytes = mac.doFinal(data.getBytes(Config.CHARSET_UTF8));
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse.toString());
        }
        return bytes;
    }

    //对字符串采用UTF-8编码后，用MD5进行摘要。
    public static byte[] encryptMD5(String data) throws IOException {
        return encryptMD5(data.getBytes(Config.CHARSET_UTF8));
    }

    //对字节流进行MD5摘要。
    public static byte[] encryptMD5(byte[] data) throws IOException {
        byte[] bytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            bytes = md.digest(data);
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse.toString());
        }
        return bytes;
    }

    public static boolean isNotEmpty(String value) {
        int strLen;
        if (value == null || (strLen = value.length()) == 0) {
            return false;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(value.charAt(i)) == false)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 截取RelativeLayout
     **/
    public static Bitmap getRelativeLayoutBitmap(RelativeLayout relativeLayout) {
        int h = 0;
        Bitmap bitmap;
        for (int i = 0; i < relativeLayout.getChildCount(); i++) {
            h += relativeLayout.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(relativeLayout.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        relativeLayout.draw(canvas);
        return bitmap;
    }
    /**
     * 截取LinearLayout
     **/
    public static Bitmap getLinearLayoutBitmap(LinearLayout linearLayout) {
        int h = 0;
        Bitmap bitmap;
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            h += linearLayout.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(linearLayout.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        linearLayout.draw(canvas);
        return bitmap;
    }

    /**
     *  截图listview
     * **/
    public static Bitmap getbBitmap(ListView listView) {
        int h = 0;
        Bitmap bitmap = null;
        // 获取listView实际高度
        for (int i = 0; i < listView.getChildCount(); i++) {
            h += listView.getChildAt(i).getHeight();
        }

        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(listView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        listView.draw(canvas);
        // 测试输出
        FileOutputStream out = null;
        try {
            out = new FileOutputStream("/sdcard/screen_test.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        return bitmap;
    }

    /**
     * 截取scrollview的屏幕
     * @param scrollView
     * @return
     */
    public static Bitmap getBitmapByView(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        // 获取listView实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
            scrollView.getChildAt(i).setBackgroundResource(R.drawable.bg_baise_5_daojiao);
        }

        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        // 测试输出
        FileOutputStream out = null;
        try {
            out = new FileOutputStream("/sdcard/screen_test.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        return bitmap;
    }

    /**
     * 截屏
     * @param activity
     * @return
     */
    public static Bitmap activityShot(Activity activity) {
        /*获取windows中最顶层的view*/
        View view = activity.getWindow().getDecorView();
        //允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        //获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;
        WindowManager windowManager = activity.getWindowManager();
        //获取屏幕宽和高
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        //去掉状态栏
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, statusBarHeight, width, height - statusBarHeight);
        //销毁缓存信息
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    /**
     * 截取除了导航栏之外的整个屏幕
     */
    public static Bitmap screenShotWholeScreen(Activity activity) {
        View dView = activity.getWindow().getDecorView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache());
        return bitmap;
    }

    //合成三张图片
    private static Bitmap mergeBitmap(Bitmap firstBitmap, Bitmap secondBitmap, Bitmap threeBitmap) {
        Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth(), firstBitmap.getHeight() + secondBitmap.getHeight() + threeBitmap.getHeight(), firstBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(firstBitmap, new Matrix(), null);
        canvas.drawBitmap(secondBitmap, 0, firstBitmap.getHeight(), null);
        canvas.drawBitmap(threeBitmap, secondBitmap.getWidth(), firstBitmap.getHeight(), null);
        return bitmap;
    }
    //合成两张图片
    public static Bitmap mergeBitmap(Bitmap firstBitmap, Bitmap secondBitmap) {
        Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth(), firstBitmap.getHeight(),firstBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(firstBitmap, new Matrix(), null);
        canvas.drawBitmap(secondBitmap, 0, 0, null);
        return bitmap;
    }

    public static String getJDUrlwithSkuId(String skuId){
        String jdUrl = "https://item.jd.com/" + skuId + ".html";
        return jdUrl;
    }

    public static Uri getUriForFile(Context context, File file, String authorities) {
        // Build.VERSION_CODES.N == 24
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //参数：authority 需要和清单文件中配置的保持完全一致：${applicationId}.xxx
            fileUri = FileProvider.getUriForFile(context, context.getPackageName() + authorities, file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    public static void showFont(WebView webView, String content) {
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        setting.setDefaultTextEncodingName("UTF-8");
        webView.setScrollBarStyle(View.SCROLL_AXIS_NONE);
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<meta charset='utf-8' />");
        sb.append("<meta name='apple-mobile-web-app-capable' content='yes'>");
        sb.append("<meta name='apple-mobile-web-app-status-bar-style' content='black'>");
        sb.append("<meta name='viewport' content='width=device-width,initial-scale=1, minimum-scale=1.0, maximum-scale=1, user-scalable=no'>");
        sb.append("<meta name='viewport' content='width=device-width, initial-scale=1.0,user-scalable=no,maximum-scale=1.0'>");
        sb.append("<style>");
        sb.append("html,body {padding:0;margin:0;}");
        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body id='cont' >");
        sb.append(content);
        sb.append("</body>");
        sb.append("</html>");
        sb.append("<script type='text/javascript'>");
        sb.append("window.onload=function(){");
        sb.append("var src=document.getElementsByTagName('img');");
        sb.append("for (var i=0; i<src.length; i++) {");
        sb.append("url = src[i].getAttribute('src');");
        sb.append("link = url;");
        sb.append("src[i].setAttribute('src',link);");
        sb.append("if(document.body.clientWidth < src[i].naturalWidth){");
        sb.append("src[i].setAttribute('width','100%');");
        sb.append("}");
        sb.append("src[i].setAttribute('height','auto');");
        sb.append("src[i].setAttribute('style','margin-top:0px;');}}");
        sb.append("</script>");
        // if (!"".equals(content)) {
        // content = "<font size='4' color='#6a6a6a'>" + content + "</font>";
        // } else {
        // content = "<br/>";
        // }
        webView.setBackgroundResource(R.drawable.et_shape);
        // webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.loadDataWithBaseURL(null, sb.toString(), "text/html", "utf-8", null);
    }

    public static String analyzingCode(String args) {
        if (args==null) {
            return "analyzingCode is null";
        }
        String code = null;
        if (args.contains("?")) { // 解析二维码数据
            String[] s1 = args.split("[?]");
            for (int i = 0; i < s1.length; i++) {
                if (s1[i].contains("=")) {
                    code = s1[i].split("[=]")[1];
                    return code;
                }
            }
        }
        return code;
    }

    public static String getVersionname() {
        String versionname = null;
        PackageManager pm = Latte.getApplicationContext().getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(Latte.getApplicationContext().getPackageName(), 0);
            versionname = packageInfo.versionName;
            return versionname;
        } catch (PackageManager.NameNotFoundException e) {}
        return versionname;
    }
}
