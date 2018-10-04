//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.upload.controller;

import com.xyhj.lian.util.RespEntity;
import com.xyhj.lian12.upload.service.IUploadService;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping({"upload"})
public class UploadController {
    private static final Logger log = LoggerFactory.getLogger(UploadController.class);
    @Autowired
    IUploadService uploadService;

    public UploadController() {
    }

    /**
     * 通用文件上传,本地不再创建文件,减少空间
     * @param file
     * @param busId
     * @return
     * @throws IOException
     */
    @PostMapping({"upload"})
    public RespEntity requestUpload(@RequestParam("file") MultipartFile file, @RequestParam(value = "busId",required = false) Integer busId) throws IOException {
        log.info("[文件上传][busID],{}", busId);
        return this.uploadService.saveUpload(busId, file.getBytes());
    }

    @PostMapping({"uploadVedio"})
    public RespEntity uploadVedio(@RequestParam("file") MultipartFile file, @RequestParam("busId") Integer busId) throws IOException {
        RespEntity respEntity = this.uploadService.saveUpload(busId, file.getBytes());
        Map<String, String> map = (Map)respEntity.getAttachment();
        String key = (String)map.get("key");
        return this.uploadService.uplodaVedio(key, busId);
    }
}
