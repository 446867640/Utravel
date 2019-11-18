package com.utravel.app.jpush;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.utravel.app.R;
import com.utravel.app.activities.base.NewsActivity;
import com.utravel.app.config.Config;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "极光推送";

	//20190920自定义消息获取
	private Bitmap bitmap = null;
	private NotificationManager notifyManager = null;
	private NotificationCompat.Builder notifyBuilder = null;
	private Notification notification = null;
	private String url = "";
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			if(bitmap!=null){
				notifyBuilder.setLargeIcon(bitmap);
			}else{
				notifyBuilder.setSmallIcon(R.mipmap.logo);
			}
			notification = notifyBuilder.build();
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notifyManager.notify(1000, notification);
		}
	};

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			LatteLogger.e(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
			if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
				String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
				LatteLogger.e(TAG, "[MyReceiver] 接收Registration Id : " + regId);
				//send the Registration Id to your server...
			} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
				LatteLogger.e(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
//				processCustomMessage(context, bundle);
			} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
				LatteLogger.e(TAG, "[MyReceiver] 接收到推送下来的通知");
				int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
				LatteLogger.e(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
			}  else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
				boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
				LatteLogger.e(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
			} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
				LatteLogger.e(TAG, "[MyReceiver] 用户点击打开了通知");
				LatteLogger.e("判断app是否在后台,state==" + Util.isAppAlive(context, Config.APP_PACKAGENAME), Config.APP_PACKAGENAME);
				method(context,bundle);
			}else {
				LatteLogger.e(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
			}
		} catch (Exception e){}
	}

	private void method(Context context, Bundle bundle) {
		if (printBundle(bundle).contains("type")) {
			SharedPreferencesUtil.putBoolean(context, "isJPush", true);
			if (printBundle(bundle).contains("announcement")) {
				SharedPreferencesUtil.putBoolean(context, "isJPush_announcement", true);
			}else {
				SharedPreferencesUtil.putBoolean(context, "isJPush_notice", true);
			}
		}
		if (Util.isAppAlive(context, Config.APP_PACKAGENAME)!=0) {
			methods2(context,bundle);
		}else {
			//启动app
			Intent intent = context.getPackageManager().getLaunchIntentForPackage(Config.APP_PACKAGENAME);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(intent);
		}
	}

	private void methods2(Context context, Bundle bundle) {
		//打开自定义的Activity
		if (printBundle(bundle).contains("type")) {
			if (printBundle(bundle).contains("announcement")) {
				//系统公告
				Intent anno = new Intent(context, NewsActivity.class);
				anno.putExtra("news", 0);
				anno.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(anno);
			}else {
				//消息列表
				Intent anno = new Intent(context,NewsActivity.class);
				anno.putExtra("news", 1);
				anno.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(anno);
			}
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
					Logger.i(TAG, "This message has no Extra data");
					continue;
				}
				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();
					while (it.hasNext()) {
						String myKey = it.next();
						sb.append("\nkey:" + key + ", value: [" + myKey + " - " +json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					Logger.e(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.get(key));
			}
		}
		return sb.toString();
	}
	
	//send msg to MainActivity
//	private void processCustomMessage(Context context, Bundle bundle) {
//		if (MainActivity.isForeground) {
//			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//			if (!ExampleUtil.isEmpty(extras)) {
//				try {
//					JSONObject extraJson = new JSONObject(extras);
//					if (extraJson.length() > 0) {
//						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//					}
//				} catch (JSONException e) {}
//			}
//			LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
//		}
//	}
}
