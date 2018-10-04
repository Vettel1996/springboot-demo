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
@RequestMapping({"vote"})
public class VoteController extends BaseController {
    @Autowired
    private UserRpc userRpc;

    public VoteController() {
    }

    @PostMapping({"list"})
    public RespEntity list(HttpServletRequest request) throws Exception {
        UserDto user = this.user(request);
        return this.userRpc.currencyList(user.getUuid());
    }

    @PostMapping({"vote"})
    public RespEntity vote(@RequestParam("currencyId") Integer currencyId, HttpServletRequest request) throws Exception {
        UserDto user = this.user(request);
        return user.getAuthLevel() == 0 ? RespEntity.error(RespCode.SYSTEM_ERROR) : this.userRpc.vote(user.getUuid(), currencyId);
    }
}
