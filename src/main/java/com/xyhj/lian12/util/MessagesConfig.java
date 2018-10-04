//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.util;

import java.util.Locale;
import javax.annotation.Resource;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessagesConfig {
    @Resource
    private MessageSource messageSource;
    @Resource
    RedisTemplate redisTemplate;

    public MessagesConfig() {
    }

    public Object getMessage() {
        return this.getMessage((Object)null);
    }

    public Object getMessage(Object code) {
        return this.getMessage(code, (String)null);
    }

    public Object getMessage(Object code, String defaultMessage) {
        return this.getMessage(code, (Object[])null, defaultMessage);
    }

    public Object getMessage(Object code, Object[] args, String defaultMessage) {
        Locale locale = LocaleContextHolder.getLocale();
        return this.getMessage(code, args, defaultMessage, locale);
    }

    public Object getMessage(Object code, Object[] args, String defaultMessage, Locale locale) {
        Object local = this.redisTemplate.opsForValue().get("localeType");
        return null == local ? "en_US" : local;
    }
}
