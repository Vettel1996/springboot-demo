//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.user.controller;

import com.xyhj.lian.exception.RedisException;
import com.xyhj.lian.util.RespEntity;
import com.xyhj.lian12.base.config.BaseController;
import com.xyhj.lian12.user.dto.UserDto;
import com.xyhj.lian12.user.interfaces.UserRpc;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/reward"})
public class ActivityController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(ActivityController.class);
    @Autowired
    private UserRpc userRpc;

    public ActivityController() {
    }

    /**
     * 抽奖
     * @param prop
     * @param type
     * @param type11
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping({"/reward"})
    public RespEntity reward(@RequestParam("prop") String prop, @RequestParam("type") String type, @RequestParam("type11") String type11, HttpServletRequest request) throws Exception {
        UserDto user = this.user(request);
        return this.userRpc.reward(prop, type, type11, user.getUuid());
    }

    /**
     * 我的奖品列表
     * @param request
     * @return
     * @throws RedisException
     * @throws Exception
     */
    @RequestMapping({"myReward"})
    public RespEntity myReward(HttpServletRequest request) throws RedisException, Exception {
        UserDto user = this.user(request);
        return this.userRpc.myReward(user.getUuid());
    }

    @RequestMapping({"ActivityRedirect"})
    public RespEntity activityRedirect(HttpServletRequest request) throws Exception {
        UserDto user = this.user(request);
        return this.userRpc.myReward(user.getUuid());
    }

    /**
     * 更新用户收货地址
     * @param request
     * @param phone
     * @param postCode
     * @param city
     * @param town
     * @param location
     * @param name
     * @return
     * @throws Exception
     */
    @RequestMapping(
            value = {"/userAddress/update"},
            method = {RequestMethod.POST}
    )
    public RespEntity updateUserAddAndStatus(HttpServletRequest request, @RequestParam(value = "phone",required = false) String phone, @RequestParam(value = "postCode",required = false) String postCode, @RequestParam(value = "city",required = false) String city, @RequestParam(value = "town",required = false) String town, @RequestParam(value = "location",required = false) String location, @RequestParam(value = "name",required = false) String name) throws Exception {
        UserDto user = this.user(request);
        return this.userRpc.updateUserAddAndStatus(user.getUuid(), phone, postCode, city, town, location, name);
    }

    @RequestMapping(
            value = {"/rewardwin/Status"},
            method = {RequestMethod.POST}
    )
    public RespEntity updateRewardWinStatus(@RequestParam("rwinid") Long rwinid) throws RedisException, SQLException {
        return this.userRpc.updateRewardWinStatus(rwinid);
    }
}
