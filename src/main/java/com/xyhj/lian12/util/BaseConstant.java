//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.util;

import com.xyhj.lian.util.StringUtils;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseConstant {
    public static final String APP_TOKEN = "appToken";
    public static final String APP_TIME = "appTime";
    private static final String key0 = "FECOI()*&<MNCXZPKL";
    private static final Charset charset = Charset.forName("UTF-8");
    private static byte[] keyBytes;

    public BaseConstant() {
    }

    /**
     * 验证是否为手机登录
     * android : 所有android设备
     * ios : iphone ipad
     * windows phone:Nokia等windows系统的手机
     * @param requestHeader
     * @return
     */
    public static boolean isMobileDevice(String requestHeader) {
        String[] deviceArray = new String[]{"android", "ios", "windows phone", "iphone"};
        if (StringUtils.isBlank(requestHeader)) {
            return false;
        } else {
            requestHeader = requestHeader.toLowerCase();

            for(int i = 0; i < deviceArray.length; ++i) {
                if (requestHeader.indexOf(deviceArray[i]) >= 0) {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * 根据提币码提取uid
     * @param addressCode
     * @return
     */
    public static Long getUidByAddressCode(String addressCode) {
        String dec = StringUtils.swapCase(addressCode.substring(0, addressCode.length() - 32));
        byte[] e = dec.getBytes(charset);
        byte[] dee = e;
        int i = 0;

        for(int size = e.length; i < size; ++i) {
            byte[] var6 = keyBytes;
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                byte keyBytes0 = var6[var8];
                e[i] = (byte)(dee[i] ^ keyBytes0);
            }
        }

        try {
            long var11 = Long.parseLong(new String(e));
        } catch (Exception var10) {
            return 0L;
        }

        return Long.parseLong(new String(e));
    }

    public static boolean isEmail(String string) {
        if (string == null) {
            return false;
        } else {
            String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern p = Pattern.compile(regEx1);
            Matcher m = p.matcher(string);
            return m.matches();
        }
    }

    static {
        keyBytes = "FECOI()*&<MNCXZPKL".getBytes(charset);
    }
}
