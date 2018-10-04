//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.balance;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xyhj.lian.util.RedisPrefix;
import com.xyhj.lian.util.RespCode;
import com.xyhj.lian.util.RespEntity;
import com.xyhj.lian12.balance.rpc.BalanceRpc;
import com.xyhj.lian12.base.config.BaseController;
import com.xyhj.lian12.sms.interfaces.SmsRpc;
import com.xyhj.lian12.user.dto.UserDto;
import com.xyhj.lian12.user.interfaces.UserRpc;
import com.xyhj.lian12.util.BaseConstant;
import com.xyhj.lian12.util.SliderVerification;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping({"/otc"})
public class RemitController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(RemitController.class);
    @Autowired
    private BalanceRpc balanceRpc;
    @Autowired
    private UserRpc userRpc;
    @Autowired
    private SmsRpc smsRpc;

    public RemitController() {
    }


    /**
     * 划账记录查询
     * @param
     * @param start
     * @param size
     * @param
     * @param beginTime
     * @param endTime
     * @param
     * @return
     * @throws Exception
     */
    @PostMapping({"/list"})
    public RespEntity selectListByUuid(HttpServletRequest request,
                                       @RequestParam(value = "start",required = false,defaultValue = "1") Integer start,
                                       @RequestParam(value = "size",required = false,defaultValue = "10") Integer size,
                                       @RequestParam(value = "beginTime", required = false) String beginTime,
                                       @RequestParam(value = "endTime", required = false) String endTime,
                                       @RequestParam (value = "coinSign" ,required = false , defaultValue = "USDT") String coinSign,
                                       @RequestParam(value = "status",required = false, defaultValue = "0") Integer status) throws Exception {
        UserDto user = this.user(request);
        coinSign= coinSign.toUpperCase();
        status = (status.intValue() == 0 ? null : status);
        return this.balanceRpc.list(user.getUid(), start, size, status, beginTime, endTime, coinSign);
    }

    /**
     * 划账币种查询
     * @param
     * @param
     * @param
     * @param
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    @GetMapping({"/list/param"})
    public RespEntity selectParamList(){


        JSONObject jsonObject = new JSONObject();
        //目前otc只支持这四种
        List<String> list = Arrays.asList("BTC","BCH","ETH","USDT");
        jsonObject.put("total", 4);
        jsonObject.put("list", list);

        return RespEntity.success(jsonObject);
        //return this.balanceRpc.ctRemitList(uid, start, size, beginTime, endTime);
    }


    /**
     * 划账记录查询
     * @param
     * @param
     * @param
     * @param
     * @param \
     * @param
     * @param
     * @return
     * @throws Exception
     */
    @PostMapping({"/third/otc/transfer_coin"})
    public RespEntity remit(HttpServletRequest request,
                            // @RequestParam("uuid") String uuid,
                            //@RequestParam("currencyId") int currencyId,
                            @RequestParam("amount") BigDecimal amount,
                            @RequestParam("kyc") String kyc,
                            @RequestParam("coinSign") String coinSign,
                            @RequestParam("phone") String phone) throws Exception {

        return new RespEntity(1018,"CHAOEX钱包维护");
//
//        UserDto user = this.user(request);
//        String email = (user.getEmail() == null ? "" : user.getEmail());
//
//        RespEntity entity = this.balanceRpc.remit(user.getUid(), user.getUuid(), 1, amount, kyc, coinSign, phone, email);
//        if(entity.status == 500) {
//            try{
//            smsRpc.sendMail("type", "otc", "support-cn@chaoex.com.hk", "划账失败, uid:" + user.getUid() + ", 金额:" + amount);
//            } catch (Exception e){
//                log.error("otc send email faile", e);
//            }
//        }
//        return entity;
    }


    /**
     * 划账记录查询
     * @param
     * @param
     * @param
     * @param
     * @param \
     * @param
     * @param
     * @return
     * @throws Exception
     */
    @PostMapping({"/transfer_coin"})
    public Object remitToOwn(@RequestParam("uid") Long uid,
                                 @RequestParam("otc_transfer_id") String otc_transfer_id,
                                 @RequestParam("coin_amount") BigDecimal coin_amount,
                                 @RequestParam("mer_transfer_inout") String mer_transfer_inout,
                                 @RequestParam("coin_sign") String coin_sign,
                                 @RequestParam("otc_transfer_time") String otc_transfer_time,
                                 @RequestParam("sign") String sign) throws Exception {

        //UserDto user = this.user(request);
        RespEntity respEntity = this.userRpc.selectByUid(uid);
        //if (respEntity != null && respEntity.getStatus() == 200 && respEntity.getAttachment() != null) {
            Object attachment = respEntity.getAttachment();
            String jsonString = JSON.toJSONString(attachment);
            System.out.println(respEntity);
            UserDto user = (UserDto)JSON.parseObject(jsonString, UserDto.class);
        return this.balanceRpc.remitToOwn(uid, user.getUuid(), otc_transfer_id, coin_amount, mer_transfer_inout, coin_sign, otc_transfer_time,sign);
//        JSONObject all = new JSONObject();
//        all.put("success",false);
//        all.put("code", 900710);
//        all.put("msg","fail");
//        return all;
    }

}
