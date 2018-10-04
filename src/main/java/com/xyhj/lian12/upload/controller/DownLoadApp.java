//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.upload.controller;

import com.xyhj.lian.util.RespEntity;
import com.xyhj.lian12.user.interfaces.UserRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/downloadapp"})
public class DownLoadApp {
    private static final Logger log = LoggerFactory.getLogger(DownLoadApp.class);
    @Autowired
    private UserRpc userRpc;

    public DownLoadApp() {
    }

    @PostMapping({"/updateLocation"})
    public RespEntity downloadapp() throws Exception {
        return this.userRpc.updateLocation();
    }
}
