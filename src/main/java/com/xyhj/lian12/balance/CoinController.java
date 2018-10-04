//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.balance;

import com.alibaba.fastjson.JSON;
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
import com.xyhj.lian12.util.HttpGetUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;

import com.xyhj.lian12.util.SliderVerification;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/coin"})
public class CoinController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(CoinController.class);
    @Autowired
    private BalanceRpc balanceRpc;
    @Autowired
    private SmsRpc smsRpc;
    @Autowired
    private UserRpc userRpc;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    public static final int currencyIdCcoin = 22;//ccoin
    public static final int currencyIdUSDT = 63; //USDT
    public static final int pointUSDT_CODE = 4;

    public CoinController() {
    }

    @PostMapping({"tradeCoinsOfBaseCoin"})
    public RespEntity tradeCoinsOfBaseCoin() {
        return this.balanceRpc.tradeCoinsOfBaseCoin(1);
    }

    @PostMapping({"entrustManageCoins"})
    public RespEntity entrustManageCoins() {
        return this.balanceRpc.entrustManageCoins();
    }

    /**
     * 查询用户充币地址
     * @param request
     * @param walletType
     * @param currentyId
     * @return
     * @throws Exception
     */
    @PostMapping({"/selectUserAddress"})
    public RespEntity selectUserAddress(HttpServletRequest request, @RequestParam(value = "walletType",required = false,defaultValue = "1") Integer walletType, @RequestParam("currentyId") int currentyId) throws Exception {
        UserDto user = this.user(request);
        return this.balanceRpc.selectUserAddress(user.getUuid(), walletType, currentyId);
    }

    /**
     * 充币记录查询
     * @param request
     * @param start
     * @param size
     * @param currencyId
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     * @throws Exception
     */
    @PostMapping({"/selectListByUuid"})
    public RespEntity selectListByUuid(HttpServletRequest request, @RequestParam(value = "start",required = false,defaultValue = "1") Integer start, @RequestParam(value = "size",required = false,defaultValue = "10") Integer size, @RequestParam("currencyId") Integer currencyId, @RequestParam("beginTime") String beginTime, @RequestParam("endTime") String endTime, @RequestParam("status") Integer status) throws Exception {
        UserDto userDto = this.user(request);
        if (currencyId == null || currencyId.equals("")) {
            currencyId = 0;
        }

        return this.balanceRpc.selectListByUuid(userDto.getUuid(), start, size, currencyId, beginTime, endTime, status);
    }

    /**
     * 虚拟币资产详情接口
     * @param request
     * @param name
     * @param type
     * @return
     * @throws Exception
     */
    @PostMapping({"/customerCoinAccount"})
    public RespEntity customerCoinAccount(HttpServletRequest request, @RequestParam(value = "name",required = false,defaultValue = "") String name, String type) throws Exception {
        UserDto user = this.user(request);
        return this.balanceRpc.customerCoinAccount(user.getUuid(), type);
    }

    /**
     * 虚拟币资产详情接口otc
     * @param request
     * @param name
     * @param
     * @return
     * @throws Exception
     */
    @PostMapping({"/customerCoinAccount2"})
    public RespEntity customerCoinAccountAll(HttpServletRequest request, @RequestParam(value = "name",required = false,defaultValue = "") String name) throws Exception {
        UserDto user = this.user(request);
        return this.balanceRpc.customerCoinAccountAll(user.getUuid());
    }

    /**
     * 数字货币余额表初始化接口
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping({"/createCustomerCoinAccount"})
    public RespEntity createCustomerCoinAccount(HttpServletRequest request) throws Exception {
        UserDto user = this.user(request);
        return this.balanceRpc.createCustomerCoinAccount(user.getUuid());
    }

    @PostMapping({"/selectTakeCoin"})
    public RespEntity selectTakeCoin(HttpServletRequest request, @RequestParam("currencyId") int currencyId) throws Exception {
        UserDto user = this.user(request);
        Map<String, Object> map = this.balanceRpc.config(4, currencyId, user.getAuthLevel(), 0, user.getUuid());
        RespEntity re = this.balanceRpc.selectTakeCoin(user.getUuid(), currencyId);
        if (re.getAttachment() != null) {
            map.put("resp", re.getAttachment());
        }

        JSONObject json = new JSONObject();
        json.put("detail", map.get("attachment"));
        json.put("resp", re.getAttachment());
        return RespEntity.success(json);
    }

    @RequestMapping({"/authConfig"})
    public Map<String, Object> authConfig(@RequestParam("actionId") Integer actionId, @RequestParam("currencyId") Integer currencyId, HttpServletRequest request) {
        UserDto user = (UserDto)request.getAttribute("user");
        return this.balanceRpc.config(actionId, currencyId, user.getGrade(), 0, user.getUuid());
    }

    /**
     * 发送短信验证码
     * @param request
     * @param vercode
     * @param codeid
     * @return
     * @throws Exception
     */
    @PostMapping({"/sendSms"})
    RespEntity sendSms(HttpServletRequest request, @RequestParam("vercode") String vercode, @RequestParam("codeid") String codeid) throws Exception {
        UserDto user = (UserDto)request.getAttribute("user");
        // 获取redis中图片验证码值
        String imgCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBIMG.getPrefix() + codeid);
        // 检查验证码是否正确
        if (StringUtils.isBlank(imgCode)) {
            return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
        } else if (!vercode.toLowerCase().equals(imgCode)) {
            return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
        } else {
            String code = (int)((Math.random() * 9.0D + 1.0D) * 100000.0D) + "";
            // 资金转出
            this.smsRpc.send(code, user.getPhone(), "05");
            return RespEntity.success((Object)null);
        }
    }

    /**
     * 发送短信验证码
     * 2.0后加入滑动验证
     * @param request
     * @param sliderToken
     * @return
     * @throws Exception
     */
    @PostMapping({"/sendSmsV2"})
    RespEntity sendSmsV2(HttpServletRequest request,
                         @RequestParam(value = "sliderToken") String sliderToken) throws Exception {
        UserDto user = (UserDto)request.getAttribute("user");
        if(!SliderVerification.verifyToken(sliderToken)){
            return new RespEntity(572, "滑动验证失败");
//            return RespEntity.error(RespCode.SLIDER_VERIFY_ERROR);
        } else {
            String code = (int)((Math.random() * 9.0D + 1.0D) * 100000.0D) + "";
            // 资金转出
            this.smsRpc.send(code, user.getPhone(), "05");
            return RespEntity.success((Object)null);
        }
    }

    /**
     * 提币发送邮箱验证码
     * @param request
     * @param vercode
     * @param codeid
     * @param type
     * @return
     * @throws Exception
     */
    @PostMapping({"/emailTakeCoin"})
    RespEntity emailTakeCoin(HttpServletRequest request, @RequestParam("vercode") String vercode, @RequestParam("codeid") String codeid, @RequestParam(value = "type",required = false,defaultValue = "1") Integer type) throws Exception {
        UserDto user = this.user(request);
        // 获取redis中图片验证码值
        String imgCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBIMG.getPrefix() + codeid);
        // 检查验证码是否正确
        if (StringUtils.isBlank(imgCode)) {
            return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
        } else if (!vercode.toLowerCase().equals(imgCode)) {
            return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
        } else {
            String phoneOrEmail;
            if (type == 2) {
                //if (StringUtils.isBlank(user.getPhone()) && user.getIsValidatePhone() == 1) {
                // fix bug:isValidatePhone为0时表示手机没有验证通过
                if (StringUtils.isBlank(user.getPhone()) && user.getIsValidatePhone() == 0) {
                    return new RespEntity(10022, "请先绑定手机号");
                }

                phoneOrEmail = user.getPhone();
            } else {
                //if (StringUtils.isBlank(user.getEmail()) && user.getIsValidateEmail() == 1) {
                // fix bug:isValidateEmail为0时表示邮箱没有验证通过
                if (StringUtils.isBlank(user.getEmail()) && user.getIsValidateEmail() == 0) {
                    return new RespEntity(10022, "请先绑定邮箱");
                }

                phoneOrEmail = user.getEmail();
            }

            log.info("提币发送邮箱：" + phoneOrEmail);
            return this.userRpc.sendEmailWithdraw(phoneOrEmail);
        }
    }

    /**
     * 提币发送邮箱验证码
     * 2.0后加入滑动验证
     * @param request
     * @param type
     * @return
     * @throws Exception
     */
    @PostMapping({"/emailTakeCoinV2"})
    RespEntity emailTakeCoinV2(HttpServletRequest request,
                               @RequestParam(value = "type",required = false,defaultValue = "1") Integer type) throws Exception {

        UserDto user = this.user(request);
        String phoneOrEmail;
        if (type == 2) {
            //if (StringUtils.isBlank(user.getPhone()) && user.getIsValidatePhone() == 1) {
            // fix bug:isValidatePhone为0时表示手机没有验证通过
            if (StringUtils.isBlank(user.getPhone()) && user.getIsValidatePhone() == 0) {
                return new RespEntity(10022, "请先绑定手机号");
            }

            phoneOrEmail = user.getPhone();
        } else {
            //if (StringUtils.isBlank(user.getEmail()) && user.getIsValidateEmail() == 1) {
            // fix bug:isValidateEmail为0时表示邮箱没有验证通过
            if (StringUtils.isBlank(user.getEmail()) && user.getIsValidateEmail() == 0) {
                return new RespEntity(10022, "请先绑定邮箱");
            }

            phoneOrEmail = user.getEmail();
        }

        log.info("提币发送邮箱：" + phoneOrEmail);
        return this.userRpc.sendEmailWithdraw(phoneOrEmail);
    }

    /**
     * 数字货币提币接口
     * @param request
     * @param currencyId
     * @param address
     * @param amount
     * @param fdPwd
     * @param actionId
     * @param vercode
     * @param codeid
     * @param note
     * @param emailCode
     * @param msgCode
     * @param gAuth
     * @return
     * @throws Exception
     */
    @PostMapping({"/takeCoin"})
    RespEntity takeCoin(HttpServletRequest request, @RequestParam("currencyId") int currencyId, @RequestParam("address") String address, @RequestParam("amount") double amount, @RequestParam("fdPwd") String fdPwd, @RequestParam("actionId") Integer actionId, @RequestParam("vercode") String vercode, @RequestParam("codeid") String codeid, @RequestParam(value = "note",required = false,defaultValue = " ") String note, @RequestParam(value = "emailCode",required = false,defaultValue = "none") String emailCode, @RequestParam(value = "msgCode",required = false,defaultValue = "none") String msgCode, @RequestParam(value = "gAuth",required = false,defaultValue = "1") Integer gAuth) throws Exception {
        UserDto user = this.user(request);
        String imgCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBIMG.getPrefix() + codeid);
        if (StringUtils.isBlank(imgCode)) {
            return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
        } else if (!vercode.toLowerCase().equals(imgCode)) {
            return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
        } else {
            this.redisTemplate.delete(RedisPrefix.DBIMG.getPrefix() + codeid);
            if (!gAuth.equals("1") && this.userRpc.isUsedGoogleAuthIn(user.getUuid())) {
                Boolean boo = this.userRpc.verifyClientCode(user.getUuid(), gAuth);
                if (!boo) {
                    return new RespEntity(10022, "谷歌认证失败");
                }
            }

            /**
             * 原添加提币地址接口
             */
            if (!fdPwd.equalsIgnoreCase(user.getFdPassword())) {
                return RespEntity.error(RespCode.USER_FDPASSWORD_NOTMATCH);
            } else {
                RespEntity inAddressResponse = this.balanceRpc.insertTakeAddress(user.getUuid(), currencyId, address, note);
                if (inAddressResponse.getStatus() != 200) {
                    return inAddressResponse;
                } else {
                    boolean risk = (Boolean)request.getAttribute("risk");
                    double fee = (Double)request.getAttribute("fee");
                    log.info("fee" + fee);
                    BigDecimal amountBig = new BigDecimal(String.valueOf(amount));
                    // 判断交易密码是否正确
                    if (!fdPwd.equalsIgnoreCase(user.getFdPassword())) {
                        return RespEntity.error(RespCode.USER_FDPASSWORD_NOTMATCH);
                    } else {
                        // 判断验证码
                        String authCodeEmail = (String)this.redisTemplate.opsForValue().get(RedisPrefix.SMSwithdraw.getPrefix() + user.getEmail());
                        String authCodePhone = (String)this.redisTemplate.opsForValue().get(RedisPrefix.SMSwithdraw.getPrefix() + user.getPhone());
                        String authCode = authCodeEmail + authCodePhone;
                        log.info("提币验证码：" + authCodeEmail + " or " + authCodePhone);
                        if (authCode.equals("nullnull")) {
                            return RespEntity.error(RespCode.USER_AUTHCODE_EXPIRED);
                        } else {
                            this.redisTemplate.delete(RedisPrefix.SMSwithdraw.getPrefix() + user.getPhone());
                            this.redisTemplate.delete(RedisPrefix.SMSwithdraw.getPrefix() + user.getEmail());
                            // 校验验证码是否有效
                            // 检查验证码是否正确
                            String key = user.getUid() + "emailTakeCoin";
                            String times = (String)this.redisTemplate.opsForValue().get(key);
                            if (times == null) {
                                times = "0";
                            }

                            log.info(key + "，输错验证码次数：" + times);
                            if (authCode.indexOf(emailCode) >= 0 && Integer.valueOf(times) <= 5) {
                                RespEntity respEntity = this.balanceRpc.takeCoin(user.getUuid(), currencyId, address, amountBig, new BigDecimal(String.valueOf(fee)), risk, msgCode);
                                log.info("respEntity:" + respEntity);
                                if (respEntity.getStatus() == 200 && risk) {
                                    request.setAttribute("ref_id", respEntity.getAttachment().toString());
                                    log.info("ref_id:" + request.getAttribute("ref_id").toString());
                                }

                                return respEntity.getStatus() != 200 ? respEntity : new RespEntity(200, "发起提币成功");
                            } else {
                                this.redisTemplate.opsForValue().set(key, Integer.valueOf(times) + 1 + "", 5L, TimeUnit.MINUTES);
                                return RespEntity.error(RespCode.USER_AUTHCODE_ERROR);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 数字货币提币接口
     * 2.0后加入滑动验证
     * @param request
     * @param currencyId
     * @param address
     * @param amount
     * @param fdPwd
     * @param actionId
     * @param note
     * @param emailCode
     * @param msgCode
     * @param gAuth
     * @return
     * @throws Exception
     */
    @PostMapping({"/takeCoinV2"})
    RespEntity takeCoinV2(HttpServletRequest request,
                          @RequestParam("currencyId") int currencyId,
                          @RequestParam("address") String address,
                          @RequestParam("amount") double amount,
                          @RequestParam("fdPwd") String fdPwd,
                          @RequestParam("actionId") Integer actionId,
                          @RequestParam(value = "note",required = false,defaultValue = " ") String note,
                          @RequestParam(value = "emailCode",required = false,defaultValue = "none") String emailCode,
                          @RequestParam(value = "msgCode",required = false,defaultValue = "none") String msgCode,
                          @RequestParam(value = "gAuth",required = false,defaultValue = "1") Integer gAuth) throws Exception {
        UserDto user = this.user(request);
        if (!gAuth.equals("1") && this.userRpc.isUsedGoogleAuthIn(user.getUuid())) {
            Boolean boo = this.userRpc.verifyClientCode(user.getUuid(), gAuth);
            if (!boo) {
                return new RespEntity(10022, "谷歌认证失败");
            }
        }

        /**
         * 原添加提币地址接口
         */
        if (!fdPwd.equalsIgnoreCase(user.getFdPassword())) {
            return RespEntity.error(RespCode.USER_FDPASSWORD_NOTMATCH);
        } else {
            RespEntity inAddressResponse = this.balanceRpc.insertTakeAddress(user.getUuid(), currencyId, address, note);
            if (inAddressResponse.getStatus() != 200) {
                return inAddressResponse;
            } else {
                boolean risk = (Boolean)request.getAttribute("risk");
                double fee = (Double)request.getAttribute("fee");
                log.info("fee" + fee);
                BigDecimal amountBig = new BigDecimal(String.valueOf(amount));
                // 判断交易密码是否正确
                if (!fdPwd.equalsIgnoreCase(user.getFdPassword())) {
                    return RespEntity.error(RespCode.USER_FDPASSWORD_NOTMATCH);
                } else {
                    // 判断验证码
                    String authCodeEmail = (String)this.redisTemplate.opsForValue().get(RedisPrefix.SMSwithdraw.getPrefix() + user.getEmail());
                    String authCodePhone = (String)this.redisTemplate.opsForValue().get(RedisPrefix.SMSwithdraw.getPrefix() + user.getPhone());
                    String authCode = authCodeEmail + authCodePhone;
                    log.info("提币验证码：" + authCodeEmail + " or " + authCodePhone);
                    if (authCode.equals("nullnull")) {
                        return RespEntity.error(RespCode.USER_AUTHCODE_EXPIRED);
                    } else {
                        this.redisTemplate.delete(RedisPrefix.SMSwithdraw.getPrefix() + user.getPhone());
                        this.redisTemplate.delete(RedisPrefix.SMSwithdraw.getPrefix() + user.getEmail());
                        // 校验验证码是否有效
                        // 检查验证码是否正确
                        String key = user.getUid() + "emailTakeCoin";
                        String times = (String)this.redisTemplate.opsForValue().get(key);
                        if (times == null) {
                            times = "0";
                        }

                        log.info(key + "，输错验证码次数：" + times);
                        if (authCode.indexOf(emailCode) >= 0 && Integer.valueOf(times) <= 5) {
                            RespEntity respEntity = this.balanceRpc.takeCoin(user.getUuid(), currencyId, address, amountBig, new BigDecimal(String.valueOf(fee)), risk, msgCode);
                            log.info("respEntity:" + respEntity);
                            if (respEntity.getStatus() == 200 && risk) {
                                request.setAttribute("ref_id", respEntity.getAttachment().toString());
                                log.info("ref_id:" + request.getAttribute("ref_id").toString());
                            }

                            return respEntity.getStatus() != 200 ? respEntity : new RespEntity(200, "发起提币成功");
                        } else {
                            this.redisTemplate.opsForValue().set(key, Integer.valueOf(times) + 1 + "", 5L, TimeUnit.MINUTES);
                            return RespEntity.error(RespCode.USER_AUTHCODE_ERROR);
                        }
                    }
                }
            }
        }
    }

    /**
     * 提币记录列表
     * @param request
     * @param start
     * @param size
     * @param status
     * @param beginTime
     * @param endTime
     * @param currentyId
     * @return
     * @throws Exception
     */
    @PostMapping({"/selectTakeList"})
    public RespEntity selectTakeList(HttpServletRequest request, @RequestParam("start") int start, @RequestParam("size") int size, @RequestParam("status") int status, @RequestParam("beginTime") String beginTime, @RequestParam("endTime") String endTime, @RequestParam("currentyId") int currentyId) throws Exception {
        UserDto user = this.user(request);
        return this.balanceRpc.selectTakeList(user.getUuid(), start, size, status, beginTime, endTime, currentyId);
    }

    @PostMapping({"/insertTakeAddress"})
    public RespEntity insertTakeAddress(HttpServletRequest request, @RequestParam("currencyId") Integer currencyId, @RequestParam("address") String address, @RequestParam("fdPwd") String fdPwd, @RequestParam("note") String note) throws Exception {
        UserDto user = this.user(request);
        // 判断交易密码是否正确
        return !fdPwd.equalsIgnoreCase(user.getFdPassword()) ? RespEntity.error(RespCode.USER_FDPASSWORD_NOTMATCH) : this.balanceRpc.insertTakeAddress(user.getUuid(), currencyId, address, note);
    }

    /**
     * 账户明细
     * @param request
     * @param start
     * @param size
     * @param currencyId
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     * @throws Exception
     */
    @PostMapping({"/allMoneyList"})
    public RespEntity allMoneyList(HttpServletRequest request, @RequestParam(value = "start",required = false,defaultValue = "1") Integer start, @RequestParam(value = "size",required = false,defaultValue = "10") Integer size, @RequestParam("currencyId") Integer currencyId, @RequestParam("beginTime") String beginTime, @RequestParam("endTime") String endTime, @RequestParam("status") Integer status) throws Exception {
        UserDto user = this.user(request);
        return this.balanceRpc.allMoneyList(user.getUuid(), start, size, currencyId, beginTime, endTime, status);
    }

    /**
     * 数字货币提币地址删除接口
     * @param request
     * @param currencyId
     * @param walletAddressId
     * @return
     * @throws Exception
     */
    @RequestMapping(
            value = {"/updateCoinAddress"},
            method = {RequestMethod.POST}
    )
    RespEntity updateCoinAddress(HttpServletRequest request, @RequestParam("currencyId") int currencyId, @RequestParam("walletAddressId") Long walletAddressId) throws Exception {
        UserDto user = this.user(request);
        return this.balanceRpc.updateCoinAddress(user.getUuid(), currencyId, walletAddressId);
    }

    @PostMapping({"coins"})
    public RespEntity coinList() {
        return this.balanceRpc.currencyList();
    }

    /**
     * ico充币
     * @param body
     * @return
     */
    @RequestMapping(
            value = {"/icoinRecharge"},
            method = {RequestMethod.POST}
    )
    RespEntity icoinRecharge(@RequestBody String body) {
        log.info("icogogo充币数据：" + body);
        JSONObject json = JSON.parseObject(body);
        BigDecimal amount = json.getBigDecimal("amount");
        String addressCode = json.getString("addressCode");
        String name = json.getString("name");
        String idNumber = json.getString("idNumber");
        Long uid = BaseConstant.getUidByAddressCode(addressCode.trim());
        if (uid == 0L) {
            return new RespEntity(1018, "提币码错误！");
        } else {
            RespEntity respEntity = this.userRpc.selectByUid(uid);
            if (respEntity != null && respEntity.getStatus() == 200 && respEntity.getAttachment() != null) {
                Object attachment = respEntity.getAttachment();
                String jsonString = JSON.toJSONString(attachment);
                System.out.println(respEntity);
                UserDto user = (UserDto)JSON.parseObject(jsonString, UserDto.class);
                return this.balanceRpc.icoinIcoRecharge(amount, user.getUuid(), addressCode.trim(), name, idNumber);
            } else {
                return new RespEntity(1018, "提币码错误！");
            }
        }
    }

    /**
     * ico充币码
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(
            value = {"/getAddressCode"},
            method = {RequestMethod.POST}
    )
    RespEntity getAddressCode(HttpServletRequest request) throws Exception {
        UserDto user = this.user(request);
        return this.balanceRpc.getAddressCode(user.getUid().toString(), user.getUuid());
    }

    @PostMapping({"rateDesc"})
    public RespEntity rateDesc() {
        return this.balanceRpc.rateDesc();
    }

    @RequestMapping({"allCurrencyRelations"})
    public RespEntity allCurrencyRelations() {
        return this.balanceRpc.allCurrencyRelations();
    }

    @RequestMapping(
            value = {"/exchangeCoin"},
            method = {RequestMethod.POST}
    )
    RespEntity exchangeCoin(HttpServletRequest request, @RequestParam("fdPwd") String fdPwd, @RequestParam("currencyId") int currencyId, @RequestParam("amount") String amount) throws Exception {
        UserDto user = this.user(request);
        return !fdPwd.equalsIgnoreCase(user.getFdPassword()) ? RespEntity.error(RespCode.USER_FDPASSWORD_NOTMATCH) : this.balanceRpc.exchangeCoin(user.getUuid(), currencyId, amount);
    }
    
    /**
    	usdt  兑换    code 
    	确认信息
     */
    @RequestMapping( value = {"/confirmMessage"},method = {RequestMethod.POST})
    RespEntity confirmMessage(HttpServletRequest request,
    		@RequestParam("fdPwd") String fdPwd, 
    		@RequestParam("currencyId") int currencyId,
    		@RequestParam(value="amount",defaultValue="") String amount) throws Exception {
        UserDto user = this.user(request);
        log.info("fdPwd="+fdPwd);
        log.info("currencyId="+currencyId);
        log.info("amount="+amount);
        
        
        if(currencyId==63) {
        	return new RespEntity(121,"功能暂未开放");
        }
        
        if(StringUtils.isBlank(amount)) {
        	return new RespEntity(122,"兑换数量不可为空");
        }
        
        if(new BigDecimal(amount).scale()>8) {
        	return new RespEntity(123,"最多允许兑换8位小数");
        }
        
        if(!fdPwd.equalsIgnoreCase(user.getFdPassword())) {
        	return RespEntity.error(RespCode.USER_FDPASSWORD_NOTMATCH);
        }
        
        String flag = this.redisTemplate.opsForValue().get("confirmMessage"+user.getUuid()+currencyId);
        if(flag==null) {
        	this.redisTemplate.opsForValue().set("confirmMessage"+user.getUuid()+currencyId,"true", 5, TimeUnit.SECONDS);
        	RespEntity respEntity = this.balanceRpc.showExchangeCoinUSDTToCode(user.getUuid(), currencyId, amount);
            log.info("@@respEntity="+respEntity);
            

            JSONObject json = null;
            if(respEntity.getStatus()==200) {
            	
            	String attachment = (String)respEntity.getAttachment();
            	log.info("返回的确认信息attachment="+attachment);
            	
            	json=new JSONObject().parseObject(attachment);
            	
            	this.redisTemplate.opsForValue().set(currencyId + user.getUuid(),attachment, 60 * 5, TimeUnit.SECONDS);
            	//String test = this.redisTemplate.opsForValue().get(currencyId + user.getUuid());
            	//log.info("##test="+test);
            	return RespEntity.success(json);
            }else {
            	return respEntity;
            }
            
        }else {
        	return new RespEntity(610,"5秒只允许操作一次");
        }
        
        //return !fdPwd.equalsIgnoreCase(user.getFdPassword()) ? RespEntity.error(RespCode.USER_FDPASSWORD_NOTMATCH) : this.balanceRpc.exchangeCoinUSDTToCode(user.getUuid(), currencyId, amount);
    }
    
    /**
		usdt  兑换    code 
		确认兑换接口
     */
    @RequestMapping(value = {"/exchangeCoinUSDTToCode"},method = {RequestMethod.POST})
    RespEntity exchangeCoinUSDTToCode(HttpServletRequest request, @RequestParam("fdPwd") String fdPwd, @RequestParam("currencyId") int currencyId, @RequestParam("amount") String amount) throws Exception {
        UserDto user = this.user(request);
        //TODO
        String message = this.redisTemplate.opsForValue().get(currencyId + user.getUuid());
        if(message==null) {
        	return new RespEntity(123,"操作已过期，请重新兑换");
        }
        if(!fdPwd.equalsIgnoreCase(user.getFdPassword())) {
        	return RespEntity.error(RespCode.USER_FDPASSWORD_NOTMATCH);
        } 
        
        
       
        String flag = this.redisTemplate.opsForValue().get("exchangeCoinUSDTToCode"+user.getUuid()+currencyId);
        if(flag==null) {
        	this.redisTemplate.opsForValue().set("exchangeCoinUSDTToCode"+user.getUuid()+currencyId,"true", 5, TimeUnit.SECONDS);
        	 RespEntity respEntity=this.balanceRpc.exchangeCoinUSDTToCode(message,user.getUuid(),currencyId,amount);
        	 if(respEntity.getStatus()==200) {
        		 this.redisTemplate.delete(currencyId + user.getUuid());
        	 }
        	 return respEntity;
        }else {
        	return new RespEntity(610,"5秒只允许操作一次");
        }
    }
    
    
    /**
     * usdt兑换code
     * code兑换usdt
     * 前端显示汇率接口
     * @Title: message   
     * @return
     */
    @RequestMapping(value = {"/message"},method = {RequestMethod.POST})
    public RespEntity message() {

    	//otc的实时价格
    	String host="http://open.otc360.top/v1/third/query_price?priceCoin=USDT";
        String string = HttpGetUtil.post(host, new HashMap());
        JSONObject json =(JSONObject) new JSONObject().parse(string);
        JSONObject jsonDate =(JSONObject) json.get("data");
        JSONObject jsonPrice =(JSONObject) jsonDate.get("price");
    	String priceBuy =(String) jsonPrice.get("buyPrice");
    	String priceSell =(String) jsonPrice.get("sellPrice");
    	
    	BigDecimal bdPriceSell = new BigDecimal(priceSell);
    	BigDecimal bdPriceBuy = new BigDecimal(priceBuy);
    	BigDecimal fee = new BigDecimal("0.03");
    	
    	JSONObject jsonMessage = new JSONObject();
    	//买价
    	jsonMessage.put("buyPrice", bdPriceBuy);
    	//卖价
    	jsonMessage.put("sellPrice", bdPriceSell.subtract(fee));
        return RespEntity.success(jsonMessage);
    }
}
