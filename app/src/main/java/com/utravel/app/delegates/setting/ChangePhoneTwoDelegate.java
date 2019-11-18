package com.utravel.app.delegates.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.utravel.app.R;
import com.utravel.app.delegates.LatterSwipeBackDelegate;
import com.utravel.app.utils.Util;

public class ChangePhoneTwoDelegate extends LatterSwipeBackDelegate implements View.OnClickListener {
    View status_bar;
    AppCompatImageView iv_back;
    AppCompatTextView tv_title;
    AppCompatEditText et_phone;
    AppCompatEditText et_yanzhengma;
    AppCompatTextView tv_yanzhengma;
    AppCompatTextView tv_next;

    @Override
    public boolean setIsDark() {
        return true;
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_setting_change_phone2;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
        initViewParams();
        initListener();
    }

    private void initViews(View rootView) {
        status_bar = (View) rootView.findViewById(R.id.status_bar);
        iv_back = (AppCompatImageView)rootView.findViewById(R.id.iv_back);
        tv_title = (AppCompatTextView) rootView.findViewById(R.id.tv_title);
        et_phone = (AppCompatEditText) rootView.findViewById(R.id.et_phone);
        tv_title.setText("修改手机号");
        et_yanzhengma = (AppCompatEditText) rootView.findViewById(R.id.et_yanzhengma);
        tv_yanzhengma = (AppCompatTextView) rootView.findViewById(R.id.tv_yanzhengma);
        tv_next = (AppCompatTextView) rootView.findViewById(R.id.tv_next);
    }

    private void initViewParams() {
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = Util.getStatusBarHeight(_mActivity);
        status_bar.setLayoutParams(params);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        tv_yanzhengma.setOnClickListener(this);
        tv_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v==iv_back) { //返回
            pop();
        }else if (v==tv_yanzhengma) { //获取验证码
            Toast.makeText(_mActivity,"获取验证码",Toast.LENGTH_SHORT).show();
        }else if (v==tv_next) { //确定
            if (et_phone.getText().toString().isEmpty()) {
                Toast.makeText(_mActivity,"请输入新手机号码",Toast.LENGTH_SHORT).show();
                return;
            }
            if (et_phone.getText().toString().length()!=11) {
                Toast.makeText(_mActivity,"错误的手机号",Toast.LENGTH_SHORT).show();
                return;
            }
            if (et_yanzhengma.getText().toString().isEmpty()) {
                Toast.makeText(_mActivity,"请输入验证码",Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(_mActivity,"确定",Toast.LENGTH_SHORT).show();
        }
    }
}
