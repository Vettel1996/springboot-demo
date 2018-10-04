//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LocalDateUtil {
    public LocalDateUtil() {
    }

    /**
     * 日期加减方法
     * @param srcDate 原日期
     * @param num 加减天数
     * @return
     */
    public static String dayOperation(String srcDate, int num) {
        if (srcDate != null && !"".equals(srcDate)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date endTmp = null;

            try {
                endTmp = sdf.parse(srcDate);
            } catch (ParseException var6) {
                var6.printStackTrace();
            }

            Calendar instance = Calendar.getInstance();
            instance.setTime(endTmp);
            instance.add(5, num);
            Date time = instance.getTime();
            return sdf.format(time);
        } else {
            return null;
        }
    }

    public static String formatDate(Date date, String format) {
        if (date != null && format != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        } else {
            return null;
        }
    }
}
