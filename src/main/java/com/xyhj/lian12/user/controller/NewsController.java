//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.user.controller;

import com.xyhj.lian.util.RespEntity;
import com.xyhj.lian12.user.interfaces.UserRpc;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/news"})
public class NewsController {
    private static final Logger log = LoggerFactory.getLogger(NewsController.class);
    @Autowired
    private UserRpc userRpc;

    public NewsController() {
    }

    @PostMapping({"/recommend"})
    public RespEntity recommend(@RequestParam("size") Integer size, @RequestParam("status") Short status) throws SQLException {
        return this.userRpc.recommend(size, status);
    }

    @PostMapping({"/news"})
    public RespEntity news(@RequestParam("size") Integer size, @RequestParam("status") Short status, @RequestParam("start") Integer start) throws SQLException {
        return this.userRpc.news(size, status, start);
    }

    @PostMapping({"/detail"})
    public RespEntity detail(@RequestParam("newsid") Long newsid) throws Exception {
        return this.userRpc.detail(newsid);
    }
}
