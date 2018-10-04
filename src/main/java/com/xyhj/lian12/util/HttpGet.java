//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

class HttpGet {
    protected static final int SOCKET_TIMEOUT = 10000;
    protected static final String GET = "GET";
    private static TrustManager myX509TrustManager = new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    };

    HttpGet() {
    }

    public static String get(String host, Map<String, String> params) {
        try {
            // 设置SSLContext
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init((KeyManager[])null, new TrustManager[]{myX509TrustManager}, (SecureRandom)null);
            String sendUrl = getUrlWithQueryString(host, params);
            // 创建URL对象
            URL uri = new URL(sendUrl);
            HttpURLConnection conn = (HttpURLConnection)uri.openConnection();
            if (conn instanceof HttpsURLConnection) {
                ((HttpsURLConnection)conn).setSSLSocketFactory(sslcontext.getSocketFactory());
            }

            // 设置相应超时
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("GET");
            int statusCode = conn.getResponseCode();
            if (statusCode != 200) {
                System.out.println("Http错误码：" + statusCode);
            }

            // 读取服务器的数据
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();
            String line = null;

            while((line = br.readLine()) != null) {
                builder.append(line);
            }

            String text = builder.toString();
            // 关闭数据流
            close(br);
            close(is);
            // 断开连接s
            conn.disconnect();
            return text;
        } catch (MalformedURLException var12) {
            var12.printStackTrace();
        } catch (IOException var13) {
            var13.printStackTrace();
        } catch (KeyManagementException var14) {
            var14.printStackTrace();
        } catch (NoSuchAlgorithmException var15) {
            var15.printStackTrace();
        }

        return null;
    }

    public static String getUrlWithQueryString(String url, Map<String, String> params) {
        if (params == null) {
            return url;
        } else {
            StringBuilder builder = new StringBuilder(url);
            if (url.contains("?")) {
                builder.append("&");
            } else {
                builder.append("?");
            }

            int i = 0;
            Iterator var4 = params.keySet().iterator();

            while(var4.hasNext()) {
                String key = (String)var4.next();
                String value = (String)params.get(key);
                if (value != null) {
                    if (i != 0) {
                        builder.append('&');
                    }

                    builder.append(key);
                    builder.append('=');
                    builder.append(encode(value));
                    ++i;
                }
            }

            return builder.toString();
        }
    }

    protected static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

    }

    /**
     * 对输入的字符串进行URL编码, 即转换为%20这种形式
     * @param input
     * @return URL编码. 如果编码失败, 则返回原文
     */
    public static String encode(String input) {
        if (input == null) {
            return "";
        } else {
            try {
                return URLEncoder.encode(input, "utf-8");
            } catch (UnsupportedEncodingException var2) {
                var2.printStackTrace();
                return input;
            }
        }
    }
}
