//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.base.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
public class InterceptorConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    TokenVerifyIntercepter tokenVerifyIntercepter;// 授权拦截
    @Autowired
    SqlInjectIntercepter sqlInjectIntercepter;// sql注入拦截

    private static String[] ary = new String[]{"/user/signLogin", "/user/register*", "/user/registerBefore", "/user/login", "/user/loginGAFirst*", "/user/loginGASecond", "/user/loginRobot", "/user/loginRobotAB", "/user/totalUsers", "/user/getimg", "/news/**", "/banner/**", "/user/sendMsg*", "/message/**", "/user/verifyBeforeRegister", "/user/verUname", "/user/verifySmsCode", "/user/resetPwd*", "/user/queryPhone", "/user/verifyMailCodePwd", "/user/sendMail*", "/test", "/downloadapp/updateLocation", "/error", "/upload/uploadVedio", "/user/getUserIdentifyInfoByUUid", "/notifyPaySuccess", "/uploadSms", "/notifyPaySuccess", "/user/sendMsgWithUid", "/updateVersion/update", "/coin/coins", "/user/userRegistCount", "/announce/list", "/announce/getInfo", "/announce/pageList", "/announce/getAnnounceCount", "/coin/icoinRecharge", "/coin/rateDesc", "/coin/entrustManageCoins", "/coin/allCurrencyRelations", "/quote/**", "/presentation/introduce", "/presentation/detail", "/presentation/locale", "/smsReport/report", "/user/sendEmailForRegister*", "/user/judgeEmailStatus", "/user/resetEmail", "/user/showPvAndUv", "/user/setLanguage/*", "/bcQuestion/list","/user/historyWordOrder","/user/wordOrderDetails","/user/article","/user/articleDetails","/user/findEmail","/user/createWordOrder","/user/uploadFile","/user/fileBigSmail","/user/createWordOrder2","/user/createWordOrder3","/user/uploadImg","/otc/list","/otc/list/param","/otc/transfer_coin","/user/trOrderDetail","/user/showChoose","/monitor/**","/common/monitor","/user/trOrderByOrderIds"};
    private static String[] authAry = new String[]{"/withDraw", "/scanCode", "/coin/takeCoin*", "/transfer", "/transferCoin"};

    public InterceptorConfiguration() {
    }

    @Bean
    public AuthVerifyIntercepter authVerifyIntercepter() {
        return new AuthVerifyIntercepter();
    }

    public void addInterceptors(InterceptorRegistry registry) {
        // 注册token拦截器
        registry.addInterceptor(this.tokenVerifyIntercepter).addPathPatterns(new String[]{"/**"}).excludePathPatterns(ary);
        // 注册SQl拦截
        registry.addInterceptor(this.sqlInjectIntercepter);
        registry.addInterceptor(this.authVerifyIntercepter()).addPathPatterns(authAry);
    }

    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/12lian/**").allowedHeaders(new String[]{"*"}).allowedMethods(new String[]{"*"}).allowedOrigins(new String[]{"*"});
    }
}
