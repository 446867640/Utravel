package com.utravel.app.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.utravel.app.BuildConfig;
import com.utravel.app.R;
import com.utravel.app.config.Config;
import com.utravel.app.latte.Latte;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.Util;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.text.DecimalFormat;

public class DownAPKService extends Service{
	private final int NotificationID = 0x10000;
	private NotificationManager mNotificationManager = null;
	private NotificationCompat.Builder builder;
	private HttpHandler<File> mDownLoadHelper;
	// 文件下载路径
	private String APK_url = "";
	// 文件保存路径(如果有SD卡就保存SD卡,如果没有SD卡就保存到手机包名下的路径)
	private String APK_dir = "";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
//		Toast.makeText(getApplicationContext(), "有新版本，已在后台为您下载更新", Toast.LENGTH_LONG).show();
		// 创建保存路径
		initAPKDir();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("onStartCommand");
		LatteLogger.e("下载onStartCommand","下载onStartCommand");

		DownFile(intent, APK_dir + Config.APP + ".apk");
		return super.onStartCommand(intent, flags, startId);
	}

	private void initAPKDir() {
		/**
		 * 创建路径的时候一定要用[/],不能使用[\],但是创建文件夹加文件的时候可以使用[\].
		 * [/]符号是Linux系统路径分隔符,而[\]是windows系统路径分隔符 Android内核是Linux.
		 */
		APK_dir = Util.getAPKDir(Latte.getApplicationContext());
		LatteLogger.e("APK_dir.......", "........APK_dir="+APK_dir);
		File destDir = new File(APK_dir);
		// 判断文件夹是否存在
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
	}
	//判断是否插入SD卡
	private boolean isHasSdcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	private void DownFile(Intent intent, String target_name) {
		LatteLogger.e("下载DownFile","下载DownFile");
		if (intent==null){
			LatteLogger.e("下载DownFile  intent==null","下载DownFile  intent==null");
			return;
		}
		// 接收Intent传来的参数:
		APK_url = intent.getStringExtra("apk_url");
		mDownLoadHelper = new HttpUtils().download(APK_url, target_name, true, true, new RequestCallBack<File>() {
			@Override
			public void onStart() {
				super.onStart();
				LatteLogger.e("开始下载文件","开始下载文件");
				mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				builder = new NotificationCompat.Builder(getApplicationContext());
				builder.setSmallIcon(R.mipmap.logo);
				builder.setTicker("正在下载新版本");
				builder.setContentTitle(getApplicationName());
				builder.setContentText("正在下载,请稍后...");
				builder.setNumber(0);
				builder.setAutoCancel(true);
				mNotificationManager.notify(NotificationID,builder.build());
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				super.onLoading(total, current, isUploading);
				LatteLogger.e("文件下载中","文件下载中");
				int x = Long.valueOf(current).intValue();
				int totalS = Long.valueOf(total).intValue();
				builder.setProgress(totalS, x, false);
				builder.setContentInfo(getPercent(x, totalS));
				mNotificationManager.notify(NotificationID,builder.build());
			}

			@Override
			public void onSuccess(ResponseInfo<File> responseInfo) {
				LatteLogger.e("文件下载完成","文件下载完成");
				installAPK(responseInfo);
				mNotificationManager.notify(NotificationID, builder.build());

//				Uri uri = Uri.fromFile(new File(responseInfo.result.getPath()));
//				installAPK(uri);

//				Intent installIntent = new Intent(Intent.ACTION_VIEW);
//				System.out.println(responseInfo.result.getPath());
//				Uri uri = Uri.fromFile(new File(responseInfo.result.getPath()));
//				installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
//				installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				PendingIntent mPendingIntent = PendingIntent.getActivity(DownAPKService.this, 0, installIntent, 0);
//				builder.setContentText("下载完成,请点击安装");
//				builder.setContentIntent(mPendingIntent);
//				mNotificationManager.notify(NotificationID, builder.build());
//				// 震动提示
//				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//				vibrator.vibrate(1000L);// 参数是震动时间(long类型)
//				stopSelf();
//				startActivity(installIntent);// 下载完成之后自动弹出安装界面
//				mNotificationManager.cancel(NotificationID);
			}

			@Override
			public void onFailure(com.lidroid.xutils.exception.HttpException e, String s) {
				LatteLogger.e("文件下载完成","文件下载完成");
				mNotificationManager.cancel(NotificationID);
				Toast.makeText(getApplicationContext(), "下载失败！", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancelled() {
				super.onCancelled();
				System.out.println("文件下载结束，停止下载器");
				mDownLoadHelper.cancel();
			}
		});
	}

	/**
	 *
	 * @param x  当前值
	 * @param total  总值 [url=home.php?mod=space&uid=7300]@return[/url] 当前百分比
	 * @Description:返回百分之值
	 */
	private String getPercent(int x, int total) {
		String result = "";// 接受百分比的值
		double x_double = x * 1.0;
		double tempresult = x_double / total;
		// 百分比格式，后面不足2位的用0补齐 ##.00%
		DecimalFormat df1 = new DecimalFormat("0.00%");
		result = df1.format(tempresult);
		return result;
	}
	/**
	 * @return
	 * @Description:获取当前应用的名称
	 */
	private String getApplicationName() {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = getApplicationContext().getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(
					getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName = (String) packageManager
				.getApplicationLabel(applicationInfo);
		return applicationName;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopSelf();
	}
	/**
	 * 安装apk文件
	 */
	private void installAPK(Uri apk) {
		// 通过Intent安装APK文件
		Intent intents = new Intent();
		intents.setAction("android.intent.action.VIEW");
		intents.addCategory("android.intent.category.DEFAULT");
		intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intents.setType("application/vnd.android.package-archive");
		intents.setData(apk);
		intents.setDataAndType(apk,"application/vnd.android.package-archive");
		mNotificationManager.cancel(NotificationID);
		startActivity(intents);
		// 如果不加上这句的话在apk安装完成之后点击单开会崩溃
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(0);
		stopSelf();
	}

	private void installAPK(ResponseInfo<File> responseInfo) {
		File apkFile = new File(responseInfo.result.getPath());
		if (!apkFile.exists()) {
			return;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
			Uri apkUri = FileProvider.getUriForFile(Latte.getApplicationContext(), BuildConfig.APPLICATION_ID + ".apkprovider", apkFile);
			Intent install = new Intent(Intent.ACTION_VIEW);
			install.addCategory(Intent.CATEGORY_DEFAULT);
			install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			install.setDataAndType(apkUri, "application/vnd.android.package-archive");
			startActivity(install);
		} else {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.setType("application/vnd.android.package-archive");
			intent.setData(Uri.fromFile(apkFile));
			intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
//		try {
////			File apkFile = new File(responseInfo.result.getPath());
//			String path = APK_dir + Config.APP + ".apk";
//			File apkFile = new File(path);
//
//			LatteLogger.e("apk路径="+responseInfo.result.getPath(), "apk路径="+responseInfo.result.getPath());
//			Intent intent = new Intent(Intent.ACTION_VIEW);
//			intent.setAction("android.intent.action.VIEW");
//			intent.addCategory("android.intent.category.DEFAULT");
//			//判断是否是AndroidN以及更高的版本
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//				Uri contentUri = FileProvider.getUriForFile(Latte.getApplicationContext(), BuildConfig.APPLICATION_ID + ".apkprovider", apkFile);
//				intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//				intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
//			} else {
//				intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
//			}
//			startActivity(intent);
////			stopSelf();
//		} catch (Exception e) {
//			LatteLogger.e("apk安装异常", e.getMessage());
//		}
	}
}
