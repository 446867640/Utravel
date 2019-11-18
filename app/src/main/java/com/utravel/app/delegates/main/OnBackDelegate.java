package com.utravel.app.delegates.main;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.utravel.app.R;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.utils.ActivityStack;

public abstract class OnBackDelegate extends LatterDelegate implements View.OnKeyListener {
    private long mExitTime = 0;
    private static final int EXIT_TIME = 2000;

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (rootView!=null) {
            rootView.setFocusableInTouchMode(true);
            rootView.requestFocus();
            rootView.setOnKeyListener(this);
        }
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if (rootView!=null) {
            rootView.setOnKeyListener(null);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN) {
            long mSecondTime = System.currentTimeMillis();
            final String exit = _mActivity.getResources().getString(R.string.exit);
            if (mSecondTime-mExitTime > EXIT_TIME) {
                Toast.makeText(getContext(), exit, Toast.LENGTH_SHORT).show();
                mExitTime = mSecondTime;
            }else {
                _mActivity.finish();
                ActivityStack.getInstance().finishAllActivity();
                android.os.Process.killProcess(android.os.Process.myPid()); //结束当前的进程
                System.exit(0);//结束虚拟机
                if (mExitTime!=0) {
                    mExitTime=0;
                }
            }
            return true;
        }
        return false;
    }
}
