package com.utravel.app.activities.base;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.activities.proxy.LoginActivity;
import com.utravel.app.activities.proxy.MainActivity;
import com.utravel.app.config.Config;
import com.utravel.app.entity.MemeberEntity;
import com.utravel.app.entity.VersionBean;
import com.utravel.app.latte.Latte;
import com.utravel.app.ui.camera.CameraImageBean;
import com.utravel.app.ui.camera.RequestCodes;
import com.utravel.app.utils.CamareAndphotoUtil;
import com.utravel.app.utils.FileUtil;
import com.utravel.app.utils.GlideCircleTransform;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import com.kyleduo.switchbutton.SwitchButton;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private View status_bar;
    private SwitchButton sb_xiaoxi;
    private AppCompatImageView iv_back, ivTouxiang;
    private AppCompatTextView tv_title, tv_nicheng, tvTaobao, tvWeixin, tvHuancen, tvExit, tv_pay_psw, tv_version;
    private LinearLayoutCompat llTouxiang, llNicheng, llTaobao, llWeixin, llPhone, llMima, ll_pay_psw, ll_updata, llXiaoxi, llHuancun;

    private MemeberEntity.DataBean memeberData;

    private boolean has_payment_password = false;
    private String mobile = null;
    private String avatar_url;
    private int gender = 0;
    private int district_id = 0;
    private int avatar_id;

    private File photoFile; //相机拍照图片保持sdcard文件

    @Override
    protected int getLayoutId() {
        return R.layout.delegate_setting;
    }

    @Override
    protected void initParams() {
        initPhotoFile();
        initViews();
        initViewParams();
        initListener();
    }

    private void initPhotoFile() {
        photoFile = new File(Util.getPhotoDir(SettingActivity.this), "avator.png");
        if ((photoFile.getParentFile() != null) && (!photoFile.getParentFile().exists())) {
            photoFile.getParentFile().mkdirs();
        }
    }

    private void initViews() {
        status_bar = (View) findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView)findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) findViewById(R.id.tv_title);
        llTouxiang = (LinearLayoutCompat)findViewById( R.id.ll_touxiang );
        ivTouxiang = (AppCompatImageView)findViewById( R.id.iv_touxiang );
        llNicheng = (LinearLayoutCompat)findViewById( R.id.ll_nicheng );
        tv_nicheng = (AppCompatTextView) findViewById( R.id.tv_nicheng );
        llTaobao = (LinearLayoutCompat)findViewById( R.id.ll_taobao );
        tvTaobao = (AppCompatTextView)findViewById( R.id.tv_taobao );
        llWeixin = (LinearLayoutCompat)findViewById( R.id.ll_weixin );
        tvWeixin = (AppCompatTextView)findViewById( R.id.tv_weixin );
        llPhone = (LinearLayoutCompat)findViewById( R.id.ll_phone );
        llMima = (LinearLayoutCompat)findViewById( R.id.ll_mima );
        llXiaoxi = (LinearLayoutCompat)findViewById( R.id.ll_xiaoxi );
        sb_xiaoxi = (SwitchButton) findViewById( R.id.sb_xiaoxi );
        llHuancun = (LinearLayoutCompat)findViewById( R.id.ll_huancun );
        tvHuancen = (AppCompatTextView)findViewById( R.id.tv_huancen );
        tvExit = (AppCompatTextView)findViewById( R.id.tv_exit );
        tv_pay_psw = (AppCompatTextView)findViewById( R.id.tv_pay_psw );
        ll_pay_psw = (LinearLayoutCompat) findViewById( R.id.ll_pay_psw );
        ll_updata = (LinearLayoutCompat) findViewById( R.id.ll_updata );
        tv_version = (AppCompatTextView) findViewById( R.id.tv_version );
    }

    private void initViewParams() {
        tv_title.setText("设置");
        tv_version.setText("当前版本号：" + Util.getVersionname());

        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(SettingActivity.this);
        status_bar.setLayoutParams(params);
        
        if (TextUtils.isEmpty(SharedPreferencesUtil.getString(this,"mobile"))) {
            llPhone.setVisibility(View.GONE);
            llMima.setVisibility(View.GONE);
            ll_pay_psw.setVisibility(View.GONE);
        }else {
            llPhone.setVisibility(View.VISIBLE);
            llMima.setVisibility(View.VISIBLE);
            ll_pay_psw.setVisibility(View.VISIBLE);
        }
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        llTouxiang.setOnClickListener(this);
        llNicheng.setOnClickListener(this);
        llTaobao.setOnClickListener(this);
        llWeixin.setOnClickListener(this);
        llPhone.setOnClickListener(this);
        llMima.setOnClickListener(this);
        tvHuancen.setOnClickListener(this);
        tvExit.setOnClickListener(this);
        ll_pay_psw.setOnClickListener(this);
        ll_updata.setOnClickListener(this);
        sb_xiaoxi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(SettingActivity.this,"打开",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(SettingActivity.this,"关闭",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) {//返回
            finish();
        }else if (v==llTouxiang) {//设置头像
            showAvatarPopwindow();
        }else if (v==llNicheng) {//设置昵称
            showNamePopwindow();
        }else if (v==llTaobao) {//淘宝授权

        }else if (v==llWeixin) {//微信绑定

        }else if (v==ll_pay_psw) {
            Intent intent = new Intent(SettingActivity.this, GRChangePayPwdActivity.class);
            if (has_payment_password) {
                intent.putExtra("has_payment_password", "0");
            }else {
                intent.putExtra("has_payment_password", "1");
            }
            startActivity(intent);
        }else if (v==llPhone) {//修改手机号
            if (mobile != null) {
                Intent intent = new Intent(SettingActivity.this, ChangePhoneActivity.class);
                intent.putExtra("mobile", mobile);
                startActivity(intent);
            }
        }else if (v==llMima) {//修改密码
            Intent intent = new Intent(SettingActivity.this, ChangeLoginMMActivity.class);
            intent.putExtra("type", "change");
            startActivity(intent);
        }else if (v==tvHuancen) {//清除缓存

        }else if (v==tvExit) {//退出登陆
            loadProcess();
            delExitLogin();
        }else if (v==ll_updata) {//检测更新
            loadProcess();
            checkVersion();
        }
    }

    @Override
    public boolean setIsDark() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMemeberData();
    }

    private void showAvatarPopwindow() {
        View parent = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        View popView = View.inflate(this, R.layout.dialog_photo, null);
        AppCompatTextView btnCamera = (AppCompatTextView) popView.findViewById(R.id.btn_camera_pop_camera);
        AppCompatTextView btnAlbum = (AppCompatTextView) popView.findViewById(R.id.btn_camera_pop_album);
        AppCompatTextView btnCancel = (AppCompatTextView) popView.findViewById(R.id.btn_camera_pop_cancel);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        final PopupWindow popWindow = new PopupWindow(popView, width, height);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);// 设置同意在外点击消失
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_camera_pop_camera: //拍照
                        if (ContextCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED  || ContextCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(SettingActivity.this, Manifest.permission.CAMERA)) {
                                show("您已经拒绝过一次");
                            }
                            ActivityCompat.requestPermissions(SettingActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CamareAndphotoUtil.REQUEST_CODE_FROM_CAMERA);
                        } else {//有权限直接调用系统相机拍照
//                            CamareAndphotoUtil.pickImageFromCamera7(SettingActivity.this, photoFile, Config.PHOTO_AUTHORITIES);
                            CamareAndphotoUtil.pickImageFromCamera(SettingActivity.this);
                        }
                        break;
                    case R.id.btn_camera_pop_album: //相册
                        if (ContextCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED  || ContextCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(SettingActivity.this, Manifest.permission.CAMERA)) {
                                show("您已经拒绝过一次");
                            }
                            ActivityCompat.requestPermissions(SettingActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CamareAndphotoUtil.REQUEST_CODE_FROM_CAMERA);
                        } else {//有权限直接调用系统相册
                            CamareAndphotoUtil.pickImageFromAlbum2(SettingActivity.this);
                        }
                        break;
                    case R.id.btn_camera_pop_cancel:
                        break;
                }
                popWindow.dismiss();
            }
        };
        btnCamera.setOnClickListener(listener);
        btnAlbum.setOnClickListener(listener);
        btnCancel.setOnClickListener(listener);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        popWindow.setBackgroundDrawable(dw);
        popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CamareAndphotoUtil.REQUEST_CODE_FROM_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                LatteLogger.e( "相机授权成功", "相机授权成功");
                CamareAndphotoUtil.pickImageFromCamera7(SettingActivity.this, photoFile, Config.PHOTO_AUTHORITIES);
            } else {
                LatteLogger.e( "相机授权失败", "相机授权失败");
            }
        }
        if (requestCode == CamareAndphotoUtil.REQUEST_CODE_FROM_CAMERA) {
            CamareAndphotoUtil.pickImageFromAlbum2(SettingActivity.this);
        }
    }

    private void takePhoto(){ //调起相机
        final String currentPhotoName = FileUtil.getFileNameByTime("IMG","jpg");
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final File tempFile = new File(Config.CAMERA_PHOTO_IDR,currentPhotoName);

        //兼容 7.0以上的写法
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.N) {
            final ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA,tempFile.getPath());
            final Uri uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
            //需要将Uri路径转化为实际路径
            final File realFile = Util.getFileByPath(FileUtil.getRealFilePath(this,uri));
            final Uri realUri = Uri.fromFile(realFile);
            CameraImageBean.getInstance().setPath(realUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        }else {
            final Uri fileUri = Uri.fromFile(tempFile);
            CameraImageBean.getInstance().setPath(fileUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
        }
        startActivityForResult( intent, RequestCodes.TAKE_PHOTO );
        SharedPreferencesUtil.putString(Latte.getApplicationContext(),"PHOTO", "take_photo");
        LatteLogger.e(
                SharedPreferencesUtil.getString(Latte.getApplicationContext(),"PHOTO"),
                SharedPreferencesUtil.getString(Latte.getApplicationContext(),"PHOTO"));
    }

    private void pickPhoto(){ //选择相册
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent,"选择获取图片的方式"), RequestCodes.PICK_PHOTO);
        SharedPreferencesUtil.putString(Latte.getApplicationContext(),"PHOTO", "pick_photo");
    }

    private void showNamePopwindow() {
        View parent = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        View popView = View.inflate(this, R.layout.dialog_nicheng, null);
        final AppCompatEditText et_name = (AppCompatEditText) popView.findViewById(R.id.et_name);
        AppCompatTextView tv_cancel = (AppCompatTextView) popView.findViewById(R.id.tv_cancel);
        AppCompatTextView tv_ok = (AppCompatTextView) popView.findViewById(R.id.tv_ok);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        final PopupWindow popWindow = new PopupWindow(popView, width, height);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);// 设置同意在外点击消失
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_cancel:
                        break;
                    case R.id.tv_ok:
                        String name = et_name.getText().toString();
                        if (et_name.getText().toString().length()==0) {
                            Toast.makeText(SettingActivity.this,"昵称不能为空",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        loadProcess();
                        patchMemeberData(name);
                        break;
                }
                popWindow.dismiss();
            }
        };
        tv_cancel.setOnClickListener(listener);
        tv_ok.setOnClickListener(listener);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        popWindow.setBackgroundDrawable(dw);
        popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data==null) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CamareAndphotoUtil.REQUEST_CODE_FROM_CAMERA) {// 从拍照
            if (resultCode == RESULT_CANCELED) {
                return;
            }
            if (data != null) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = bundle.getParcelable("data");
                loadProcess();
                uploadimg(getCameraImage(bundle));
            }
        }
        if (requestCode == CamareAndphotoUtil.REQUEST_CODE_FROM_ALBUM) {// 从相册取
            if (resultCode == RESULT_CANCELED) {
                return;
            }
            if (resultCode == RESULT_OK) {
                try {
                    Uri imageUri = data.getData();
                    if (imageUri != null) {
                        Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        loadProcess();
                        uploadimg(urlToStr(imageUri));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    show("异常");
                }
            }
        }
    }

    private String getCameraImage(Bundle bundle) {
        String strState = Environment.getExternalStorageState();
        if (!strState.equals(Environment.MEDIA_MOUNTED)) {
            LatteLogger.i("TAG", "SD卡不存在");
        }
        String fileName = Config.APP + System.currentTimeMillis() + ".jpg"; // 此处可以改为时间
        Bitmap bitmap = (Bitmap) bundle.get("data");
        File file = new File(Environment.getExternalStorageDirectory()
                .toString() + "/tu/");
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

    public String urlToStr(Uri uri) {
        String path = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            Cursor cursor = SettingActivity.this.getContentResolver().query(
                    uri, new String[] { MediaStore.Images.Media.DATA }, null,
                    null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
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

    private void uploadimg(final String filePath) {
        String url = Config.UPLOAD_IMG_URL;
        final String apiName = "上传图片接口";
        File file = new File(filePath);
        String fileName = file.getName();
        OkHttpUtils
                .post()
                .url(url)
                .addHeader("Authorization", SharedPreferencesUtil.getString(SettingActivity.this, "Token"))
                .addHeader("Accept", "application/json")
                .addParams("type", "avatar")
                .addParams("time", System.currentTimeMillis() + "")
                .addFile("file", fileName, file)
                .build().execute(new StringCallback(){
            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                show("图片上传失败");
                LatteLogger.e("失败","失败");
                dismissLoadProcess();
                if (arg1.getMessage()!=null) {
                    if (arg1.getMessage().contains("401")) {
                        showMsg401();
                    }
                }
            }
            @Override
            public void onResponse(String arg0, int arg1) {
                show("成功");
                LatteLogger.e("成功","成功");
                dismissLoadProcess();
                parseUploadImg(arg0);
            }
        });
    }

    private void parseUploadImg(String result) {
        try {
            org.json.JSONObject json = new org.json.JSONObject(result);
            if (json.has("error")) {
                show(json.optString("error",""));
                return;
            }
            org.json.JSONObject data = json.getJSONObject("data");
            if (data.optInt("id")!=0) {
                avatar_id = data.optInt("id",0);
            }
            if (data.optString("url")!=null) {
                avatar_url = data.optString("url");
                Glide.with(SettingActivity.this)
                        .load(avatar_url)
                        .transform(new GlideCircleTransform(SettingActivity.this))
                        .into(ivTouxiang);
            }
            //修改图片
            putChangeAvatar(avatar_id);
        } catch (Exception e) {}
    }


    public void putChangeAvatar(int id){
        String url = Config.AVATAR; //请求接口url
        String apiName = "单独保存图片接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(this, "Token"));
        addHeader.put("Content-Type", "application/x-www-form-urlencoded");
        addParams.put("avatar_id", id + "");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.put(apiName, this, url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    JSONObject json = JSON.parseObject(arg0);
                    if (json!=null ) {
                        if (json.containsKey("error") ) {
                            Toast.makeText(SettingActivity.this,json.getString("error"),Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Glide.with(SettingActivity.this)
                                .load(avatar_url)
                                .transform(new GlideCircleTransform(SettingActivity.this))
                                .into(ivTouxiang);
                        Toast.makeText(SettingActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                    }else {
                        Glide.with(SettingActivity.this)
                                .load(avatar_url)
                                .transform(new GlideCircleTransform(SettingActivity.this))
                                .into(ivTouxiang);
                        Toast.makeText(SettingActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                    }
                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            showMsg401();
                            finish();
                        }
                    }
                }
            });
    }

    private void patchMemeberData(String name) {
        String url = Config.REGISTER_URL; //接口url
        String apiName = "修改个人信息接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Content-Type", "application/x-www-form-urlencoded");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(this, "Token"));
        addParams.put("name", name);
        addParams.put("gender", gender + "");
        addParams.put("district_id", district_id + "");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.patch(apiName, this, url, addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        dismissLoadProcess();
                        processPatchMemeberData(arg0);
                    }
                },
                new NetConnectionNew.FailCallback() {
                    @Override
                    public void onFail(Call arg0, Exception arg1, int arg2) {
                        dismissLoadProcess();
                        if (arg1.getMessage()!=null) {
                            if (arg1.getMessage().contains("401")) {
                                showMsg401();
                            }
                        }
                    }
                });
    }

    private void processPatchMemeberData (String arg0){
        JSONObject json = JSON.parseObject(arg0);
        if (json.containsKey("error")) {
            Toast.makeText(this,json.getString("error"),Toast.LENGTH_SHORT).show();
            return;
        }
        String name = json.getJSONObject("data").getString("name");
        tv_nicheng.setText(name);
    }

    private void delExitLogin() {
        String url = Config.AUTH_TOKEN; //接口url
        String apiName = "退出登录接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(this, "Token"));
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.delete(apiName, this, url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    JSONObject json = JSON.parseObject(arg0);
                    if (json!=null && json.containsKey("error")) {
                        Toast.makeText(SettingActivity.this,json.getString("error"),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(SettingActivity.this,"退出登录成功",Toast.LENGTH_SHORT).show();
                    SharedPreferencesUtil.clearlogin(SettingActivity.this);
                    goToNextAty(LoginActivity.class);
                    finish();
                    SharedPreferencesUtil.putBoolean(SettingActivity.this,"isSplashCome", true);

                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            showMsg401();
                            finish();
                        }
                    }
                }
            });
    }

    private void getMemeberData() {
        String url = Config.REGISTER_URL; //接口url
        String apiName = "查看用户信息接口"; //接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(this, "Token"));
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, this, url, addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        processMemeberData(arg0);
                    }
                },
                new NetConnectionNew.FailCallback() {
                    @Override
                    public void onFail(Call arg0, Exception arg1, int arg2) {
                        if (arg1.getMessage()!=null) {
                            if (arg1.getMessage().contains("401")) {
                                showMsg401();
                                finish();
                            }
                        }
                    }
                });
    }

    private void processMemeberData (String arg0){
        JSONObject json = JSON.parseObject(arg0);
        if (json.containsKey("error")) {
            Toast.makeText(SettingActivity.this, json.getString("error"), Toast.LENGTH_SHORT).show();
            return;
        }
        memeberData = parseMemeberData(arg0).getData();
        String avatar = memeberData.getAvatar()==null? null:memeberData.getAvatar().getUrl();
        String name = memeberData.getName();
        mobile = memeberData.getMobile();
        //头像
        Glide.with(this)
                .load(avatar)
                .error(R.mipmap.touxiang_gray)
                .transform(new GlideCircleTransform(this))
                .into(ivTouxiang);
        //昵称
        if (name != null) {
            tv_nicheng.setText(name);
        }
        //是否有支付密码
        has_payment_password = memeberData.isHas_payment_password();
        if (has_payment_password) {
            tv_pay_psw.setText("修改支付密码");
        }else {
            tv_pay_psw.setText("设置支付密码");
        }
    }

    private MemeberEntity parseMemeberData(String arg0) {
        return new Gson().fromJson(arg0, MemeberEntity.class);
    }

    private void checkVersion() { //检测版本更新接口
        String url = Config.FIR_API_URL; //接口url
        String apiName = "检测版本更新接口"; // 接口名
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addParams.put("api_token", Config.FIR_TOKEN);
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, this, url, addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processVersionData(arg0);
                }
            }, new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    LatteLogger.e("更新接口失败", arg1.getMessage());
                }
            });
    }

    private void processVersionData(String result) {
        VersionBean bean = new Gson().fromJson(result, VersionBean.class);
        int version = Integer.parseInt(bean.getVersion());
        int versionName = Integer.parseInt(Util.getVersionInfo(SettingActivity.this)[0]);
        if (version > versionName) { //有版本更新了，请下载
            goToNextAty(SplashActivity.class);
        } else { //没有版本更新，今日首页
            show("已经是最新版本");
        }
    }
}

