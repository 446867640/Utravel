package com.utravel.app.activities.base;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.utravel.app.R;
import com.utravel.app.activities.proxy.LoginActivity;
import com.utravel.app.config.Config;
import com.utravel.app.utils.CamareAndphotoUtil;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class PersonRzActivity extends BaseActivity implements View.OnClickListener {
    private View status_bar;
    private ImageView iv_back;
    private TextView tv_title;
    private EditText etUsername;
    private EditText etCard;
    private ImageView iv_1;
    private TextView tv_remark;
    private TextView tv_ok;

    private String filePath;
    private int front_of_id_card_id;
    private String type = "front_of_id_card";

    @Override
    public boolean setIsDark() {
        return true;
    }

    @Override
    protected int getLayoutId() { return R.layout.delegate_my_personrz; }

    @Override
    protected void initParams() {
        initViews();
        initViewParams();
        initListener();
    }

    private void initViewParams() {
        tv_title.setText("实名认证");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(this);
        status_bar.setLayoutParams(params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCertificationState();
    }

    private void initViews() {
        status_bar = (View) findViewById(R.id.status_bar);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_remark = (TextView) findViewById(R.id.tv_remark);
        etUsername = (EditText) findViewById(R.id.et_username);
        etCard = (EditText) findViewById(R.id.et_card);
        iv_1 = (ImageView) findViewById(R.id.iv_1);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        iv_1.setOnClickListener(this);
        tv_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == iv_back) {
            finish();
        }else if (v == iv_1) {
            hideKey(etUsername);
            hideKey(etCard);
            showAvatarPopwindow();
        }else if (v == tv_ok) {
            if (TextUtils.isEmpty(etUsername.getText())) {
                show("用户名不能为空");
                return;
            }
            if (TextUtils.isEmpty(etCard.getText())) {
                show("身份证号不能为空");
                return;
            }
            if (front_of_id_card_id==0) {
                show("请补全证件照");
                return;
            }
            loadProcess();
            sendRenZheng();
        }
    }

    private void isCanTiJiao(boolean b) {
        if (b) {
            tv_ok.setEnabled(true);
            tv_ok.setTextColor(Color.parseColor("#FFFFFF"));
            tv_ok.setBackgroundResource(R.drawable.bg_hongse_40_daojiao);
        }else {
            tv_ok.setEnabled(false);
            tv_ok.setTextColor(Color.parseColor("#FFFFFF"));
            tv_ok.setBackgroundResource(R.drawable.bg_huise_40_daojiao);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CamareAndphotoUtil.REQUEST_CODE_FROM_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                LatteLogger.e( "相机授权成功", "相机授权成功");
                CamareAndphotoUtil.pickImageFromCamera(PersonRzActivity.this);
            } else {
                LatteLogger.e( "相机授权失败", "相机授权失败");
            }
        }
        if (requestCode == CamareAndphotoUtil.REQUEST_CODE_FROM_CAMERA) {
            CamareAndphotoUtil.pickImageFromAlbum2(PersonRzActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CamareAndphotoUtil.REQUEST_CODE_FROM_CAMERA) {// 从拍照
            if (resultCode == RESULT_CANCELED) {
                return;
            }
            if (data != null) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = bundle.getParcelable("data");
                loadProcess();
                uploadimg(Util.getCameraImage(bundle),bitmap);
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
                        uploadimg(urlToStr(imageUri),bit);
                    }
                } catch (Exception e) {}
            }
        }
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
                    case R.id.btn_camera_pop_camera:
                        if (ContextCompat.checkSelfPermission(PersonRzActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED  || ContextCompat.checkSelfPermission(PersonRzActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(PersonRzActivity.this, Manifest.permission.CAMERA)) {
                                show("您已经拒绝过一次");
                            }
                            ActivityCompat.requestPermissions(PersonRzActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CamareAndphotoUtil.REQUEST_CODE_FROM_CAMERA);
                        } else {//有权限直接调用系统相机拍照
                            CamareAndphotoUtil.pickImageFromCamera(PersonRzActivity.this);
                        }
                        break;
                    case R.id.btn_camera_pop_album:
                        if (ContextCompat.checkSelfPermission(PersonRzActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED  || ContextCompat.checkSelfPermission(PersonRzActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(PersonRzActivity.this, Manifest.permission.CAMERA)) {
                                show("您已经拒绝过一次");
                            }
                            ActivityCompat.requestPermissions(PersonRzActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CamareAndphotoUtil.REQUEST_CODE_FROM_CAMERA);
                        } else {//有权限直接调用系统相机拍照
                            CamareAndphotoUtil.pickImageFromAlbum2(PersonRzActivity.this);
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
        ColorDrawable dw = new ColorDrawable(0x90000000);
        popWindow.setBackgroundDrawable(dw);
        popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
        fileName = Environment.getExternalStorageDirectory().toString()
                + "/tu/" + fileName;
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
            Cursor cursor = PersonRzActivity.this.getContentResolver().query(
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

    private void uploadimg(String filePath,final Bitmap bitmap) {
        String url = Config.UPLOAD_IMG_URL;
        String apiName = "上传图片接口";
        String interfaceName = "file";
        List<String> filePaths = new ArrayList<>();
        Map<String, String> addHeader = new HashMap<String, String>(); //请求头
        Map<String, String> addParams = new HashMap<String, String>(); //请求体
        addHeader.put("Accept", "application/json");
        addHeader.put("Content-Type", "multipart/form-data");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(PersonRzActivity.this, "Token"));
        addParams.put("time", System.currentTimeMillis() + "");
        addParams.put("type", type);
        filePaths.add(filePath);
        NetConnectionNew.postUpLoad(apiName,PersonRzActivity.this,url,addHeader,addParams,filePaths,interfaceName,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String result, int arg1) {
                    dismissLoadProcess();
                    iv_1.setImageBitmap(bitmap);
                    parseUploadImg(result);
                }
            }, new NetConnectionNew.FailCallback() {
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

    private void parseUploadImg(String result) {
        JSONObject json = JSON.parseObject(result);
        if (json.containsKey("error")) {
            show(json.getString("error"));
            return;
        }
        JSONObject data = json.getJSONObject("data");
        front_of_id_card_id = data.getInteger("id");
    }

    private void sendRenZheng() {
        String url = Config.PERSON_RENZHENG_URL;
        String apiName = "提交个人认证接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(PersonRzActivity.this, "Token"));
        addHeader.put("Content-Type", "application/x-www-form-urlencoded");
        addParams.put("real_name", etUsername.getText().toString());
        addParams.put("identity_number", etCard.getText().toString());
        addParams.put("front_of_id_card_id",front_of_id_card_id + "");
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.put(apiName, PersonRzActivity.this, url,addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        dismissLoadProcess();
                        processSendRenZhengData(arg0);
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

    private void processSendRenZhengData(String arg0) {
        JSONObject json = JSON.parseObject(arg0);
        if (json!=null){
            if (json.containsKey("error")) {
                show(json.getString("error"));
                return;
            }
            show(getResources().getString(R.string.renzheng_successful));
            finish();
        }else{
            show(getResources().getString(R.string.renzheng_successful));
            finish();
        }
    }

    private void getCertificationState() {
        String url = Config.GET_RENZHENG_INFO;
        String apiName = "查看认证状态接口";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(PersonRzActivity.this, "Token"));
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, PersonRzActivity.this, url,addHeader, addParams,
                new NetConnectionNew.SuccessCallback() {
                    @Override
                    public void onSuccess(String arg0, int arg1) {
                        parseCertificationState(arg0);
                    }
                },
                new NetConnectionNew.FailCallback() {
                    @Override
                    public void onFail(Call arg0, Exception arg1, int arg2) {
                        if (arg1.getMessage()!=null) {
                            if (arg1.getMessage().contains("401")) {
                                showMsg401();
                                finish();
                            }else if (arg1.getMessage().contains("404")){
                                tv_remark.setVisibility(View.GONE);
                                tv_remark.setText("");
                                tv_ok.setText("提交");
                                isCanTiJiao(true);
                            }
                        }
                    }
                });
    }

    private void parseCertificationState(String arg0) {
        JSONObject json = JSON.parseObject(arg0);
        if (json!=null){
            if (json.containsKey("error")) {
                show(json.getString("error"));
                return;
            }
            JSONObject data = json.getJSONObject("data");
            //0-未认证，1-已认证，2-驳回认证，3-认证中
            String state = data.getString("state");
            if (state.equals("uncertified")) {//0-未认证
                tv_remark.setVisibility(View.GONE);
                tv_remark.setText("");
                tv_ok.setText("重新提交");
                isCanTiJiao(true);
            }else if (state.equals("certified")) {//1-已认证
                tv_remark.setVisibility(View.GONE);
                tv_remark.setText("");
                tv_ok.setText("已认证");
                isCanTiJiao(false);
            }else if (state.equals("rejected")) {//2-驳回认证
                tv_remark.setVisibility(View.VISIBLE);
                tv_remark.setText("驳回认证（" + data.getString("remark") + "），请重新认证");
                tv_ok.setText("重新提交");
                isCanTiJiao(true);
            }
        }
    }
}
