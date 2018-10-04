package com.xyhj.lian12.util;

import org.apache.commons.lang.StringUtils;

import com.dingxianginc.ctu.client.CaptchaClient;
import com.dingxianginc.ctu.client.model.CaptchaResponse;

public class SliderVerification {

    private static final String appId = "d7a38fa07a37ec87d77cb6c676c44e4c";
    private static final String appSecretKey = "5fa1b811e25668c16581912331de30da";

    public static boolean verifyToken(String token){
    	System.out.println("verifyToken token:" + token);
        if(StringUtils.isNotBlank(token)){
            CaptchaClient captchaClient = new CaptchaClient(appId,appSecretKey);
            //特殊情况需要额外指定服务器,可以在这个指定，默认情况下不需要设置
            CaptchaResponse response;
            boolean result = false;
            try {
				response = captchaClient.verifyToken(token);
				result = response.getResult();
				System.out.println("verifyToken status:" + response.getCaptchaStatus());
				System.out.println("verifyToken result:" + result);
				//确保验证状态是SERVER_SUCCESS，SDK中有容错机制，在网络出现异常的情况会返回通过
				if (result) {
					//token验证通过，继续其他流程
					return true;
				} else {
					//token验证失败，业务系统可以直接阻断该次请求或者继续弹验证码
					return false;
				}
			} catch (Exception e) {
				System.out.println("verifyToken error:" + e);
				e.printStackTrace();
			}
        }
        return  false;
    }
}
