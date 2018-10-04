//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.user.controller;

import com.xyhj.lian.util.RespCode;
import com.xyhj.lian.util.RespEntity;
import com.xyhj.lian12.user.interfaces.UserRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/notice"})
public class NoticeController {
    private static final Logger log = LoggerFactory.getLogger(NoticeController.class);
    @Autowired
    private UserRpc userRpc;

    public NoticeController() {
    }

    /**
     * 删除通知
     * @param noticeid
     * @return
     * @throws Exception
     */
    @PostMapping({"/del"})
    public RespEntity del(@RequestParam("noticeid") Long noticeid) throws Exception {
        return this.userRpc.del(noticeid);
    }

    /**
     * 我的通知列表
     * @param size
     * @param start
     * @param uid
     * @return
     * @throws Exception
     */
    @PostMapping({"/notices"})
    public RespEntity notices(@RequestParam(value = "size",required = false) Integer size, @RequestParam(value = "start",required = false) Integer start, @RequestParam("uid") Long uid) throws Exception {
        return uid == null ? RespEntity.error(RespCode.COMMON_PARAM_BLANK) : this.userRpc.notices(size, start, uid);
    }

    /**
     * 查看已读
     * @param noticeid
     * @return
     * @throws Exception
     */
    @PostMapping({"/updateNotices"})
    public RespEntity updateNotices(@RequestParam("noticeid") Long noticeid) throws Exception {
        return this.userRpc.updateNotices(noticeid);
    }
}
