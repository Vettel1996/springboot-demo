//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.upload.service.impl;

import com.google.common.collect.Maps;
import com.xyhj.lian.util.MD5Util;
import com.xyhj.lian.util.RespEntity;
import com.xyhj.lian12.upload.service.IUploadService;
import com.xyhj.lian12.upload.util.QiNiuUploadUtil;
import com.xyhj.lian12.upload.util.UploadUtil;
import java.util.Date;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UploadServiceImpl implements IUploadService {
    private static final Logger log = LoggerFactory.getLogger(UploadServiceImpl.class);

    public UploadServiceImpl() {
    }

    public RespEntity saveUpload(Integer busId, String location) {
        String fileKey = MD5Util.encrypt(location) + (new Date()).getTime();

        try {
            String key = QiNiuUploadUtil.upload(fileKey, location);
            Map<String, String> params = Maps.newHashMap();
            params.put("key", fileKey);
            return RespEntity.success(key);
        } catch (Exception var6) {
            var6.printStackTrace();
            log.error("[七牛SDK异常]{}", var6.getMessage());
            return RespEntity.success((Object)null);
        }
    }

    public RespEntity saveUpload(Integer busId, byte[] bytes) {
        String fileKey = MD5Util.encrypt((new Date()).getTime() + "" + Math.random() + busId);

        try {
            UploadUtil.uploadImg(busId, bytes, fileKey);
            Map<String, String> params = Maps.newHashMap();
            params.put("key", fileKey);
            return RespEntity.success(params);
        } catch (Exception var5) {
            var5.printStackTrace();
            log.error("[七牛SDK异常]{}", var5.getMessage());
            return RespEntity.success((Object)null);
        }
    }

    public RespEntity uplodaVedio(String fileKey, Integer userid) {
        try {
            QiNiuUploadUtil.uplodaVedio(fileKey, userid);
            Map<String, String> params = Maps.newHashMap();
            params.put("key", fileKey);
            return RespEntity.success(params);
        } catch (Exception var4) {
            var4.printStackTrace();
            log.error("[七牛SDK异常]{}", var4.getMessage());
            return RespEntity.success((Object)null);
        }
    }
}
