package com.utravel.app.activities.proxy;

import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.delegates.withdraw.TixianDetailDelegate1;

public class TixianDetailActivity extends ProxyActivity {

    @Override
    public LatterDelegate setRootDelegate() { return new TixianDetailDelegate1(); }

}
