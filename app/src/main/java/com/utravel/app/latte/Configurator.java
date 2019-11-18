package com.utravel.app.latte;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.utravel.app.web.event.Event;
import com.utravel.app.web.event.EventManager;
import java.util.HashMap;

public class Configurator {

    private static final HashMap<Object, Object> LATTE_CONFIGS = new HashMap<>();

    private Configurator() {
        LATTE_CONFIGS.put(ConfigType.CONFIG_READY.name(), false);
    }

    public static Configurator getInstance() {
        return Holder.INSTANCE;
    }

    final static HashMap<Object, Object> getLatteConfigs() {
        return LATTE_CONFIGS;
    }

    private static class Holder {
        private static final Configurator INSTANCE = new Configurator();
    }

    public final void configure() {
        LATTE_CONFIGS.put(ConfigType.CONFIG_READY.name(), true);
    }

    private void checkConfiguration() {
        final boolean isReady = (boolean) LATTE_CONFIGS.get(ConfigType.CONFIG_READY.name());
        if (!isReady) {
            throw new RuntimeException("Configuration没有完成,请先配置");
        }
    }

    @SuppressWarnings("unchecked")
    public final <T> T getConfiguration(Enum<ConfigType> key) {
        checkConfiguration();
        return (T) LATTE_CONFIGS.get(key.name());
    }

    public final Configurator withApiHost(String host) {
        LATTE_CONFIGS.put(ConfigType.API_HOST.name(), host);
        return this;
    }

    public final Configurator withKepler(String appKey, String secret) {
        LATTE_CONFIGS.put(ConfigType.JD_KEPLER_APPKEY.name(), appKey);
        LATTE_CONFIGS.put(ConfigType.JD_KEPLER_KEYSECRET.name(), secret);
        return this;
    }

    public final Configurator withJavascriptInterface(@NonNull String name) {
        LATTE_CONFIGS.put(ConfigType.JAVASCRIPT_INTERFACE.name(), name);
        return this;
    }

    public final Configurator withEvaluateJavascript(@NonNull String method) {
        LATTE_CONFIGS.put(ConfigType.EVALUATE_JAVASCRIPT.name(), method);
        return this;
    }

    public final Configurator withWebEvent(@NonNull String name, @NonNull Event event) {
        final EventManager manager = EventManager.getInstance();
        manager.addEvent(name, event);
        return this;
    }

    public final Configurator withWeChatAppId(String appId) {
        LATTE_CONFIGS.put(ConfigType.WE_CHAT_APP_ID.name(), appId);
        return this;
    }

    public final Configurator withWeChatAppSecret(String secret) {
        LATTE_CONFIGS.put(ConfigType.WE_CHAT_APP_SECRET.name(), secret);
        return this;
    }

    public final Configurator withActivity(Activity activity) {
        LATTE_CONFIGS.put(ConfigType.ACTIVITY.name(), activity);
        return this;
    }
}
