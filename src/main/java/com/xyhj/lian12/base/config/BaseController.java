//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.base.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xyhj.lian.util.RedisPrefix;
import com.xyhj.lian.util.RespEntity;
import com.xyhj.lian12.user.dto.UserDto;
import com.xyhj.lian12.user.interfaces.UserRpc;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RestController;

/**
 * 从缓存拿用户信息
 */
@RestController
public class BaseController {
    private static final Logger log = LoggerFactory.getLogger(BaseController.class);
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private UserRpc userRpc;

    public BaseController() {
    }

    public UserDto user(String uid) throws Exception {
        if (uid == null || "".equals(uid)) {
            throw new Exception("缺少必要的参数uid");
        }

        UserDto user = (UserDto)JSONObject.parseObject((String)this.redisTemplate.opsForValue().get(RedisPrefix.DBUser.getPrefix() + uid), UserDto.class);

        if (user == null) {
            RespEntity respEntity = this.userRpc.selectByUid(Long.valueOf(uid));
            if (respEntity != null && respEntity.getStatus() == 200) {
                Object attachment = respEntity.getAttachment();
                String jsonString = JSON.toJSONString(attachment);
                user = (UserDto)JSON.parseObject(jsonString, UserDto.class);
            }

            this.userRpc.cacheUser(user);
        } else {
            return user;
        }

        return user;
    }

    public UserDto user(HttpServletRequest request) throws Exception {
        String uid = request.getParameter("uid");
        return user(uid);
    }
}
