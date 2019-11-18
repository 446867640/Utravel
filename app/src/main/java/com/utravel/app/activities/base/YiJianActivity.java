package com.utravel.app.activities.base;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
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
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.utravel.app.R;
import com.utravel.app.activities.proxy.LoginActivity;
import com.utravel.app.config.Config;
import com.utravel.app.entity.NoReadYiJianBean;
import com.utravel.app.ui.Loading_view;
import com.utravel.app.utils.CamareAndphotoUtil;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class YiJianActivity extends BaseActivity implements View.OnClickListener {
    private View status_bar;
    private AppCompatImageView iv_back;
    private AppCompatTextView tv_title;
    private ImageView iv1;
    private ImageView iv2;
    private ImageView iv3;
    private EditText et1;
    private TextView tv_ok;
    private Loading_view loading;//进度加载dailog
    private List<String> filePaths = new ArrayList<String>();
    private String filePath1;
    private String filePath2;
    private String filePath3;
    private int position=1;
    private InputMethodManager imm;
    private boolean isFirst = true;
    private List<NoReadYiJianBean.DataBean> feedBackdatas;
    private List<TextView> tvs = new ArrayList<TextView>();
    //倒计时
    private int time = 5;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(time > 0){
                time = time - 1;
                tvs.get(0).setText(time+"s");
                handler.removeMessages(0);
                handler.sendEmptyMessageDelayed(0,1000);
            }else {
                //把消息移除
                tvs.get(0).setText("已读");
                tvs.get(0).setTextColor(Color.parseColor("#FFFFFF"));
                tvs.get(0).setBackgroundResource(R.drawable.bg_hongse_40_daojiao);
                tvs.get(0).setEnabled(true);
                handler.removeCallbacksAndMessages(null);
            }
        }
    };

    @Override
    public boolean setIsDark() { return true; }

    @Override
    protected int getLayoutId() { return R.layout.activity_yi_jian; }

    @Override
    protected void initParams() {
        initViews();
        initViewParams();
        initListener();
        loadProcess();
        getFeedBackData();
    }

    @Override
    protected synchronized void onDestroy() {
        super.onDestroy();
        if (handler!=null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void initViews() {
        status_bar = (View) findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView) findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) findViewById(R.id.tv_title);
        iv1 = (ImageView) findViewById(R.id.iv1);
        iv2 = (ImageView) findViewById(R.id.iv2);
        iv3 = (ImageView) findViewById(R.id.iv3);
        et1 = (EditText) findViewById(R.id.et1);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
    }

    private void initViewParams() {
        tv_title.setText("意见反馈");
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(this);
        status_bar.setLayoutParams(params);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        iv1.setOnClickListener(this);
        iv2.setOnClickListener(this);
        iv3.setOnClickListener(this);
        tv_ok.setOnClickListener(this);
        et1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showOrhideKey();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) {
            finish();
        }else if (v==iv1) {
            position = 1;
            hideKey();
            showAvatarPopwindow();
        }else if (v==iv2) {
            position = 2;
            hideKey();
            showAvatarPopwindow();
        }else if (v==iv3) {
            position = 3;
            hideKey();
            showAvatarPopwindow();
        }else if (v==tv_ok) {
            if (TextUtils.isEmpty(et1.getText())) {
                show("内容不能为空");
                return;
            }
            loadProcess();
            postFeed();
        }
    }

    private void showPopWindow(final NoReadYiJianBean.DataBean dataBean) {
        View parent = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        View popView = View.inflate(this, R.layout.pop_yijian, null);
        final PopupWindow popWindow = new PopupWindow(popView, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);// 设置同意在外点击消失
        popWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        TextView tv_title = (TextView) popView.findViewById(R.id.tv_title);
        TextView tv_content = (TextView) popView.findViewById(R.id.tv_content);
        TextView tv_ok = (TextView) popView.findViewById(R.id.tv_ok);
        tv_title.setText(dataBean.getTitle());
        tv_content.setText(dataBean.getContent());
        tvs.add(tv_ok);
        if (isFirst) {
            tv_ok.setEnabled(false);
        }
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
                //调用已读接口
                patchFeedBackData(dataBean.getId());
            }
        });
        ColorDrawable dw = new ColorDrawable(0x30000000);
        popWindow.setBackgroundDrawable(dw);
        popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    private void showOrhideKey(){
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void hideKey(){
        imm.hideSoftInputFromWindow(et1.getWindowToken(), 0);
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
                if (position == 1) {
                    iv1.setImageBitmap(bitmap);
                    filePath1 = getCameraImage(bundle);
                    if (filePath1!=null || !filePath1.equals("")) {
                        iv2.setVisibility(View.VISIBLE);
                    }
                }else if (position == 2) {
                    iv2.setImageBitmap(bitmap);
                    filePath2 = getCameraImage(bundle);
                    if (filePath2!=null || !filePath2.equals("")) {
                        iv3.setVisibility(View.VISIBLE);
                    }
                }else if (position == 3) {
                    filePath3 = getCameraImage(bundle);
                }
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
                        Bitmap bit = BitmapFactory
                                .decodeStream(getContentResolver()
                                        .openInputStream(imageUri));
                        LatteLogger.i("bit", String.valueOf(bit));
                        if (position == 1) {
                            iv1.setImageBitmap(bit);
                            filePath1 = urlToStr(imageUri);
                            if (filePath1!=null || !filePath1.equals("")) {
                                iv2.setVisibility(View.VISIBLE);
                            }
                        }else if (position == 2) {
                            iv2.setImageBitmap(bit);
                            filePath2 = urlToStr(imageUri);
                            if (filePath2!=null || !filePath2.equals("")) {
                                iv3.setVisibility(View.VISIBLE);
                            }
                        }else if (position == 3) {
                            iv3.setImageBitmap(bit);
                            filePath3 = urlToStr(imageUri);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getCameraImage(Bundle bundle) { //根据Bundle获取图片在sd卡的路径
        String strState = Environment.getExternalStorageState();
        if (!strState.equals(Environment.MEDIA_MOUNTED)) {
            LatteLogger.i("TAG", "SD卡不存在");
        }
        String fileName = Config.APP + System.currentTimeMillis() + ".jpg"; // 此处可以改为时间
        // Bundle bundle = data.getExtras();
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
            Cursor cursor = YiJianActivity.this.getContentResolver().query(
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
                        if (ContextCompat.checkSelfPermission(YiJianActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED  || ContextCompat.checkSelfPermission(YiJianActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(YiJianActivity.this, Manifest.permission.CAMERA)) {
                                show("您已经拒绝过一次");
                            }
                            ActivityCompat.requestPermissions(YiJianActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CamareAndphotoUtil.REQUEST_CODE_FROM_CAMERA);
                        } else {//有权限直接调用系统相机拍照
                            CamareAndphotoUtil.pickImageFromCamera(YiJianActivity.this);
                        }
                        break;
                    case R.id.btn_camera_pop_album:
                        if (ContextCompat.checkSelfPermission(YiJianActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED  || ContextCompat.checkSelfPermission(YiJianActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(YiJianActivity.this, Manifest.permission.CAMERA)) {
                                show("您已经拒绝过一次");
                            }
                            ActivityCompat.requestPermissions(YiJianActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CamareAndphotoUtil.REQUEST_CODE_FROM_CAMERA);
                        } else {//有权限直接调用系统相册
                            CamareAndphotoUtil.pickImageFromAlbum2(YiJianActivity.this);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CamareAndphotoUtil.REQUEST_CODE_FROM_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                LatteLogger.e( "相机授权成功", "相机授权成功");
                CamareAndphotoUtil.pickImageFromCamera(YiJianActivity.this);
            } else {
                LatteLogger.e( "相机授权失败", "相机授权失败");
            }
        }
        if (requestCode == CamareAndphotoUtil.REQUEST_CODE_FROM_CAMERA) {
            CamareAndphotoUtil.pickImageFromAlbum2(YiJianActivity.this);
        }
    }

    private void postFeed() {
        if (filePath1!=null) {
            filePaths.add(filePath1);
        }
        if (filePath2!=null) {
            filePaths.add(filePath2);
        }
        if (filePath3!=null) {
            filePaths.add(filePath3);
        }
        String url = Config.POST_FEED;
        final String apiName = "意见反馈接口";
        PostFormBuilder param = OkHttpUtils
            .post()
            .url(url)
            .addHeader("Authorization", SharedPreferencesUtil.getString(YiJianActivity.this, "Token"))
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type","application/x-www-form-urlencoded")
            .addParams("content", et1.getText().toString())
            .addParams("time", System.currentTimeMillis() + "");
        for (int i = 0; i < filePaths.size(); i++) {
            File file = new File(filePaths.get(i));
            String fileName = file.getName();
            param.addFile("images[]", fileName, file);
        }
        param.build().execute(new StringCallback(){
            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                dismissLoadProcess();
                if (arg1.getMessage()!=null) {
                    if (arg1.getMessage().contains("401")) {
                        showMsg401();
                    }else if (arg1.getMessage().contains("404")){
                        show(Config.ERROR404);
                    }
                }
            }
            @Override
            public void onResponse(String arg0, int arg1) {
                dismissLoadProcess();
                JSONObject json = JSON.parseObject(arg0);
                if (json != null) {
                    if (json.containsKey("error")) {
                        show(json.getString("error"));
                        return;
                    }
                    show("提交成功");
                    iv1.setImageResource(R.mipmap.yjaddbg);
                    iv2.setImageResource(R.mipmap.yjaddbg);
                    iv3.setImageResource(R.mipmap.yjaddbg);
                    filePaths.clear();
                    filePath1 = null;
                    filePath2 = null;
                    filePath3 = null;
                    et1.setText("");
                    iv2.setVisibility(View.INVISIBLE);
                    iv3.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void getFeedBackData() {
        String apiName = "未读意见反馈列表接口";
        String url = Config.GET_FEED;
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(YiJianActivity.this, "Token"));
        addParams.put("time", System.currentTimeMillis() + "");
        NetConnectionNew.get(apiName, YiJianActivity.this, url,addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    processFeedBackData(arg0);
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

    private void processFeedBackData(String arg0) {
        JSONObject json = JSON.parseObject(arg0);
        if (json != null) {
            if (json.containsKey("error")) {
                show(json.getString("error"));
                return;
            }
            feedBackdatas = parseFeedBackData(arg0).getData();
            if (feedBackdatas.size()>0) {
                tvs.clear();
                showPopWindow(feedBackdatas.get(0));
                handler.sendEmptyMessageDelayed(0,1000);
            }
        }
    }

    private NoReadYiJianBean parseFeedBackData(String arg0) {
        return new Gson().fromJson(arg0, NoReadYiJianBean.class);
    }

    private void patchFeedBackData(int id) {
        String apiName = "客服反馈已读接口";
        String url = Config.GET_FEED_READ + id + "/read";
        Map<String, String> addHeader = new HashMap<String, String>();
        Map<String, String> addParams = new HashMap<String, String>();
        addHeader.put("Accept", "application/json");
        addHeader.put("Authorization", SharedPreferencesUtil.getString(YiJianActivity.this, "Token"));
        addHeader.put("Content-Type", "application/json");
        addParams.put("time", System.currentTimeMillis()+"");
        NetConnectionNew.patch(apiName, YiJianActivity.this, url,addHeader, addParams,
            new NetConnectionNew.SuccessCallback() {
                @Override
                public void onSuccess(String arg0, int arg1) {
                    dismissLoadProcess();
                    //刷新未读接口数据
                    getFeedBackData();
                    JSONObject json = JSON.parseObject(arg0);
                    if (json != null) {
                        if (json.containsKey("error")) {
                            show(json.getString("error"));
                            return;
                        }
                    }else {

                    }

                }
            },
            new NetConnectionNew.FailCallback() {
                @Override
                public void onFail(Call arg0, Exception arg1, int arg2) {
                    dismissLoadProcess();
                    //刷新未读接口数据
                    getFeedBackData();
                    if (arg1.getMessage()!=null) {
                        if (arg1.getMessage().contains("401")) {
                            showMsg401();
                        }
                    }
                }
            });
    }
}
