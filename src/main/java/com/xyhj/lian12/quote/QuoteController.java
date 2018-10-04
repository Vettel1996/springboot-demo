//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.quote;

import com.google.common.base.Strings;
import com.xyhj.lian.util.RespCode;
import com.xyhj.lian.util.RespEntity;
import com.xyhj.lian12.balance.rpc.BalanceRpc;
import com.xyhj.lian12.base.config.BaseController;
import com.xyhj.lian12.dragon.interfaces.DragonRpc;
import com.xyhj.lian12.quote.interfaces.QuoteExchange;
import com.xyhj.lian12.quote.interfaces.QuoteKline;
import com.xyhj.lian12.quote.interfaces.QuoteProcess;
import com.xyhj.lian12.quote.interfaces.QuoteRpc;
import com.xyhj.lian12.quotespec.QuoteTradeSpecRpc;
import com.xyhj.lian12.user.dto.UserDto;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 行情接口
 */
@RestController
@RequestMapping({"quote"})
public class QuoteController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(QuoteController.class);
    @Autowired
    private DragonRpc dragonRpc;
    @Autowired
    private QuoteRpc quoteRpc;
    @Autowired
    private QuoteProcess quoteProcess;
    @Autowired
    private QuoteKline quoteKline;
    @Autowired
    private QuoteExchange quoteExchange;
    @Autowired
    private QuoteTradeSpecRpc specRpc;
    @Autowired
    private BalanceRpc balanceRpc;

    public QuoteController() {
    }

    @GetMapping({"presentation"})
    public RespEntity getPresentation(@RequestParam("currencyNameEn") String currencyNameEn) {
        try {
            return this.quoteExchange.get(currencyNameEn);
        } catch (Exception var3) {
            return new RespEntity(13601, "未查询到此币种详情");
        }
    }

    /**
     * 成交记录接口
     * @param tradeCurrencyId
     * @param baseCurrencyId
     * @param limit
     * @return
     */
    @GetMapping({"tradeHistory"})
    public RespEntity tradeHistory(@RequestParam("tradeCurrencyId") String tradeCurrencyId, @RequestParam("baseCurrencyId") String baseCurrencyId, @RequestParam(value = "limit",defaultValue = "20") Integer limit) {
        try {
            return this.dragonRpc.tradeHistory(tradeCurrencyId, baseCurrencyId, limit);
        } catch (Exception var5) {
            log.error("行情接口tradeHistory出问题 {}", var5);
            return RespEntity.error(RespCode.SYSTEM_ERROR);
        }
    }

    /**
     * 深度接口
     * @param tradeCurrencyId
     * @param baseCurrencyId
     * @param limit
     * @return
     */
    @GetMapping({"tradeDeepin"})
    public RespEntity tradeDeepin(@RequestParam("tradeCurrencyId") String tradeCurrencyId, @RequestParam("baseCurrencyId") String baseCurrencyId, @RequestParam("limit") Integer limit) {
        try {
            return this.dragonRpc.tradeDeepin(tradeCurrencyId, baseCurrencyId, limit);
        } catch (Exception var5) {
            log.error("行情接口tradeDeepin出问题 {}", var5);
            return RespEntity.error(RespCode.SYSTEM_ERROR);
        }
    }

    @GetMapping({"/v2/tradeDeepin"})
    public RespEntity tradeDeepin(@RequestParam("coins") String coins, @RequestParam("limit") Integer limit) {
        try {
            return this.quoteRpc.tradeDeepin(coins, limit);
        } catch (Exception var4) {
            log.error("行情接口 v2 tradeDeepin出问题 {}", var4);
            return RespEntity.error(RespCode.SYSTEM_ERROR);
        }
    }

    @GetMapping({"/v2/tradeHistory"})
    public RespEntity tradeHistory(@RequestParam("coins") String coins, @RequestParam("limit") Integer limit) {
        try {
            return this.quoteRpc.tradeHistory(coins, limit);
        } catch (Exception var4) {
            log.error("行情接口 v2 tradeHistory出问题 {}", var4);
            return RespEntity.error(RespCode.SYSTEM_ERROR);
        }
    }

    /**
     * 获取实时行情
     * @param tradeCurrencyId
     * @param baseCurrencyId
     * @return
     */
    @GetMapping({"realTime"})
    public RespEntity realTime(@RequestParam(value = "tradeCurrencyId", required = false) String tradeCurrencyId, @RequestParam("baseCurrencyId") String baseCurrencyId) {
        if(StringUtils.isEmpty(tradeCurrencyId)){
            return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
        }
        try {
            return this.dragonRpc.realTime(tradeCurrencyId, baseCurrencyId);
        } catch (Exception var4) {
            log.error("行情接口realTime出问题 {}", var4);
            return RespEntity.error(RespCode.SYSTEM_ERROR);
        }
    }

