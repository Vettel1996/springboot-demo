//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.upload.service;

import com.xyhj.lian.util.RespEntity;

public interface IUploadService {
    RespEntity saveUpload(Integer var1, String var2);

    RespEntity saveUpload(Integer var1, byte[] var2);

    RespEntity uplodaVedio(String var1, Integer var2);
}
