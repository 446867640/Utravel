package com.utravel.app.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.utravel.app.R;
import com.utravel.app.utils.DensityUtil;
import com.utravel.app.utils.Util;

import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

public class NameDialog extends BaseDialogDelegate implements View.OnClickListener {
    AppCompatEditText et_name;
    AppCompatTextView tv_cancel;
    AppCompatTextView tv_ok;

    public static NameDialog newInstance() {
        NameDialog delegate = new NameDialog();
        return delegate;
    }

    @Override
    public Object setLayout() {
        return R.layout.dialog_nicheng;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = Util.getScreenWidth(_mActivity)- DensityUtil.dp2px(_mActivity,80);
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initViews(rootView);
    }

    private void initViews(View rootView) {
        et_name = (AppCompatEditText) rootView.findViewById(R.id.et_name);
        tv_cancel = (AppCompatTextView) rootView.findViewById(R.id.tv_cancel);
        tv_ok = (AppCompatTextView) rootView.findViewById(R.id.tv_ok);
        tv_cancel.setOnClickListener(this);
        tv_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v==tv_cancel) {
        }else if (v==tv_ok) {
            EventBusActivityScope.getDefault(getActivity()).post(et_name.getText().toString());
        }
        getDialog().cancel();
    }
}
