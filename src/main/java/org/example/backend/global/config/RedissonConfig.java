package org.example.backend.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String REDIS_HOST;

    @Value("${spring.data.redis.port}")
    private String REDIS_PORT;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String redisUrl = String.format("redis://%s:%s", REDIS_HOST, REDIS_PORT);
        config.useSingleServer()
                .setAddress(redisUrl)
                .setConnectionMinimumIdleSize(20)   // 유휴 커넥션
                .setConnectionPoolSize(64);         // 최대 커넥션
        return Redisson.create(config);
    }

}

