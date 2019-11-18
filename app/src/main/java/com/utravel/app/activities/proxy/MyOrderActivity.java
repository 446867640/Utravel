package com.utravel.app.activities.proxy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.delegates.order.MyOrderDelegate1;

public class MyOrderActivity extends ProxyActivity {
    private int position = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (getIntent().getIntExtra("code",0)!=0) {
            position = getIntent().getIntExtra("code",0);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public LatterDelegate setRootDelegate() { return MyOrderDelegate1.newInstance(position); }
}