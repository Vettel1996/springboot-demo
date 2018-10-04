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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/updateVersion"})
public class UpdateAppVersionController {
    private static final Logger log = LoggerFactory.getLogger(UpdateAppVersionController.class);
    @Autowired
    private UserRpc userRpc;

    public UpdateAppVersionController() {
    }

    @PostMapping({"/update"})
    public RespEntity updateAndroidVersion(@RequestParam("version") String version, @RequestParam("source") Integer source, @RequestParam(value = "channelID",required = false) String channelID) throws Exception {
        return this.userRpc.update(version, source, channelID);
    }
}
