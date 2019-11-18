package com.utravel.app.activities.proxy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.delegates.order.DDItemDetailDelegate1;

public class DDItemDetailActivity extends ProxyActivity {
    private String order_id = "1";

    @Override
    public LatterDelegate setRootDelegate() {return DDItemDetailDelegate1.newInstance(order_id);}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (!TextUtils.isEmpty(getIntent().getStringExtra("order_id"))) {
            order_id = getIntent().getStringExtra("order_id");
        }
        super.onCreate(savedInstanceState);
    }
}
