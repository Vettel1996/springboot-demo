//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.base.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.xyhj.lian.util.*;
import com.xyhj.lian12.user.dto.UserDto;
import com.xyhj.lian12.util.BaseConstant;
import com.xyhj.lian12.util.NetworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * token拦截器，验证是否包含token和uid
 */
@Component
@Order(1)
public class TokenVerifyIntercepter implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(TokenVerifyIntercepter.class);
    private static ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final List<String> forbidList = new ArrayList<String>() {{
        add("27611");
        add("25507");
    }};

    Long begin = System.currentTimeMillis();

    public TokenVerifyIntercepter() {
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        this.begin = System.currentTimeMillis();
        String local = request.getParameter("local");
        if (local == null) {
            local = "";
        }

        String var5 = "登录超时,请重新登录";

        try {
            response.setContentType("application/json;charset=utf-8");
            // 获取需要校验的参数
            String token = request.getParameter("token");
            String uid = request.getParameter("uid");

            if (!Strings.isNullOrEmpty(uid) && !Strings.isNullOrEmpty(token)) {
                if (forbidList.contains(uid)) {
                    mapper.writeValue(response.getWriter(), RespEntity.error(RespCode.SYSTEM_LIMIT_ACCOUNT_ERROR));
                    return false;
                }
                return this.verfiyToken(uid, token, response, request);
            } else {
                mapper.writeValue(response.getWriter(), RespEntity.error(RespCode.COMMON_PARAM_BLANK));
                return false;
            }
        } catch (Exception var8) {
            mapper.writeValue(response.getWriter(), RespEntity.error(RespCode.COMMON_PARAM_BLANK));
            return false;
        }
    }

    public boolean verfiyToken(String uid, String token, HttpServletResponse response, HttpServletRequest request) throws IOException {
        String timeKey = "TIME" + uid;
        String tokenKey = RedisPrefix.DBToken.getPrefix() + uid;
        String requestHeader = request.getHeader("User-Agent");
        if (BaseConstant.isMobileDevice(requestHeader)) {
            tokenKey = "appToken" + uid;
            timeKey = "appTime" + uid;
        }

        String time = (String) this.redisTemplate.opsForValue().get(timeKey);
        String tokeno = (String) this.redisTemplate.opsForValue().get(tokenKey);
        if (!Strings.isNullOrEmpty(time) && !Strings.isNullOrEmpty(tokeno) && this.getToken(time + uid).equals(tokeno) && tokeno.equals(token)) {
            Long d = (System.currentTimeMillis() - Long.valueOf(time)) / 1000L / 60L / 60L;
            UserDto user = (UserDto) JSONObject.parseObject((String) this.redisTemplate.opsForValue().get(RedisPrefix.DBUser.getPrefix() + uid), UserDto.class);
            request.setAttribute("user", user);
            if (BaseConstant.isMobileDevice(requestHeader)) {
                if (d < 720L) {
                    this.redisTemplate.opsForValue().set(tokenKey, token, 7L, TimeUnit.DAYS);
                    this.redisTemplate.opsForValue().set(timeKey, String.valueOf(time), 7L, TimeUnit.DAYS);
                } else {
                    log.info("uid:" + uid + ",登录时间：" + (new Date(Long.valueOf(time))).toString());
                    log.info("uid:" + uid + ",现在时间：" + (new Date()).toString());
                }
            } else if (d < 24L) {
                this.redisTemplate.opsForValue().set(tokenKey, token, (long) RedisPrefix.DBToken.getExpriedTime(), TimeUnit.SECONDS);
                this.redisTemplate.opsForValue().set(timeKey, String.valueOf(time), (long) RedisPrefix.DBToken.getExpriedTime(), TimeUnit.SECONDS);
            } else {
                log.info("uid:" + uid + ",登录时间：" + (new Date(Long.valueOf(time))).toString());
                log.info("uid:" + uid + ",现在时间：" + (new Date()).toString());
                log.info("请求url：" + request.getRequestURL());
            }

            return true;
        } else {
            mapper.writeValue(response.getWriter(), RespEntity.error(RespCode.USER_TOKEN_ERROR));
            return false;
        }
    }

    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        double distance = (double) (System.currentTimeMillis() - this.begin) / Double.valueOf(1000.0D);
        if (distance > 2.0D) {
            HttpServletRequest request = httpServletRequest;
            List<String> list = new ArrayList();
            String key = DateUtils.format(new Date()) + "request";
            if (this.redisTemplate.opsForValue().get(key) == null) {
                this.redisTemplate.opsForValue().set(key, JSONObject.toJSONString(list), 3L, TimeUnit.HOURS);
            }

            //List<String> list = (List)JSONObject.parseObject((String)this.redisTemplate.opsForValue().get(key), List.class);
            list = (List) JSONObject.parseObject((String) this.redisTemplate.opsForValue().get(key), List.class);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("spend_time->" + distance + ",");
            stringBuilder.append("time->" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss") + ",");

            try {
                stringBuilder.append("ip->" + NetworkUtil.getIpAddress(request) + ",");
            } catch (IOException var12) {
                var12.printStackTrace();
            }

            stringBuilder.append("url->" + httpServletRequest.getRequestURL());
            log.info("请求时间超时：" + stringBuilder.toString());
            log.info("请求参数：" + JSONObject.toJSONString(httpServletRequest.getParameterMap()));
            list.add(stringBuilder.toString());
            this.redisTemplate.opsForValue().set(key, JSONObject.toJSONString(list), 3L, TimeUnit.HOURS);
        }

        if (distance > 1.0D) {
            log.warn("警告：" + httpServletRequest.getRequestURL() + "请求用时：" + distance);
        }

    }

    public String getToken(String key) {
        return XTEAUtils.encryt(key);
    }
}
