//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class PropertiesUtil {
    private static final String MESSAGES = "/messages.properties";
    private static volatile Properties properties = getInstance();

    public PropertiesUtil() {
    }

    public static Properties getInstance() {
        if (properties == null) {
            Class var0 = Properties.class;
            synchronized(Properties.class) {
                if (properties == null) {
                    properties = new Properties();
                }
            }
        }

        return properties;
    }

    public static Set readProperties() {
        try {
            properties.load(new InputStreamReader(PropertiesUtil.class.getResourceAsStream("/messages.properties"), "UTF-8"));
            return properties.keySet();
        } catch (IOException var1) {
            return null;
        }
    }

    public static Set readProperties(String type) {
        try {
            properties.load(new InputStreamReader(PropertiesUtil.class.getResourceAsStream(type), "UTF-8"));
            return properties.keySet();
        } catch (IOException var2) {
            return null;
        }
    }

    public static Object readProperties(Object key, String type) {
        try {
            properties.load(new InputStreamReader(PropertiesUtil.class.getResourceAsStream(type), "UTF-8"));
            return properties.get(key);
        } catch (IOException var3) {
            return null;
        }
    }

    public static void writProperties(String messages, String pKey, String pValue) {
        FileOutputStream out = null;

        try {
            properties.load(new InputStreamReader(PropertiesUtil.class.getResourceAsStream(messages), "UTF-8"));
            System.out.println(PropertiesUtil.class.getResource(messages));
            out = new FileOutputStream(Object.class.getResource(messages).getPath());
            properties.setProperty(pKey, pValue);
            properties.store(out, "modify" + (new Date()).toString());
            out.flush();
        } catch (IOException var13) {
            var13.printStackTrace();
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
            } catch (IOException var12) {
                var12.printStackTrace();
            }

        }

    }

    public static void writPropertiesList(String pKey) {
        Map<Integer, String> getMessagesMap = ConstantUtil.getMessagesMap;
        Iterator var2 = getMessagesMap.keySet().iterator();

        while(var2.hasNext()) {
            Integer messagesType = (Integer)var2.next();
            String getMessagesMapValue = (String)getMessagesMap.get(messagesType);
            switch(messagesType) {
                case 0:
                    writProperties(getMessagesMapValue, pKey, pKey);
                    break;
                case 1:
                default:
                    writProperties(getMessagesMapValue, pKey, pKey);
                    break;
                case 2:
                    String typeValue = (String)ConstantUtil.getType.get(messagesType);
                    String paramValue = TranslateApi.getZhCnToType(pKey, typeValue);
                    System.out.println(paramValue);
                    writProperties(getMessagesMapValue, pKey, paramValue);
                    break;
                case 3:
                    String typeValue1 = (String)ConstantUtil.getType.get(messagesType);
                    String paramValue1 = TranslateApi.getZhCnToType(pKey, typeValue1);
                    System.out.println(paramValue1);
                    writProperties(getMessagesMapValue, pKey, paramValue1);
            }
        }

    }

    public static String translateMessage(String messageValue, String locale) {
        if ("en_US".equals(locale)) {
            messageValue = readProperties(messageValue, (String)ConstantUtil.getMessagesMap.get(ConstantUtil.TWO)).toString();
        } else if ("zh_TW".equals(locale)) {
            messageValue = readProperties(messageValue, (String)ConstantUtil.getMessagesMap.get(ConstantUtil.THREE)).toString();
        }

        return messageValue;
    }
}
