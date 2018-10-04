//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TranslateApi {
    private static final Integer MAX_LENGTH = 2000;
    private static TransApi api = TransApi.getInstance();

    public TranslateApi() {
    }

    public static String getZhCnToType(String queryParam, String type) {
        if (queryParam.length() <= MAX_LENGTH) {
            return translate(queryParam, type);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            List<String> listQueryParams = getStrList(queryParam, MAX_LENGTH);
            Iterator var4 = listQueryParams.iterator();

            while(var4.hasNext()) {
                String param = (String)var4.next();
                stringBuilder.append(translate(param, type));
            }

            return stringBuilder.toString();
        }
    }

    public static String translate(String queryParam, String type) {
        StringBuilder stringBuilder = new StringBuilder();
        Object object = api.getTransResult(queryParam, "auto", type);
        JSONObject jsonInit = JSONObject.parseObject(object.toString());
        JSONArray jsonArray = (JSONArray)jsonInit.get("trans_result");
        Iterator var6 = jsonArray.iterator();

        while(var6.hasNext()) {
            Object o = var6.next();
            JSONObject jsonObject = JSONObject.parseObject(o.toString());
            stringBuilder.append(jsonObject.get("dst"));
        }

        return stringBuilder.toString();
    }

    public static List<String> getStrList(String inputString, int length) {
        int size = inputString.length() / length;
        List<String> list = new ArrayList();
        if (inputString.length() % length != 0) {
            ++size;
        }

        for(int index = 0; index < size; ++index) {
            String childStr = substring(inputString, index * length, (index + 1) * length);
            list.add(childStr);
        }

        return list;
    }

    public static String substring(String str, int f, int t) {
        if (f > str.length()) {
            return null;
        } else {
            return t > str.length() ? str.substring(f, str.length()) : str.substring(f, t);
        }
    }
}
