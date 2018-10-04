//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.util;

public class PhoneFilter {
    public PhoneFilter() {
    }

    public static String phoneFil(String mobile) {
        String str = "";

        for(int i = 0; i < mobile.length(); ++i) {
            if (i == mobile.length() - 11) {
                str = str + mobile.charAt(i);
            } else if (i == mobile.length() - 10) {
                str = str + mobile.charAt(i);
            } else if (i == mobile.length() - 9) {
                str = str + mobile.charAt(i);
            } else if (i == mobile.length() - 4) {
                str = str + mobile.charAt(i);
            } else if (i == mobile.length() - 3) {
                str = str + mobile.charAt(i);
            } else if (i == mobile.length() - 2) {
                str = str + mobile.charAt(i);
            } else if (i == mobile.length() - 1) {
                str = str + mobile.charAt(i);
            } else {
                str = str + "*";
            }
        }

        return str;
    }
}
