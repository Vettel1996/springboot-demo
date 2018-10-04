//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.util;

import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignValidUtil {
    private static final Logger log = LoggerFactory.getLogger(SignValidUtil.class);

    public SignValidUtil() {
    }

    /**
     * 校验签名效果
     * @param type 1：MD5，3：RSA
     * @param secret
     * @param list
     * @param sign
     * @return
     */
    public static Boolean validSign(int type, String secret, List<String> list, String sign) {
        list.sort(String::compareTo);
        String input = String.join("&", list);
        return validSign(type, secret, input, sign);
    }

    /**
     * 校验签名是否有效
     * @param type
     * @param secret
     * @param input
     * @param sign
     * @return
     */
    public static Boolean validSign(int type, String secret, String input, String sign) {
        if (type == 1) {
            String out = MD5.md5(input + "&" + secret);
            if (out.equals(sign)) {
                return true;
            }
        } else if (type == 3) {
            try {
                return RSACoder.verify(input.getBytes(), secret, sign);
            } catch (Exception var6) {
                var6.printStackTrace();
            }
        }

        return false;
    }

    public static String sign(int type, String secret, List<String> list) {
        list.sort(String::compareTo);
        String input = String.join("&", list);
        return sign(type, secret, input);
    }

    public static String sign(int type, String secret, String input) {
        log.info("type:" + type + " secret:" + secret + " input:" + input);
        String out = null;
        if (type == 1) {
            out = MD5.md5(input + "&" + secret);
        } else if (type == 3) {
            try {
                out = RSACoder.sign(input.getBytes(), secret);
            } catch (Exception var5) {
                var5.printStackTrace();
            }
        }

        return out;
    }
}