/*    *//**
     * 获取实时行情
     * @param baseCurrencyId
     * @param tradeCurrencyId
     * @return
     *//*
    @GetMapping({"realTime"})
    public RespEntity realTime(@RequestParam("baseCurrencyId") String baseCurrencyId,
            @RequestParam("tradeCurrencyId") String tradeCurrencyId) {
        try {
            return this.quoteRpc.realTime(baseCurrencyId, tradeCurrencyId);
        } catch (Exception var4) {
            log.error("行情接口realTime出问题 {}", var4);
            return RespEntity.error(RespCode.SYSTEM_ERROR);
        }
    }*/

    @GetMapping({"public"})
    public RespEntity poloniex() {
        return this.quoteRpc.poloniexQuote();
    }

    @GetMapping({"public2"})
    public RespEntity poloniexC() {
        return this.quoteRpc.poloniexC();
    }

    /**
     * 获取实时行情
     * @param coins
     * @return
     */
    @GetMapping({"/v2/realTime"})
    public RespEntity newQuote(@RequestParam("coins") String coins) {
        if (Strings.isNullOrEmpty(coins)) {
            return new RespEntity(13100, "请正确填写参数");
        } else if (!coins.contains("_")) {
            return new RespEntity(13100, "请正确填写参数");
        } else {
            try {
                return this.quoteRpc.quote(coins);
            } catch (Exception var3) {
                log.error("行情接口 v2  realTime出问题 {}", var3);
                return RespEntity.error(RespCode.SYSTEM_ERROR);
            }
        }
    }

    @GetMapping({"/v2/realTime2"})
    public RespEntity newQuote2(@RequestParam("coins") String coins) {
        if (Strings.isNullOrEmpty(coins)) {
            return new RespEntity(13100, "请正确填写参数");
        } else if (!coins.contains("_")) {
            return new RespEntity(13100, "请正确填写参数");
        } else {
            try {
                return this.quoteRpc.quote2(coins);
            } catch (Exception var3) {
                log.error("行情接口 v2  realTime出问题 {}", var3);
                return RespEntity.error(RespCode.SYSTEM_ERROR);
            }
        }
    }

    @GetMapping({"order"})
    public RespEntity order(@RequestParam("orderNo") String orderNo) {
        try {
            return this.dragonRpc.singleOrderQuery(orderNo);
        } catch (Exception var3) {
            log.error("行情接口order出问题 {}", var3);
            return RespEntity.error(RespCode.SYSTEM_ERROR);
        }
    }

    @GetMapping({"noTrades"})
    public RespEntity noTrades(@RequestParam("status") Integer status) {
        try {
            return this.dragonRpc.getNoTrade(status);
        } catch (Exception var3) {
            log.error("行情接口noTrades出问题 {}", var3);
            return RespEntity.error(RespCode.SYSTEM_ERROR);
        }
    }

    @GetMapping({"EntrustByType"})
    public RespEntity EntrustByType(HttpServletRequest request, @RequestParam("type") Integer type, @RequestParam("baseCurrencyId") Integer baseCurrencyId, @RequestParam("tradeCurrencyId") Integer tradeCurrencyId) {
        try {
            String remoteIp = request.getHeader("x-forwarded-for");
            return this.specRpc.entrust(remoteIp, baseCurrencyId, tradeCurrencyId, type);
        } catch (Exception var6) {
            log.error("行情接口EntrustByType出问题 {}", var6);
            return RespEntity.error(RespCode.SYSTEM_ERROR);
        }
    }

    @GetMapping({"allTradeHistory"})
    public RespEntity allTradeHistory(@RequestParam("baseCurrencyId") Integer baseCurrencyId, @RequestParam("tradeCurrencyId") Integer tradeCurrencyId, @RequestParam("size") Integer size, @RequestParam("passCard") String passCard) {
        if (!passCard.equals("3907d9aef154cc4533b41cd56ac04afa")) {
            return RespEntity.error(RespCode.SYSTEM_ERROR);
        } else {
            try {
                return this.balanceRpc.tradeHistory(baseCurrencyId, tradeCurrencyId, size);
            } catch (Exception var6) {
                log.error("行情接口allTradeHistory出问题 {}", var6);
                return RespEntity.error(RespCode.SYSTEM_ERROR);
            }
        }
    }

    @GetMapping({"unTradeOrders"})
    public RespEntity unTradeOrders(@RequestParam("baseCurrencyId") Integer baseCurrencyId, @RequestParam("tradeCurrencyId") Integer tradeCurrencyId, @RequestParam("size") Integer size, @RequestParam("passCard") String passCard, HttpServletRequest request) throws Exception {
        if (!passCard.equals("3907d9aef154cc4533b41cd56ac04afa")) {
            return RespEntity.error(RespCode.SYSTEM_ERROR);
        } else {
            try {
                UserDto user = this.user(request);
                return this.balanceRpc.unTradeOrder(baseCurrencyId, tradeCurrencyId, size, user.getUuid());
            } catch (Exception var7) {
                log.error("行情接口unTradeOrders出问题 {}", var7);
                return RespEntity.error(RespCode.SYSTEM_ERROR);
            }
        }
    }

    @GetMapping({"klineHistory/{symbol}/{type}"})
    public RespEntity klineHistory(@PathVariable("symbol") String symbol,
                                   @PathVariable("type") Integer type,
                                   @RequestParam("prevTradeTime")Long prevTradeTime) {
        try {
            return this.quoteKline.klineHistory(symbol, type, prevTradeTime);
        } catch (Exception var4) {
            return RespEntity.error(RespCode.SYSTEM_ERROR);
        }
    }
}
