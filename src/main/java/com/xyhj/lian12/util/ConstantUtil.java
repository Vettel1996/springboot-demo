//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.util;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

public class ConstantUtil {
    public static final Integer TWO = 2;
    public static final Integer THREE = 3;
    public static final String MESSAGES_NOT = "/messages_not.properties";
    public static final String MESSAGES = "/messages.properties";
    public static final String MESSAGES_EN_US = "/messages_en_US.properties";
    public static final String MESSAGES_ZH_CN = "/messages_zh_CN.properties";
    public static final String MESSAGES_ZH_TW = "/messages_zh_TW.properties";
    public static Map<Integer, String> getMessagesMap = new HashMap<Integer, String>() {
        {
            this.put(-1, "/messages_not.properties");
            this.put(0, "/messages.properties");
            this.put(1, "/messages_zh_CN.properties");
            this.put(2, "/messages_en_US.properties");
            this.put(3, "/messages_zh_TW.properties");
        }
    };
    public static final String EN = "en";
    public static final String CHT = "cht";
    public static Map<Integer, String> getType = new Hashtable<Integer, String>() {
        {
            this.put(2, "en");
            this.put(3, "cht");
        }
    };

    public ConstantUtil() {
    }

    public static String getUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    public static String getUuid2() {
    	String replace = UUID.randomUUID().toString().replace("-", "");
    	long currentTimeMillis = System.currentTimeMillis();
        return replace.substring(0,15)+currentTimeMillis+replace.substring(14,31);
    }
    
 
}
