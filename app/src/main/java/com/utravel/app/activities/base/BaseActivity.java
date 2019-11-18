package com.utravel.app.activities.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.utravel.app.R;
import com.utravel.app.activities.proxy.LoginActivity;
import com.utravel.app.config.Config;
import com.utravel.app.ui.Loading_view;
import com.utravel.app.utils.ActivityStack;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.ToastUtil;
import com.utravel.app.utils.Util;

public abstract class BaseActivity extends FragmentActivity {
    private boolean isCreate = false;
    public Loading_view loading;//加载中
    public int screenWidth;
    public InputMethodManager imm;

    public abstract boolean setIsDark();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ActivityStack.getInstance().addActivity(this);
        setContentView(getLayoutId());
        isCreate = true;
        //获取屏幕宽
        screenWidth = Util.getScreenWidth(this);
        //沉浸式透明状态栏
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //软键盘
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected void onResume() {
        if (isCreate) {
            isCreate = false;
            initParams();
        }
        super.onResume();
        Util.setStatusBarMode(this,setIsDark());
    }

    public void loadProcess(){
        if (loading == null) {
            loading = new Loading_view(BaseActivity.this, R.style.CustomDialog);
            loading.show();
        } else {
            if(!loading.isShowing()){
                loading.show();
            }
        }
    }

    public void dismissLoadProcess(){
        if (loading!=null) {
            loading.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected abstract int getLayoutId();

    protected abstract void initParams();

    public void goToNextAty(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    public void goToNextAty(Class<?> cls,String key,String value) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(key, value);
        startActivity(intent);
    }

    public void hideKey(View view){
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void show(String msg){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }

    public void show401() {
        SharedPreferencesUtil.clearlogin(this);
        goToNextAty(LoginActivity.class);
        SharedPreferencesUtil.putBoolean(this,Config.IS_INIT_TREE_ID, true);
    }

    public void showMsg401() {
        ToastUtil.showShortToast(Config.ERROR401);
        SharedPreferencesUtil.clearlogin(this);
        goToNextAty(LoginActivity.class);
        SharedPreferencesUtil.putBoolean(this, Config.IS_INIT_TREE_ID, true);
    }

}
