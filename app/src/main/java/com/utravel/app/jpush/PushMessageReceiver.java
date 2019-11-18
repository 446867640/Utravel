package com.utravel.app.jpush;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.utravel.app.activities.proxy.MainActivity;
import com.utravel.app.config.Config;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

public class PushMessageReceiver extends JPushMessageReceiver{
    private static final String TAG = "PushMessageReceiver";
    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        LatteLogger.e(TAG,"[onMessage] "+customMessage);
//        processCustomMessage(context,customMessage);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage message) {
        LatteLogger.e(TAG,"[点击打开通知] " + message);
        try{
            //打开自定义的Activity
            String extras = message.notificationExtras;
            if (!TextUtils.isEmpty(extras)) {
                JSONObject json = JSON.parseObject(extras);
                if (json.containsKey("type")) {
                    String type = json.getString("type");
                    method(context,type);
                }
            }
        }catch (Throwable throwable){}
    }

    @Override
    public void onMultiActionClicked(Context context, Intent intent) {
        LatteLogger.e(TAG, "[onMultiActionClicked] 用户点击了通知栏按钮");
        String nActionExtra = intent.getExtras().getString(JPushInterface.EXTRA_NOTIFICATION_ACTION_EXTRA);

        //开发者根据不同 Action 携带的 extra 字段来分配不同的动作。
        if(nActionExtra==null){
            LatteLogger.e(TAG,"ACTION_NOTIFICATION_CLICK_ACTION nActionExtra is null");
            return;
        }
        if (nActionExtra.equals("my_extra1")) {
            LatteLogger.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮一");
        } else if (nActionExtra.equals("my_extra2")) {
            LatteLogger.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮二");
        } else if (nActionExtra.equals("my_extra3")) {
            LatteLogger.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮三");
        } else {
            LatteLogger.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮未定义");
        }
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage message) {
        LatteLogger.e(TAG,"[onNotifyMessageArrived] "+message);
    }

    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage message) {
        LatteLogger.e(TAG,"[onNotifyMessageDismiss] "+message);
    }

    @Override
    public void onRegister(Context context, String registrationId) {
        LatteLogger.e(TAG,"[onRegister] "+registrationId);
    }

    @Override
    public void onConnected(Context context, boolean isConnected) {
        LatteLogger.e(TAG,"[onConnected] "+isConnected);
    }

    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
        LatteLogger.e(TAG,"[onCommandResult] "+cmdMessage);
    }

    @Override
    public void onTagOperatorResult(Context context,JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onTagOperatorResult(context,jPushMessage);
        super.onTagOperatorResult(context, jPushMessage);
    }
    @Override
    public void onCheckTagOperatorResult(Context context,JPushMessage jPushMessage){
        TagAliasOperatorHelper.getInstance().onCheckTagOperatorResult(context,jPushMessage);
        super.onCheckTagOperatorResult(context, jPushMessage);
    }
    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onAliasOperatorResult(context,jPushMessage);
        super.onAliasOperatorResult(context, jPushMessage);
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onMobileNumberOperatorResult(context,jPushMessage);
        super.onMobileNumberOperatorResult(context, jPushMessage);
    }

    //send msg to MainActivity
//    private void processCustomMessage(Context context, CustomMessage customMessage) {
//        if (MainActivity.isForeground) {
//            String message = customMessage.message;
//            String extras = customMessage.extra;
//            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//            if (!ExampleUtil.isEmpty(extras)) {
//                try {
//                    JSONObject extraJson = new JSONObject(extras);
//                    if (extraJson.length() > 0) {
//                        msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//                    }
//                } catch (JSONException e) {
//
//                }
//
//            }
//            LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
//        }
//    }


    private void method(Context context, String type) {
        SharedPreferencesUtil.putBoolean(context, "isJPush", true);
        if (type.equals("announcement")) {
            SharedPreferencesUtil.putBoolean(context, "isJPush_announcement", true);
        }else {
            SharedPreferencesUtil.putBoolean(context, "isJPush_notice", true);
        }
        if (Util.isAppAlive(context, Config.APP_PACKAGENAME) != 0) { //APP未退出
            Intent anno = new Intent(context, MainActivity.class);
            anno.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(anno);
        }else { //退出APP
            //启动app
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(Config.APP_PACKAGENAME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
//        else if (Util.isAppAlive(context, Config.APP_PACKAGENAME) == 2) {
//            if (type.equals(Config.ANNOUNCEMENT)) { //系统公告
//                Intent anno = new Intent(context, NewsActivity.class);
//                anno.putExtra(Config.NEWS_KEY, 0);
//                anno.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                context.startActivity(anno);
//            }else { //消息列表
//                Intent anno = new Intent(context,NewsActivity.class);
//                anno.putExtra(Config.NEWS_KEY, 1);
//                anno.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                context.startActivity(anno);
//            }
    }
}
