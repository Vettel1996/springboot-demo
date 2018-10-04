//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.user.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xyhj.lian.util.MD5Util;
import com.xyhj.lian.util.RedisPrefix;
import com.xyhj.lian.util.RespCode;
import com.xyhj.lian.util.RespEntity;
import com.xyhj.lian12.base.config.BaseController;
import com.xyhj.lian12.user.dto.AbroadUserAuth;
import com.xyhj.lian12.user.dto.UserDto;
import com.xyhj.lian12.user.interfaces.UserRpc;
import com.xyhj.lian12.util.BaseConstant;
import com.xyhj.lian12.util.DateUtil;
import com.xyhj.lian12.util.NetworkUtil;
import com.xyhj.lian12.util.SignValidUtil;
import com.xyhj.lian12.util.SliderVerification;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequestMapping({"/user"})
public class AbroadUserController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(AbroadUserController.class);
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private UserRpc userRpc;
    @Value("${upload.image.path}")
    private String uploadPath;
    
    
    @Value("${upload.udesk.image.path}")
    private String uploadUdeskPath;
    
    @Autowired
	private BaseController baseController;

    public AbroadUserController() {
    }

    @PostMapping({"/userAccount"})
    public RespEntity userAccount(HttpServletRequest request) throws Exception {
        UserDto userDto = this.user(request);
        return this.userRpc.userAccount(userDto.getUuid());
    }

    /**
     * 登陆接口
     * @param email
     * @param pwd
     * @param codeid
     * @param vercode
     * @param source
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping({"/login"})
    public RespEntity login(@RequestParam("email") String email, @RequestParam("pwd") String pwd, @RequestParam("codeid") String codeid, @RequestParam("vercode") String vercode, @RequestParam("source") Integer source, HttpServletRequest request) throws Exception {
        String requestHeader = request.getHeader("User-Agent");
        source = 1;
        if (BaseConstant.isMobileDevice(requestHeader)) {
            source = 2;
        }

        if (!StringUtils.isBlank(pwd) && !StringUtils.isBlank(email) && !StringUtils.isBlank(codeid) && !StringUtils.isBlank(vercode)) {
            String imgCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBIMG.getPrefix() + codeid);
            this.redisTemplate.delete(RedisPrefix.DBIMG.getPrefix() + codeid);
            if (StringUtils.isBlank(imgCode)) {
                return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
            } else {
                return !vercode.toLowerCase().equals(imgCode) ? RespEntity.error(RespCode.USER_IMGCODE_ERROR) : this.userRpc.login(email, pwd, source, NetworkUtil.getIpAddress(request));
            }
        } else {
            return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
        }
    }

    /**
     * 登陆接口
     * @param email
     * @param pwd
     * @param codeid
     * @param vercode
     * @param source
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping({"loginGAFirst"})
    public RespEntity loginGAFirst(@RequestParam("email") String email, @RequestParam("pwd") String pwd, @RequestParam(value = "codeid",required = false) String codeid, @RequestParam(value = "vercode",required = false) String vercode, @RequestParam("source") Integer source, HttpServletRequest request) throws Exception {
        try {
            String requestHeader = request.getHeader("User-Agent");
            source = 1;
            if (BaseConstant.isMobileDevice(requestHeader)) {
                source = 2;
            }

            if (!StringUtils.isBlank(pwd) && !StringUtils.isBlank(email)) {
                if (!StringUtils.isBlank(codeid) && !StringUtils.isBlank(vercode)) {
                    String imgCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBIMG.getPrefix() + codeid);
                    this.redisTemplate.delete(RedisPrefix.DBIMG.getPrefix() + codeid);
                    if (StringUtils.isBlank(imgCode)) {
                        return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
                    }

                    // 检查验证码是否正确
                    if (!vercode.toLowerCase().equals(imgCode)) {
                        return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
                    }
                }

                return this.userRpc.loginGAFirst(email, pwd, source, NetworkUtil.getIpAddress(request));
            } else {
                return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
            }
        } catch (Exception var9) {
            var9.printStackTrace();
            log.info("log fail {}", var9);
            return new RespEntity(21001, "登录失败");
        }
    }
    
    /**
     * 登录(增加滑动验证，去掉图片验证码验证)
     * @param email
     * @param pwd
     * @param token
     * @param source
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping({"loginGAFirstSlider"})
    public RespEntity loginGAFirstSlider(
    		@RequestParam("email") String email, 
    		@RequestParam("pwd") String pwd, 
    		@RequestParam(value = "sliderToken") String sliderToken, 
    		@RequestParam("source") Integer source, HttpServletRequest request) throws Exception {
        try {
            String requestHeader = request.getHeader("User-Agent");
            source = 1;
            if (BaseConstant.isMobileDevice(requestHeader)) {
                source = 2;
            }

            if (!StringUtils.isBlank(pwd) && !StringUtils.isBlank(email)) {
            	if(SliderVerification.verifyToken(sliderToken)) {          		
            		return this.userRpc.loginGAFirst(email, pwd, source, NetworkUtil.getIpAddress(request));
            	}else {
            		return new RespEntity(700, "滑动验证失败");
            	}
            } else {
                return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
            }
        } catch (Exception var9) {
            var9.printStackTrace();
            log.info("log fail {}", var9);
            return new RespEntity(21001, "登录失败");
        }
    }

    /**
     * 登录
     * 2.0之后增加滑动验证，去掉图片验证码验证
     * @param email
     * @param pwd
     * @param sliderToken
     * @param source
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping({"loginGAFirstV2"})
    public RespEntity loginGAFirstV2(
            @RequestParam("email") String email,
            @RequestParam("pwd") String pwd,
            @RequestParam(value = "sliderToken") String sliderToken,
            @RequestParam("source") Integer source, HttpServletRequest request) throws Exception {
        try {
            String requestHeader = request.getHeader("User-Agent");
            source = 1;
            if (BaseConstant.isMobileDevice(requestHeader)) {
                source = 2;
            }

            if (!StringUtils.isBlank(pwd) && !StringUtils.isBlank(email)) {
                if(SliderVerification.verifyToken(sliderToken)) {
                    return this.userRpc.loginGAFirst(email, pwd, source, NetworkUtil.getIpAddress(request));
                }else {
                    return new RespEntity(572, "滑动验证失败");
//            	    return RespEntity.error(RespCode.SLIDER_VERIFY_ERROR);
                }
            } else {
                return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
            }
        } catch (Exception var9) {
            var9.printStackTrace();
            log.info("log fail {}", var9);
            return new RespEntity(21001, "登录失败");
        }
    }

    @PostMapping({"loginGASecond"})
    public RespEntity loginGASecond(@RequestParam("clientPassword") Integer clientPassword, @RequestParam("source") Integer source, @RequestParam("email") String email, HttpServletRequest request) throws IOException {
        if (clientPassword != null && !StringUtils.isBlank(email)) {
            String requestHeader = request.getHeader("User-Agent");
            source = 1;
            if (BaseConstant.isMobileDevice(requestHeader)) {
                source = 2;
            }

            return this.userRpc.loginGASecond(clientPassword, source, NetworkUtil.getIpAddress(request), email);
        } else {
            return new RespEntity(5557, "请输入谷歌验证器密码");
        }
    }

    /**
     * 登陆接口
     * @param email
     * @param pwd
     * @param passCard
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping({"/loginRobotAB"})
    public RespEntity loginRobot(@RequestParam("email") String email, @RequestParam("pwd") String pwd, @RequestParam("passCard") String passCard, HttpServletRequest request) throws Exception {
        return !passCard.equals("3907d9aef154cc4533b41cd56ac04afa") ? RespEntity.error(RespCode.SYSTEM_ERROR) : this.userRpc.login(email, pwd, 1, NetworkUtil.getIpAddress(request));
    }

    @PostMapping({"/signLogin"})
    public RespEntity signLogin(@RequestParam("email") String email, @RequestParam("pwd") String pwd, @RequestParam("timestamp") String timestamp, @RequestParam("sign") String sign, HttpServletRequest request) throws Exception {
        if (DateUtil.timestampValid(timestamp, 3600000)) {
            return new RespEntity(903, "timestamp无效或过期");
        } else {
            String ip = NetworkUtil.getIpAddress(request);
            RespEntity respEntity = this.userRpc.loginGAFirst(email, pwd, 5, ip);
            log.info("respEntity: " + JSON.toJSONString(respEntity));
            // 未成功
            if (respEntity.status != 200) {
                return respEntity;
            } else {
                Map map = (Map)respEntity.attachment;
                log.info("map={}",map);
                String suid = String.valueOf(map.get("uid"));
                UserDto userDto = this.user(suid);
                
                //防止缓存不命中返回的userDto为空，所以再通过数据库查询，保险
                if(userDto==null) {
                	log.info("user=>{}",userDto);
                	RespEntity respEntity2 = this.userRpc.selectByUid(Long.valueOf(suid));
                    if (respEntity2 != null && respEntity2.getStatus() == 200) {
                        Object attachment = respEntity2.getAttachment();
                        String jsonString = JSON.toJSONString(attachment);
                        userDto = (UserDto)JSON.parseObject(jsonString, UserDto.class);
                    }
                }
                String secretPublic = userDto.getSecretPublic();
                String secretPrivate = userDto.getSecretPrivate();
                log.info("secretPublic=" + secretPublic + " secretPrivate=" + secretPrivate);
                if (secretPublic != null && SignValidUtil.validSign(3, secretPublic, timestamp, sign)) {
                    return respEntity;
                } else {
                    if (secretPrivate != null) {
                        String s = SignValidUtil.sign(3, secretPrivate, timestamp);
                        log.info("timestamp=" + timestamp + " sign=" + s);
                    }

                    return new RespEntity(902, "签名验证失败");
                }
            }
        }
    }

    @RequestMapping({"/registerBefore"})
    public RespEntity registerBefore(HttpServletRequest request) throws Exception {
        String register_switch = (String)this.redisTemplate.opsForValue().get("register_switch");
        if (register_switch != null) {
            String ip = NetworkUtil.getIpAddress(request);
            JsonObject json = this.getIp(ip);
            log.info("ip:" + json.toString());
            if (json.get("code").getAsInt() == 0 && json.get("data").getAsJsonObject().get("country_id").getAsString().equals("CN")) {
                return new RespEntity(500, "非常抱歉，根据您的IP地址，我们暂不支持您所在的国家或地区在ChaoEx使用该服务");
            }
        }

        return RespEntity.success("");
    }

    /**
     * 注册
     * @param email
     * @param pwd
     * @param vercode
     * @param inviteId
     * @param request
     * @param codeid
     * @param imgcode
     * @return
     * @throws Exception
     */
    @PostMapping({"/register"})
    public RespEntity register(@RequestParam("email") String email,
                               @RequestParam("pwd") String pwd,
                               @RequestParam("vercode") String vercode,
                               String inviteId,
                               HttpServletRequest request,
                               @RequestParam("codeid") String codeid,
                               @RequestParam("imgcode") String imgcode) throws Exception {
        String code = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBIMG.getPrefix() + codeid);
        this.redisTemplate.delete(RedisPrefix.DBIMG.getPrefix() + codeid);
        if (!imgcode.toLowerCase().equals(code)) {
            return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
        } else {
            String register_switch = (String)this.redisTemplate.opsForValue().get("register_switch");
            String authCode;
            if (register_switch != null) {
                authCode = NetworkUtil.getIpAddress(request);
                JsonObject json = this.getIp(authCode);
                log.info("ip:" + json.toString());
                if (json.get("code").getAsInt() == 0 && json.get("data").getAsJsonObject().get("country_id").getAsString().equals("CN")) {
                    return new RespEntity(500, "非常抱歉，根据您的IP地址，我们暂不支持您所在的国家或地区在ChaoEx使用该服务");
                }
            }

            System.out.println("邀请码：" + inviteId);
            if (!StringUtils.isBlank(email) && !StringUtils.isBlank(pwd) && !StringUtils.isBlank(vercode)) {
                if (!this.checkPwd(pwd)) {
                    return RespEntity.error(RespCode.USER_PASSWORD_CHECKED);
                } else {
                    authCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBMAIL.getPrefix() + email);
                    // 校验验证码是否有效
                    if (!vercode.equalsIgnoreCase(authCode)) {
                        return RespEntity.error(RespCode.USER_AUTHCODE_ERROR);
                    } else {
                        UserDto dto = new UserDto();
                        dto.setCode(inviteId);
                        dto.setEmail(email);
                        dto.setPassword(pwd);
                        // 开始注册
                        return this.userRpc.register(dto);
                    }
                }
            } else {
                return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
            }
        }
    }

    /**
     * 注册
     * 2.0之后加入滑动验证
     * @param email
     * @param pwd
     * @param inviteId
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping({"/registerV2"})
    public RespEntity register(@RequestParam("email") String email,
                               @RequestParam("pwd") String pwd,
                               @RequestParam("vercode") String vercode,
                               String inviteId,
                               HttpServletRequest request) throws Exception {

        String register_switch = (String)this.redisTemplate.opsForValue().get("register_switch");
        String authCode;
        if (register_switch != null) {
            authCode = NetworkUtil.getIpAddress(request);
            JsonObject json = this.getIp(authCode);
            log.info("ip:" + json.toString());
            if (json.get("code").getAsInt() == 0 && json.get("data").getAsJsonObject().get("country_id").getAsString().equals("CN")) {
                return new RespEntity(500, "非常抱歉，根据您的IP地址，我们暂不支持您所在的国家或地区在ChaoEx使用该服务");
            }
        }

        System.out.println("邀请码：" + inviteId);
        if (!StringUtils.isBlank(email) && !StringUtils.isBlank(pwd) && !StringUtils.isBlank(vercode)) {

            if (!this.checkPwd(pwd)) {
                return RespEntity.error(RespCode.USER_PASSWORD_CHECKED);
            } else {
                authCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBMAIL.getPrefix() + email);
                // 校验验证码是否有效
                if (!vercode.equalsIgnoreCase(authCode)) {
                    return RespEntity.error(RespCode.USER_AUTHCODE_ERROR);
                } else {
                    UserDto dto = new UserDto();
                    dto.setCode(inviteId);
                    dto.setEmail(email);
                    dto.setPassword(pwd);
                    // 开始注册
                    return this.userRpc.register(dto);
                }
            }

        } else {
            return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
        }
    }

    public JsonObject getIp(String ip) {
        String s = (String)this.redisTemplate.opsForValue().get(ip);
        if (s == null) {
            RestTemplate restTemplate = new RestTemplate();
            String returnSringcn = (String)restTemplate.getForObject("http://ip.taobao.com/service/getIpInfo.php?ip=" + ip, String.class, new Object[0]);
            JsonObject json = (JsonObject)(new Gson()).fromJson(returnSringcn, JsonObject.class);
            this.redisTemplate.opsForValue().set(ip, json.toString(), 1L, TimeUnit.DAYS);
            return json;
        } else {
            return (JsonObject)(new Gson()).fromJson(s, JsonObject.class);
        }
    }

    @PostMapping({"/judgeEmailStatus"})
    public RespEntity judgeEmailStatus(@RequestParam("email") String email) throws Exception {
        return StringUtils.isBlank(email) ? RespEntity.error(RespCode.COMMON_PARAM_BLANK) : this.userRpc.judgeEmailStatus(email);
    }

    @RequestMapping({"/sendEmailForRegister"})
    public RespEntity sendEmailForRegister(@RequestParam("email") String email, @RequestParam("codeid") String codeid, @RequestParam("imgcode") String imgcode) throws Exception {
        String imgCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBIMG.getPrefix() + codeid);
        if (StringUtils.isBlank(imgCode)) {
            this.redisTemplate.delete(RedisPrefix.DBIMG.getPrefix() + codeid);
            return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
        } else if (!imgcode.toLowerCase().equals(imgCode)) {
            return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
        } else {
            UserDto userByEmail = this.userRpc.findUserByEmail(email);
            if (userByEmail != null) {
                return email.contains("@") ? RespEntity.error(RespCode.USER_BIND_EMAIL_ERROR_EXIST) : RespEntity.error(RespCode.USER_PHONE_EXIST);
            } else {
                return this.userRpc.sendEmailForRegister(email);
            }
        }
    }

    /**
     * 注册前发送短信／邮箱
     * 2.0只有加入滑动验证
     * @param email
     * @param sliderToken
     * @return
     * @throws Exception
     */
    @RequestMapping({"/sendEmailForRegisterV2"})
    public RespEntity sendEmailForRegisterV2(@RequestParam("email") String email,
                                             @RequestParam(value = "sliderToken") String sliderToken) throws Exception {
        if(!SliderVerification.verifyToken(sliderToken)){
            return new RespEntity(572, "滑动验证失败");
//            return RespEntity.error(RespCode.SLIDER_VERIFY_ERROR);
        } else {
            UserDto userByEmail = this.userRpc.findUserByEmail(email);
            if (userByEmail != null) {
                return email.contains("@") ? RespEntity.error(RespCode.USER_BIND_EMAIL_ERROR_EXIST) : RespEntity.error(RespCode.USER_PHONE_EXIST);
            } else {
                return this.userRpc.sendEmailForRegister(email);
            }
        }
    }

    @PostMapping({"/sendMail"})
    public RespEntity sendMail(@RequestParam("email") String email, @RequestParam("codeid") String codeid, @RequestParam("imgcode") String imgcode) throws Exception {
        String imgCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBIMG.getPrefix() + codeid);
        if (!imgcode.toLowerCase().equals(imgCode)) {
            this.redisTemplate.delete(RedisPrefix.DBIMG.getPrefix() + codeid);
            return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
        } else {
            return this.userRpc.sendMail(email);
        }
    }

    @PostMapping({"/sendMailV2"})
    public RespEntity sendMail(@RequestParam("email") String email,
                               @RequestParam(value = "sliderToken") String sliderToken) throws Exception {
        if(!SliderVerification.verifyToken(sliderToken)){
            return new RespEntity(572, "滑动验证失败");
//            return RespEntity.error(RespCode.SLIDER_VERIFY_ERROR);
        } else {
            return this.userRpc.sendMail(email);
        }
    }

    /**
     * 通过邮件重置密码
     * @param email
     * @param vercode
     * @param codeid
     * @param imgcode
     * @param newPwd
     * @return
     * @throws Exception
     */
    @PostMapping({"/resetPwd"})
    public RespEntity resetPwd(@RequestParam("email") String email, @RequestParam("vercode") String vercode, @RequestParam("codeid") String codeid, @RequestParam("imgcode") String imgcode, @RequestParam("newPwd") String newPwd) throws Exception {
        String code = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBIMG.getPrefix() + codeid);
        this.redisTemplate.delete(RedisPrefix.DBIMG.getPrefix() + codeid);
        if (!imgcode.toLowerCase().equals(code)) {
            return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
        } else {
            UserDto user = this.userRpc.findUserByEmail(email);
            if (user == null) {
                return RespEntity.error(RespCode.USER_FINDPWD_NOUSER);
            } else if (!this.checkPwd(newPwd)) {
                return RespEntity.error(RespCode.USER_PASSWORD_CHECKED);
            } else {
                String fdPwd = MD5Util.encryptFdPwd(newPwd, user.getUid());
                if (fdPwd.equals(user.getFdPassword())) {
                    return RespEntity.error(RespCode.USER_FDANDLOGINSAME_ERROR);
                } else {
                    String authCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBMAILPWD.getPrefix() + email);
                    if (com.xyhj.lian.util.StringUtils.isBlank(authCode)) {
                        return RespEntity.error(RespCode.USER_AUTHCODE_EXPIRED);
                    } else if (!authCode.equalsIgnoreCase(vercode)) {
                        return RespEntity.error(RespCode.USER_AUTHCODE_ERROR);
                    } else {
                        if (email.contains("@")) {
                            if (user.getIsValidateEmail() == 0) {
                                return new RespEntity(500, "邮箱未绑定");
                            }
                        } else if (user.getIsValidatePhone() == 0) {
                            return new RespEntity(500, "手机未绑定");
                        }

                        return this.userRpc.resetPwdByEmail(user.getUid(), newPwd);
                    }
                }
            }
        }
    }

    /**
     * 通过邮件重置密码
     * 2.0之后加入滑动验证
     * @param email
     * @param vercode
     * @param newPwd
     * @return
     * @throws Exception
     */
    @PostMapping({"/resetPwdV2"})
    public RespEntity resetPwdV2(@RequestParam("email") String email,
                                 @RequestParam("vercode") String vercode,
                                 @RequestParam("newPwd") String newPwd) throws Exception {
        UserDto user = this.userRpc.findUserByEmail(email);
        if (user == null) {
            return RespEntity.error(RespCode.USER_FINDPWD_NOUSER);
        } else if (!this.checkPwd(newPwd)) {
            return RespEntity.error(RespCode.USER_PASSWORD_CHECKED);
        } else {
            String fdPwd = MD5Util.encryptFdPwd(newPwd, user.getUid());
            if (fdPwd.equals(user.getFdPassword())) {
                return RespEntity.error(RespCode.USER_FDANDLOGINSAME_ERROR);
            } else {
                String authCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBMAILPWD.getPrefix() + email);
                if (com.xyhj.lian.util.StringUtils.isBlank(authCode)) {
                    return RespEntity.error(RespCode.USER_AUTHCODE_EXPIRED);
                } else if (!authCode.equalsIgnoreCase(vercode)) {
                    return RespEntity.error(RespCode.USER_AUTHCODE_ERROR);
                } else {
                    if (email.contains("@")) {
                        if (user.getIsValidateEmail() == 0) {
                            return new RespEntity(500, "邮箱未绑定");
                        }
                    } else if (user.getIsValidatePhone() == 0) {
                        return new RespEntity(500, "手机未绑定");
                    }

                    return this.userRpc.resetPwdByEmail(user.getUid(), newPwd);
                }
            }
        }
    }

    /**
     * 个人中心修改密码 - : 发送邮件
     * @param request
     * @param codeid
     * @param type
     * @param imgcode
     * @return
     * @throws Exception
     */
    @PostMapping({"/sendMailInUserCenter"})
    public RespEntity sendMailInUserCenter(HttpServletRequest request, @RequestParam("codeid") String codeid, @RequestParam(value = "type",required = false,defaultValue = "2") String type, @RequestParam("imgcode") String imgcode) throws Exception {
        String imgCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBIMG.getPrefix() + codeid);
        if (!imgcode.toLowerCase().equals(imgCode)) {
            this.redisTemplate.delete(RedisPrefix.DBIMG.getPrefix() + codeid);
            return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
        } else {
            UserDto userDto = this.user(request);
            userDto = this.userRpc.getUserByUuid(userDto.getUuid());
            if (type.equals("2")) {
                return userDto.getIsValidatePhone() == 0 ? new RespEntity(500, "手机未绑定") : this.userRpc.sendMail(userDto.getPhone());
            } else {
                return userDto.getIsValidateEmail() == 0 ? new RespEntity(500, "邮箱未绑定") : this.userRpc.sendMail(userDto.getEmail());
            }
        }
    }

    /**
     * 个人中心修改密码 - : 发送邮件
     * 2.0之后加入滑动验证
     * @param request
     * @param type
     * @param sliderToken
     * @return
     * @throws Exception
     */
    @PostMapping({"/sendMailInUserCenterV2"})
    public RespEntity sendMailInUserCenterV2(HttpServletRequest request,
                                             @RequestParam(value = "type",required = false,defaultValue = "2") String type,
                                             @RequestParam(value = "sliderToken") String sliderToken) throws Exception {
        if(!SliderVerification.verifyToken(sliderToken)){
            return new RespEntity(572, "滑动验证失败");
//            return RespEntity.error(RespCode.SLIDER_VERIFY_ERROR);
        } else {
            UserDto userDto = this.user(request);
            userDto = this.userRpc.getUserByUuid(userDto.getUuid());
            if (type.equals("2")) {
                return userDto.getIsValidatePhone() == 0 ? new RespEntity(500, "手机未绑定") : this.userRpc.sendMail(userDto.getPhone());
            } else {
                return userDto.getIsValidateEmail() == 0 ? new RespEntity(500, "邮箱未绑定") : this.userRpc.sendMail(userDto.getEmail());
            }
        }
    }

    /**
     * 个人中心修改密码 二 :
     * @param request
     * @param codeid
     * @param imgcode
     * @param oldPwd
     * @param newPwd
     * @param vercode
     * @return
     * @throws Exception
     */
    @PostMapping({"/resetPwdInUserCenter"})
    public RespEntity resetPwdInUserCenter(HttpServletRequest request, @RequestParam("codeid") String codeid, @RequestParam("imgcode") String imgcode, @RequestParam("oldPwd") String oldPwd, @RequestParam("newPwd") String newPwd, @RequestParam("vercode") String vercode) throws Exception {
        String imgCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBIMG.getPrefix() + codeid);
        if (!imgcode.toLowerCase().equalsIgnoreCase(imgCode)) {
            this.redisTemplate.delete(RedisPrefix.DBIMG.getPrefix() + codeid);
            return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
        } else {
            UserDto userDto = this.user(request);
            if (!userDto.getPassword().equalsIgnoreCase(oldPwd)) {
                return RespEntity.error(RespCode.USER_OLDPWD_ERROR);
            } else if (!this.checkPwd(newPwd)) {
                return RespEntity.error(RespCode.USER_PASSWORD_CHECKED);
            } else {
                String fdPwd = MD5Util.encryptFdPwd(newPwd, userDto.getUid());
                if (fdPwd.equals(userDto.getFdPassword())) {
                    return RespEntity.error(RespCode.USER_FDANDLOGINSAME_ERROR);
                } else {
                    String authCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBMAILPWD.getPrefix() + userDto.getEmail());
                    if (!vercode.equalsIgnoreCase(authCode)) {
                        authCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBMAILPWD.getPrefix() + userDto.getPhone());
                        if (!vercode.equalsIgnoreCase(authCode)) {
                            return RespEntity.error(RespCode.USER_AUTHCODE_ERROR);
                        }
                    }

                    return this.userRpc.resetPwdByEmail(userDto.getUid(), newPwd);
                }
            }
        }
    }

    /**
     * 个人中心修改密码 二 :
     * 2.0之后加入滑动验证
     * @param request
     * @param oldPwd
     * @param newPwd
     * @param vercode
     * @return
     * @throws Exception
     */
    @PostMapping({"/resetPwdInUserCenterV2"})
    public RespEntity resetPwdInUserCenter(HttpServletRequest request,
                                           @RequestParam("oldPwd") String oldPwd,
                                           @RequestParam("newPwd") String newPwd,
                                           @RequestParam("vercode") String vercode) throws Exception {
        UserDto userDto = this.user(request);
        if (!userDto.getPassword().equalsIgnoreCase(oldPwd)) {
            return RespEntity.error(RespCode.USER_OLDPWD_ERROR);
        } else if (!this.checkPwd(newPwd)) {
            return RespEntity.error(RespCode.USER_PASSWORD_CHECKED);
        } else {
            String fdPwd = MD5Util.encryptFdPwd(newPwd, userDto.getUid());
            if (fdPwd.equals(userDto.getFdPassword())) {
                return RespEntity.error(RespCode.USER_FDANDLOGINSAME_ERROR);
            } else {
                String authCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBMAILPWD.getPrefix() + userDto.getEmail());
                if (!vercode.equalsIgnoreCase(authCode)) {
                    authCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBMAILPWD.getPrefix() + userDto.getPhone());
                    if (!vercode.equalsIgnoreCase(authCode)) {
                        return RespEntity.error(RespCode.USER_AUTHCODE_ERROR);
                    }
                }

                return this.userRpc.resetPwdByEmail(userDto.getUid(), newPwd);
            }
        }
    }

    @PostMapping({"/sendForResetEmail"})
    public RespEntity sendForResetEmail(HttpServletRequest request, @RequestParam("codeid") String codeid, @RequestParam("imgcode") String imgcode) throws Exception {
        String imgCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBIMG.getPrefix() + codeid);
        this.redisTemplate.delete(RedisPrefix.DBIMG.getPrefix() + codeid);
        if (StringUtils.isBlank(imgCode)) {
            return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
        } else if (!imgcode.toLowerCase().equals(imgCode)) {
            return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
        } else {
            UserDto userDto = this.user(request);
            return this.userRpc.sendForResetEmail(userDto.getEmail());
        }
    }

    /**
     * 修改邮箱 提交邮箱
     * @param request
     * @param oldEmail
     * @param newEmail
     * @param vercode
     * @return
     * @throws Exception
     */
    @PostMapping({"/resetEmail"})
    public RespEntity resetEmail(HttpServletRequest request, @RequestParam("oldEmail") String oldEmail, @RequestParam("newEmail") String newEmail, @RequestParam("vercode") String vercode) throws Exception {
        UserDto userDto = this.user(request);
        if (!userDto.getEmail().equals(oldEmail)) {
            return RespEntity.error(RespCode.USER_PWD_EMAIL_ISOTHERS);
        } else if (oldEmail.equals(newEmail)) {
            return new RespEntity(102, "新邮箱不能与旧邮箱一致");
        } else {
            // 判断新邮箱的状态
            UserDto user = this.userRpc.findUserByEmail(newEmail);
            if (user != null) {
                return RespEntity.error(RespCode.USER_BIND_EMAIL_ERROR_EXIST);
            } else {
                // 判断验证码
                String authCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBMAIL.getPrefix() + userDto.getEmail());
                if (com.xyhj.lian.util.StringUtils.isBlank(authCode)) {
                    return RespEntity.error(RespCode.USER_AUTHCODE_EXPIRED);
                } else {
                    return !authCode.equalsIgnoreCase(vercode) ? RespEntity.error(RespCode.USER_AUTHCODE_ERROR) : this.userRpc.resetEmail(oldEmail, newEmail);
                }
            }
        }
    }

    @RequestMapping(
            value = {"/verifyBind"},
            method = {RequestMethod.POST}
    )
    public RespEntity verifyBind(@RequestParam("phoneOrEmail") String phoneOrEmail, HttpServletRequest request, @RequestParam("codeid") String codeid, @RequestParam("imgcode") String imgcode, @RequestParam("vercode") String vercode) throws Exception {
        String imgCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBIMG.getPrefix() + codeid);
        if (!imgcode.toLowerCase().equalsIgnoreCase(imgCode)) {
            this.redisTemplate.delete(RedisPrefix.DBIMG.getPrefix() + codeid);
            return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
        } else {
            UserDto userDto = this.user(request);
            userDto = this.userRpc.getUserByUuid(userDto.getUuid());
            if (phoneOrEmail != null && !phoneOrEmail.equals("")) {
                if (phoneOrEmail.contains("@")) {
                    if (!userDto.getEmail().equals(phoneOrEmail)) {
                        return RespEntity.error(RespCode.USER_PWD_EMAIL_ISOTHERS);
                    }
                } else if (!phoneOrEmail.equals(userDto.getPhone())) {
                    return RespEntity.error(RespCode.USER_PHONE_INCORRECT);
                }
            } else {
                if (userDto.getIsValidateEmail() == 1) {
                    phoneOrEmail = userDto.getEmail();
                }

                if (userDto.getIsValidatePhone() == 1) {
                    phoneOrEmail = userDto.getPhone();
                }
            }

            String oldauthCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBSMS.getPrefix() + phoneOrEmail);
            log.info("手机或者邮箱：" + phoneOrEmail + "，验证码：" + oldauthCode + "，传入验证码：" + vercode);
            return !vercode.equalsIgnoreCase(oldauthCode) ? RespEntity.error(RespCode.USER_AUTHCODE_ERROR) : RespEntity.success((Object)null);
        }
    }

    /**
     * 绑定手机或者邮箱验证
     * 2.0之后加入滑动验证
     * @param phoneOrEmail
     * @param request
     * @param vercode
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"/verifyBindV2"}, method = {RequestMethod.POST})
    public RespEntity verifyBindV2(@RequestParam("phoneOrEmail") String phoneOrEmail,
                                   HttpServletRequest request,
                                   @RequestParam("vercode") String vercode) throws Exception {
        UserDto userDto = this.user(request);
        userDto = this.userRpc.getUserByUuid(userDto.getUuid());
        if (phoneOrEmail != null && !phoneOrEmail.equals("")) {
            if (phoneOrEmail.contains("@")) {
                if (!userDto.getEmail().equals(phoneOrEmail)) {
                    return RespEntity.error(RespCode.USER_PWD_EMAIL_ISOTHERS);
                }
            } else if (!phoneOrEmail.equals(userDto.getPhone())) {
                return RespEntity.error(RespCode.USER_PHONE_INCORRECT);
            }
        } else {
            if (userDto.getIsValidateEmail() == 1) {
                phoneOrEmail = userDto.getEmail();
            }

            if (userDto.getIsValidatePhone() == 1) {
                phoneOrEmail = userDto.getPhone();
            }
        }

        String oldauthCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBSMS.getPrefix() + phoneOrEmail);
        log.info("手机或者邮箱：" + phoneOrEmail + "，验证码：" + oldauthCode + "，传入验证码：" + vercode);
        return !vercode.equalsIgnoreCase(oldauthCode) ? RespEntity.error(RespCode.USER_AUTHCODE_ERROR) : RespEntity.success((Object)null);
    }

    /**
     * 绑定手机或者邮箱
     * @param phoneOrEmail 手机或者邮箱
     * @param request
     * @param codeid
     * @param type
     * @param imgcode
     * @return
     * @throws Exception
     */
    @RequestMapping(
            value = {"/bindPhoneSendMsg"},
            method = {RequestMethod.POST}
    )
    public RespEntity bindPhoneSendMsg(@RequestParam(value = "phone",required = false) String phoneOrEmail, HttpServletRequest request, @RequestParam("codeid") String codeid, @RequestParam("type") String type, @RequestParam("imgcode") String imgcode) throws Exception {
        log.info("phoneOrEmail:" + phoneOrEmail);
        log.info("codeid:" + codeid);
        log.info("type:" + type);
        log.info("imgcode:" + imgcode);
        // 检查验证码是否正确
        String imgCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBIMG.getPrefix() + codeid);
        if (!imgcode.toLowerCase().equals(imgCode)) {
            this.redisTemplate.delete(RedisPrefix.DBIMG.getPrefix() + codeid);
            return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
        } else {
            // 判断账户是否被占用
            UserDto userByEmail = this.userRpc.findUserByEmail(phoneOrEmail);
            if (type.equals("1")) {
                if (userByEmail == null) {
                    if (phoneOrEmail.contains("@")) {
                        return RespEntity.error(RespCode.USER_PWD_EMAIL_ISOTHERS);
                    }

                    return new RespEntity(500, "原手机号不正确");
                }

                this.userRpc.bindPhoneSendMsg(phoneOrEmail);
            } else if (type.equals("2")) {
                if (userByEmail != null) {
                    if (phoneOrEmail.contains("@")) {
                        return RespEntity.error(RespCode.USER_BIND_EMAIL_ERROR_EXIST);
                    }

                    return RespEntity.error(RespCode.USER_PHONE_EXIST);
                }

                // 开始发短信
                this.userRpc.bindPhoneSendMsg(phoneOrEmail);
            } else {
                if (!type.equals("3")) {
                    return RespEntity.error(RespCode.SMS_SEND_ERROR);
                }

                UserDto userDto = this.user(request);
                userDto = this.userRpc.getUserByUuid(userDto.getUuid());
                System.out.println((new Gson()).toJson(userDto));
                if (userDto.getIsValidatePhone() != null && userDto.getIsValidatePhone() == 1) {
                    log.info("给手机发送验证码：" + userDto.getPhone());
                    // 开始发短信
                    this.userRpc.bindPhoneSendMsg(userDto.getPhone());
                } else {
                    if (userDto.getIsValidateEmail() == null || userDto.getIsValidateEmail() != 1) {
                        log.info("发送验证码异常");
                        return RespEntity.error(RespCode.SMS_SEND_ERROR);
                    }

                    log.info("给邮箱发送验证码：" + userDto.getEmail());
                    // 开始发短信
                    this.userRpc.bindPhoneSendMsg(userDto.getEmail());
                }
            }

            return RespEntity.success((Object)null);
        }
    }

    /**
     * 绑定手机或者邮箱
     * @param phoneOrEmail
     * @param request
     * @param type
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"/bindPhoneSendMsgV2"}, method = {RequestMethod.POST})
    public RespEntity bindPhoneSendMsgV2(@RequestParam(value = "phone",required = false) String phoneOrEmail,
                                         HttpServletRequest request,
                                         @RequestParam("type") String type) throws Exception {
        log.info("phoneOrEmail:" + phoneOrEmail);
        log.info("type:" + type);

        // 判断账户是否被占用
        UserDto userByEmail = this.userRpc.findUserByEmail(phoneOrEmail);
        if (type.equals("1")) {
            if (userByEmail == null) {
                if (phoneOrEmail.contains("@")) {
                    return RespEntity.error(RespCode.USER_PWD_EMAIL_ISOTHERS);
                }

                return new RespEntity(500, "原手机号不正确");
            }

            this.userRpc.bindPhoneSendMsg(phoneOrEmail);
        } else if (type.equals("2")) {
            if (userByEmail != null) {
                if (phoneOrEmail.contains("@")) {
                    return RespEntity.error(RespCode.USER_BIND_EMAIL_ERROR_EXIST);
                }

                return RespEntity.error(RespCode.USER_PHONE_EXIST);
            }

            // 开始发短信
            this.userRpc.bindPhoneSendMsg(phoneOrEmail);
        } else {
            if (!type.equals("3")) {
                return RespEntity.error(RespCode.SMS_SEND_ERROR);
            }

            UserDto userDto = this.user(request);
            userDto = this.userRpc.getUserByUuid(userDto.getUuid());
            System.out.println((new Gson()).toJson(userDto));
            if (userDto.getIsValidatePhone() != null && userDto.getIsValidatePhone() == 1) {
                log.info("给手机发送验证码：" + userDto.getPhone());
                // 开始发短信
                this.userRpc.bindPhoneSendMsg(userDto.getPhone());
            } else {
                if (userDto.getIsValidateEmail() == null || userDto.getIsValidateEmail() != 1) {
                    log.info("发送验证码异常");
                    return RespEntity.error(RespCode.SMS_SEND_ERROR);
                }

                log.info("给邮箱发送验证码：" + userDto.getEmail());
                // 开始发短信
                this.userRpc.bindPhoneSendMsg(userDto.getEmail());
            }
        }

        return RespEntity.success((Object)null);
    }

    @RequestMapping(
            value = {"/bindPhone"},
            method = {RequestMethod.POST}
    )
    public RespEntity bindPhone(@RequestParam(value = "oldPhone",required = false,defaultValue = "") String oldPhone, @RequestParam("newPhone") String newPhone, @RequestParam("vercode") String vercode, String oldVercode, @RequestParam("codeid") String codeid, @RequestParam("imgcode") String imgcode, HttpServletRequest request) throws Exception {
        String imgCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBIMG.getPrefix() + codeid);
        if (!imgcode.toLowerCase().equalsIgnoreCase(imgCode)) {
            this.redisTemplate.delete(RedisPrefix.DBIMG.getPrefix() + codeid);
            return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
        } else {
            log.info("oldPhone:" + oldPhone);
            log.info("newPhone:" + newPhone);
            log.info("vercode:" + vercode);
            log.info("oldVercode:" + oldVercode);
            UserDto userDto = this.user(request);
            userDto = this.userRpc.getUserByUuid(userDto.getUuid());
            if (!oldPhone.equals("")) {
                if (oldPhone.contains("@")) {
                    if (!userDto.getEmail().equals(oldPhone)) {
                        return RespEntity.error(RespCode.USER_PWD_EMAIL_ISOTHERS);
                    }
                } else if (!oldPhone.equals(userDto.getPhone())) {
                    return RespEntity.error(RespCode.USER_PHONE_INCORRECT);
                }
            }

            UserDto userByEmail = this.userRpc.findUserByEmail(newPhone);
            if (userByEmail != null) {
                return newPhone.contains("@") ? RespEntity.error(RespCode.USER_BIND_EMAIL_ERROR_EXIST) : RespEntity.error(RespCode.USER_PHONE_EXIST);
            } else {
                if (oldPhone == null || oldPhone.equals("")) {
                    userDto = this.userRpc.getUserByUuid(userDto.getUuid());
                    if (userDto.getIsValidatePhone() != null && userDto.getIsValidatePhone() == 1) {
                        oldPhone = userDto.getPhone();
                    } else if (userDto.getIsValidateEmail() != null && userDto.getIsValidateEmail() == 1) {
                        oldPhone = userDto.getEmail();
                    }
                }

                String oldauthCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBSMS.getPrefix() + oldPhone);
                if (oldVercode != null && !oldVercode.equalsIgnoreCase(oldauthCode)) {
                    return new RespEntity(500, "原验证码不正确");
                } else {
                    String authCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBSMS.getPrefix() + newPhone);
                    return !vercode.equalsIgnoreCase(authCode) ? RespEntity.error(RespCode.USER_AUTHCODE_ERROR) : this.userRpc.bindPhone(newPhone, userDto.getUid());
                }
            }
        }
    }

    /**
     * 更换绑定手机号
     * 2.0之后加入滑动验证
     * @param oldPhone
     * @param newPhone
     * @param vercode
     * @param oldVercode
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(
            value = {"/bindPhoneV2"},
            method = {RequestMethod.POST}
    )
    public RespEntity bindPhoneV2(
            @RequestParam(value = "oldPhone",required = false,defaultValue = "") String oldPhone,
            @RequestParam("newPhone") String newPhone,
            @RequestParam("vercode") String vercode,
            String oldVercode,
            HttpServletRequest request) throws Exception {

        log.info("oldPhone:" + oldPhone);
        log.info("newPhone:" + newPhone);
        log.info("vercode:" + vercode);
        log.info("oldVercode:" + oldVercode);
        UserDto userDto = this.user(request);
        userDto = this.userRpc.getUserByUuid(userDto.getUuid());
        if (!oldPhone.equals("")) {
            if (oldPhone.contains("@")) {
                if (!userDto.getEmail().equals(oldPhone)) {
                    return RespEntity.error(RespCode.USER_PWD_EMAIL_ISOTHERS);
                }
            } else if (!oldPhone.equals(userDto.getPhone())) {
                return RespEntity.error(RespCode.USER_PHONE_INCORRECT);
            }
        }

        UserDto userByEmail = this.userRpc.findUserByEmail(newPhone);
        if (userByEmail != null) {
            return newPhone.contains("@") ? RespEntity.error(RespCode.USER_BIND_EMAIL_ERROR_EXIST) : RespEntity.error(RespCode.USER_PHONE_EXIST);
        } else {
            if (oldPhone == null || oldPhone.equals("")) {
                userDto = this.userRpc.getUserByUuid(userDto.getUuid());
                if (userDto.getIsValidatePhone() != null && userDto.getIsValidatePhone() == 1) {
                    oldPhone = userDto.getPhone();
                } else if (userDto.getIsValidateEmail() != null && userDto.getIsValidateEmail() == 1) {
                    oldPhone = userDto.getEmail();
                }
            }

            String oldauthCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBSMS.getPrefix() + oldPhone);
            if (StringUtils.isNotBlank(oldVercode) && !oldVercode.equalsIgnoreCase(oldauthCode)) {
                return new RespEntity(500, "原验证码不正确");
            } else {
                String authCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBSMS.getPrefix() + newPhone);
                return !vercode.equalsIgnoreCase(authCode) ? RespEntity.error(RespCode.USER_AUTHCODE_ERROR) : this.userRpc.bindPhone(newPhone, userDto.getUid());
            }
        }
    }

    /**
     * 个人中心设置交易密码码 - :
     * @param request
     * @param oldFdPassWord
     * @param vercode
     * @param newFdPassWord
     * @param codeid
     * @param imgcode
     * @return
     * @throws Exception
     */
    @PostMapping({"/bindFdPwd"})
    public RespEntity bindFdPwd(HttpServletRequest request, @RequestParam(value = "oldFdPassWord",required = false) String oldFdPassWord, @RequestParam("vercode") String vercode, @RequestParam("newFdPassWord") String newFdPassWord, @RequestParam("codeid") String codeid, @RequestParam("imgcode") String imgcode) throws Exception {
        String imgCode = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBIMG.getPrefix() + codeid);
        if (!imgcode.toLowerCase().equalsIgnoreCase(imgCode)) {
            this.redisTemplate.delete(RedisPrefix.DBIMG.getPrefix() + codeid);
            return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
        } else {
            UserDto user = this.user(request);
            // 判断老交易密码是否正确
            if (!StringUtils.isBlank(oldFdPassWord) && !oldFdPassWord.equalsIgnoreCase(user.getFdPassword())) {
                return new RespEntity(123, "原交易密码不正确");
            } else if (newFdPassWord.length() >= 6 && newFdPassWord.length() <= 16) {//判断交易密码的格式
                String codeInDb = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBMAILPWD.getPrefix() + user.getEmail());
                if (!vercode.equalsIgnoreCase(codeInDb)) {
                    codeInDb = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBMAILPWD.getPrefix() + user.getPhone());
                    if (!vercode.equalsIgnoreCase(codeInDb)) {
                        return RespEntity.error(RespCode.USER_CODE_INCORRECT);
                    }
                }
                // 交易密码加盐
                String afterencrypt = MD5Util.encryptPwd(newFdPassWord);
                // 判断交易密码是否和用户密码相同
                if (afterencrypt.equals(user.getPassword())) {
                    return RespEntity.error(RespCode.USER_FDANDLOGINSAME_ERROR);
                } else {
                    // 将交易密码进行混淆后再插入
                    String fdPwd = MD5Util.encryptFdPwd(newFdPassWord, user.getUid());
                    return this.userRpc.bindFdpwd(user.getUid(), fdPwd);
                }
            } else {
                return RespEntity.error(RespCode.USER_FDPASSWORDAU_ERROR);
            }
        }
    }

    /**
     * 个人中心设置交易密码码 - :
     * 2.0之后加入滑动验证
     * @param request
     * @param oldFdPassWord
     * @param vercode
     * @param newFdPassWord
     * @return
     * @throws Exception
     */
    @PostMapping({"/bindFdPwdV2"})
    public RespEntity bindFdPwdV2(HttpServletRequest request,
                                  @RequestParam(value = "oldFdPassWord",required = false) String oldFdPassWord,
                                  @RequestParam("vercode") String vercode,
                                  @RequestParam("newFdPassWord") String newFdPassWord) throws Exception {
        UserDto user = this.user(request);
        // 判断老交易密码是否正确
        if (!StringUtils.isBlank(oldFdPassWord) && !oldFdPassWord.equalsIgnoreCase(user.getFdPassword())) {
            return new RespEntity(123, "原交易密码不正确");
        } else if (newFdPassWord.length() >= 6 && newFdPassWord.length() <= 16) {//判断交易密码的格式
            String codeInDb = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBMAILPWD.getPrefix() + user.getEmail());
            if (!vercode.equalsIgnoreCase(codeInDb)) {
                codeInDb = (String)this.redisTemplate.opsForValue().get(RedisPrefix.DBMAILPWD.getPrefix() + user.getPhone());
                if (!vercode.equalsIgnoreCase(codeInDb)) {
                    return RespEntity.error(RespCode.USER_CODE_INCORRECT);
                }
            }
            // 交易密码加盐
            String afterencrypt = MD5Util.encryptPwd(newFdPassWord);
            // 判断交易密码是否和用户密码相同
            if (afterencrypt.equals(user.getPassword())) {
                return RespEntity.error(RespCode.USER_FDANDLOGINSAME_ERROR);
            } else {
                // 将交易密码进行混淆后再插入
                String fdPwd = MD5Util.encryptFdPwd(newFdPassWord, user.getUid());
                return this.userRpc.bindFdpwd(user.getUid(), fdPwd);
            }
        } else {
            return RespEntity.error(RespCode.USER_FDPASSWORDAU_ERROR);
        }
    }

    /**
     * 提交用户那一堆乱七八糟的信息
     * @param firstName
     * @param secondName
     * @param uid
     * @param gender
     * @param country
     * @param postCode
     * @param address
     * @param phone
     * @param birthday
     * @param idNumber
     * @param IDCardSingnTime
     * @param IDCardOutOfTime
     * @param profession
     * @param property
     * @param annualSalary
     * @param resortType
     * @param positiveImages
     * @param oppositeImages
     * @param handImages
     * @param locationImages
     * @param professionOther
     * @param propertyOther
     * @param resortTypeOther
     * @return
     * @throws Exception
     */
    @PostMapping({"/submitUserInfo"})
    public RespEntity submitUserInfo(@RequestParam("firstName") String firstName, @RequestParam("secondName") String secondName, @RequestParam("uid") Long uid, @RequestParam("gender") Integer gender, @RequestParam("country") String country, @RequestParam(value = "postCode", required = false, defaultValue = "") String postCode, @RequestParam("address") String address, @RequestParam("phone") String phone, @RequestParam("birthday") String birthday, @RequestParam("idNumber") String idNumber, @RequestParam("IDCardSingnTime") String IDCardSingnTime, @RequestParam("IDCardOutOfTime") String IDCardOutOfTime, @RequestParam("profession") Integer profession, @RequestParam("property") String property, @RequestParam("annualSalary") Integer annualSalary, @RequestParam("resortType") Integer resortType, @RequestParam("positiveImages") String positiveImages, @RequestParam("oppositeImages") String oppositeImages, @RequestParam("handImages") String handImages, @RequestParam(value = "locationImages",required = false,defaultValue = "WechatIMG51.jpeg") String locationImages, @RequestParam(value = "professionOther",required = false) String professionOther, @RequestParam(value = "propertyOther",required = false) String propertyOther, @RequestParam(value = "resortTypeOther",required = false) String resortTypeOther) throws Exception {
        if (StringUtils.isBlank(professionOther)) {
            professionOther = "";
        }

        if (StringUtils.isBlank(propertyOther)) {
            propertyOther = "";
        }

        if (StringUtils.isBlank(resortTypeOther)) {
            resortTypeOther = "";
        }

        if (locationImages.equals("")) {
            locationImages = "WechatIMG51.jpeg";
        }

        return isValidDate(birthday) && isValidDate(IDCardSingnTime) && isValidDate(IDCardOutOfTime) ? this.userRpc.submitUserInfo(firstName, secondName, uid, gender, country, postCode, address, phone, birthday, idNumber, IDCardSingnTime, IDCardOutOfTime, profession, property, annualSalary, resortType, positiveImages, oppositeImages, handImages, locationImages, professionOther, propertyOther, resortTypeOther) : new RespEntity(2005, "时间格式不对");
    }

    /**
     * 外包用提交认证信息
     * @param firstName
     * @param secondName
     * @param uid
     * @param country
     * @param birthday
     * @param idNumber
     * @param positiveImages
     * @param oppositeImages
     * @param handImages
     * @return
     * @throws Exception
     */
    @PostMapping({"/wbSubmitUserInfo"})
    public RespEntity wbSubmitUserInfo(@RequestParam("firstName") String firstName, @RequestParam("secondName") String secondName, @RequestParam("uid") Long uid, @RequestParam("country") String country, @RequestParam("birthday") String birthday, @RequestParam("idNumber") String idNumber, @RequestParam("positiveImages") String positiveImages, @RequestParam("oppositeImages") String oppositeImages, @RequestParam("handImages") String handImages) throws Exception {
        if (!isValidDate(birthday)) {
            return new RespEntity(2005, "时间格式不对");
        } else {
            AbroadUserAuth abroadUserAuth = AbroadUserAuth.builder().secondName(secondName).uid(uid).country(country).birthday(birthday).idNumber(idNumber).positiveImages(positiveImages).oppositeImages(oppositeImages).handImages(handImages).build();
            return this.userRpc.wbSubmitUserInfo(abroadUserAuth);
        }
    }

    /**
     * 科伟说的啥UV PV 啥的
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping({"/showPvAndUv"})
    public RespEntity showPvAndUv(HttpServletRequest request) throws Exception {
        Map ret = Maps.newHashMap();
        String countKey = "userRegistCount";
        Object userRegistCount = this.redisTemplate.opsForValue().get(countKey);
        if (userRegistCount == null) {
            userRegistCount = this.userRpc.userRegistCount();
            this.redisTemplate.opsForValue().set(countKey, userRegistCount.toString(), 30L, TimeUnit.SECONDS);
        }

        Long count = Long.valueOf(userRegistCount.toString()) + 10000L + 4720L;
        String ipAddress = NetworkUtil.getIpAddress(request);
        String time = (new SimpleDateFormat("yyyy-MM-dd")).format((new Date()).getTime());
        this.redisTemplate.opsForSet().add(time + "UV", new String[]{ipAddress});
        Long UV = this.redisTemplate.opsForSet().size(time + "UV");
        String timeBeforOne = getSpecifiedDayBefore(time);
        this.redisTemplate.delete(timeBeforOne + "UV");
        this.redisTemplate.opsForList().leftPush(time + "PV", ipAddress);
        Long PV = this.redisTemplate.opsForList().size(time + "PV");
        this.redisTemplate.delete(timeBeforOne + "PV");
        String key = "baseMinutes";
        long base = System.currentTimeMillis() / 1000L / 60L;
        String addKey = "addbaseMinutes";
        Object baseMinutes = this.redisTemplate.opsForValue().get(key);
        if (baseMinutes != null) {
            Object addbaseMinutes = this.redisTemplate.opsForValue().get(addKey);
            if (addbaseMinutes == null) {
                addbaseMinutes = "0.0104";
                this.redisTemplate.opsForValue().set(addKey, (String)addbaseMinutes);
            }

            double d = (double)(base - Long.valueOf(baseMinutes.toString())) * Double.valueOf(addbaseMinutes.toString());
            count = count + BigDecimal.valueOf(d).setScale(0, 4).longValue();
            System.out.println("每分钟已经增加：" + count);
        } else {
            this.redisTemplate.opsForValue().set(key, base + "");
        }

        ret.put("count", count);
        ret.put("UV", UV);
        ret.put("PV", PV);
        return RespEntity.success(ret);
    }

    public Boolean checkPwd(String pwd) {
        String regex = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[\\s\\S]{8,16}$";
        return pwd.matches(regex) ? Boolean.TRUE : Boolean.FALSE;
    }

    public static String getSpecifiedDayBefore(String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;

        try {
            date = (new SimpleDateFormat("yyyy-MM-dd")).parse(specifiedDay);
        } catch (ParseException var5) {
            var5.printStackTrace();
        }

        c.setTime(date);
        int day = c.get(5);
        c.set(5, day - 1);
        String dayBefore = (new SimpleDateFormat("yyyy-MM-dd")).format(c.getTime());
        return dayBefore;
    }

    /**
     * 判断时间格式 格式必须为“YYYY-MM-dd”
     * @param str
     * @return
     */
    public static boolean isValidDate(String str) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = formatter.parse(str);
            return str.equals(formatter.format(date));
        } catch (Exception var3) {
            return false;
        }
    }

    @PostMapping({"changeUsername"})
    public RespEntity changeUsername(@RequestParam("username") String username, @RequestParam("uid") Long uid) {
        return username.length() >= 6 && username.length() <= 18 && !isNumeric(username) ? this.userRpc.changeUsername(username, uid) : RespEntity.error(RespCode.COMMON_PARAM_BLANK);
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
    
    /**
	 * 用户历史工单 根据用户uid查找 
	 * @Title: historyWordOrder 
	 * @return RespEntity 
	 * @throws
	 */
	@RequestMapping(value={ "/historyWordOrder" } ,method = {RequestMethod.GET})
	public RespEntity historyWordOrder(@RequestParam(name = "uid", defaultValue = "") String uid) throws Exception {
		log.info("uid:" + uid);

		if (StringUtils.isBlank(uid)) {
			log.info("uid为空,uid={}",uid);
			return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
		}
		

		String user = redisTemplate.opsForValue().get(RedisPrefix.DBUser.getPrefix() + uid);

		if (StringUtils.isBlank(user)) {
			log.info("用户未登陆,uid={}",uid);
			// 用户未登录
			return RespEntity.error(RespCode.USER_NOT_LOGIN);
		}
		// 用户已登录,查询用户信息
		UserDto user2 = baseController.user(uid);
		

		if (StringUtils.isBlank(user2.getEmail())) {
			log.info("用户为绑定邮箱或邮箱格式错误,uid={}",uid);
			return RespEntity.error(RespCode.USER_BIND_EMAIL_FORMAT_ERROR);
		}

		String email = user2.getEmail();
		// 超级管理员邮箱
		String adminemail = "47313253@qq.com";
		// 鉴权token
		String token = "5d5fce33-5647-4e73-8b57-2248475e49b9";
		// 10位时间戳
		long timestamp10 = System.currentTimeMillis() / 1000L;
		// 签名算法
		@SuppressWarnings("deprecation")
		String sign = DigestUtils.shaHex(adminemail + "&" + token + "&" + timestamp10);
		// 用户历史工单url
		String url = "http://chaoex-zh-tw.udesk.cn/open_api_v1/customers/feeds?type=email&content=" + email + "&email="
				+ adminemail + "&timestamp=" + timestamp10 + "&sign=" + sign;
		log.info("用户历史工单url= {}",url);

		return httpGetUtil(url);
	}

	/**
	 * 工单详情 根据工单id查找 
	 * @return RespEntity 
	 * @throws
	 */
	@RequestMapping(value={ "/wordOrderDetails" },method = {RequestMethod.GET})
	public RespEntity wordOrderDetails(@RequestParam(name = "id", defaultValue = "") String id) throws Exception {
		log.info("id={}" + id);

		if (StringUtils.isBlank(id)) {
			log.info("参数错误,id={}",id);
			return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
		}

		String adminemail = "47313253@qq.com";
		String token = "5d5fce33-5647-4e73-8b57-2248475e49b9";
		long timestamp10 = System.currentTimeMillis() / 1000L;
		@SuppressWarnings("deprecation")
		String sign = DigestUtils.shaHex(adminemail + "&" + token + "&" + timestamp10);
		// 获取历史工单里面的一个详细工单信息
		String wordOrderId = id;
		String url = "http://chaoex-zh-tw.udesk.cn/open_api_v1/tickets/detail?id=" + wordOrderId + "&email="
				+ adminemail + "&timestamp=" + timestamp10 + "&sign=" + sign;
		log.info("工单详情 url={}" + url);

		return httpGetUtil(url);
	}

	/**
	 * 文章分类列表详情 ---此方法只获取‘公告’分类 列表
	 *  @Title: Article 
	 *  @return RespEntity 
	 *  @throws
	 */
	@RequestMapping(value={ "/article" },method = {RequestMethod.GET})
	public RespEntity article(@RequestParam(name = "local", defaultValue = "zh_TW") String local) throws Exception {
		String QueryString = null;
		String Secret = null;
		String md5Hex = null;
		
		
		String url = null;
		//1是中文 2是英文
		if("en_US".equals(local)) {//英文
			// 47850是 英文  公告类别的 分组Id
			String typeid = "47850";
			QueryString = "per_page=2&page=1";
			Secret = "4224f559ff881507b04dbac75c6ef1cb";
			md5Hex = DigestUtils.md5Hex(QueryString + "&" + Secret);
			log.info("英文请求,local={}", local);

			url = "http://chaoex-en-us.udesk.cn/api/v1/categories/" + typeid + "/articles?" + QueryString + "&sign="
					+ md5Hex;
		}else {//中文
			// 47901是 中文  公告类别的 分组Id
			String typeid = "47901";
			QueryString = "per_page=2&page=1";
			Secret = "4a622a10e3b83830b58f9ecc5af9f777";
			md5Hex = DigestUtils.md5Hex(QueryString + "&" + Secret);
			log.info("中文请求,local={}", local);
			// 文章分类列表详情url
			url = "http://chaoex-zh-tw.udesk.cn/api/v1/categories/" + typeid + "/articles?" + QueryString + "&sign="
					+ md5Hex;
		}
		
		log.info("公告列表  url={}", url);
		
		RespEntity respEntity = httpGetUtil(url);
		JSONObject attachment = (JSONObject) respEntity.attachment;
		JSONArray contents1 = (JSONArray) attachment.get("contents");
		
		JSONArray contents = new JSONArray();
		Integer [] ids = new Integer[contents1.size()];
		for (int i = 0; i < contents1.size(); i++) {
			JSONObject content = JSON.parseObject(contents1.get(i).toString());
			Integer id = (Integer) content.get("id");
			ids[i]=id;
		}
		
		Arrays.sort(ids);
		
		
		for (int i = ids.length-1; i >=0; i--) {
			for (int j = 0; j < contents1.size(); j++) {
				JSONObject content = JSON.parseObject(contents1.get(j).toString());
				Integer id = (Integer) content.get("id");
				if(ids[i].equals(id)) {
					contents.add(content);
				}
			}
		}
		
		
		attachment.put("contents", contents);
		
		return respEntity;
	}


	/**
	 * 文章详情 根据文章id查找文章详情 
	 * @Title: articleDetails 
	 * @return RespEntity 
	 * @throws
	 */
	@RequestMapping(value={ "/articleDetails" },method = {RequestMethod.GET})
	public RespEntity articleDetails(@RequestParam(name = "id", defaultValue = "") String id,
			@RequestParam(name = "type", defaultValue = "1") Integer type) throws Exception {
		
		log.info("id:" + id);
		if (StringUtils.isBlank(id)) {
			log.info("参数为空,id={}",type);
			return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
		}

		String Secret1 = null;
		String md5Hex1 = null;
		String url = null;
		if(type==2) {//英文    密钥和算法
			Secret1 = "4224f559ff881507b04dbac75c6ef1cb";
			md5Hex1 = DigestUtils.md5Hex(Secret1);
			log.info("英文请求,type={}",type);
			url = "http://chaoex-en-us.udesk.cn/api/v1/articles/" + id + "?sign=" + md5Hex1;
		}else {//中文   密钥和算法
			Secret1 = "4a622a10e3b83830b58f9ecc5af9f777";
			md5Hex1 = DigestUtils.md5Hex(Secret1);
			log.info("中文请求,type={}",type);
			url = "http://chaoex-zh-tw.udesk.cn/api/v1/articles/" + id + "?sign=" + md5Hex1;
		}

		log.info("文章详情   url={}" + url);
	
		
		return httpGetUtil(url);
	}
	
	
	/**
	 * 根据用户uid 查询用户邮箱
	 * 额外参数是udesk要求使用的sha1算法
	 * @Title: findEmail   
	 * @param uid
	 * @param appid
	 * @param appkey
	 * @param email
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/findEmail"},method = {RequestMethod.GET})
	public RespEntity findEmail(@RequestParam(name = "uid", defaultValue = "") String uid,
			@RequestParam(name = "appid", defaultValue = "") String appid,
			@RequestParam(name = "appkey", defaultValue = "") String appkey,
			@RequestParam(name = "email", defaultValue = "") String email) throws Exception {
		log.info("请求参数,uid={},appid={},appkey={},email={}",uid,appid,appkey,email);

		if (StringUtils.isBlank(uid)) {
			log.info("有参数为空,uid={}",uid);
			return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
		}

		String user = redisTemplate.opsForValue().get(RedisPrefix.DBUser.getPrefix() + uid);
		log.info("缓存数据,user = {}",user);
		if (StringUtils.isBlank(user)) {
			log.info("用户未登陆,uid={}",uid);
			// 用户未登录
			return RespEntity.error(RespCode.USER_NOT_LOGIN);
		}
		
		
		
		// 用户已登录,查询用户信息		
		JSONObject u = JSON.parseObject(user);
		String userEmail = (String) u.get("email");
		
		if (StringUtils.isBlank(userEmail)) {
			log.info("用户为绑定邮箱或邮箱格式错误,uid={}",uid);
			return RespEntity.error(RespCode.USER_BIND_EMAIL_FORMAT_ERROR);
		}

		String userName=appid+"&"+email+"&"+userEmail;
		@SuppressWarnings("deprecation")
		String sign = DigestUtils.shaHex(userName+"&"+appkey);

		JSONObject json = new JSONObject();
		json.put("sign", sign);
		json.put("email", userEmail);
		log.info("请求结束  json={}",json);
		return RespEntity.success(json);
	}
	
	
	
	
	
	@RequestMapping(value={"/createWordOrder"},method = {RequestMethod.POST})
	public Map createWordOrder(@RequestParam(name = "userSubject") String userSubject,
			@RequestParam(name = "userContent") String userContent,
			@RequestParam(name = "userEmail") String userEmail,
			@RequestParam(name = "userName") String userName,
			@RequestParam(name = "file") MultipartFile[] file
			) throws Exception {

		Map<String,Object> map = new HashMap<String,Object>();
		
		if(StringUtils.isBlank(userSubject)&&StringUtils.isBlank(userContent)&&StringUtils.isBlank(userEmail)&&StringUtils.isBlank(userName)) {
			//return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
			map.put("status", 0);
			map.put("message", "参数不能为空");
			log.info("map={}",map);
			return map;
		}
		
		int userSubjectLen = String_length(userSubject);
		if(userSubjectLen>255) {
			//return RespEntity.error(RespCode.UDESK_CODE_NULL);
			map.put("status", 1);
			map.put("message", "userSubject长度超过255限制");
			log.info("map={}",map);
			return map;
		}
		int userNameLen = String_length(userName);
		if(userNameLen>255) {
			//return RespEntity.error(RespCode.UDESK_CODE_NULL);
			map.put("status", 2);
			map.put("message", "userName长度超过255限制");
			log.info("map={}",map);
			return map;
		}
		
		boolean isUserEmail = userEmail.matches("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$");
		if(!isUserEmail) {
			map.put("status", 3);
			map.put("message", "邮箱格式不正确");
			log.info("map={}",map);
			return map;
		}
		
		Map m = fileBigSmail(file);
		Integer status = (Integer) m.get("status");
		if(status!=1) {
			log.info("map={}",m);
			return m;
		}
		
		RespEntity customersField = getCustomersField(userEmail);
		JSONObject json = (JSONObject) customersField.getAttachment();
		Integer code = (Integer) json.get("code");
		if(code==1000) {
			log.info("查询客户详情成功=>");
			// 超级管理员邮箱
			String adminemail = "47313253@qq.com";
			// 鉴权token
			String token = "5d5fce33-5647-4e73-8b57-2248475e49b9";
			// 10位时间戳
			long timestamp10 = System.currentTimeMillis() / 1000L;
			// 签名算法
			@SuppressWarnings("deprecation")
			String sign = DigestUtils.shaHex(adminemail + "&" + token + "&" + timestamp10);
			
			String url = "http://chaoex-zh-tw.udesk.cn/open_api_v1/tickets?email="
					+ adminemail + "&timestamp=" + timestamp10 + "&sign=" + sign;

			
			/*if(StringUtils.isBlank(url)) {
				return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
			}*/
			// 创建Httpclient对象
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);
			CloseableHttpResponse response = null;
			String content = null;
			
			
			
			String ticked="{\n" + 
					"    \"ticket\":{\n" + 
					"        \"subject\":\""+userSubject+"\",\n" + 
					"        \"content\":\""+userContent+"\",\n" + 
					"        \"template_id\":3,\n" + 
					"        \"type\":\"email\",\n" + 
					"        \"type_content\":\""+userEmail+"\",\n" + 
					"        \"ticket_field\":{\n" + 
					"        }\n" + 
					"    }\n" + 
					"}";
			httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
			StringEntity stringEntity = new StringEntity(ticked,Charset.forName("UTF-8"));
			stringEntity.setContentEncoding("UTF-8");
			
			httpPost.setEntity(stringEntity);
			
			
			try {
				// 执行请求
				response = httpclient.execute(httpPost);
				// 判断返回状态是否为200
				if (response.getStatusLine().getStatusCode() == 200) {
					content = EntityUtils.toString(response.getEntity(), "UTF-8");
				} else {
					content = EntityUtils.toString(response.getEntity(), "UTF-8");
				}
			} finally {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			}

			JSONObject json1 = JSONObject.parseObject(content);
			log.info("请求结束,第三方返回的数据为  json={}",json1);
			Integer code2 = (Integer) json1.get("code");
			Integer ticket_id = (Integer) json1.get("ticket_id");
			if(code2==1000) {
				log.info("创建工单成功=>  json={}",json1);
				//TODO
				//循环上传文件
				for (int i = 0; i < file.length; i++) {
					Map uploadFile = uploadFile(ticket_id+"", file[i]);
					map.put("uploadFile"+i, uploadFile);
				}
				
				
				map.put("status", 200);
				map.put("message", "创建工单成功");
				map.put("content", json1);
				return map;   
			}
			
			//return  RespEntity.success(json1);
			map.put("status", 45);
			map.put("message", "创建工单失败");
			map.put("content", json1);
			return map;
		}else {
			
			RespEntity createCustomers = createCustomers(userEmail, userName);
			JSONObject json1 = (JSONObject) createCustomers.getAttachment();
			Integer code1 = (Integer) json1.get("code");
			if(code1==1000) {
				log.info("创建客户成功=> json={}",json1);
				Map createWordOrder = createWordOrder(userSubject, userContent, userEmail, userName,file);
				//Map createWordOrder = createWordOrder(userSubject, userContent, userEmail, userName);
				//Integer  status= (Integer) createWordOrder.get("status");
				
				return createWordOrder;
			}else {
				log.info("创建客户失败=>  json={}",json1);
				//return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
				map.put("status", 44);
				map.put("message", "创建客户失败");
				
				return map;
			}
		}
		
		
	}
	
	/**
	 * 创建客户
	 * @Title: createCustomers   
	 * @param email
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public RespEntity createCustomers(String email,String name) throws Exception {

		
		// 超级管理员邮箱
		String adminemail = "47313253@qq.com";
		// 鉴权token
		String token = "5d5fce33-5647-4e73-8b57-2248475e49b9";
		System.out.println(token.length());
		// 10位时间戳
		long timestamp10 = System.currentTimeMillis() / 1000L;
		// 签名算法
		@SuppressWarnings("deprecation")
		String sign = DigestUtils.shaHex(adminemail + "&" + token + "&" + timestamp10);
		// 创建客户  url
		String url = "http://chaoex-zh-tw.udesk.cn/open_api_v1/customers?email="
				+ adminemail + "&timestamp=" + timestamp10 + "&sign=" + sign;
		log.info("创建客户 url= {}",url);


		if(StringUtils.isBlank(url)) {
			return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
		}
		// 创建Httpclient对象
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpResponse response = null;
		String content = null;
		

		String customer="{\n" + 
				"    \"customer\": {\n" + 
				"        \"email\":\""+email+"\",\n" + 
				"        \"nick_name\": \""+name+"\",\n" + 
				"        \"custom_fields\": {\n" + 
				"            \"TextField_25775\": \"普通文本内容\"\n" + 
				"        },\n" + 
				"        \"weixins\": [\n" + 
				"        ]\n" + 
				"    },\n" + 
				"    \"other_emails\": [\n" + 
				"    ]\n" + 
				"}";
		

		httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
		StringEntity stringEntity = new StringEntity(customer,Charset.forName("UTF-8"));
		stringEntity.setContentEncoding("UTF-8");
		httpPost.setEntity(stringEntity);
		
		try {
			// 执行请求
			response = httpclient.execute(httpPost);
			// 判断返回状态是否为200
			if (response.getStatusLine().getStatusCode() == 200) {
				content = EntityUtils.toString(response.getEntity(), "UTF-8");
			} else {
				content = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} finally {
			if (response != null) {
				response.close();
			}
			httpclient.close();
		}
		
		JSONObject json = JSONObject.parseObject(content);
		log.info("请求结束,第三方返回的数据为  json={}",json);
		return  RespEntity.success(json);
	}
	
	
	/**
	 * 获取客户信息
	 * @Title: getCustomersField   
	 * @param email
	 * @return
	 * @throws Exception
	 */
	public RespEntity getCustomersField(String email) throws Exception {
		// 超级管理员邮箱
		String adminemail = "47313253@qq.com";
		// 鉴权token
		String token = "5d5fce33-5647-4e73-8b57-2248475e49b9";
		// 10位时间戳
		long timestamp10 = System.currentTimeMillis() / 1000L;
		// 签名算法
		@SuppressWarnings("deprecation")
		String sign = DigestUtils.shaHex(adminemail + "&" + token + "&" + timestamp10);
		// 查询客户  url
		String url = "http://chaoex-zh-tw.udesk.cn/open_api_v1/customers/get_customer?type=email&content=" + email + "&email="
				+ adminemail + "&timestamp=" + timestamp10 + "&sign=" + sign;
		log.info("查询客户 url= {}",url);

		return httpGetUtil(url);
	}

	
	
	
	/**
	 * 上传附件，对应工单
	 * @Title: uploadFile   
	 * @param ticket_id
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/uploadFile"},method = {RequestMethod.POST})
	public Map uploadFile(String ticket_id,
			MultipartFile file) throws Exception {
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		String uuid = UUID.randomUUID().toString().replace("-", "").substring(0,6);
		String originalFilename = file.getOriginalFilename();
		String extName = originalFilename.substring(originalFilename.lastIndexOf("."));
		String staName = originalFilename.substring(0,originalFilename.lastIndexOf("."));
		String fileName=staName+"-"+uuid+extName;
		// 超级管理员邮箱
		String adminemail = "47313253@qq.com";
		// 鉴权token
		String token = "5d5fce33-5647-4e73-8b57-2248475e49b9";
		System.out.println(token.length());
		// 10位时间戳
		long timestamp10 = System.currentTimeMillis() / 1000L;
		// 签名算法
		@SuppressWarnings("deprecation")
		String sign = DigestUtils.shaHex(adminemail + "&" + token + "&" + timestamp10);
		// 创建客户  url
		String url = "http://chaoex-zh-tw.udesk.cn/open_api_v1/tickets/upload_file?email="
				+ adminemail + "&timestamp=" + timestamp10 + "&sign=" + sign+"&ticket_id="+ticket_id+"&file_name="+fileName;
		log.info("创建客户 url= {}",url);
	
		
		 String result="";
	          
	        try {

	            // 服务器的域名
	            URL url1 = new URL(url);
	            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
	            // 设置为POST情
	            conn.setRequestMethod("POST");
	            // 发送POST请求必须设置如下两行
	            conn.setDoOutput(true);
	            conn.setDoInput(true);
	            conn.setUseCaches(false);
	            // 设置请求头参数
	            conn.setRequestProperty("connection", "Keep-Alive");
	            conn.setRequestProperty("Charsert", "UTF-8");
	            conn.setRequestProperty("Content-Type", "application/octet-stream");

	            OutputStream out = new DataOutputStream(conn.getOutputStream());


	            StringBuilder sb = new StringBuilder();
	         
	            // 将参数头的数据写入到输出流中
	            out.write(sb.toString().getBytes());

	            // 数据输入流,用于读取文件数据
	            DataInputStream in = new DataInputStream(file.getInputStream());
	            byte[] bufferOut = new byte[1024*20];
	            int bytes = 0;
	            // 每次读8KB数据,并且将文件数据写入到输出流中
	            while ((bytes = in.read(bufferOut)) != -1) {
	                out.write(bufferOut, 0, bytes);
	            }
	            in.close();

	            out.flush();
	            out.close();

	            // 定义BufferedReader输入流来读取URL的响应
	            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                result += line; //这里读取的是上边url对应的上传文件接口的返回值，读取出来后，然后接着返回到前端，实现接口中调用接口的方式
	            }
	        } catch (Exception e) {
	            System.out.println("发送POST请求出现异常！" + e);
	            e.printStackTrace();
	            map.put("status", 44);
	    		map.put("message", "上传附件失败");
	    		
	    		return map;
	          
	        }
		map.put("status", 200);
		map.put("message", "上传附件成功");
		return map;
	}
	

	
	
	/**
	 * 判断上传附件的大小   
	 * @Title: fileBigSmail   
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={ "/fileBigSmail" },method = {RequestMethod.POST})
	public Map fileBigSmail(@RequestParam(name = "file") MultipartFile[] file) throws Exception {
		
		Map<String,Object> map = new HashMap<String,Object>();
		if(file==null||file.length==0) {
			map.put("status", 0);
			map.put("message", "文件file参数为空");
			return map;
		}
		int len =file.length;
		if(len==1) {
			long size = file[0].getSize();
			
			if(size>15728640) {
				map.put("status", 2);
				map.put("message", "文件大小不能大于15M");
				log.info("上传错误  {}",map);
				return map;
			}
		}
		
		if(len>10) {
			map.put("status", 4);
			map.put("message", "文件数不能超过10个");
			log.info("上传错误  {}",map);
			return map;
		}
		long sizeSum=0;
		for (int i = 0; i < file.length; i++) {
			sizeSum+= file[i].getSize();
		}
		if(sizeSum>20971520) {
			map.put("status", 3);
			map.put("message", "文件总大小不能大于20M");
			return map;
		}
		
		map.put("status", 1);
		map.put("message", "上传成功");
		
		return map;
	}
	
	
	/**
	 * 判断上传附件 
	 * @Title: fileBigSmail   
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={ "/uploadImg" },method = {RequestMethod.POST})
	public Map uploadImg(@RequestParam(name = "file") MultipartFile file) throws Exception {
		
		Map<String,Object> map = new HashMap<String,Object>();
		if(file.isEmpty()) {
			map.put("status", 0);
			map.put("message", "文件file参数为空");
			return map;
		}
			long size = file.getSize();
			
		if(size>15728640) {
			map.put("status", 2);
			map.put("message", "文件大小不能大于15M");
			log.info("上传错误  {}",map);
			return map;
		}
		
		
		try {
			
		
        //上传文件路径
        String path = uploadUdeskPath+"/";
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0,8);
		String originalFilename = file.getOriginalFilename();
		String extName = originalFilename.substring(originalFilename.lastIndexOf("."));
		String staName = originalFilename.substring(0,originalFilename.lastIndexOf("."));
		String fileName=staName+"-"+uuid+extName;
		log.info("保存路径={}",path+fileName);
		 File newFile=new File(path,fileName);
		
		 file.transferTo(newFile);

        map.put("status", 1);
		map.put("message", "上传成功");
		map.put("filename", fileName);
		return map;
		} catch (Exception e) {
			map.put("status", 44);
			map.put("message", "失败");
			return map;
		}
	}
	
	
	@RequestMapping(value={"/createWordOrder2"},method = {RequestMethod.POST})
	public Map createWordOrder2(@RequestParam(name = "userSubject") String userSubject,
			@RequestParam(name = "userContent") String userContent,
			@RequestParam(name = "userEmail") String userEmail,
			@RequestParam(name = "userName") String userName,
			@RequestParam(name = "files") String[] files
			) throws Exception {

		Map<String,Object> map = new HashMap<String,Object>();
		
		if(StringUtils.isBlank(userSubject)&&StringUtils.isBlank(userContent)&&StringUtils.isBlank(userEmail)&&StringUtils.isBlank(userName)) {
			//return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
			map.put("status", 0);
			map.put("message", "参数不能为空");
			log.info("map={}",map);
			return map;
		}
		
		int userSubjectLen = String_length(userSubject);
		if(userSubjectLen>255) {
			//return RespEntity.error(RespCode.UDESK_CODE_NULL);
			map.put("status", 1);
			map.put("message", "userSubject长度超过255限制");
			log.info("map={}",map);
			return map;
		}
		int userNameLen = String_length(userName);
		if(userNameLen>255) {
			//return RespEntity.error(RespCode.UDESK_CODE_NULL);
			map.put("status", 2);
			map.put("message", "userName长度超过255限制");
			log.info("map={}",map);
			return map;
		}
		
		boolean isUserEmail = userEmail.matches("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$");
		if(!isUserEmail) {
			map.put("status", 3);
			map.put("message", "邮箱格式不正确");
			log.info("map={}",map);
			return map;
		}
		
		/*Map m = fileBigSmail(file);
		Integer status = (Integer) m.get("status");
		if(status!=1) {
			log.info("map={}",m);
			return m;
		}*/
		
		int len = files.length;
		if(len>10) {
			map.put("status", 4);
			map.put("message", "文件不能超过10个");
			log.info("map={}",map);
			return map;
		}
		
		RespEntity customersField = getCustomersField(userEmail);
		JSONObject json = (JSONObject) customersField.getAttachment();
		Integer code = (Integer) json.get("code");
		if(code==1000) {
			log.info("查询客户详情成功=>");
			// 超级管理员邮箱
			String adminemail = "47313253@qq.com";
			// 鉴权token
			String token = "5d5fce33-5647-4e73-8b57-2248475e49b9";
			// 10位时间戳
			long timestamp10 = System.currentTimeMillis() / 1000L;
			// 签名算法
			@SuppressWarnings("deprecation")
			String sign = DigestUtils.shaHex(adminemail + "&" + token + "&" + timestamp10);
			
			String url = "http://chaoex-zh-tw.udesk.cn/open_api_v1/tickets?email="
					+ adminemail + "&timestamp=" + timestamp10 + "&sign=" + sign;

			
			/*if(StringUtils.isBlank(url)) {
				return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
			}*/
			// 创建Httpclient对象
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);
			CloseableHttpResponse response = null;
			String content = null;
			
			
			
			String ticked="{\n" + 
					"    \"ticket\":{\n" + 
					"        \"subject\":\""+userSubject+"\",\n" + 
					"        \"content\":\""+userContent+"\",\n" + 
					"        \"template_id\":3,\n" + 
					"        \"type\":\"email\",\n" + 
					"        \"type_content\":\""+userEmail+"\",\n" + 
					"        \"ticket_field\":{\n" + 
					"        }\n" + 
					"    }\n" + 
					"}";
			httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
			StringEntity stringEntity = new StringEntity(ticked,Charset.forName("UTF-8"));
			stringEntity.setContentEncoding("UTF-8");
			
			httpPost.setEntity(stringEntity);
			
			
			try {
				// 执行请求
				response = httpclient.execute(httpPost);
				// 判断返回状态是否为200
				if (response.getStatusLine().getStatusCode() == 200) {
					content = EntityUtils.toString(response.getEntity(), "UTF-8");
				} else {
					content = EntityUtils.toString(response.getEntity(), "UTF-8");
				}
			} finally {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			}

			JSONObject json1 = JSONObject.parseObject(content);
			log.info("请求结束,第三方返回的数据为  json={}",json1);
			Integer code2 = (Integer) json1.get("code");
			Integer ticket_id = (Integer) json1.get("ticket_id");
			if(code2==1000) {
				log.info("创建工单成功=>  json={}",json1);
				//TODO
				//循环上传文件
				for (int i = 0; i < files.length; i++) {
					Map uploadFile = uploadFile2(ticket_id+"", files[i]);
					map.put("uploadFile"+i, uploadFile);
				}
				
				
				map.put("status", 200);
				map.put("message", "创建工单成功");
				map.put("content", json1);
				return map;   
			}
			
			//return  RespEntity.success(json1);
			map.put("status", 45);
			map.put("message", "创建工单失败");
			map.put("content", json1);
			return map;
		}else {
			
			RespEntity createCustomers = createCustomers(userEmail, userName);
			JSONObject json1 = (JSONObject) createCustomers.getAttachment();
			Integer code1 = (Integer) json1.get("code");
			if(code1==1000) {
				log.info("创建客户成功=> json={}",json1);
				Map createWordOrder = createWordOrder2(userSubject, userContent, userEmail, userName,files);
				//Map createWordOrder = createWordOrder(userSubject, userContent, userEmail, userName);
				//Integer  status= (Integer) createWordOrder.get("status");
				
				return createWordOrder;
			}else {
				log.info("创建客户失败=>  json={}",json1);
				//return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
				map.put("status", 44);
				map.put("message", "创建客户失败");
				
				return map;
			}
		}
		
		
	}
	
	@RequestMapping(value={"/createWordOrder3"},method = {RequestMethod.POST})
	public Map createWordOrder3(@RequestParam(name = "userSubject") String userSubject,
			@RequestParam(name = "userContent") String userContent,
			@RequestParam(name = "userEmail") String userEmail,
			@RequestParam(name = "userName") String userName,
			@RequestParam(name = "filestr") String filestr
			) throws Exception {
		
		
		log.info("filestr="+filestr);
		log.info("userSubject="+userSubject);
		log.info("userContent="+userContent);
		log.info("userEmail="+userEmail);
		log.info("userName="+userName);

		Map<String,Object> map = new HashMap<String,Object>();
		
		String[] files = null;
		if(!StringUtils.isBlank(filestr)) {
			files = filestr.split("<>");
			int len = files.length;
			if(len>10) {
				map.put("status", 4);
				map.put("message", "文件不能超过10个");
				log.info("map={}",map);
				return map;
			}
		}
		
		
		
		if(StringUtils.isBlank(userSubject)&&StringUtils.isBlank(userContent)&&StringUtils.isBlank(userEmail)&&StringUtils.isBlank(userName)) {
			//return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
			map.put("status", 0);
			map.put("message", "参数不能为空");
			log.info("map={}",map);
			return map;
		}
		
		int userSubjectLen = String_length(userSubject);
		if(userSubjectLen>255) {
			//return RespEntity.error(RespCode.UDESK_CODE_NULL);
			map.put("status", 1);
			map.put("message", "userSubject长度超过255限制");
			log.info("map={}",map);
			return map;
		}
		int userNameLen = String_length(userName);
		if(userNameLen>255) {
			//return RespEntity.error(RespCode.UDESK_CODE_NULL);
			map.put("status", 2);
			map.put("message", "userName长度超过255限制");
			log.info("map={}",map);
			return map;
		}
		
		boolean isUserEmail = userEmail.matches("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$");
		if(!isUserEmail) {
			map.put("status", 3);
			map.put("message", "邮箱格式不正确");
			log.info("map={}",map);
			return map;
		}
		
		/*Map m = fileBigSmail(file);
		Integer status = (Integer) m.get("status");
		if(status!=1) {
			log.info("map={}",m);
			return m;
		}*/
		
		
		RespEntity customersField = getCustomersField(userEmail);
		JSONObject json = (JSONObject) customersField.getAttachment();
		Integer code = (Integer) json.get("code");
		if(code==1000) {
			log.info("查询客户详情成功=>");
			// 超级管理员邮箱
			String adminemail = "47313253@qq.com";
			// 鉴权token
			String token = "5d5fce33-5647-4e73-8b57-2248475e49b9";
			// 10位时间戳
			long timestamp10 = System.currentTimeMillis() / 1000L;
			// 签名算法
			@SuppressWarnings("deprecation")
			String sign = DigestUtils.shaHex(adminemail + "&" + token + "&" + timestamp10);
			
			String url = "http://chaoex-zh-tw.udesk.cn/open_api_v1/tickets?email="
					+ adminemail + "&timestamp=" + timestamp10 + "&sign=" + sign;

			
			/*if(StringUtils.isBlank(url)) {
				return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
			}*/
			// 创建Httpclient对象
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);
			CloseableHttpResponse response = null;
			String content = null;
			
			
			
			String ticked="{\n" + 
					"    \"ticket\":{\n" + 
					"        \"subject\":\""+userSubject+"\",\n" + 
					"        \"content\":\""+userContent+"\",\n" + 
					"        \"template_id\":3,\n" + 
					"        \"type\":\"email\",\n" + 
					"        \"type_content\":\""+userEmail+"\",\n" + 
					"        \"ticket_field\":{\n" + 
					"        }\n" + 
					"    }\n" + 
					"}";
			httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
			StringEntity stringEntity = new StringEntity(ticked,Charset.forName("UTF-8"));
			stringEntity.setContentEncoding("UTF-8");
			
			httpPost.setEntity(stringEntity);
			
			
			try {
				// 执行请求
				response = httpclient.execute(httpPost);
				// 判断返回状态是否为200
				if (response.getStatusLine().getStatusCode() == 200) {
					content = EntityUtils.toString(response.getEntity(), "UTF-8");
				} else {
					content = EntityUtils.toString(response.getEntity(), "UTF-8");
				}
			} finally {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			}

			JSONObject json1 = JSONObject.parseObject(content);
			log.info("请求结束,第三方返回的数据为  json={}",json1);
			Integer code2 = (Integer) json1.get("code");
			Integer ticket_id = (Integer) json1.get("ticket_id");
			if(code2==1000) {
				log.info("创建工单成功=>  json={}",json1);
				//TODO
				//循环上传文件
				if(files!=null) {
					for (int i = 0; i < files.length; i++) {
						Map uploadFile = uploadFile2(ticket_id+"", files[i]);
						map.put("uploadFile"+i, uploadFile);
					}
				}
				
				
				map.put("status", 200);
				map.put("message", "创建工单成功");
				map.put("content", json1);
				return map;   
			}
			
			//return  RespEntity.success(json1);
			map.put("status", 45);
			map.put("message", "创建工单失败");
			map.put("content", json1);
			return map;
		}else {
			
			RespEntity createCustomers = createCustomers(userEmail, userName);
			JSONObject json1 = (JSONObject) createCustomers.getAttachment();
			Integer code1 = (Integer) json1.get("code");
			if(code1==1000) {
				log.info("创建客户成功=> json={}",json1);
				Map createWordOrder = createWordOrder2(userSubject, userContent, userEmail, userName,files);
				//Map createWordOrder = createWordOrder(userSubject, userContent, userEmail, userName);
				//Integer  status= (Integer) createWordOrder.get("status");
				
				return createWordOrder;
			}else {
				log.info("创建客户失败=>  json={}",json1);
				//return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
				map.put("status", 44);
				map.put("message", "创建客户失败");
				
				return map;
			}
		}
		
		
	}
	
	
	
	
	/**
	 * 上传附件，对应工单
	 * @Title: uploadFile   
	 * @param ticket_id
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/uploadFile2"},method = {RequestMethod.POST})
	public Map uploadFile2(String ticket_id,
			String fileName) throws Exception {
		
		Map<String,Object> map = new HashMap<String,Object>();
		
	/*	String uuid = UUID.randomUUID().toString().replace("-", "").substring(0,6);
		String originalFilename = file.getOriginalFilename();
		String extName = originalFilename.substring(originalFilename.lastIndexOf("."));
		String staName = originalFilename.substring(0,originalFilename.lastIndexOf("."));
		String fileName=staName+"-"+uuid+extName;*/
		// 超级管理员邮箱
		String adminemail = "47313253@qq.com";
		// 鉴权token
		String token = "5d5fce33-5647-4e73-8b57-2248475e49b9";
		System.out.println(token.length());
		// 10位时间戳
		long timestamp10 = System.currentTimeMillis() / 1000L;
		// 签名算法
		@SuppressWarnings("deprecation")
		String sign = DigestUtils.shaHex(adminemail + "&" + token + "&" + timestamp10);
		// 创建客户  url
		String url = "http://chaoex-zh-tw.udesk.cn/open_api_v1/tickets/upload_file?email="
				+ adminemail + "&timestamp=" + timestamp10 + "&sign=" + sign+"&ticket_id="+ticket_id+"&file_name="+fileName;
		log.info("创建客户 url= {}",url);
	
		
		 String result="";
	          
	        try {

	            // 服务器的域名
	            URL url1 = new URL(url);
	            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
	            // 设置为POST情
	            conn.setRequestMethod("POST");
	            // 发送POST请求必须设置如下两行
	            conn.setDoOutput(true);
	            conn.setDoInput(true);
	            conn.setUseCaches(false);
	            // 设置请求头参数
	            conn.setRequestProperty("connection", "Keep-Alive");
	            conn.setRequestProperty("Charsert", "UTF-8");
	            conn.setRequestProperty("Content-Type", "application/octet-stream");

	            OutputStream out = new DataOutputStream(conn.getOutputStream());


	            StringBuilder sb = new StringBuilder();
	         
	            // 将参数头的数据写入到输出流中
	            out.write(sb.toString().getBytes());
	            
	            File file = new File(uploadUdeskPath+"/"+fileName);
	            FileInputStream fileInputStream = new FileInputStream(file);
	            // 数据输入流,用于读取文件数据
	            DataInputStream in = new DataInputStream(fileInputStream);
	            byte[] bufferOut = new byte[1024*20];
	            int bytes = 0;
	            // 每次读8KB数据,并且将文件数据写入到输出流中
	            while ((bytes = in.read(bufferOut)) != -1) {
	                out.write(bufferOut, 0, bytes);
	            }
	            in.close();

	            out.flush();
	            out.close();

	            // 定义BufferedReader输入流来读取URL的响应
	            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                result += line; //这里读取的是上边url对应的上传文件接口的返回值，读取出来后，然后接着返回到前端，实现接口中调用接口的方式
	            }
	        } catch (Exception e) {
	            System.out.println("发送POST请求出现异常！" + e);
	            e.printStackTrace();
	            map.put("status", 44);
	    		map.put("message", "上传附件失败");
	    		
	    		return map;
	          
	        }
		map.put("status", 200);
		map.put("message", "上传附件成功");
		return map;
	}
	
	
	
	//httpclient  get请求
	public static RespEntity httpGetUtil(String url) throws Exception {
		if(StringUtils.isBlank(url)) {
			return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
		}
		// 创建Httpclient对象
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response = null;
		String content = null;
		try {
			// 执行请求
			response = httpclient.execute(httpGet);
			// 判断返回状态是否为200
			if (response.getStatusLine().getStatusCode() == 200) {
				content = EntityUtils.toString(response.getEntity(), "UTF-8");
			} else {
				content = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		}catch (Exception e) {
			log.info("udesk请求错误");
		} finally {
			if (response != null) {
				response.close();
			}
			httpclient.close();
		}

		JSONObject json = JSONObject.parseObject(content);
		log.info("请求结束");
		return RespEntity.success(json);
	}
	
	
	
	
	
	//字符串的字符长度
	public static int String_length(String value) {
		int valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";
		for (int i = 0; i < value.length(); i++) {
			String temp = value.substring(i, i + 1);
			if (temp.matches(chinese)) {
				valueLength += 2;
			} else {
				valueLength += 1;
			}
		}
		
		return valueLength;
	}

	
}
