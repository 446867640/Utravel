package com.utravel.app.activities.proxy;

import com.utravel.app.delegates.LatterDelegate;
import com.utravel.app.delegates.login.LoginChoiceDelegate1;
import com.utravel.app.delegates.login.LoginPhoneDelegate1;

public class LoginActivity extends ProxyActivity {

    @Override
    public LatterDelegate setRootDelegate() { return new LoginPhoneDelegate1(); }

}
