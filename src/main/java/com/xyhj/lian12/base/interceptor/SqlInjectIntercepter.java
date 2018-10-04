//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.base.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyhj.lian.util.RespEntity;
import com.xyhj.lian12.util.MessagesConfig;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * url sql 注入拦截器
 */
@Component
@Order(3)
public class SqlInjectIntercepter implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(SqlInjectIntercepter.class);
    @Autowired
    private MessagesConfig messagesConfig;
    private static ObjectMapper mapper = new ObjectMapper();

    public SqlInjectIntercepter() {
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setContentType("application/json;charset=utf-8");
        String v = this.sqlInjectInterceptor(request, response, handler);
        if (!v.equals("")) {
            mapper.writeValue(response.getWriter(), new RespEntity(500, "sql拦截, " + v));
            return false;
        } else {
            return true;
        }
    }

    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }

    public String sqlInjectInterceptor(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Enumeration names = request.getParameterNames();

        while(names.hasMoreElements()) {
            String name = (String)names.nextElement();
            String[] values = request.getParameterValues(name);
            String[] var7 = values;
            int var8 = values.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                String value = var7[var9];
                if (value.indexOf("<") == 0 && value.indexOf(">") == value.length() - 1) {
                    return "非法参数";
                }
            }
        }

        return "";
    }
}
