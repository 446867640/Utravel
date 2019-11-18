package com.utravel.app.latte;

import android.content.Context;

import java.util.HashMap;

public final class Latte {

    public static Configurator init(Context context) {
        getConfigurations().put(ConfigType.APPLICATION__CONTEXT.name(), context.getApplicationContext());
        return Configurator.getInstance();
    }

    private static HashMap<Object, Object> getConfigurations() {
        return Configurator.getInstance().getLatteConfigs();
    }

    public static Context getApplicationContext() {
        return (Context) getConfigurations().get(ConfigType.APPLICATION__CONTEXT.name());
    }

    public static Configurator getConfigurator() {
        return Configurator.getInstance();
    }
}
