package com.utravel.app.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.utravel.app.R;
import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

public class SampleDialog extends BaseDialogDelegate {

    public static SampleDialog newInstance() {
        SampleDialog delegate = new SampleDialog();
        return delegate;
    }

    @Override
    public Object setLayout() {
        return null;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }
}
