//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.base.interceptor;

import com.google.gson.Gson;
import com.xyhj.lian.util.DateUtils;
import com.xyhj.lian12.balance.dto.RcBillControl;
import com.xyhj.lian12.balance.rpc.BalanceRpc;
import com.xyhj.lian12.base.config.BaseController;
import com.xyhj.lian12.user.dto.UserDto;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Order(2)
public class AuthVerifyIntercepter extends BaseController implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(AuthVerifyIntercepter.class);
    @Autowired
    private BalanceRpc authorityRpc;
    static Gson g = new Gson();

    public AuthVerifyIntercepter() {
    }

    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        UserDto user = this.user(httpServletRequest);
        Map<String, Object> map = new HashMap();
        String fdPasswordUpdateTime = user.getFdPasswordUpdateTime();
        double amount = Double.valueOf(httpServletRequest.getParameter("amount"));
        int currencyId = Integer.valueOf(httpServletRequest.getParameter("currencyId"));
        String local = httpServletRequest.getParameter("local");
        if (local == null) {
            local = "";
        }

        String rechargeType = httpServletRequest.getParameter("rechargeType");
        if (rechargeType == null) {
            rechargeType = "";
        }

        int actionId = Integer.valueOf(httpServletRequest.getParameter("actionId"));
        if (actionId != 1) {
            if (fdPasswordUpdateTime == null) {
                map.put("status", 201);
                map.put("message", "请设置交易密码");
                httpServletResponse.setCharacterEncoding("UTF-8");
                httpServletResponse.setHeader("Content-type", "text/html;charset=UTF-8");
                httpServletResponse.getWriter().write(g.toJson(map));
                return false;
            }

            Date parse = DateUtils.parse(fdPasswordUpdateTime, "yyyy-MM-dd HH:mm:ss");
            log.info("最后修改交易密码时间:" + fdPasswordUpdateTime);
            long l = System.currentTimeMillis() - parse.getTime();
            long hour = l / 1000L / 60L / 60L;
            if (hour < 24L) {
                map.put("status", 201);
                String message = "交易密码修改之后，24小时不能交易哦！";
                if (local.equals("en_US")) {
                    message = "You can't trade within 24 hours after changing the trade password";
                } else if (local.equals("zh_TW")) {
                    message = "交易密碼修改之後，24小時不能交易哦！";
                }

                map.put("message", message);
                httpServletResponse.setCharacterEncoding("UTF-8");
                httpServletResponse.setHeader("Content-type", "text/html;charset=UTF-8");
                httpServletResponse.getWriter().write(g.toJson(map));
                return false;
            }
        }

        Map<String, Object> auth = (Map)this.authorityRpc.auth(actionId, currencyId, amount, user.getUuid(), user.getAuthLevel(), rechargeType);
        log.info("权限验证返回结果->" + (new Gson()).toJson(auth));
        int status = (Integer)auth.get("status");
        // 权限通过
        if (status == 200) {
            Map<String, Object> attachment = (Map)auth.get("attachment");
            double fee = (Double)attachment.get("fee");// 手续费
            boolean risk = (Boolean)attachment.get("risk");
            httpServletRequest.setAttribute("fee", fee);
            httpServletRequest.setAttribute("feeRate", attachment.get("feeRate"));// 手续费率
            httpServletRequest.setAttribute("risk", risk);
            httpServletRequest.setAttribute("risk_reason", attachment.get("risk_reason"));
            return true;
        } else {
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setHeader("Content-type", "text/html;charset=UTF-8");
            httpServletResponse.getWriter().write(g.toJson(auth));
            return false;
        }
    }

    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    /**
     * 访问完毕
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param e
     * @throws Exception
     */
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        boolean risk = (Boolean)httpServletRequest.getAttribute("risk");
        Object refIdo = httpServletRequest.getAttribute("ref_id");
        // 需要风控
        if (risk && refIdo != null) {
            String refId = refIdo.toString();
            double amount = Double.valueOf(httpServletRequest.getParameter("amount"));
            int currencyId = Integer.valueOf(httpServletRequest.getParameter("currencyId"));
            int actionId = Integer.valueOf(httpServletRequest.getParameter("actionId"));
            UserDto user = (UserDto)httpServletRequest.getAttribute("user");
            String risk_reason = (String)httpServletRequest.getAttribute("risk_reason");
            RcBillControl rcBillControl = new RcBillControl();
            rcBillControl.setAmount(BigDecimal.valueOf(amount));
            rcBillControl.setCoinId(currencyId);
            rcBillControl.setUserActionId(actionId);
            rcBillControl.setCustomerUuid(user.getUuid());
            rcBillControl.setRefUuid(refId);
            rcBillControl.setReason(risk_reason);
            rcBillControl.setLastEditBy("系统");
            rcBillControl.setCreateTime("系统");
            Map<String, Object> control = this.authorityRpc.control(rcBillControl);
            log.info("生成风控单结果——>" + g.toJson(control));
        }

    }
}
