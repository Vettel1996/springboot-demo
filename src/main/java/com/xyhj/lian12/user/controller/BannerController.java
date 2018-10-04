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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/banner"})
public class BannerController {
    private static final Logger log = LoggerFactory.getLogger(BannerController.class);
    @Autowired
    private UserRpc userRpc;

    public BannerController() {
    }

    /**
     * 显示列表
     * @param type
     * @return
     * @throws Exception
     */
    @PostMapping({"/banner"})
    public RespEntity banner(@RequestParam("type") Short type) throws Exception {
        return this.userRpc.listPageBanner(type);
    }
}
