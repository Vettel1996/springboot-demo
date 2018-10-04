//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.base.interceptor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ParameterRequestWrapper extends HttpServletRequestWrapper {
    private Map<String, String[]> params;

    public ParameterRequestWrapper(HttpServletRequest request) {
        // 将request交给父类，以便于调用对应方法的时候，将其输出，其实父亲类的实现方式和第一种new的方式类似
        super(request);
        this.params = new HashMap();
        // 将参数表，赋予给当前的Map以便于持有request中的参数
        this.params.putAll(request.getParameterMap());
    }

    /**
     * 重载一个构造方法
     * @param request
     * @param extendParams
     */
    public ParameterRequestWrapper(HttpServletRequest request, Map<String, Object> extendParams) {
        this(request);
        this.addAllParameters(extendParams);// 这里将扩展参数写入参数表
    }

    public String getParameter(String name) {
        String[] values = (String[])this.params.get(name);
        return values != null && values.length != 0 ? values[0] : null;
    }

    public String[] getParameterValues(String name) {
        return (String[])this.params.get(name);
    }

    public void addAllParameters(Map<String, Object> otherParams) {
        Iterator var2 = otherParams.entrySet().iterator();

        while(var2.hasNext()) {
            Entry<String, Object> entry = (Entry)var2.next();
            this.addParameter((String)entry.getKey(), entry.getValue());
        }

    }

    public void addParameter(String name, Object value) {
        if (value != null) {
            if (value instanceof String[]) {
                this.params.put(name, (String[])((String[])value));
            } else if (value instanceof String) {
                this.params.put(name, new String[]{(String)value});
            } else {
                this.params.put(name, new String[]{String.valueOf(value)});
            }
        }

    }
}
