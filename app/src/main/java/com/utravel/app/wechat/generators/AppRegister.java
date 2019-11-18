package com.utravel.app.wechat.generators;

import com.utravel.app.wechat.templates.AppRegisterTemplate;
import com.igou.latte.annotations.AppRegisterGenerator;

@SuppressWarnings("unused")
@AppRegisterGenerator(
        packageName = "com.utravel.app",
        registerTemplete = AppRegisterTemplate.class
)
public interface AppRegister {

}
