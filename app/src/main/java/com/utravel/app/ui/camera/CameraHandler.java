package com.utravel.app.ui.camera;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.utravel.app.R;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.PermissionCheckerDelegate;
import com.utravel.app.dialog.PhotoDialog;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.Util;
import java.io.File;

/**
 * 照片处理类
 */
public class CameraHandler implements View.OnClickListener{

    private final AlertDialog DIALOG;
    private final PermissionCheckerDelegate DELEGATE;

    public CameraHandler(PermissionCheckerDelegate delegate) {
        this.DELEGATE = delegate;
        DIALOG = new AlertDialog.Builder(DELEGATE.getContext()).create();
    }

    final void beginCameraDialog() {
        DIALOG.show();
        Window window = DIALOG.getWindow();
        if (window!=null) {
            window.setContentView(R.layout.dialog_choice_photo);
            window.setWindowAnimations(R.style.Animation_Design_BottomSheetDialog);

            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            params.width = Util.getScreenWidth(DIALOG.getContext())- DensityUtil.dp2px(DIALOG.getContext(),16);
            window.setAttributes(params);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private String getPhotoName(){
        return Util.getFileNameByTime("IMG","jpg");
    }

    private void takePhoto(){ //调起相机
        final String currentPhotoName = getPhotoName();
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final File tempFile = new File(Config.CAMERA_PHOTO_IDR,currentPhotoName);

        //兼容 7.0以上的写法
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.N) {
            final ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA,tempFile.getPath());
            final Uri uri = DELEGATE.getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
            //需要将Uri路径转化为实际路径
            final File realFile =
                    Util.getFileByPath(Util.getRealFilePath(DELEGATE.getContext(),uri));
            final Uri realUri = Uri.fromFile(realFile);
            CameraImageBean.getInstance().setPath(realUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        }else {
            final Uri fileUri = Uri.fromFile(tempFile);
            CameraImageBean.getInstance().setPath(fileUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
        }
        DELEGATE.startActivityForResult( intent, RequestCodes.TAKE_PHOTO );
    }

    private void pickPhoto(){ //选择相册
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        DELEGATE.startActivityForResult(
                Intent.createChooser(intent,"选择获取图片的方式"), RequestCodes.PICK_PHOTO);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.tv_xiangce) {//相册

        }else if (v.getId()==R.id.tv_paizhao) {//拍照

        }else if (v.getId()==R.id.tv_cancel) {//取消

        }
    }
}
