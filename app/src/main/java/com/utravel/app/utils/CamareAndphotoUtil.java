package com.utravel.app.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.File;

public class CamareAndphotoUtil {
	public static final int REQUEST_CODE_FROM_CAMERA = 5001;
	public static final int REQUEST_CODE_FROM_ALBUM = 5002;
	public static final int REQUEST_CODE_PHOTO_CLIP = 5003;


	/**
	 * 存放拍照图片的uri地址
	 */
	public static Uri imageUriFromCamera;

	/**
	 * 显示获取照片不同方式对话框
	 * @param activity
	 */
	public static void showImagePickDialog(final Activity activity) {
		String title = "选择获取图片方式";
		String[] items = new String[] { "拍照", "相册" };
		new AlertDialog.Builder(activity).setTitle(title)
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
							case 0:
								pickImageFromCamera(activity);
								break;

							case 1:
								pickImageFromAlbum2(activity);
								break;
						}
					}
				}).show();
	}
	/**
	 * 显示获取照片不同方式popupwindow
	 * @param activity
	 */
//	public static void showImagePickDialog1(final Activity activity) {
//		View parent = ((ViewGroup)((Activity)activity).findViewById(android.R.id.content))
//				.getChildAt(0);
//
//		View popView = View.inflate(activity, R.layout.pop_choice_pic, null);
//		TextView tv_exit_cancel = (TextView) popView.findViewById(R.id.tv_exit_cancel);
//		TextView tv_exit_ok = (TextView) popView.findViewById(R.id.tv_exit_ok);
//
//		int width = activity.getResources().getDisplayMetrics().widthPixels;
//		int height = activity.getResources().getDisplayMetrics().heightPixels;
//
//		final PopupWindow popWindow = new PopupWindow(popView, width, height);
//		popWindow.setFocusable(true);
//		popWindow.setOutsideTouchable(false);// 设置同意在外点击消失
//
//		View.OnClickListener listener = new View.OnClickListener() {
//
//			public void onClick(View v) {
//				switch (v.getId()) {
//					case R.id.tv_exit_cancel:
//						pickImageFromCamera(activity);
//						break;
//					case R.id.tv_exit_ok:
//						pickImageFromAlbum2(activity);
//						break;
//				}
//				popWindow.dismiss();
//			}
//		};
//		tv_exit_cancel.setOnClickListener(listener);
//		tv_exit_ok.setOnClickListener(listener);
//		ColorDrawable dw = new ColorDrawable(0x90000000);
//		popWindow.setBackgroundDrawable(dw);
//		popWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
//	}

	//打开相机拍照获取图片(该方式获取到的图片是缩略图）
	public static void pickImageFromCamera(final Activity activity) {
		imageUriFromCamera = createImageUri(activity);
		Intent intent = new Intent();
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriFromCamera);
		activity.startActivityForResult(intent, REQUEST_CODE_FROM_CAMERA);
	}

	//打开相机拍照获取图片(该方式获取到的图片是原图）7.0以上
	public static void pickImageFromCamera7(final Activity activity, File file, String authorities) {
		//创建打开本地相机的意图对象
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		//设置图片的保存位置(兼容Android7.0)
		Uri fileUri = Util.getUriForFile(activity, file, authorities);
		//指定图片保存位置
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		//开启意图
		activity.startActivityForResult(intent, REQUEST_CODE_FROM_CAMERA);
	}

	/**
	 * 打开本地相册选取图片
	 * @param activity
	 */
	public static void pickImageFromAlbum(final Activity activity) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		activity.startActivityForResult(intent, REQUEST_CODE_FROM_ALBUM);
	}

	/**
	 * 打开本地相册选取图片2
	 *
	 * @param activity
	 */
	public static void pickImageFromAlbum2(final Activity activity) {
		Intent intent = new Intent();
		if (Build.VERSION.SDK_INT < 19) {
			intent.setAction(Intent.ACTION_GET_CONTENT);
		}else {
			intent.setAction(Intent.ACTION_PICK);
		}
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
		activity.startActivityForResult(intent, REQUEST_CODE_FROM_ALBUM);
	}

	/**
	 * 创建一条图片uri，用于保存拍照后的图片
	 *
	 * @param context
	 * @return
	 */
	private static Uri createImageUri(Context context) {
		String name = "traffic" + System.currentTimeMillis();
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, name);
		values.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".jpeg");
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		Uri uri = context.getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		return uri;
	}

	/**
	 * 删除一条图片
	 * @param context
	 * @param uri
	 */
	public static void deleteImageUri(Context context, Uri uri) {
		context.getContentResolver().delete(imageUriFromCamera, null, null);
	}

	/**
	 * 获取图片文件路径
	 * @param context
	 * @param uri
	 * @return
	 */
	public static String getImageAbsolutePath(Context context, Uri uri){

		return null;
	}

	/****
	 * 调用系统自带切图工具对图片进行裁剪
	 * 微信也是
	 *
	 * @param uri
	 */
	public static void photoClip(final Activity activity,Uri uri) {
		// 调用系统中自带的图片剪裁
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		activity.startActivityForResult(intent, REQUEST_CODE_PHOTO_CLIP);
	}
}
