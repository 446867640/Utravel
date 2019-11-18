package com.utravel.app.activities.proxy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.delegates.detail.AccountDetailDelegate1;

public class AccountLogActivity extends ProxyActivity{
    private String type = "balance";

    @Override
    public LatterDelegate setRootDelegate() { return AccountDetailDelegate1.newInstance(type); }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (!TextUtils.isEmpty(getIntent().getStringExtra("account_log"))) {
            type = getIntent().getStringExtra("account_log");
        }
        super.onCreate(savedInstanceState);
    }
}
