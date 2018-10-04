//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.user.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.ASMUtils;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.xyhj.lian.exception.RedisException;
import com.xyhj.lian.util.CreateImageCode;
import com.xyhj.lian.util.MD5Util;
import com.xyhj.lian.util.RedisPrefix;
import com.xyhj.lian.util.RespCode;
import com.xyhj.lian.util.RespEntity;
import com.xyhj.lian.util.VerifyUtil;
import com.xyhj.lian12.balance.dto.CtCustomerAccountCtb;
import com.xyhj.lian12.balance.rpc.BalanceRpc;
import com.xyhj.lian12.base.config.BaseController;
import com.xyhj.lian12.upload.util.UploadLocal;
import com.xyhj.lian12.user.dto.IdentifyVo;
import com.xyhj.lian12.user.dto.InvitedVo;
import com.xyhj.lian12.user.dto.QRCodeUtil;
import com.xyhj.lian12.user.dto.UserDto;
import com.xyhj.lian12.user.interfaces.UserRpc;
import com.xyhj.lian12.util.*;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户相关接口
 */
@RestController
@RequestMapping({ "/user" })
public class UserController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private UserRpc userRpc;
	@Autowired
	private BalanceRpc balanceRpc;

	@Value("${upload.image.path}")
	private String uploadPath;
	static List<String> languageList = Arrays.asList("en_US", "zh_TW");

	@Value("${oss.id}")
	private String ACCESS_KEY_ID;
	// 阿里云API的密钥Access Key Secret
	@Value("${oss.secret}")
	private String ACCESS_KEY_SECRET;
	// 阿里云API的bucket名称
	@Value("${oss.bucket}")
	private String BACKET_NAME;

	public UserController() {
	}

	/**
	 * 登陆接口
	 * 
	 * @param uname
	 * @param pwd
	 * @param passCard
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "/loginRobot" })
	public RespEntity loginRobot(@RequestParam("uname") String uname, @RequestParam("pwd") String pwd,
			@RequestParam(value = "passCard", required = false) String passCard, HttpServletRequest request)
			throws Exception {
		return !passCard.equals("3907d9aef154cc4533b41cd56ac04afa") ? RespEntity.error(RespCode.SYSTEM_ERROR)
				: this.userRpc.login(uname, pwd, 1, NetworkUtil.getIpAddress(request));
	}

	@PostMapping({ "logout" })
	public RespEntity logout(HttpServletRequest request, @RequestParam("uid") Long uid) throws Exception {
		String requestHeader = request.getHeader("User-Agent");
		Integer source = 1;
		if (BaseConstant.isMobileDevice(requestHeader)) {
			source = 2;
		}

		UserDto user = this.user(request);
		return this.userRpc.logout(uid, user.getEmail(), source);
	}

	@PostMapping({ "/updateFdPwdEnabled" })
	RespEntity updateFdPwdEnabled(HttpServletRequest request, @RequestParam("uid") Long uid,
			@RequestParam("enabled") Integer enabled, @RequestParam("fdPwd") String fdPwd) throws Exception {
		UserDto user = this.user(request);
		// 判断交易密码是否正确
		return !fdPwd.equalsIgnoreCase(user.getFdPassword()) ? RespEntity.error(RespCode.USER_FDPASSWORD_NOTMATCH)
				: this.userRpc.updateFdPwdEnabled(uid, enabled);
	}

	@PostMapping({ "/selectFdPwdEnabled" })
	RespEntity selectFdPwdEnabled(HttpServletRequest request) throws Exception {
		UserDto user = this.user(request);
		return this.userRpc.selectFdPwdEnabled(user.getUid());
	}

	@RequestMapping({ "/showOrderList" })
	RespEntity showOrderList(HttpServletRequest request,
			@RequestParam(value = "status", required = false, defaultValue = "0") int status,
			@RequestParam(value = "start", required = false, defaultValue = "1") Integer start,
			@RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
			@RequestParam(value = "currencyId", required = false, defaultValue = "2") Integer currencyId,
			@RequestParam(value = "baseCurrencyId", required = false, defaultValue = "1") Integer baseCurrencyId)
			throws Exception {
		// 计算从第几个开始 前端的start传页数
		Integer first = (start - 1) * Integer.valueOf(size);
		UserDto user = this.user(request);
		return this.userRpc.showOrderList(user.getUuid(), status, first, size, currencyId, baseCurrencyId);
	}

	@PostMapping({ "/trOrderListByCustomer" })
	RespEntity trOrderListByCustomer(HttpServletRequest request,
			@RequestParam(value = "type", required = false, defaultValue = "1") Integer type,
			@RequestParam(value = "buyOrSell", required = false) Integer buyOrSell,
			@RequestParam(value = "status", required = false, defaultValue = "10") Integer status,
			@RequestParam(value = "beginTime", required = false) String beginTime,
			@RequestParam(value = "endTime", required = false) String endTime,
			@RequestParam("currencyId") Integer currencyId,
			@RequestParam(value = "baseCurrencyId", required = false, defaultValue = "1") Integer baseCurrencyId,
			@RequestParam("start") Integer start, @RequestParam("size") Integer size,
			@RequestParam(value = "priceType", required = false, defaultValue = "0") Integer priceType)
			throws Exception {
		UserDto user = this.user(request);
		return this.userRpc.trOrderListByCustomer(user.getUuid(), type, buyOrSell, status, beginTime, endTime,
				currencyId, baseCurrencyId, start, size, priceType);
	}

	
	 /**
     * 根据委托单id批量查询订单信息
     * orderIds='xxxx','xxxx','xxxx'
     */
	@PostMapping({ "/trOrderByOrderIds" })
	RespEntity trOrderByOrderIds(HttpServletRequest request,
			@RequestParam(value = "orderIds", required = true) String  orderIds,
    		@RequestParam(value = "currencyId", required = true) Integer currencyId,
    		@RequestParam(value = "baseCurrencyId", required = true) Integer baseCurrencyId)throws Exception {
		log.info("orderIds={},currencyId={},baseCurrencyId={}",orderIds,currencyId,baseCurrencyId);
		return this.userRpc.trOrderByOrderIds(orderIds,currencyId,baseCurrencyId);
	}	
		
	
	
	
	
	@GetMapping(value = "trOrderDetail")
	RespEntity trOrderListByCustomer(@RequestParam(value = "orderNo") String orderNo) throws Exception {
		return RespEntity.success(userRpc.trOrderByOrdeNo(orderNo));
	}

	/**
	 * 获取用户成交记录
	 * 
	 * @param request
	 * @param buyOrSell
	 * @param status
	 * @param currencyId
	 * @param baseCurrencyId
	 * @param start
	 * @param size
	 * @param priceType
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "/trTradeListByCustomer" })
	RespEntity trTradeListByCustomer(HttpServletRequest request,
			@RequestParam(value = "type", required = false, defaultValue = "1") Integer type,
			@RequestParam(value = "status", required = false, defaultValue = "10") Integer status,
			@RequestParam(value = "buyOrSell", required = false) Integer buyOrSell,
			@RequestParam("currencyId") Integer currencyId,
			@RequestParam(value = "baseCurrencyId", required = false, defaultValue = "1") Integer baseCurrencyId,
			@RequestParam("size") Integer size, @RequestParam("start") Integer start,
			@RequestParam(value = "priceType", required = false, defaultValue = "0") Integer priceType)
			throws Exception {
		UserDto user = this.user(request);
		return this.userRpc.trTradeListByCustomer(user.getUuid(), type, status, buyOrSell, currencyId, baseCurrencyId,
				start, size, priceType);
	}

	/**
	 * 签到接口
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "/signin" })
	public RespEntity signin(HttpServletRequest request) throws Exception {
		UserDto user = this.user(request);
		return this.userRpc.signin(user.getUuid());
	}

	/**
	 * 我的签到列表
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "/mySingins" })
	public RespEntity mySingins(HttpServletRequest request) throws Exception {
		UserDto user = this.user(request);
		return this.userRpc.mySingins(user.getUuid());
	}

	/**
	 * 验证用户名
	 * 
	 * @param uname
	 * @param codeid
	 * @param vercode
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "/verUname" })
	public RespEntity verUname(@RequestParam("uname") String uname, @RequestParam("codeid") String codeid,
			@RequestParam("vercode") String vercode) throws Exception {
		if (StringUtils.isBlank(uname)) {
			return RespEntity.error(RespCode.USER_PWDORUNAME_ERROR);
		} else {
			String imgCodeFromDB = (String) this.redisTemplate.opsForValue()
					.get(RedisPrefix.DBIMG.getPrefix() + codeid);
			if (StringUtils.isBlank(imgCodeFromDB)) {
				return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
			} else {
				return !vercode.toLowerCase().equals(imgCodeFromDB) ? RespEntity.error(RespCode.USER_IMGCODE_ERROR)
						: this.userRpc.verUname(uname);
			}
		}
	}

	public Boolean checkPwd(String pwd) {
		if (pwd.length() >= 6 && pwd.length() <= 18 && !pwd.matches("[a-zA-Z]+") && !pwd.matches("^\\d\\d*$")) {
			Pattern p = Pattern.compile("[一-龥]");
			Matcher m = p.matcher(pwd);
			return m.find() ? Boolean.FALSE : Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	/**
	 * 获取验证码
	 * 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "/getimg" })
	public RespEntity getimg(HttpServletResponse response) throws Exception {
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0L);
		CreateImageCode vCode = new CreateImageCode(100, 30, 5, 10);
		log.info("[验证码]{}", vCode.getCode().toLowerCase());
		String baseCode = CreateImageCode.imgToBase(vCode.getBuffImg());
		String codeUUID = com.xyhj.lian.util.StringUtils.getUUID();
		// 将验证码数据内容加入redis
		this.redisTemplate.opsForValue().set(RedisPrefix.DBIMG.getPrefix() + codeUUID, vCode.getCode().toLowerCase(),
				300L, TimeUnit.SECONDS);
		Map<String, Object> attachment = new HashMap();
		attachment.put("codeUUID", codeUUID);
		attachment.put("IMGCode", baseCode);
		return RespEntity.success(attachment);
	}

	/**
	 * 更新用户认证资料 此接口暂时只能调用一次
	 * 
	 * @param name
	 * @param country
	 * @param idNumber
	 * @param positiveImages
	 * @param oppositeImages
	 * @param handImages
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "auth" })
	public RespEntity submitAuth(@RequestParam("name") String name,
			@RequestParam(value = "country", required = false) String country,
			@RequestParam("idNumber") String idNumber,
			@RequestParam(value = "positiveImages", required = false) String positiveImages,
			@RequestParam(value = "oppositeImages", required = false) String oppositeImages,
			@RequestParam(value = "handImages", required = false) String handImages, HttpServletRequest request)
			throws Exception {
		UserDto user = this.user(request);
		// 校验参数是否正确
		if (VerifyUtil.isChineseText(name) && VerifyUtil.isIdNumber(idNumber)) {
			// 判断是否已经提交过
			if (user.getIsAuthPrimary() == 1) {
				return RespEntity.error(RespCode.USER_AUTH_REVIEW);
			} else if (user.getIsAuthPrimary() == 2) {
				return RespEntity.error(RespCode.USER_AUTH_CHECK_OK);
			} else {
				return user.getIsAuthPrimary() == -1 ? RespEntity.error(RespCode.USER_AUTH_REFUSED)
						: this.userRpc.submitAuth(name, country, idNumber, positiveImages, oppositeImages, handImages,
								user.getUid(), user.getUuid());
			}
		} else {
			return RespEntity.error(RespCode.USER_AUTH_UPDATE_ERROR);
		}
	}

	/**
	 * 获取认证信息
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "authInfo" })
	public RespEntity authInfo(HttpServletRequest request) throws Exception {
		UserDto user = this.user(request);
		RespEntity respEntity = this.userRpc.authInfo(user.getUuid());
		return respEntity;
	}

	/**
	 * 我的登录历史
	 * 
	 * @param type
	 * @param start
	 * @param size
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "myLogins" })
	public RespEntity myLogins(@RequestParam("type") Integer type, @RequestParam("start") Integer start,
			@RequestParam("size") Integer size, @RequestParam("uid") Long uid) throws Exception {
		RespEntity respEntity = this.userRpc.myLogins(uid, type, start, size);
		return respEntity;
	}

	/**
	 * 获取用户认证设置资料
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "userInfo" })
	public RespEntity userInfo(HttpServletRequest request) throws Exception {
		UserDto user = this.user(request);
		RespEntity respEntity = this.userRpc.userInfo(user.getUuid());
		return respEntity;
	}

	/**
	 * 绑定常用地址
	 * 
	 * @param request
	 * @param country
	 * @param city
	 * @param town
	 * @param location
	 * @param phone
	 * @param name
	 * @param postCode
	 * @param isDefault
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "address" })
	public RespEntity bindAddress(HttpServletRequest request, @RequestParam("country") String country,
			@RequestParam("city") String city, @RequestParam("town") String town,
			@RequestParam("location") String location, @RequestParam("phone") String phone,
			@RequestParam("name") String name, @RequestParam(value = "postCode", defaultValue = "0000") String postCode,
			@RequestParam(value = "isDefault", required = false, defaultValue = "1") Integer isDefault)
			throws Exception {
		UserDto user = this.user(request);
		return this.userRpc.bindAddress(user.getUuid(), country, city, town, location, phone, name, postCode,
				isDefault);
	}

	@PostMapping({ "/update/address" })
	public RespEntity updateAddress(HttpServletRequest request, @RequestParam("uaid") Long uaid,
			@RequestParam("country") String country, @RequestParam("city") String city,
			@RequestParam("town") String town, @RequestParam("location") String location,
			@RequestParam("phone") String phone, @RequestParam("name") String name,
			@RequestParam("postCode") String postCode,
			@RequestParam(value = "isDefault", required = false, defaultValue = "1") Integer isDefault)
			throws Exception {
		UserDto user = this.user(request);
		return this.userRpc.updateAddress(user.getUuid(), uaid, country, city, town, location, phone, name, postCode,
				isDefault);
	}

	/**
	 * 我的积分列表
	 * 
	 * @param request
	 * @param start
	 * @param size
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "myPoints" })
	public RespEntity myPoints(HttpServletRequest request,
			@RequestParam(value = "start", required = false, defaultValue = "1") Integer start,
			@RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
			@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime) throws Exception {
		UserDto u = this.user(request);
		RespEntity respEntity = this.userRpc.myPoints(u.getUuid(), start, size, startTime, endTime);
		return respEntity;
	}

	/**
	 * 个人中心用户信息接口
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "/personalInfo" })
	public RespEntity personalInfo(HttpServletRequest request) throws Exception {
		UserDto user = this.user(request);
		if (user == null) {
			return RespEntity.error(RespCode.USER_FINDPWD_NOUSER);
		} else {
			RespEntity respEntity = this.userRpc.personalInfo(user.getUid());
			return respEntity;
		}
	}

	/**
	 * 发送短信 注册
	 * 
	 * @param phone
	 * @param vercode
	 * @param codeid
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "/sendMsg" })
	public RespEntity sendMsg(@RequestParam("phone") String phone, @RequestParam("vercode") String vercode,
			@RequestParam("codeid") String codeid) throws Exception {
		String imgCode = (String) this.redisTemplate.opsForValue().get(RedisPrefix.DBIMG.getPrefix() + codeid);
		// 检查验证码是否正确
		if (StringUtils.isBlank(imgCode)) {
			return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
		} else if (!vercode.toLowerCase().equals(imgCode)) {
			return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
		} else if (StringUtils.isBlank(phone)) {
			return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
		} else {
			return !VerifyUtil.isPhoneLegal(phone) ? RespEntity.error(RespCode.USER_PHONE_ERROR)
					: this.userRpc.sendMsg(phone);
		}
	}

	/**
	 * 发送短信 注册 2.0只有加入滑动验证
	 * 
	 * @param phone
	 * @param sliderToken
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "/sendMsgV2" })
	public RespEntity sendMsgV2(@RequestParam("phone") String phone,
			@RequestParam(value = "sliderToken") String sliderToken) throws Exception {
		if (StringUtils.isBlank(phone)) {
			return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
		} else if (!SliderVerification.verifyToken(sliderToken)) {
			return new RespEntity(572, "滑动验证失败");
			// return RespEntity.error(RespCode.SLIDER_VERIFY_ERROR);
		} else {
			return !VerifyUtil.isPhoneLegal(phone) ? RespEntity.error(RespCode.USER_PHONE_ERROR)
					: this.userRpc.sendMsg(phone);
		}
	}

	/**
	 * 登录后的重置密码
	 * 
	 * @param pwd
	 * @param uid
	 * @param newPwd
	 * @param smsCode
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "/rePwdWithLogin" })
	public RespEntity rePwdWithLogin(@RequestParam("pwd") String pwd, @RequestParam("uid") Long uid,
			@RequestParam("newPwd") String newPwd, @RequestParam("smsCode") String smsCode, HttpServletRequest request)
			throws Exception {
		if (!StringUtils.isBlank(pwd) && !StringUtils.isBlank(newPwd) && !StringUtils.isBlank(smsCode)) {
			if (!this.checkPwd(newPwd)) {
				return RespEntity.error(RespCode.USER_PASSWORD_CHECKED);
			} else {
				UserDto userDto = this.user(request);
				String codeInDB = (String) this.redisTemplate.opsForValue()
						.get(RedisPrefix.DBSMS.getPrefix() + userDto.getPhone());
				if (!StringUtils.isBlank(codeInDB) && smsCode.equals(codeInDB)) {
					if (!userDto.getPassword().toLowerCase().equals(pwd)) {
						return RespEntity.error(RespCode.USER_RESRTPWD_FAIL);
					} else {
						String fdPwd = MD5Util.encryptFdPwd(newPwd, uid);
						// 判断登录密码是否与交易密码相同
						if (fdPwd.equals(userDto.getFdPassword())) {
							return RespEntity.error(RespCode.USER_FDANDLOGINSAME_ERROR);
						} else {
							String afterencrypt = MD5Util.encryptPwd(newPwd);
							return this.userRpc.rePwdWithLogin(uid, afterencrypt, userDto.getPhone());
						}
					}
				} else {
					return RespEntity.error(RespCode.USER_AUTHCODE_ERROR);
				}
			}
		} else {
			return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
		}
	}

	/**
	 * 右上角通知接口
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "/getScores" })
	public RespEntity getScores(HttpServletRequest request) throws Exception {
		UserDto userDto = this.user(request);
		return this.userRpc.getScores(userDto.getUid());
	}

	/**
	 * 发送短信 重置密码
	 * 
	 * @param uid
	 * @param vercode
	 * @param codeid
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "sendMsgWithUid" })
	public RespEntity sendMsgWithUid(@RequestParam("uid") Long uid, @RequestParam("vercode") String vercode,
			@RequestParam("codeid") String codeid, @RequestParam("type") String type) throws Exception {
		String imgCode = (String) this.redisTemplate.opsForValue().get(RedisPrefix.DBIMG.getPrefix() + codeid);
		// 检查验证码是否正确
		if (StringUtils.isBlank(imgCode)) {
			return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
		} else if (!vercode.toLowerCase().equals(imgCode)) {
			return RespEntity.error(RespCode.USER_IMGCODE_ERROR);
		} else {
			RespEntity respEntity = this.userRpc.selectByUid(uid);
			UserDto user = new UserDto();
			if (respEntity != null && respEntity.getStatus() == 200) {
				Object attachment = respEntity.getAttachment();
				String jsonString = JSON.toJSONString(attachment);
				user = (UserDto) JSON.parseObject(jsonString, UserDto.class);
			}

			return this.userRpc.sendMsgWithUid(user.getPhone(), type);
		}
	}

	@PostMapping({ "totalUsers" })
	public RespEntity totalReigisterUsers() {
		RespEntity respEntity = this.userRpc.totalReigisterUsers();
		return respEntity;
	}

	@PostMapping({ "queryPhone" })
	public RespEntity queryPhone(@RequestParam("phone") String phone) {
		return this.userRpc.queryPhone(phone);
	}

	@PostMapping({ "checkOldPwd" })
	public RespEntity checkOldPwd(@RequestParam("pwd") String pwd, @RequestParam("uid") Long uid)
			throws SQLException, RedisException {
		return !StringUtils.isBlank(pwd) && uid != null ? this.userRpc.checkOldPwd(pwd, uid)
				: RespEntity.error(RespCode.COMMON_PARAM_BLANK);
	}

	@PostMapping({ "reName" })
	public RespEntity reName(@RequestParam("newName") String newName, @RequestParam("uid") Long uid,
			HttpServletRequest request) throws Exception {
		if (StringUtils.isBlank(newName)) {
			return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
		} else if (!ASMUtils.checkName(newName)) {
			return RespEntity.error(RespCode.USER_UNAME_NOTMATCH);
		} else {
			UserDto user = this.user(request);
			if (user.getUname().equals(newName)) {
				return RespEntity.error(RespCode.USER_UNAME_ISSAME);
			} else {
				RespEntity queryRet = this.userRpc.queryUname(newName);
				return queryRet.getStatus() != 200 ? RespEntity.error(RespCode.USER_USEREXIST)
						: this.userRpc.reName(newName, uid);
			}
		}
	}

	/**
	 * 初级认证接口
	 * 
	 * @param name
	 * @param country
	 * @param idNumber
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "/submitJuniorAuth" })
	public RespEntity submitJuniorAuth(@RequestParam("name") String name,
			@RequestParam(value = "country", required = false) String country,
			@RequestParam("idNumber") String idNumber, @RequestParam("uid") Long uid) throws Exception {
		if (StringUtils.isBlank(name)) {
			return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
		} else {
			return StringUtils.isBlank(idNumber) ? RespEntity.error(RespCode.COMMON_PARAM_BLANK)
					: this.userRpc.submitJuniorAuth(name, country, idNumber, uid);
		}
	}

	/**
	 * 查询等记认证及其状态接口
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "selectAuthLevel" })
	public RespEntity selectAuthLevel(HttpServletRequest request) throws Exception {
		log.info("selectAuthLevel-------12lian----");
		UserDto userDto = this.user(request);
		log.info("查询等级认证userDto.getUuid():" + userDto.getUuid());
		return this.userRpc.selectAuthLevel(userDto.getUuid());
	}

	/**
	 * 高级认证接口
	 * 
	 * @param gender
	 * @param nation
	 * @param occupation
	 * @param birthday
	 * @param location
	 * @param positiveImage
	 * @param oppositeImage
	 * @param handImage
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "submitSeniorAuth" })
	public RespEntity submitSeniorAuth(@RequestParam("gender") Integer gender, @RequestParam("nation") String nation,
			@RequestParam("occupation") String occupation, @RequestParam("birthday") String birthday,
			@RequestParam("location") String location, @RequestParam("positiveImage") String positiveImage,
			@RequestParam("oppositeImage") String oppositeImage, @RequestParam("handImage") String handImage,
			@RequestParam("uid") Long uid) throws Exception {
		return this.userRpc.submitSeniorAuth(gender, nation, occupation, birthday, location, positiveImage,
				oppositeImage, handImage, uid);
	}

	/**
	 * 文件上传接口
	 * 
	 * @param positiveImage
	 * @param oppositeImage
	 * @param handImage
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "uploadImage" })
	public RespEntity uploadImage(@RequestParam("positiveImage") MultipartFile positiveImage,
			@RequestParam("oppositeImage") MultipartFile oppositeImage,
			@RequestParam("handImage") MultipartFile handImage) throws Exception {
		InputStream in1 = positiveImage.getInputStream();
		InputStream in2 = oppositeImage.getInputStream();
		InputStream in3 = handImage.getInputStream();
		// 名称+时间戳+随机数 避免重复覆盖
		String uuid = ConstantUtil.getUuid2();
		String positiveImageName = uuid + "positiveImageName.jpg";
		String oppositeImageName = uuid + "oppositeImageName.jpg";
		String handImageName = uuid + "handImageName.jpg";
		log.info("上传图片三张---path:" + this.uploadPath);
		UploadLocal.listFileNames(positiveImageName, oppositeImageName, handImageName, in1, in2, in3, this.uploadPath);
		JSONObject json = new JSONObject();
		json.put("positiveImage", positiveImageName);
		json.put("oppositeImage", oppositeImageName);
		json.put("handImage", handImageName);
		return RespEntity.success(json);
	}

	/**
	 * 文件上传接口单张
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "uploadImageSingle" })
	public RespEntity uploadImage(MultipartFile file) throws Exception {
		InputStream in = file.getInputStream();
		String finaName = ConstantUtil.getUuid2() + ".jpg";
		log.info("上传图片单张--path:" + this.uploadPath);
		UploadLocal.listFileNames(finaName, in, this.uploadPath);
		return RespEntity.success(finaName);
	}

	@PostMapping({ "/getUserIdentifyInfoByUUid" })
	public IdentifyVo getUserIdentifyInfoByUUid(String uuid) throws Exception {
		return this.userRpc.getUserIdentifyInfoByUUid(uuid);
	}

	@PostMapping({ "/getInvitedInfo" })
	public RespEntity getInvitedInfo(@RequestParam("uid") Long uid, @RequestParam("page") Integer page,
			@RequestParam("size") Integer size, HttpServletRequest request) throws Exception {
		InvitedVo invitedInfo = this.userRpc.getInvitedInfo(uid, page, size);
		String iniviteId = invitedInfo.getInviteId();
		String url = request.getHeader("host") + "/register/" + iniviteId;
		String base64Url = QRCodeUtil.base64(url);
		invitedInfo.setBaseImg(base64Url);
		return RespEntity.success(invitedInfo);
	}

	/**
	 * 设置语言
	 * 
	 * @param language
	 * @param request
	 * @return
	 */
	@RequestMapping({ "/setLanguage/{language}" })
	public RespEntity setLanguage(@PathVariable String language, HttpServletRequest request) {
		if (languageList.contains(language)) {
			request.getSession().setAttribute("language", language);
			return RespEntity.success("");
		} else {
			return new RespEntity(500, "不支持此语言");
		}
	}

	@PostMapping({ "/showNodeMessage" })
	public RespEntity showNodeMessage(@RequestParam("uid") Long uid,
			@RequestParam(value = "thisMethodType", defaultValue = "0") String thisMethodType) {

		if (StringUtils.isBlank(uid + "")) {
			return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
		}

		log.info("uid=" + uid);
		// 通过uid查找用户信息
		RespEntity respEntity = this.userRpc.selectByUid(uid);
		Object attachment = respEntity.getAttachment();
		String jsonString = JSON.toJSONString(attachment);
		UserDto user = (UserDto) JSON.parseObject(jsonString, UserDto.class);
		log.info("user=" + user);
		// 通过uuid查找用户账户 cashAmount8
		CtCustomerAccountCtb ccac = this.balanceRpc.getCustomerAccount(user.getUuid());
		BigDecimal cashAmount = ccac.getCashAmount();
		BigDecimal cashAmount8 = cashAmount.setScale(8, BigDecimal.ROUND_HALF_UP);
		log.info("cashAmount8=" + cashAmount8);
		// 可申请节点数量 numNodes
		BigDecimal decimal = cashAmount.divideToIntegralValue(new BigDecimal(100000));
		String numNodes = decimal.setScale(0).toString();
		log.info("numNodes=" + numNodes);

		// 取当前日期的后31天 time31
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();// 使用默认时区和语言环境获得一个日历。
		cal.add(Calendar.DAY_OF_MONTH, +31);// 取当前日期的后31天.
		Date time = cal.getTime();
		String time31 = df.format(time);
		log.info("time31=" + time31);

		
		
		Date date = new Date();
		String t2 = "2018-07-09";
		SimpleDateFormat ss1 = new SimpleDateFormat("yyyy-MM-dd");
		Date parse = null;
		try {
			parse = ss1.parse(t2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (date.getTime() < parse.getTime()) {
			time31 = "2018-08-09";
		}

		// 平台今日节点收益
		String t = "2018-08-09";
		SimpleDateFormat ss = new SimpleDateFormat("yyyy-MM-dd");
		Date pdate = null;
		try {
			pdate = ss.parse(t);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		BigDecimal m = new BigDecimal(500000);
		Calendar cale = Calendar.getInstance();
		BigDecimal nodeProfit = null;
		RespEntity respE = null;
		Date time3 = cale.getTime();
		String tt = ss.format(time3);
		String yesterday_profit = this.redisTemplate.opsForValue().get("yesterday_profit");
		
		if(yesterday_profit==null) {
			

				Date time2 = cale.getTime();
				
				log.info("1time.getTime()= {} <=  pdate.getTime()={}", cale.getTime(), pdate.getTime());
				if (time2.getTime() <= pdate.getTime()) {
					log.info("1");
					respE = this.balanceRpc.findNodeAllCount();
					if (respE.status == 200) {
						Integer count = (Integer) respE.attachment;
						if (count.equals(0)) {
							nodeProfit = new BigDecimal(500000);
						} else {
							nodeProfit = m.divide(new BigDecimal(count), 8, BigDecimal.ROUND_DOWN);
						}
					}

				} else {
					log.info("2");
					RespEntity resp = this.balanceRpc.findNodeCount();
					if (resp.status == 200) {
						Integer count = (Integer) resp.attachment;
						if (count.equals(0)) {
							RespEntity findNodeAllCount = this.balanceRpc.findNodeAllCount();
							Integer co = (Integer) findNodeAllCount.attachment;
							if (co.equals(0)) {
								nodeProfit = m;
							} else {
								nodeProfit = m.divide(new BigDecimal(co), 8, BigDecimal.ROUND_DOWN);
								// nodeProfit=new BigDecimal(500000);
							}
						} else {
							nodeProfit = m.divide(new BigDecimal(count), 8, BigDecimal.ROUND_DOWN);
						}
					}

				}
			
			
		}else {
			
			nodeProfit=new BigDecimal(yesterday_profit);
		}
		
		
		
		log.info("nodeProfit=" + nodeProfit);

		// 可解锁节点数量
		RespEntity resp = this.balanceRpc.findNodeCountByUid(uid);
		Integer unLockNodeCount = 0;
		if (resp.status == 200) {
			unLockNodeCount = (Integer) resp.attachment;
		}

		log.info("unLockNodeCount=" + unLockNodeCount);

		// 全网锁仓的节点总数量
		if (respE == null) {
			respE = this.balanceRpc.findNodeAllCount();
		}

		// Integer allNode =0;
		Integer allNode = (Integer) respE.attachment;

		log.info("allNode=" + allNode);

		JSONObject json = new JSONObject();
		json.put("cashAmount", cashAmount8);
		json.put("numNodes", numNodes);
		json.put("profitTime", time31);
		json.put("nodeProfit", nodeProfit);
		json.put("unLockNodeCount", unLockNodeCount);
		json.put("allNode", allNode);

		if ("1".equals(thisMethodType)) {
			json.put("fdPassword", user.getFdPassword());
			json.put("uuid", user.getUuid());
		}
		log.info("json=" + json);
		return RespEntity.success(json);
	}

	@PostMapping({ "/node" })
	public RespEntity node(@RequestParam("uid") Integer uid, @RequestParam("nodeType") String nodeType,
			@RequestParam("fdPwd") String fdPwd,
			@RequestParam(value = "lockNodeNum", defaultValue = "0") String lockNodeNum,
			@RequestParam(value = "unLockNodeNum", defaultValue = "0") String unLockNodeNum,
			@RequestParam(value = "type", defaultValue = "0") String type) {

		// return new RespEntity(798,"此功能维护中");

		log.info("uid=" + uid);
		log.info("nodeType=" + nodeType);
		log.info("fdPwd=" + fdPwd);
		log.info("lockNodeNum=" + lockNodeNum);
		log.info("unLockNodeNum=" + unLockNodeNum);
		log.info("type=" + type);

		if (StringUtils.isBlank(uid + "")) {

			return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
		}

		if (StringUtils.isBlank(fdPwd)) {
			// 交易密码不可为空
			return RespEntity.error(RespCode.NODE_FDPWD_NULL);
		}

		RespEntity respEntity = showNodeMessage(Long.valueOf(uid), "1");
		JSONObject json = (JSONObject) respEntity.attachment;
		String fdPassword = (String) json.get("fdPassword");

		if (StringUtils.isBlank(fdPassword) || "null".equals(fdPassword)) {
			// 请先设置交易密码
			return RespEntity.error(RespCode.NODE_USER_NOTFDPWD);
		}
		if (!fdPassword.equalsIgnoreCase(fdPwd)) {
			// 交易密码错误
			return RespEntity.error(RespCode.USER_FDPASSWORD_NOTMATCH);
		}

		// Integer flag = 0;

		if (StringUtils.isBlank(nodeType)) {
			return RespEntity.error(RespCode.NODE_TYPE_NULL);
		} else if ("1".equals(nodeType)) {// 申请节点 锁定
			if (StringUtils.isBlank(lockNodeNum)) {
				// 申请节点数量不可为空
				return RespEntity.error(RespCode.NODE_LOCK_NUM);
			}
			String snu = (String) json.get("numNodes");
			Integer numNodes = Integer.valueOf(snu);

			Float fa = 0f;
			int intNum = 0;
			Boolean flag = true;
			try {
				fa = Float.valueOf(lockNodeNum);
				intNum = (int) fa.floatValue();
				flag = intNum == fa;
			} catch (Exception e) {
				flag = false;
			}
			// Integer lockNodeNumm = Integer.valueOf(lockNodeNum);
			if (numNodes < intNum || intNum <= 0 || !flag) {
				// 节点数量错误
				return RespEntity.error(RespCode.NODE_NOTINT);
			}

			String lockuid = this.redisTemplate.opsForValue().get("lock" + uid);

			if (lockuid == null) {
				this.redisTemplate.opsForValue().set("lock" + uid, "100", 5, TimeUnit.SECONDS);
				// 全部判断正确后，
				String profitTime = (String) json.get("profitTime");
				String uuid = (String) json.get("uuid");
				RespEntity resp = this.balanceRpc.addNode(uid, intNum, profitTime, type, uuid);
				resp.setMessage("申请节点成功");
				resp.setAttachment(null);
				return resp;
			} else {
				return new RespEntity(610, "5秒只允许操作一次");
			}

		} else if ("2".equals(nodeType)) {// 解锁节点 解锁
			if (StringUtils.isBlank(unLockNodeNum)) {
				// 解锁节点数量不可为空
				return RespEntity.error(RespCode.NODE_UNLOCK_NUM);
			}

			Integer numNodes = (Integer) json.get("unLockNodeCount");
			// Integer numNodes = Integer.valueOf(snu);
			Float fa = 0f;
			int intNum = 0;
			Boolean flag = true;
			try {
				fa = Float.valueOf(unLockNodeNum);
				intNum = (int) fa.floatValue();
				flag = intNum == fa;
			} catch (Exception e) {
				flag = false;
			}
			if (numNodes < intNum || intNum <= 0 || !flag) {
				// 节点数量错误
				return RespEntity.error(RespCode.NODE_NOTINT);
			}

			String unlockuid = this.redisTemplate.opsForValue().get("unlock" + uid);

			if (unlockuid == null) {
				this.redisTemplate.opsForValue().set("unlock" + uid, "101", 5, TimeUnit.SECONDS);
				// 全部判断正确后，
				// String profitTime = (String) json.get("profitTime");
				String uuid = (String) json.get("uuid");
				RespEntity resp = this.balanceRpc.updateNode(uid, intNum, type, uuid);
				resp.setMessage("解锁节点成功");
				resp.setAttachment(null);
				return resp;

			} else {
				return new RespEntity(610, "5秒只允许操作一次");
			}

		} else {
			// 节点类型参数错误
			return RespEntity.error(RespCode.NODE_TYPE_UNCORRECT);
		}

	}

	@PostMapping({ "/ownChoose" })
	public RespEntity ownChoose(@RequestParam("uid") Integer uid,
			@RequestParam(value = "checkJson", defaultValue = "0") String checkJson,
			@RequestParam(value = "symbol", defaultValue = "0") String symbol,
			@RequestParam(value = "checkedType", defaultValue = "0") Integer checkedType,
			@RequestParam(value = "type", defaultValue = "0") Integer type) {

		log.info("uid={}", uid);
		log.info("checkJson={}", checkJson);
		log.info("symbol={}", symbol);
		log.info("checkedType={}", checkedType);
		log.info("type={}", type);
		if (type == 0 || uid <= 0) {
			return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
		}

		if (type == 1) {// pc
			if (checkedType == 0) {
				return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
			}
			if (checkedType == 1) {// 选择
				RespEntity respEntity = this.userRpc.checked(uid, symbol);
				return respEntity;
			}
			if (checkedType == 2) {// 取消选择
				RespEntity respEntity = this.userRpc.unCheck(uid, symbol);
				return respEntity;
			}
		}

		if (type == 2 || type == 3) {// app
			if (checkJson == "0") {
				return RespEntity.error(RespCode.COMMON_PARAM_BLANK);
			}

			RespEntity respEntity = this.userRpc.checkedJson(uid, checkJson);
			return respEntity;
		}

		return null;
	}

	@PostMapping({ "/showChoose" })
	public RespEntity showChoose(@RequestParam(value="uid",defaultValue = "0" ) Integer uid) {

		log.info("uid={}", uid);
		if (uid == null) {
			uid = 0;
		}
		log.info("uid=" + uid);
		RespEntity showChoose = this.userRpc.showChoose(uid);
		log.info("showChoose=" + showChoose);
		return showChoose;
	}

	/**
	 * 获取oss的sts认证
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping({ "/getsts" })
	public RespEntity getSts(HttpServletRequest request) throws Exception {
		log.info("getsts-------12lian----");
		// UserDto userDto = this.user(request);
		System.out.println(ACCESS_KEY_ID);
		Map<String, String> stsToken = AliyunOSSClientUtil.getStsToken(ACCESS_KEY_ID, ACCESS_KEY_SECRET);
		stsToken.put("bucket", BACKET_NAME);
		// log.info("查询等级认证userDto.getUuid():" + userDto.getUuid());
		return RespEntity.success(stsToken);
	}

}
