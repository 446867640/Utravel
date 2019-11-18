package com.utravel.app.delegates.main.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.utravel.app.R;
import com.utravel.app.delegates.LatterDelegate;

public class QianZaiFansDelegate extends LatterDelegate {

    @Nullable
    @Override
    public Object setLayout() {
        return R.layout.delegate_my;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }
}
