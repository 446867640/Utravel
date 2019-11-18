package com.utravel.app.activities.proxy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.widget.FrameLayout;

import com.utravel.app.R;
import com.utravel.app.config.Config;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.utils.ActivityStack;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.utils.ToastUtil;
import com.utravel.app.utils.Util;

import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

public abstract class ProxyActivity extends SupportActivity {

    public abstract LatterDelegate setRootDelegate();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.getInstance().addActivity(this);
        initContainer(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        if ( actionBar!=null ) {
            actionBar.hide();
        }
    }

    private void initContainer(@Nullable Bundle savedInstanceState){
        final FrameLayout container = new FrameLayout(ProxyActivity.this);
        container.setId(R.id.delegater_container);
        setContentView(container);
        if (savedInstanceState==null) {
            loadRootFragment(R.id.delegater_container, setRootDelegate());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        System.runFinalization();
    }

    @Override
    public void onBackPressedSupport() {
        // 对于 4个类别的主Fragment内的回退back逻辑,已经在其onBackPressedSupport里各自处理了
        super.onBackPressedSupport();
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }

    public void goToNextAty(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    public void goToNextAty(Class<?> cls,String key,String value) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(key, value);
        startActivity(intent);
    }

    public void show401() {
        SharedPreferencesUtil.clearlogin(this);
        goToNextAty(LoginActivity.class);
        SharedPreferencesUtil.putBoolean(this, Config.IS_INIT_TREE_ID, true);
    }

    public void showMsg401() {
        ToastUtil.showShortToast(Config.ERROR401);
        SharedPreferencesUtil.clearlogin(this);
        goToNextAty(LoginActivity.class);
        SharedPreferencesUtil.putBoolean(this, Config.IS_INIT_TREE_ID, true);
    }
}
