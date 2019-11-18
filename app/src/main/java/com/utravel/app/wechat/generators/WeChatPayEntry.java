package com.utravel.app.wechat.generators;

import com.utravel.app.wechat.templates.WXPayEntryTemplate;
import com.igou.latte.annotations.PayEntryGenerator;

@SuppressWarnings("unused")
@PayEntryGenerator(
        packageName = "com.utravel.app",
        payEntryTemplete = WXPayEntryTemplate.class
)
public interface WeChatPayEntry {

}
