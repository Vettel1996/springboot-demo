//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.authority;

import com.google.gson.Gson;
import com.xyhj.lian.util.RespEntity;
import com.xyhj.lian12.balance.rpc.BalanceRpc;
import com.xyhj.lian12.user.dto.UserDto;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorityController {
    private static final Logger log = LoggerFactory.getLogger(AuthorityController.class);
    @Autowired
    BalanceRpc balanceRpc;
    @Autowired
    RedisTemplate redisTemplate;

    public AuthorityController() {
    }

    @RequestMapping({"/authConfig"})
    public Map<String, Object> authConfig(@RequestParam("actionId") Integer actionId, @RequestParam("currencyId") Integer currencyId, Integer rechargeType, HttpServletRequest request) {
        UserDto user = (UserDto)request.getAttribute("user");
        if (rechargeType == null) {
            rechargeType = 0;
        }

        return this.balanceRpc.config(actionId, currencyId, user.getGrade(), rechargeType, user.getUuid());
    }

    @RequestMapping({"/vote/{vote}"})
    public RespEntity vote(@PathVariable String vote, HttpServletRequest request) {
        UserDto user = (UserDto)request.getAttribute("user");
        Gson g = new Gson();
        String key = "vote-uid-map";
        Object oo = this.redisTemplate.opsForValue().get("vote-uid-map");
        Map<String, String> o = new HashMap();
        if (oo != null) {
            System.out.println(oo.toString());
            o = (Map)g.fromJson(oo.toString(), Map.class);
        }

        System.out.println(o);
        if (((Map)o).containsKey(user.getUid() + "")) {
            return new RespEntity(500, "您已经投票给" + (String)((Map)o).get(user.getUid() + "") + "过！");
        } else {
            ((Map)o).put(user.getUid() + "", vote);
            this.redisTemplate.opsForValue().set(key, g.toJson(o), 15L, TimeUnit.DAYS);
            Map<String, Long> collect = (Map)((Map)o).values().stream().collect(Collectors.groupingBy((t) -> {
                return t;
            }, Collectors.counting()));
            return RespEntity.success(collect);
        }
    }
}
