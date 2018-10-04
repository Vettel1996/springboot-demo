//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.user.controller;

import com.xyhj.lian.util.RespCode;
import com.xyhj.lian.util.RespEntity;
import com.xyhj.lian12.base.config.BaseController;
import com.xyhj.lian12.user.dto.UserDto;
import com.xyhj.lian12.user.interfaces.UserRpc;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"user"})
public class GoogleAuthController extends BaseController {
    @Autowired
    private UserRpc userRpc;

    public GoogleAuthController() {
    }

    @PostMapping({"isUsedGoogleAuth"})
    public RespEntity isUsedGoogleAuth(HttpServletRequest request) {
        try {
            UserDto user = this.user(request);
            return this.userRpc.isUsedGoogleAuth(user.getUuid());
        } catch (Exception var3) {
            return RespEntity.error(RespCode.SYSTEM_ERROR);
        }
    }

    @PostMapping({"getSecretKey"})
    public RespEntity getSecretKey(HttpServletRequest request) {
        try {
            UserDto user = this.user(request);
            return this.userRpc.getSecretKey(user.getUuid());
        } catch (Exception var3) {
            return RespEntity.error(RespCode.SYSTEM_ERROR);
        }
    }

    @PostMapping({"bindGoogleAuth"})
    public RespEntity bindGoogleAuth(HttpServletRequest request, @RequestParam("loginPassword") String loginPassword, @RequestParam("clientPassword") Integer clientPassword) {
        try {
            UserDto user = this.user(request);
            return this.userRpc.bindGoogleAuth(user.getUuid(), loginPassword.toUpperCase(), clientPassword);
        } catch (Exception var5) {
            return RespEntity.error(RespCode.SYSTEM_ERROR);
        }
    }

    @PostMapping({"closeGoogleAuth"})
    public RespEntity closeGoogleAuth(HttpServletRequest request, @RequestParam("loginPassword") String loginPassword, @RequestParam("clientPassword") Integer clientPassword) throws Exception {
        UserDto user = this.user(request);
        return this.userRpc.closeGoogleAuth(user.getUuid(), loginPassword.toUpperCase(), clientPassword);
    }
}
