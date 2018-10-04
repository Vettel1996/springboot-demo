//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.base.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    protected String redisHost;
    @Value("${spring.redis.port}")
    protected Integer redisPort;
    @Value("${spring.redis.timeout}")
    protected Integer redisTimeOut;
    @Value("${spring.redis.pool.max-idle}")
    protected Integer redisMaxIdle;
    @Value("${spring.redis.pool.max-wait}")
    protected Integer redisMaxWait;

    public RedisConfig() {
    }

    @Bean
    RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate();
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(this.redisMaxIdle);
        jedisPoolConfig.setMaxWaitMillis((long)this.redisMaxWait);
        jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
        JedisShardInfo jedisShardInfo = new JedisShardInfo(this.redisHost, this.redisPort);
        jedisConnectionFactory.setShardInfo(jedisShardInfo);
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
