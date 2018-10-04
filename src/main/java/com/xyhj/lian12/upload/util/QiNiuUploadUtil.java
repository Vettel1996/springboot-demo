//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.upload.util;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.processing.OperationManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;
import com.xyhj.lian.exception.RedisException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QiNiuUploadUtil {
    private static final Logger log = LoggerFactory.getLogger(QiNiuUploadUtil.class);
    // 构造一个带指定Zone对象的配置类
    private static Configuration cfg = null;
    private static UploadManager uploadManager = null;
    private static Auth auth = null;
    private static String upToken = null;
    private static String bucket = "12lian";
    private static String vedioBucket = "vediomask";
    // 水印样式
    private static String fops = "imageView2/2/w/10000/h/10000/q/100|watermark/2/text/5rC05Y2w5rC05Y2w/font/5a6L5L2T/fontsize/500/fill/I0VGRUZFRg==/dissolve/84/gravity/SouthEast/dx/10/dy/10";
    private static Map<String, Long> timeout = new HashMap();

    public QiNiuUploadUtil() {
    }

    public static String upload(String key, String localFile) throws RedisException {
        if (System.currentTimeMillis() / 1000L - (Long)timeout.get(upToken) > 3600L) {
            upToken = auth.uploadToken(vedioBucket);
            timeout.put(upToken, System.currentTimeMillis() / 1000L);
        }

        try {
            Response response = uploadManager.put(localFile, key, upToken);
            // 解析上传成功的结果
            DefaultPutRet putRet = (DefaultPutRet)response.jsonToObject(DefaultPutRet.class);
            log.info("[七牛SDK上传成功][KEY]{}", putRet.key);
            log.info("[七牛SDK上传成功][HASH]{}", putRet.hash);
            return putRet.key;
        } catch (QiniuException var6) {
            Response r = var6.response;
            log.error("[七牛SDK异常]{}", r.toString());

            try {
                log.error("[七牛SDK异常]{}", r.bodyString());
            } catch (QiniuException var5) {
                var5.printStackTrace();
            }

            return null;
        }
    }

    public static String upload(String key, byte[] bytes, String target) throws RedisException {
        // 水印样式
        if (target != null) {
            bucket = vedioBucket;
        }

        String fops = "watermark/2/text/d3d3LjEybGlhbi5jb20=/font/5a6L5L2T/fontsize/1000/fill/I0ZGRkZGRg==/dissolve/85/gravity/SouthEast/dx/20/dy/20";
        String urlbase64 = UrlSafeBase64.encodeToString(bucket + ":" + key);
        String pfops = fops + "|saveas/" + urlbase64;
        StringMap persistentOps = (new StringMap()).putNotEmpty("persistentOps", pfops);
        Auth a = Auth.create("b3bp4uv5hGe7jKKKa43udUdAb-8oROcdgvx-Tob5", "UA5d6ABG2HZgQAF93HKdL8ksIECFx9LFOsvFNNgu");
        String s = a.uploadToken(bucket, (String)null, 3600L, persistentOps, true);

        Response r;
        try {
            UploadManager uploadM = new UploadManager(cfg);
            r = uploadM.put(bytes, key, s);
            log.info("[七牛SDK上传成功,返回数据为", r.bodyString());
            DefaultPutRet putRet = (DefaultPutRet)r.jsonToObject(DefaultPutRet.class);
            log.info("[七牛SDK上传成功][KEY]{}", putRet.key);
            log.info("[七牛SDK上传成功][HASH]{}", putRet.hash);
            return putRet.key;
        } catch (QiniuException var13) {
            r = var13.response;
            log.error("[七牛SDK异常]{}", r.toString());

            try {
                log.error("[七牛SDK异常]{}", r.bodyString());
            } catch (QiniuException var12) {
                var12.printStackTrace();
            }

            return null;
        }
    }

    public static String uplodaVedio(String fileKey, Integer userid) {
        if (System.currentTimeMillis() / 1000L - (Long)timeout.get(upToken) > 3600L) {
            upToken = auth.uploadToken(bucket);
            timeout.put(upToken, System.currentTimeMillis() / 1000L);
        }

        // 水印样式
        Auth a = Auth.create("b3bp4uv5hGe7jKKKa43udUdAb-8oROcdgvx-Tob5", "UA5d6ABG2HZgQAF93HKdL8ksIECFx9LFOsvFNNgu");
        OperationManager operater = new OperationManager(a, cfg);
        String pictureurl = UrlSafeBase64.encodeToString("http://source.12lian.com/watermask.png");
        String fops = "avthumb/mp4/wmGravity/NorthEast/wmOffsetX/10/wmOffsetY/450/wmImage/" + pictureurl;
        String urlbase64 = UrlSafeBase64.encodeToString(bucket + ":" + userid);
        String pfops = fops + "|saveas/" + urlbase64;
        StringMap params = (new StringMap()).putWhen("force", 1, true).putNotEmpty("pipeline", (String)null);

        try {
            operater.pfop(bucket, fileKey, pfops, params);
            log.info("视频转码成功");
        } catch (QiniuException var13) {
            Response r = var13.response;
            log.error("[七牛SDK异常]{}", r.toString());

            try {
                log.error("[七牛SDK异常]{}", r.bodyString());
            } catch (QiniuException var12) {
                var12.printStackTrace();
            }
        }

        return "sucess";
    }

    static {
        cfg = new Configuration(Zone.zone1());
        uploadManager = new UploadManager(cfg);
        auth = Auth.create("b3bp4uv5hGe7jKKKa43udUdAb-8oROcdgvx-Tob5", "UA5d6ABG2HZgQAF93HKdL8ksIECFx9LFOsvFNNgu");
        upToken = auth.uploadToken(bucket, (String)null, 3600L, (new StringMap()).putNotEmpty("persistentOps", fops));
        timeout.put(upToken, System.currentTimeMillis() / 1000L);
    }
}
