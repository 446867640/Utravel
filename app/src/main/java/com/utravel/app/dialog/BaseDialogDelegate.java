package com.utravel.app.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.utravel.app.R;
import com.utravel.app.ui.Loading_view;
import com.utravel.app.utils.ToastUtil;
import com.utravel.app.utils.Util;

import es.dmoral.toasty.Toasty;
import me.yokeyword.fragmentation.ISupportActivity;

public abstract class BaseDialogDelegate extends DialogFragment {
    protected FragmentActivity _mActivity;
    View rootView = null;
    public Loading_view loading = null;
    public int screenWidth;
    public InputMethodManager imm;

    public abstract Object setLayout();
    public abstract void onBindView(@Nullable Bundle savedInstanceState, View rootView);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (setLayout() instanceof Integer) {
            rootView = inflater.inflate((Integer) setLayout(),container,false);
        } else if (setLayout() instanceof View) {
            rootView = (View) setLayout();
        } else {
            throw new ClassCastException("setLayout() must be int or View!");
        }
        onBindView(savedInstanceState, rootView);
        //获取屏幕宽
        screenWidth = Util.getScreenWidth(getContext());
        //软键盘
        imm = (InputMethodManager) _mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        _mActivity = (FragmentActivity) activity;
    }

    @Override
    public void show(final FragmentManager manager, final String tag) {
        if (_mActivity instanceof ISupportActivity) {
            ((ISupportActivity) _mActivity).getSupportDelegate().post(new Runnable() {
                @Override
                public void run() {
                    BaseDialogDelegate.super.show(manager, tag);
                }
            });
            return;
        }
        super.show(manager, tag);
    }

    public void loadProcess(){
        if (loading == null) {
            loading = new Loading_view(_mActivity, R.style.CustomDialog);
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

    public void show(String msg){
        ToastUtil.showShortToast(msg);
    }

    public void showSuccess(String msg){
        Toasty.success(_mActivity, msg, Toasty.LENGTH_SHORT, true).show();
    }

    public void showError(String msg){
        Toasty.error(_mActivity, msg, Toasty.LENGTH_SHORT, true).show();
    }

    public void showInfo(String msg){
        Toasty.info(_mActivity, msg, Toasty.LENGTH_SHORT, true).show();
    }

    public void showWarning(String msg){
        Toasty.warning(_mActivity, msg, Toasty.LENGTH_SHORT, true).show();
    }

    public void showCustom(@NonNull CharSequence msg, Drawable icon, @ColorInt int tintColor, @ColorInt int textColor, int duration, boolean withIcon, boolean shouldTint){
        Toasty.Config.getInstance()
                .setToastTypeface(Typeface.createFromAsset(_mActivity.getAssets(), "PCap Terminal.otf"))
                .allowQueue(false)
                .apply();

        Toasty.custom(_mActivity, msg,
                icon,
                tintColor,
                textColor,
                Toasty.LENGTH_SHORT, withIcon, shouldTint).show();

        Toasty.Config.reset();
    }

    public void hideKey(View view){
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void goToNextAty(Class<?> cls) {
        Intent intent = new Intent(_mActivity, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void goToNextAty(Class<?> cls,String key,String value) {
        Intent intent = new Intent(_mActivity, cls);
        intent.putExtra(key, value);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void goToNextAty(Class<?> cls,String key,int value) {
        Intent intent = new Intent(_mActivity, cls);
        intent.putExtra(key, value);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
