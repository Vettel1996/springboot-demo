//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.util;

import java.util.HashMap;
import java.util.Map;

public class TransApi {
    private static final String APP_ID = "2015063000000001";
    private static final String SECURITY_KEY = "12345678";
    private static volatile TransApi transApi;
    private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";
    private String appid;
    private String securityKey;

    public static TransApi getInstance() {
        if (transApi == null) {
            Class var0 = TranslateApi.class;
            synchronized(TranslateApi.class) {
                if (transApi == null) {
                    transApi = new TransApi("2015063000000001", "12345678");
                }
            }
        }

        return transApi;
    }

    public TransApi(String appid, String securityKey) {
        this.appid = appid;
        this.securityKey = securityKey;
    }

    public String getTransResult(String query, String from, String to) {
        Map<String, String> params = this.buildParams(query, from, to);
        return HttpGet.get("http://api.fanyi.baidu.com/api/trans/vip/translate", params);
    }

    private Map<String, String> buildParams(String query, String from, String to) {
        Map<String, String> params = new HashMap();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);
        params.put("appid", this.appid);
        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);
        // 签名
        String src = this.appid + query + salt + this.securityKey;// 加密前的原文
        params.put("sign", MD5.md5(src));
        return params;
    }
}
