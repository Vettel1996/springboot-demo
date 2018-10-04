//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.upload.util;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadUtil {
    private static final Logger log = LoggerFactory.getLogger(UploadUtil.class);
    // 构造一个带指定Zone对象的配置类
    private static Configuration cfg = null;
    private static UploadManager uploadManager = null;
    private static Auth auth = null;
    private static String upToken = null;
    private static String bucket = "12lian";
    private static String vedioBucket = "vediomask";
    // 水印样式
    public static String pictureurl = UrlSafeBase64.encodeToString("http://source.12lian.com/watermask.png");
    private static Map<String, Long> timeout = new HashMap();

    public UploadUtil() {
    }

    public static String uploadImg(Integer busId, byte[] bytes, String key) {
        if (System.currentTimeMillis() / 1000L - (Long)timeout.get(upToken) > 3600L) {
            upToken = auth.uploadToken(bucket);
            timeout.put(upToken, System.currentTimeMillis() / 1000L);
        }

        String fops = "watermark/1/gravity/SouthEast/dx/20/dy/20/image/" + pictureurl;
        String urlbase64 = UrlSafeBase64.encodeToString(bucket + ":" + key);
        String pfops = fops + "|saveas/" + urlbase64;
        StringMap persistentOps = (new StringMap()).putNotEmpty("persistentOps", pfops);
        String s = auth.uploadToken(bucket, (String)null, 3600L, persistentOps, true);
        return commonUpload(bytes, key, s);
    }

    public static String uploadVedio(byte[] bytes, String key) throws QiniuException {
        if (System.currentTimeMillis() / 1000L - (Long)timeout.get(upToken) > 3600L) {
            upToken = auth.uploadToken(vedioBucket);
            timeout.put(upToken, System.currentTimeMillis() / 1000L);
        }

        String fops = "avthumb/mp4/wmGravity/NorthEast/wmOffsetX/10/wmOffsetY/450/wmImage/" + pictureurl;
        String urlbase64 = UrlSafeBase64.encodeToString(vedioBucket + ":" + key);
        (new StringBuilder()).append(fops).append("|saveas/").append(urlbase64).toString();
        StringMap params = (new StringMap()).putWhen("force", 1, true).putNotEmpty("pipeline", (String)null);
        String s = auth.uploadToken(vedioBucket, (String)null, 3600L, params, true);
        return commonUpload(bytes, key, s);
    }

    public static String commonUpload(byte[] bytes, String key, String s) {
        Response r;
        try {
            UploadManager uploadM = new UploadManager(cfg);
            r = uploadM.put(bytes, key, s);
            log.info("[七牛SDK上传成功,返回数据为", r.bodyString());
            // 解析上传成功的结果
            DefaultPutRet putRet = (DefaultPutRet)r.jsonToObject(DefaultPutRet.class);
            log.info("[七牛SDK上传成功][KEY]{}", putRet.key);
            log.info("[七牛SDK上传成功][HASH]{}", putRet.hash);
            return putRet.key;
        } catch (QiniuException var7) {
            r = var7.response;
            log.error("[七牛SDK异常]{}", r.toString());

            try {
                log.error("[七牛SDK异常]{}", r.bodyString());
            } catch (QiniuException var6) {
                var6.printStackTrace();
            }

            return null;
        }
    }

    static {
        cfg = new Configuration(Zone.zone1());
        uploadManager = new UploadManager(cfg);
        auth = Auth.create("b3bp4uv5hGe7jKKKa43udUdAb-8oROcdgvx-Tob5", "UA5d6ABG2HZgQAF93HKdL8ksIECFx9LFOsvFNNgu");
        timeout.put(upToken, System.currentTimeMillis() / 1000L);
    }
}
