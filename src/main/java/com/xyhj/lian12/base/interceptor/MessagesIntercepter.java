//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.base.interceptor;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xyhj.lian.util.StringUtils;
import com.xyhj.lian12.util.ConstantUtil;
import com.xyhj.lian12.util.MessagesConfig;
import com.xyhj.lian12.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ResponseBodyAdvice是spring4.1的新特性，其作用是在响应体写出之前做一些处理；比如，修改返回值、加密等。
 */
@ControllerAdvice
public class MessagesIntercepter implements ResponseBodyAdvice {
    private static final Logger log = LoggerFactory.getLogger(MessagesIntercepter.class);
    @Autowired
    private MessagesConfig messagesConfig;
    @Autowired
    private RedisTemplate redisTemplate;

    public MessagesIntercepter() {
    }

    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        Map<String, Object> map = beanToMap(body);
        String message = (String) map.get("message");
        if (message != null && !message.equals("")) {
            Set propertiesValue = PropertiesUtil.readProperties();
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) serverHttpRequest;
            String local = serverRequest.getServletRequest().getParameter("local");
            if (local != null && local.equals("zh-CN")) {
                return body;
            }

            if (local == null) {
                Object language = serverRequest.getServletRequest().getSession().getAttribute("language");
                if (language != null) {
                    local = language.toString();
                } else {
                    local = this.messagesConfig.getMessage((Object) null).toString();
                }
            }

            String key = "fanyi" + message + local;
            Object o = this.redisTemplate.opsForValue().get(key);
            if (o == null && propertiesValue.contains(message)) {
                message = translateMessage(message, propertiesValue, local);
                this.redisTemplate.opsForValue().set(key, message, 1L, TimeUnit.HOURS);
            } else if (o != null) {
                message = o.toString();
            } else {
                log.info("翻译信息异常：" + message + ",请求url：" + serverHttpRequest.getURI().getPath());
            }

