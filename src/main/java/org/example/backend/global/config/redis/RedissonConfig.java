package org.example.backend.global.config.redis;

import java.util.List;
import java.util.Map;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RedissonConfig {

    private final RedisClusterProperties redisClusterProperties;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        List<String> formattedNodes = redisClusterProperties.getNodes().stream()
                .map(node -> node.startsWith("redis://") ? node : "redis://" + node)
                .toList();

        config.useClusterServers()
                .addNodeAddress(formattedNodes.toArray(new String[0]))
                .setScanInterval(2000)
                .setIdleConnectionTimeout(10000)
                .setConnectTimeout(10000)
                .setRetryAttempts(3)
                .setRetryInterval(1500)
                .setMasterConnectionPoolSize(10)
                .setMasterConnectionMinimumIdleSize(5);
                /*.setNatMap(Map.of(
                        "192.168.6.15:6379",     "192.168.1.119:30001", // redis-0
                        "192.168.40.207:6379",   "192.168.0.121:30002", // redis-1
                        "192.168.6.17:6379",     "192.168.1.119:30003", // redis-2
                        "192.168.40.209:6379",   "192.168.0.121:30004", // redis-3
                        "192.168.6.19:6379",     "192.168.1.119:30005", // redis-4
                        "192.168.40.211:6379",   "192.168.0.121:30006"  // redis-5
                ));*/



        return Redisson.create(config);
    }
}
