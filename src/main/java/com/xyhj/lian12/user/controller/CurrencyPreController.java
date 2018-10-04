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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 币种介绍
 */
@RestController
@RequestMapping({"presentation"})
public class CurrencyPreController {
    private static final Logger log = LoggerFactory.getLogger(CurrencyPreController.class);
    @Autowired
    private UserRpc userRpc;
    @Autowired
    RedisTemplate redisTemplate;

    public CurrencyPreController() {
    }

    @PostMapping({"introduce"})
    public RespEntity introduce(@RequestParam Integer size, @RequestParam Integer status, @RequestParam Integer start) throws SQLException {
        if (null == size) {
            size = 10;
        }

        if (null == status) {
            status = 1;
        }

        if (null == start) {
            start = 1;
        }

        return this.userRpc.introduce(size, status, start);
    }

    /**
     * 通过id查询信息
     * @param id
     * @param idType 当idType="currency"时，查询的是币种id
     * @return
     * @throws SQLException
     */
    @PostMapping({"detail"})
    public RespEntity detail(@RequestParam("id") Integer id, @RequestParam("idType") String idType) throws SQLException {
        if (null == id) {
            id = 0;
        }

        return this.userRpc.detail(id, idType);
    }

    @PostMapping({"locale"})
    public RespEntity detail(@RequestParam("localeType") String localeType) throws SQLException {
        this.redisTemplate.opsForValue().set("localeType", localeType);
        return new RespEntity(200, "成功");
    }
}
