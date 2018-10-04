//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.base.interceptor;

import com.xyhj.lian.util.RespEntity;
import com.xyhj.lian12.util.ConstantUtil;
import com.xyhj.lian12.util.PropertiesUtil;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
class GlobalDefaultExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

    GlobalDefaultExceptionHandler() {
    }

    @ExceptionHandler({Exception.class})
    public RespEntity defaultErrorHandler(HttpServletRequest req, Exception e) {
        log.error("全局捕获异常 {}", e);
        String locale = req.getParameter("local");
        String messageValue = "网络连接错误，请稍后重试";
        if ("en_US".equals(locale)) {
            messageValue = PropertiesUtil.readProperties(messageValue, (String)ConstantUtil.getMessagesMap.get(ConstantUtil.TWO)).toString();
        } else if ("zh_TW".equals(locale)) {
            messageValue = PropertiesUtil.readProperties(messageValue, (String)ConstantUtil.getMessagesMap.get(ConstantUtil.THREE)).toString();
        }

        log.info("请求url：" + req.getRequestURL());
        if (e instanceof MethodArgumentTypeMismatchException) {
            messageValue = "参数类型不匹配";
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            messageValue = "请求类型不匹配";
        }

        return new RespEntity(500, messageValue);
    }
}
