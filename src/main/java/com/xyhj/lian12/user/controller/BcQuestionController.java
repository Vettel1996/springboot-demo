//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.user.controller;

import com.xyhj.lian.util.RespEntity;
import com.xyhj.lian12.user.dto.BcQuestion;
import com.xyhj.lian12.user.interfaces.UserRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 常见的问题
 */
@RestController
@RequestMapping({"/bcQuestion"})
public class BcQuestionController {
    private static final Logger log = LoggerFactory.getLogger(BcQuestionController.class);
    @Autowired
    private UserRpc userRpc;

    public BcQuestionController() {
    }

    /**
     * 显示列表
     * @param bcQuestion
     * @return
     */
    @RequestMapping({"list"})
    public RespEntity list(BcQuestion bcQuestion) {
        bcQuestion.setStatus(1);
        return RespEntity.success(this.userRpc.bcQuestionList(bcQuestion));
    }
}
