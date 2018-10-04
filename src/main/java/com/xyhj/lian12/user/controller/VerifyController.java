//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.user.controller;

import com.alibaba.fastjson.JSON;
import com.xyhj.lian.exception.RedisException;
import com.xyhj.lian.util.RedisPrefix;
import com.xyhj.lian.util.RespCode;
import com.xyhj.lian.util.RespEntity;
import com.xyhj.lian12.base.config.BaseController;
import com.xyhj.lian12.user.dto.UserDto;
import com.xyhj.lian12.user.interfaces.UserRpc;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/user"})
public class VerifyController extends BaseController {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    public UserRpc userRpc;

    public VerifyController() {
    }

    @GetMapping({"verifyTokenAndUid"})
    public RespEntity verifyTokenAndUid(@RequestParam("uid") Long uid, @RequestParam("token") String token) throws SQLException, RedisException, Exception {
        if (StringUtils.isBlank(token)) {
            return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
        } else {
            RespEntity respEntity = this.userRpc.selectByUid(uid);
            if (respEntity.getAttachment() == null) {
                return RespEntity.error(RespCode.USER_NOTEXIST);
            } else {
                UserDto userByUid = (UserDto)respEntity.getAttachment();
                String tokenInDb = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBToken.getPrefix() + uid);
                return tokenInDb != null && token.equals(tokenInDb) && userByUid != null ? RespEntity.success((Object)null) : RespEntity.error(RespCode.USER_TOKEN_ERROR);
            }
        }
    }

    /**
     * 验证短信吗 判断验证码是否正确
     * @param uid
     * @param smsCode
     * @return
     * @throws Exception
     */
    @PostMapping({"/verifySmsCode"})
    public RespEntity verifySmsCode(@RequestParam("uid") Long uid, @RequestParam("smsCode") String smsCode) throws Exception {
        RespEntity respEntity = this.userRpc.selectByUid(uid);
        if (respEntity != null && respEntity.getStatus() == 200) {
            Object attachment = respEntity.getAttachment();
            String jsonString = JSON.toJSONString(attachment);
            UserDto user = (UserDto)JSON.parseObject(jsonString, UserDto.class);
            String smsInDb = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBSMS.getPrefix() + user.getPhone());
            if (StringUtils.isBlank(smsInDb)) {
                // 验证码过期
                return RespEntity.error(RespCode.USER_AUTHCODE_ERROR);
            } else {
                return !smsCode.equals(smsInDb) ? RespEntity.error(RespCode.USER_AUTHCODE_ERROR) : RespEntity.success((Object)null);
            }
        } else {
            return RespEntity.error(RespCode.USER_AUTHCODE_ERROR);
        }
    }

    /**
     * 验证邮箱随机码
     * @param uid
     * @param mailCode
     * @return
     * @throws Exception
     */
    @PostMapping({"/verifyMailCodePwd"})
    public RespEntity verifyMailCodePwd(@RequestParam("uid") Long uid, @RequestParam("mailCode") String mailCode) throws Exception {
        String mailCodeInDb = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBMAILPWD.getPrefix() + uid);
        if (StringUtils.isBlank(mailCodeInDb)) {
            // 验证码过期
            return RespEntity.error(RespCode.USER_AUTHCODE_ERROR);
        } else {
            return !mailCodeInDb.equals(mailCode) ? RespEntity.error(RespCode.USER_AUTHCODE_ERROR) : RespEntity.success((Object)null);
        }
    }

    @PostMapping({"/verifyBeforeRegister"})
    public RespEntity verifyBeforeRegister(@RequestParam("uname") String uname) throws SQLException {
        return StringUtils.isBlank(uname) ? RespEntity.error(RespCode.COMMON_PARAM_BLANK) : this.userRpc.verifyBeforeRegister(uname);
    }

    /**
     * 更新手机号:
     * 2/验证重置手机的验证码
     * @param request
     * @param code
     * @param newPhone
     * @param oldPhone
     * @return
     * @throws Exception
     */
    @PostMapping({"/verifyPhoneCode"})
    public RespEntity verifyPhoneCode(HttpServletRequest request, @RequestParam("code") String code, @RequestParam("newPhone") String newPhone, @RequestParam("oldPhone") String oldPhone) throws Exception {
        if (!StringUtils.isBlank(code) && !StringUtils.isBlank(newPhone) && !StringUtils.isBlank(oldPhone)) {
            // 验证验证码
            String codeInDb = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBSMS.getPrefix() + newPhone);
            if (null != codeInDb && codeInDb.equals(code)) {
                UserDto user = this.user(request);
                return !user.getPhone().equals(oldPhone) ? RespEntity.error(RespCode.USER_PHONE_INCORRECT) : this.userRpc.verifyPhoneCode(oldPhone, newPhone);
            } else {
                return RespEntity.error(RespCode.USER_CODE_INCORRECT);
            }
        } else {
            return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
        }
    }

    /**
     * 个人中心-重置手机-发送验证码接口
     * 1/获取手机的验证码
     * @param request
     * @param oldPhone
     * @param newPhone
     * @param vercode
     * @param codeid
     * @return
     * @throws Exception
     */
    @PostMapping({"/verifyPhoneSendMsg"})
    public RespEntity verifyPhoneSendMsg(HttpServletRequest request, @RequestParam("oldPhone") String oldPhone, @RequestParam("newPhone") String newPhone, @RequestParam("vercode") String vercode, @RequestParam("codeid") String codeid) throws Exception {
        UserDto user = this.user(request);
        if (!StringUtils.isBlank(oldPhone) && !StringUtils.isBlank(newPhone) && !StringUtils.isBlank(vercode) && !StringUtils.isBlank(codeid)) {
            // 检查验证码是否正确
            String imgCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBIMG.getPrefix() + codeid);
            if (StringUtils.isBlank(imgCode)) {
                return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
            } else if (!vercode.toLowerCase().equals(imgCode)) {
                return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
            } else if (!user.getPhone().equals(oldPhone)) {
                return RespEntity.error(RespCode.USER_PHONE_INCORRECT);
            } else {
                RespEntity respEntity = this.userRpc.queryPhone(newPhone);
                return respEntity.getStatus() != 200 ? RespEntity.error(RespCode.USER_PHONE_EXIST) : this.userRpc.verifyPhoneSendMsg(newPhone);
            }
        } else {
            return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
        }
    }

    /**
     * 验证邮箱 绑定
     * @param request
     * @param code
     * @return
     * @throws Exception
     */
    @PostMapping({"/verifyMailCode"})
    public RespEntity verifyEmailCode(HttpServletRequest request, @RequestParam("code") Integer code) throws Exception {
        if (code == null) {
            return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
        } else {
            UserDto user = this.user(request);
            return user == null ? RespEntity.error(RespCode.USER_NOT_LOGIN) : this.userRpc.verifyMailCode(user.getUid(), code);
        }
    }
}
