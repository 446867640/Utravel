package com.utravel.app.wechat.generators;

import com.utravel.app.wechat.templates.WXEntryTemplate;
import com.igou.latte.annotations.EntryGenerator;

@SuppressWarnings("unused")
@EntryGenerator(
        packageName = "com.utravel.app",
        entryTemplete = WXEntryTemplate.class
)
public interface WeChatEntry {

}
