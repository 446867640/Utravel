package com.utravel.app.delegates.main.my;//package com.utravel.app.delegates.main.my;
//
//import android.content.ContentResolver;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.support.annotation.Nullable;
//import android.text.TextUtils;
//import android.view.Gravity;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//import android.widget.TextView;
//import android.widget.Toast;
//import com.ali.auth.third.ui.LoginActivity;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.utravel.app.R;
//import com.utravel.app.activities.PersonRzActivity;
//import com.utravel.app.config.Config;
//import com.utravel.app.delegates.LatterSwipeBackDelegate;
//import com.utravel.app.utils.CamareAndphotoUtil;
//import com.utravel.app.utils.NetConnectionNew;
//import com.utravel.app.utils.SharedPreferencesUtil;
//import com.utravel.app.utils.Util;
//import com.zhy.http.okhttp.OkHttpUtils;
//import com.zhy.http.okhttp.callback.StringCallback;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import okhttp3.Call;
//
//public class PersonRzDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
//    private View status_bar;
//    private ImageView iv_back;
//    private TextView text;
//    private EditText etUsername;
//    private EditText etCard;
//    private ImageView iv_1;
//    private TextView tv_remark;
//    private TextView tv_ok;
//
//    private String filePath;
//    private int front_of_id_card_id;
//    private String type = "front_of_id_card";
//
//    @Override
//    public boolean setIsDark() { return true; }
//
//    @Override
//    public Object setLayout() { return R.layout.delegate_my_personrz; }
//
//    @Override
//    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
//        initViews(rootView);
//        initViewParams();
//    }
//
//    private void initViewParams() {
//        text.setText("实名认证");
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.height = Util.getStatusBarHeight(getContext());
//        status_bar.setLayoutParams(params);
//    }
//
//    @Override
//    public void onSupportVisible() {
//        super.onSupportVisible();
//        loadProcess();
//        getCertificationState();
//    }
//
//    private void initViews(View rootView) {
//        status_bar = (View) rootView.findViewById(R.id.status_bar);
//        iv_back = (ImageView) rootView.findViewById(R.id.iv_back);
//        text = (TextView) rootView.findViewById(R.id.text);
//        tv_remark = (TextView) rootView.findViewById(R.id.tv_remark);
//        etUsername = (EditText) rootView.findViewById(R.id.et_username);
//        etCard = (EditText) rootView.findViewById(R.id.et_card);
//        iv_1 = (ImageView) rootView.findViewById(R.id.iv_1);
//        tv_ok = (TextView) rootView.findViewById(R.id.tv_ok);
//        iv_back.setOnClickListener(this);
//        iv_1.setOnClickListener(this);
//        tv_ok.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (v == iv_back) {
//            pop();
//        }else if (v == iv_1) {
//            hideKey(etUsername);
//            hideKey(etCard);
//            showAvatarPopwindow();
//        }else if (v == tv_ok) {
//            if (TextUtils.isEmpty(etUsername.getText())) {
//                show("用户名不能为空");
//                return;
//            }
//            if (TextUtils.isEmpty(etCard.getText())) {
//                show("身份证号不能为空");
//                return;
//            }
//            if (front_of_id_card_id==0) {
//                show("请补全证件照");
//                return;
//            }
//            loadProcess();
//            sendRenZheng();
//        }
//    }
//
//    private void isCanTiJiao(boolean b) {
//        if (b) {
//            tv_ok.setEnabled(true);
//            tv_ok.setTextColor(Color.parseColor("#FFFFFF"));
//            tv_ok.setBackgroundResource(R.drawable.haixiangbtnbg);
//        }else {
//            tv_ok.setEnabled(false);
//            tv_ok.setTextColor(Color.parseColor("#F4F4F4"));
//            tv_ok.setBackgroundResource(R.drawable.bg_20_ccc_banyuan);
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CamareAndphotoUtil.REQUEST_CODE_FROM_CAMERA) {// 从拍照
//            if (resultCode == RESULT_CANCELED) {
//                return;
//            }
//            if (data != null) {
//                Bundle bundle = data.getExtras();
//                Bitmap bitmap = bundle.getParcelable("data");
//                loadProcess();
//                uploadimg(getCameraImage(bundle),bitmap);
//            }
//        }
//        if (requestCode == CamareAndphotoUtil.REQUEST_CODE_FROM_ALBUM) {// 从相册取
//            if (resultCode == RESULT_CANCELED) {
//                return;
//            }
//            if (resultCode == RESULT_OK) {
//                try {
//                    Uri imageUri = data.getData();
//                    if (imageUri != null) {
//                        Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
//                        Log.i("bit", String.valueOf(bit));
//                        loadProcess();
//                        uploadimg(urlToStr(imageUri),bit);
//                    }
//                } catch (Exception e) {}
//            }
//        }
//    }
//
//    /**
//     * 根据Bundle获取图片在sd卡的路径
//     */
//    private String getCameraImage(Bundle bundle) {
//        String strState = Environment.getExternalStorageState();
//        if (!strState.equals(Environment.MEDIA_MOUNTED)) {
//            Log.e("TAG", "SD卡不存在");
//        }
//        String fileName = Config.APP + System.currentTimeMillis() + ".jpg"; // 此处可以改为时间
//        Bitmap bitmap = (Bitmap) bundle.get("data");
//        File file = new File(Environment.getExternalStorageDirectory()
//                .toString() + "/tu/");
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        fileName = Environment.getExternalStorageDirectory().toString()
//                + "/tu/" + fileName;
//        FileOutputStream stream = null;
//        try {
//            stream = new FileOutputStream(fileName);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (stream != null) {
//                    stream.flush();
//                    stream.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return fileName;
//    }
//
//    private void showAvatarPopwindow() {
//        View parent = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
//        View popView = View.inflate(this, R.layout.dialog_photo, null);
//        Button btnCamera = (Button) popView.findViewById(R.id.btn_camera_pop_camera);
//        Button btnAlbum = (Button) popView.findViewById(R.id.btn_camera_pop_album);
//        Button btnCancel = (Button) popView.findViewById(R.id.btn_camera_pop_cancel);
//        int width = getResources().getDisplayMetrics().widthPixels;
//        int height = getResources().getDisplayMetrics().heightPixels;
//        final PopupWindow popWindow = new PopupWindow(popView, width, height);
//        popWindow.setFocusable(true);
//        popWindow.setOutsideTouchable(false);// 设置同意在外点击消失
//        View.OnClickListener listener = new View.OnClickListener() {
//            public void onClick(View v) {
//                switch (v.getId()) {
//                    case R.id.btn_camera_pop_camera:
//                        CamareAndphotoUtil.pickImageFromCamera(PersonRzActivity.this);
//                        break;
//                    case R.id.btn_camera_pop_album:
//                        CamareAndphotoUtil.pickImageFromAlbum2(PersonRzActivity.this);
//                        break;
//                    case R.id.btn_camera_pop_cancel:
//                        break;
//                }
//                popWindow.dismiss();
//            }
//        };
//        btnCamera.setOnClickListener(listener);
//        btnAlbum.setOnClickListener(listener);
//        btnCancel.setOnClickListener(listener);
//        ColorDrawable dw = new ColorDrawable(0x30000000);
//        popWindow.setBackgroundDrawable(dw);
//        popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
//    }
//
//    public String urlToStr(Uri uri) {
//        String path = null;
//        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
//            Cursor cursor = getContext().getContentResolver().query(
//                    uri, new String[] { MediaStore.Images.Media.DATA }, null,
//                    null, null);
//            if (cursor != null) {
//                if (cursor.moveToFirst()) {
//                    int columnIndex = cursor
//                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                    if (columnIndex > -1) {
//                        path = cursor.getString(columnIndex);
//                    }
//                }
//                cursor.close();
//            }
//            return path;
//        } else {
//            path = uri.getPath();
//            return path;
//        }
//    }
//
//    //图片上传接口
//    private void uploadimg(String filePath,final Bitmap bitmap) {
//        String url = Config.UPLOAD_IMG_URL;
//        final String apiName = "上传图片接口";
//        File file = new File(filePath);
//        String fileName = file.getName();
//        OkHttpUtils
//                .post()
//                .url(url)
//                .addHeader("Authorization", Config.getString(PersonRzActivity.this, "Token"))
//                .addHeader("Accept", "application/json")
//                .addParams("type", type)
//                .addParams("time", System.currentTimeMillis() + "")
//                .addFile("file", fileName, file)
//                .build().execute(new StringCallback(){
//            @Override
//            public void onError(Call arg0, Exception arg1, int arg2) {
//                dismissLoadProcess();
//                if (arg1.getMessage()!=null) {
//                    if (arg1.getMessage().contains("401")) {
//                        Toast.makeText(PersonRzActivity.this, Config.ERROR401, Toast.LENGTH_SHORT).show();
//                        Config.clearlogin(PersonRzActivity.this);
//                        Intent intent = new Intent(PersonRzActivity.this, LoginActivity.class);
//                        startActivity(intent);
//                    }
//                }
//            }
//            @Override
//            public void onResponse(String arg0, int arg1) {
//                dismissLoadProcess();
//                iv_1.setImageBitmap(bitmap);
//                parseUploadImg(arg0);
//            }
//        });
//    }
//
//    private void parseUploadImg(String result) {
//        try {
//            JSONObject json = new JSONObject(result);
//            if (json.has("error")) {
//                showMsg(json.optString("error",""));
//                return;
//            }
//            JSONObject data = json.getJSONObject("data");
//            int id = data.optInt("id");
//            front_of_id_card_id = id;
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void sendRenZheng() {
//        String url = Config.PERSON_RENZHENG_URL;
//        String apiName = "提交个人认证接口";
//        Map<String, String> addHeader = new HashMap<String, String>();
//        Map<String, String> addParams = new HashMap<String, String>();
//        addHeader.put("Accept", "application/json");
//        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
//        addHeader.put("Content-Type", "application/x-www-form-urlencoded");
//        addParams.put("real_name", etUsername.getText().toString());
//        addParams.put("identity_number", etCard.getText().toString());
//        addParams.put("front_of_id_card_id",front_of_id_card_id + "");
//        addParams.put("time", System.currentTimeMillis() + "");
//        NetConnectionNew.put(apiName, this, url,addHeader, addParams,
//            new NetConnectionNew.SuccessCallback() {
//                @Override
//                public void onSuccess(String arg0, int arg1) {
//                    dismissLoadProcess();
//                    JSONObject json = JSON.parseObject(arg0);
//                    if (json!=null) {
//                        if (json.containsKey("error")) {
//                            show(json.getString("error"));
//                            return;
//                        }
//                        show("认证信息已提交");
//                        pop();
//                    }else {
//                        show("认证信息已提交");
//                        pop();
//                    }
//                }
//            },
//            new NetConnectionNew.FailCallback() {
//                @Override
//                public void onFail(Call arg0, Exception arg1, int arg2) {
//                    dismissLoadProcess();
//                    if (arg1.getMessage()!=null) {
//                        if (arg1.getMessage().contains("401")) {
//                            Toast.makeText(PersonRzActivity.this, Config.ERROR401, Toast.LENGTH_SHORT).show();
//                            Config.clearlogin(PersonRzActivity.this);
//                            Intent intent = new Intent(PersonRzActivity.this,LoginActivity.class);
//                            startActivity(intent);
//                        }
//                    }
//                }
//            });
//    }
//
//    private void getCertificationState() {
//        String url = Config.GET_RENZHENG_INFO;
//        String apiName = "查看认证状态接口";
//        Map<String, String> addHeader = new HashMap<String, String>();
//        Map<String, String> addParams = new HashMap<String, String>();
//        addHeader.put("Accept", "application/json");
//        addHeader.put("Authorization", SharedPreferencesUtil.getString(getContext(), "Token"));
//        addParams.put("time", System.currentTimeMillis() + "");
//        NetConnectionNew.get(apiName, this, url,addHeader, addParams,
//                new NetConnectionNew.SuccessCallback() {
//                    @Override
//                    public void onSuccess(String arg0, int arg1) {
//                        dismissLoadProcess();
//                        parseCertificationState(arg0);
//                    }
//                },
//                new NetConnectionNew.FailCallback() {
//                    @Override
//                    public void onFail(Call arg0, Exception arg1, int arg2) {
//                        dismissLoadProcess();
//                        if (arg1.getMessage()!=null) {
//                            if (arg1.getMessage().contains("401")) {
//
//                                Toast.makeText(PersonRzActivity.this, Config.ERROR401, Toast.LENGTH_SHORT).show();
//                                Config.clearlogin(PersonRzActivity.this);
//                                Intent intent = new Intent(PersonRzActivity.this,LoginActivity.class);
//                                startActivity(intent);
//                            }else if (arg1.getMessage().contains("404")){
//                                tv_remark.setVisibility(View.GONE);
//                                tv_remark.setText("");
//                                tv_ok.setText("提交");
//                                isCanTiJiao(true);
//                            }
//                        }
//                    }
//                });
//    }
//
//    private void parseCertificationState(String arg0) {
//        JSONObject json =  JSON.parseObject(arg0);
//        if (json.containsKey("error")) {
//            show(json.getString("error"));
//            return;
//        }
//        JSONObject data = json.getJSONObject("data");
//        //0-未认证，1-已认证，2-驳回认证，3-认证中
//        String state = data.getString("state");
//        if (state.equals("uncertified")) {//0-未认证
//            tv_remark.setVisibility(View.GONE);
//            tv_remark.setText("");
//            tv_ok.setText("重新提交");
//            isCanTiJiao(true);
//        }else if (state.equals("certified")) {//1-已认证
//            tv_remark.setVisibility(View.GONE);
//            tv_remark.setText("");
//            tv_ok.setText("已认证");
//            isCanTiJiao(false);
//        }else if (state.equals("rejected")) {//2-驳回认证
//            tv_remark.setVisibility(View.VISIBLE);
//            tv_remark.setText("驳回认证（" + data.getString("remark") + "），请重新认证");
//            tv_ok.setText("重新提交");
//            isCanTiJiao(true);
//        }
//    }
//}
