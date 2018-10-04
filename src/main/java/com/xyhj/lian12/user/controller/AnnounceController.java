//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.user.controller;

import com.xyhj.lian.util.RespEntity;
import com.xyhj.lian12.user.interfaces.UserRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/announce"})
public class AnnounceController {
    private static final Logger log = LoggerFactory.getLogger(AnnounceController.class);
    @Autowired
    private UserRpc userRpc;

    public AnnounceController() {
    }

    @RequestMapping({"/list"})
    public RespEntity getAnnounceList(@RequestParam("num") Integer num, String local, Integer linkId) throws Exception {
        return this.userRpc.getAnnounceList(num, linkId, local);
    }

    @RequestMapping({"/getInfo"})
    public RespEntity getAnnounceInfo(@RequestParam("announceId") Integer announceId) throws Exception {
        return this.userRpc.getAnnounceInfo(announceId);
    }

    @RequestMapping({"/pageList"})
    public RespEntity pageList(@RequestParam(value = "page",defaultValue = "1") Integer page, @RequestParam(value = "rows",defaultValue = "10") Integer rows, String local, Integer linkId) throws Exception {
        return this.userRpc.announcePageList(page, linkId, rows, local);
    }

    /**
     * 获取总数
     * @param local
     * @param linkId
     * @return
     * @throws Exception
     */
    @RequestMapping({"/getAnnounceCount"})
    public RespEntity getAnnounceCount(String local, Integer linkId) throws Exception {
        return this.userRpc.getAnnounceCount(local, linkId);
    }
}
