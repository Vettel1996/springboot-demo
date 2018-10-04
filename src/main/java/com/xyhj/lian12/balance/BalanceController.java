//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.balance;

import com.google.gson.Gson;
import com.xyhj.lian.util.RespEntity;
import com.xyhj.lian12.balance.dto.CtCustomerAccountCtbWater;
import com.xyhj.lian12.balance.dto.CtTransferBill;
import com.xyhj.lian12.balance.dto.RcBillControl;
import com.xyhj.lian12.balance.rpc.BalanceRpc;
import com.xyhj.lian12.user.dto.UserDto;
import com.xyhj.lian12.user.interfaces.UserRpc;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class BalanceController {
    private static final Logger log = LoggerFactory.getLogger(BalanceController.class);
    @Autowired
    private BalanceRpc balanceRpc;
    @Autowired
    private UserRpc userRpc;
    @Autowired
    private CoinController coinController;

    public BalanceController() {
    }

    @PostMapping({"withdrawClose"})
    RespEntity withdrawClose(@RequestParam("refId") String refId, HttpServletRequest request) {
        UserDto user = (UserDto)request.getAttribute("user");
        int i = Calendar.getInstance().get(11);
        if (i >= 16) {
            return new RespEntity(500, "撤销时间为每日0点到16点");
        } else {
            RcBillControl rcBillControl = new RcBillControl();
            rcBillControl.setRefUuid(refId);
            rcBillControl.setStatus(1);
            rcBillControl.setCustomerUuid(user.getUuid());
            List<RcBillControl> rcBillControls = this.balanceRpc.controlList(rcBillControl);
            return rcBillControl != null && rcBillControls.size() > 0 ? new RespEntity(500, "您的申请的提现已在风控中，请联系客服取消") : this.balanceRpc.withdrawCheck(refId, "用户前台取消", -1);
        }
    }

    @RequestMapping(
            value = {"/viewReCharge/{rechargeId}"},
            method = {RequestMethod.GET}
    )
    public RespEntity viewReCharge(@PathVariable String rechargeId, HttpServletRequest request) {
        UserDto user = (UserDto)request.getAttribute("user");
        return RespEntity.success(this.balanceRpc.viewReCharge(rechargeId, user.getUuid()));
    }

    @RequestMapping(
            value = {"/uploadSms"},
            method = {RequestMethod.POST}
    )
    public RespEntity upload(@RequestParam("sigin") String sigin, @RequestParam("file") MultipartFile file) throws Exception {
        if (!sigin.equals("855692E94C7FD3AB0237ADAC5DA23C8F")) {
            throw new Exception("签名验证失败");
        } else {
            byte[] bytes = file.getBytes();
            String j = new String(bytes, "UTF-8");
            log.info("上传短信:" + j);
            return this.balanceRpc.uploadSms(j, sigin);
        }
    }

    @RequestMapping(
            value = {"/transferCoin"},
            method = {RequestMethod.POST}
    )
    public RespEntity transferCoin(HttpServletRequest request, @RequestParam("amount") Double amount, @RequestParam("toCustomerUid") Long toCustomerUid, @RequestParam("currencyId") Integer currencyId, @RequestParam("tradePassword") String tradePassword, @RequestParam("uname") String uname, @RequestParam("smsMessage") String smsMessage) throws Exception {
        UserDto user = (UserDto)request.getAttribute("user");
        double fee = (Double)request.getAttribute("fee");
        boolean risk = (Boolean)request.getAttribute("risk");
        RespEntity re = this.userRpc.judgeFdPwdAndimgCode(smsMessage, tradePassword, user.getUid());
        if (re.getStatus() != 200) {
            return re;
        } else {
            RespEntity respEntity = this.userRpc.selectByUid(toCustomerUid);
            if (respEntity.getStatus() != 200) {
                return respEntity;
            } else {
                Gson g = new Gson();
                Map m = (Map)g.fromJson(g.toJson(respEntity.getAttachment()), Map.class);
                String unames = (String)m.get("uname");
                if (!uname.equals(unames)) {
                    return new RespEntity(500, "用户uid:" + toCustomerUid + "和用户昵称:" + uname + "不匹配");
                } else {
                    String fromCustomerUuid = user.getUuid();
                    String toCustomerUuid = (String)m.get("uuid");
                    CtTransferBill ctTransferBill = new CtTransferBill();
                    ctTransferBill.setFromCustomerUuid(fromCustomerUuid);
                    ctTransferBill.setToCustomerUuid(toCustomerUuid);
                    ctTransferBill.setCurrencyId(currencyId);
                    ctTransferBill.setAmount(BigDecimal.valueOf(amount));
                    ctTransferBill.setInitAmount(BigDecimal.valueOf(amount));
                    ctTransferBill.setPaidAmount(ctTransferBill.getAmount().subtract(BigDecimal.valueOf(fee)));
                    ctTransferBill.setTelephoneSn(user.getPhone());
                    ctTransferBill.setFee(BigDecimal.valueOf(fee));
                    ctTransferBill.setBankFee(BigDecimal.valueOf(0L));
                    ctTransferBill.setNote("");
                    ctTransferBill.setCreateBy("系统");
                    ctTransferBill.setLastEditBy("系统");
                    ctTransferBill.setParentAbs(toCustomerUid + "," + user.getUid());
                    if (risk) {
                        ctTransferBill.setRiskFlag(2);
                    } else {
                        ctTransferBill.setRiskFlag(1);
                    }

                    ctTransferBill.setStatus(0);
                    RespEntity result = this.balanceRpc.ctTransferBillSave(ctTransferBill);
                    if (result.getStatus() == 200) {
                        Map<String, Object> attachment = (Map)result.getAttachment();
                        request.setAttribute("ref_id", attachment.get("ref_id"));
                    }

                    return result;
                }
            }
        }
    }

    @RequestMapping({"/transferCoinList"})
    public RespEntity transferCoinList(HttpServletRequest request, CtTransferBill bill, @RequestParam(defaultValue = "1") Integer type) throws Exception {
        UserDto user = (UserDto)request.getAttribute("user");
        if (type == 1) {
            bill.setFromCustomerUuid(user.getUuid());
        } else {
            bill.setToCustomerUuid(user.getUuid());
        }

        return this.balanceRpc.ctTransferBillList(bill);
    }

    @GetMapping({"test"})
    public RespEntity test() {
        return new RespEntity(500, "错误");
    }

    @RequestMapping({"/waterChart"})
    RespEntity waterChart(CtCustomerAccountCtbWater ctCustomerAccountCtbWater, HttpServletRequest request) {
        UserDto user = (UserDto)request.getAttribute("user");
        ctCustomerAccountCtbWater.setCustomerUuid(user.getUuid());
        return RespEntity.success(this.balanceRpc.waterChart(ctCustomerAccountCtbWater));
    }
    
    @RequestMapping(value = {"/getNodeList"},method = {RequestMethod.POST})
    RespEntity getNodeList(@RequestParam("uid") String uid,
    		@RequestParam("page") String page) throws Exception {
       
    	log.info("uid="+uid);
    	log.info("page="+page);
        return this.balanceRpc.getNodeList(uid,page);
    }
    
 
    @RequestMapping(value = {"/getNodeListByNodeIds"},method = {RequestMethod.POST})
    RespEntity getNodeListByNodeIds(@RequestParam("nodeIds") String nodeIds) throws Exception {
    	log.info("uid="+nodeIds);
        return this.balanceRpc.getNodeListByNodeIds(nodeIds);
    }
    
    @RequestMapping(value = {"/getProfitList"},method = {RequestMethod.POST})
    RespEntity getProfitList(@RequestParam(value="uid",defaultValue="") String uid, 
			@RequestParam(value="startTime",defaultValue="") String startTime, 
			@RequestParam(value="endTime",defaultValue="") String endTime,
			@RequestParam(value="page",defaultValue="1") Integer page,
			@RequestParam(value="state",defaultValue="") String state) throws Exception {
    	log.info("uid="+uid);
    	log.info("page="+page);
        return this.balanceRpc.getProfitList(uid,startTime,endTime,page,state);
    }
    
    @RequestMapping(value = {"/rightMessage"},method = {RequestMethod.POST})
    RespEntity rightMessage(@RequestParam("uid") Long uid) throws Exception {
    	log.info("uid="+uid);
        return this.balanceRpc.rightMessage(uid);
    }
    
    
    @RequestMapping(value = "/mgrGetNodeList", method = RequestMethod.POST)
	 RespEntity  mgrGetNodeList(@RequestParam(value="uid",defaultValue="") String uid,
			 @RequestParam(value="startLockTime",defaultValue="") String startLockTime,
			 @RequestParam(value="endLockTime",defaultValue="") String endLockTime,
			 @RequestParam(value="startDayNum",defaultValue="") String startDayNum,
			 @RequestParam(value="endDayNum",defaultValue="") String endDayNum,
			 @RequestParam(value="state",defaultValue="") String state,
			 @RequestParam(value="startUnLockTime",defaultValue="") String startUnLockTime,
			 @RequestParam(value="endUnLockTime",defaultValue="") String endUnLockTime,
			 @RequestParam(value="page",defaultValue="1") String page) {
    	
    	return this.balanceRpc.mgrGetNodeList(uid, startLockTime, endLockTime, startDayNum, endDayNum, state, startUnLockTime, endUnLockTime, page);
    }
    
    
    
}