            map.put("message", message);
        }

        return map;
    }

    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[一-龥]");
        Matcher m = p.matcher(str);
        return m.find();
    }

    public static void translateInfo(Map map, Set propertiesValue, String locale) {
        Iterator var3 = map.keySet().iterator();

        while (var3.hasNext()) {
            Object key = var3.next();
            if (null != map.get(key) && isContainChinese(map.get(key).toString())) {// true中文
                String mapkey = map.get(key).toString();
                if (mapkey.length() > 20) {
                    mapkey = mapkey.substring(0, 20);
                }

                if (propertiesValue.contains(mapkey)) {// messages.properties文件中存在key
                    if ("en_US".equals(locale)) {// spring解析区域
                        map.put(key, PropertiesUtil.readProperties(map.get(key), (String) ConstantUtil.getMessagesMap.get(ConstantUtil.TWO)));
                    } else if ("zh_TW".equals(locale)) {
                        map.put(key, PropertiesUtil.readProperties(map.get(key), (String) ConstantUtil.getMessagesMap.get(ConstantUtil.THREE)));
                    }
                } else {
                    map.put(key, compileString(mapkey, propertiesValue, locale));
                }
            }
        }

    }

    public static String translateMessage(String messageValue, Set propertiesValue, String locale) {
        if (propertiesValue.contains(messageValue)) {
            if ("en_US".equals(locale)) {
                messageValue = PropertiesUtil.readProperties(messageValue, (String) ConstantUtil.getMessagesMap.get(ConstantUtil.TWO)).toString();
            } else if ("zh_TW".equals(locale)) {
                messageValue = PropertiesUtil.readProperties(messageValue, (String) ConstantUtil.getMessagesMap.get(ConstantUtil.THREE)).toString();
            }
        } else {
            messageValue = compileString(messageValue, propertiesValue, locale);
        }

        if (isContainChinese(messageValue) && locale.equals("en_US")) {// 如果还是中文，调用有道翻译api
            String url = "http://fanyi.youdao.com/openapi.do?keyfrom=rubick&key=920168526&type=data&doctype=json&version=1.1&q=" + messageValue + "&from=zh-CHS&to=EN";
            RestTemplate restTemplate = new RestTemplate();
            String forObject = (String) restTemplate.getForObject(url, String.class, new Object[0]);
            Gson gson = new Gson();
            JsonObject jsonObject = (JsonObject) gson.fromJson(forObject, JsonObject.class);
            if (jsonObject.get("errorCode").getAsInt() == 0) {
                messageValue = jsonObject.get("translation").getAsJsonArray().get(0).getAsString();
            }
        }

        return messageValue;
    }

    public static String compileString(String messageValue, Set propertiesValue, String locale) {
        String[] compileArray = new String[]{"{0}", "{1}"};
        Iterator var4 = propertiesValue.iterator();

        while (var4.hasNext()) {
            Object key = var4.next();
            if (key.toString().contains(compileArray[0])) {
                Boolean flag = Pattern.matches(key.toString().replace(compileArray[0], ".*"), messageValue);
                String messageValuePart;
                String messageValuePro;
                if (flag) {
                    Integer start = key.toString().indexOf(compileArray[0]);
                    String open = key.toString().substring(0, start);
                    String close = key.toString().substring(start + 3);
                    messageValuePart = StringUtils.substringBetween(messageValue, open, close);
                    if ("en_US".equals(locale)) {
                        messageValuePro = PropertiesUtil.readProperties(key, (String) ConstantUtil.getMessagesMap.get(ConstantUtil.TWO)).toString();
                        return messageValuePro.replace(compileArray[0], messageValuePart);
                    }

                    if ("zh_TW".equals(locale)) {
                        messageValuePro = PropertiesUtil.readProperties(key, (String) ConstantUtil.getMessagesMap.get(ConstantUtil.THREE)).toString();
                        return messageValuePro.replace(compileArray[0], messageValuePart);
                    }
                } else if (key.toString().contains(compileArray[1])) {
                    Boolean flag2 = Pattern.matches(key.toString().replace(compileArray[0], ".*").replace(compileArray[1], ".*"), messageValue);
                    if (flag2) {
                        Integer start = key.toString().indexOf(compileArray[0]);
                        Integer start2 = key.toString().indexOf(compileArray[1]);
                        messageValuePart = key.toString().substring(0, start);
                        messageValuePro = key.toString().substring(start + 3, start2);
                        String close2 = key.toString().substring(start2 + 3);
                        //String messageValuePart = StringUtils.substringBetween(messageValue, messageValuePart, messageValuePro);
                        messageValuePart = StringUtils.substringBetween(messageValue, messageValuePart, messageValuePro);
                        String messageValuePartTwo = messageValue.replace(messageValuePart, "").replace(messageValuePro, "").replace(close2, "").replace(messageValuePart, "");
                        //String messageValuePro;
                        if ("en_US".equals(locale)) {
                            messageValuePro = PropertiesUtil.readProperties(key, (String) ConstantUtil.getMessagesMap.get(ConstantUtil.TWO)).toString();
                            return messageValuePro.replace(compileArray[0], messageValuePart).replace(compileArray[1], messageValuePartTwo);
                        }

                        if ("zh_TW".equals(locale)) {
                            messageValuePro = PropertiesUtil.readProperties(key, (String) ConstantUtil.getMessagesMap.get(ConstantUtil.THREE)).toString();
                            return messageValuePro.replace(compileArray[0], messageValuePart).replace(compileArray[1], messageValuePartTwo);
                        }
                    }
                }
            }
        }

        return messageValue;
    }

    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = Maps.newHashMap();
        if (bean instanceof HashMap) {
            map = (Map) bean;
        }

        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            Iterator var3 = beanMap.keySet().iterator();

            while (var3.hasNext()) {
                Object key = var3.next();
                ((Map) map).put(key + "", beanMap.get(key));
            }
        }

        return (Map) map;
    }
}
