//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    public DateUtil() {
    }

    public static boolean timestampValid(String timestamp, int limit) {
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df2.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date time = df2.parse(timestamp);
            long now = (new Date()).getTime();
            return Math.abs(now - time.getTime()) <= (long)limit;
        } catch (ParseException var6) {
            var6.printStackTrace();
            return false;
        }
    }
}
