//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.base.config;

import feign.Feign;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.netflix.feign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({Feign.class})
@AutoConfigureBefore({FeignAutoConfiguration.class})
public class FeignOkHttpConfig {
    public FeignOkHttpConfig() {
    }

    @Bean
    public OkHttpClient okHttpClient() {
        return (new Builder()).readTimeout(60L, TimeUnit.SECONDS).connectTimeout(60L, TimeUnit.SECONDS).writeTimeout(120L, TimeUnit.SECONDS).connectionPool(new ConnectionPool()).build();
    }
}
