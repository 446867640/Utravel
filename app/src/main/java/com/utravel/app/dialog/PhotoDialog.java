package com.utravel.app.dialog;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.utravel.app.R;
import com.utravel.app.latte.Latte;
import com.utravel.app.ui.camera.RequestCodes;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.FileUtil;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;

public class PhotoDialog extends BaseDialogDelegate implements View.OnClickListener, DialogInterface.OnKeyListener {
    LinearLayoutCompat ll_bottom;
    AppCompatTextView tv_xiangce;
    AppCompatTextView tv_paizhao;
    AppCompatTextView tv_cancel;

    public Uri imageUriFromCamera;

    public static PhotoDialog newInstance() {
        PhotoDialog delegate = new PhotoDialog();
        return delegate;
    }

    @Override
    public Object setLayout() {
        return R.layout.dialog_choice_photo;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        Util.slideToUp(rootView);
        initViews(rootView);
    }

    private void initViews(View rootView) {
        ll_bottom = (LinearLayoutCompat)rootView.findViewById(R.id.ll_bottom);
        tv_xiangce = (AppCompatTextView) rootView.findViewById(R.id.tv_xiangce);
        tv_paizhao = (AppCompatTextView) rootView.findViewById(R.id.tv_paizhao);
        tv_cancel = (AppCompatTextView) rootView.findViewById(R.id.tv_cancel);
        tv_xiangce.setOnClickListener(this);
        tv_paizhao.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        getDialog().setOnKeyListener(this);
//        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false); //点击外部
    }

    @Override
    public void onClick(View v) {
        if (v==tv_xiangce) {//相册
            pickPhoto();
        }else if (v==tv_paizhao) {//拍照
            takePhoto();
        }else if (v==tv_cancel) {//取消

        }
        dismissWithAnimationToDown(rootView);
    }

    /**
     * 设置弹窗的显示位置和大小
     */
    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
//        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = Util.getScreenWidth(_mActivity)- DensityUtil.dp2px(_mActivity,16);
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * 设置dialog消失动画
     */
    public void dismissWithAnimationToDown(View view){
        Animation slide = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f);
        slide.setDuration(400);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        view.startAnimation(slide);
        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) { getDialog().cancel();}
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    /**
     * 设置物理按键返回dialog消失动画
     */
    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN) {
            dismissWithAnimationToDown(rootView);
            return true;
        }
        return false;
    }

    private String getPhotoName(){
        return FileUtil.getFileNameByTime("IMG","jpg");
    }

    private void takePhoto(){ //调起相机
        imageUriFromCamera = createImageUri(getContext());
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult( intent, RequestCodes.TAKE_PHOTO );
        SharedPreferencesUtil.putString(Latte.getApplicationContext(),"PHOTO", "take_photo");
        LatteLogger.e(
                SharedPreferencesUtil.getString(Latte.getApplicationContext(),"PHOTO"),
                SharedPreferencesUtil.getString(Latte.getApplicationContext(),"PHOTO"));
//        final String currentPhotoName = getPhotoName();
//        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        final File tempFile = new File(Config.CAMERA_PHOTO_IDR,currentPhotoName);
//
//        //兼容 7.0以上的写法
//        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.N) {
//            final ContentValues contentValues = new ContentValues(1);
//            contentValues.put(MediaStore.Images.Media.DATA,tempFile.getPath());
//            final Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
//            //需要将Uri路径转化为实际路径
//            final File realFile = Util.getFileByPath(FileUtil.getRealFilePath(getContext(),uri));
//            final Uri realUri = Uri.fromFile(realFile);
//            CameraImageBean.getInstance().setPath(realUri);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
//        }else {
//            final Uri fileUri = Uri.fromFile(tempFile);
//            CameraImageBean.getInstance().setPath(fileUri);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
//        }
//        startActivityForResult( intent, RequestCodes.TAKE_PHOTO );
//        SharedPreferencesUtil.putString(Latte.getApplicationContext(),"PHOTO", "take_photo");
//        LatteLogger.e(
//                SharedPreferencesUtil.getString(Latte.getApplicationContext(),"PHOTO"),
//                SharedPreferencesUtil.getString(Latte.getApplicationContext(),"PHOTO"));
    }

    private void pickPhoto(){ //选择相册
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }else {
            intent.setAction(Intent.ACTION_PICK);
        }
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(intent, RequestCodes.PICK_PHOTO);
        SharedPreferencesUtil.putString(Latte.getApplicationContext(),"PHOTO", "pick_photo");
        LatteLogger.e(
                SharedPreferencesUtil.getString(Latte.getApplicationContext(),"PHOTO"),
                SharedPreferencesUtil.getString(Latte.getApplicationContext(),"PHOTO"));

//        final Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(Intent.createChooser(intent,"选择获取图片的方式"), RequestCodes.PICK_PHOTO);
//        SharedPreferencesUtil.putString(Latte.getApplicationContext(),"PHOTO", "pick_photo");
//        LatteLogger.e(
//                SharedPreferencesUtil.getString(Latte.getApplicationContext(),"PHOTO"),
//                SharedPreferencesUtil.getString(Latte.getApplicationContext(),"PHOTO"));
    }

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
}
