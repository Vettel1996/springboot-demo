//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.order;

import com.xyhj.lian.util.RespEntity;
import com.xyhj.lian12.base.config.BaseController;
import com.xyhj.lian12.dragon.interfaces.DragonRpc;
import com.xyhj.lian12.user.dto.UserDto;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/order"})
public class OrderController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    DragonRpc dragonRpc;

    public OrderController() {
    }

    @RequestMapping({"order"})
    public RespEntity order(@RequestParam Long uid, @RequestParam Integer currencyId, @RequestParam Integer baseCurrencyId, @RequestParam Integer type, @RequestParam Integer buyOrSell, @RequestParam BigDecimal price, @RequestParam BigDecimal num, @RequestParam String fdPassword, @RequestParam Integer source) {
        log.info("OrderController.order, uid:{}, currencyId:{}, baseCurrencyId:{}, type:{}, buyOrSell:{}, price:{}, num:{}, fdPassword:{}, source:{}",
                uid, currencyId, baseCurrencyId, type, buyOrSell, price, num, fdPassword, source);
        return this.dragonRpc.order(uid, currencyId, baseCurrencyId, type, buyOrSell, price, num, fdPassword, source);
    }

    /**
     * TODO 记录返回值 未做 mod by
     * @param uid
     * @param currencyId
     * @param fdPassword
     * @param orderNo
     * @param source
     * @return
     */
    @RequestMapping({"cancel"})
    public RespEntity cancelOrder(@RequestParam Long uid, @RequestParam Integer currencyId, @RequestParam String fdPassword, @RequestParam String orderNo, @RequestParam Integer source) {
        log.info("OrderController.cancel, uid:{}, currencyId:{}, fdPassword:{}, orderNo:{}, source:{}",
                uid, currencyId, fdPassword, orderNo, source);
        return this.dragonRpc.cancel(uid, currencyId, fdPassword, orderNo, source);
    }

    @RequestMapping({"personalOrders"})
    public RespEntity personalOrders(HttpServletRequest req, @RequestParam("page") Integer page, @RequestParam("size") Integer size, @RequestParam("status") Integer status) throws Exception {
        UserDto user = this.user(req);
        return this.dragonRpc.personalOrders(user.getUuid(), status, page, size);
    }
}
