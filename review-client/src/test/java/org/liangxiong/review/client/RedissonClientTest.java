package org.liangxiong.review.client;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-09-18 20:34
 * @description redisson测试
 **/
public class RedissonClientTest {

    public static void main(String[] args) {
        Config config = new Config();
        config.useSentinelServers()
                .setMasterName("liangxiong")
                .setPassword("liangxiong")
                .setDatabase(0)
                .addSentinelAddress("redis://192.168.199.2:26379", "redis://192.168.199.3:26379", "redis://192.168.199.4:26379");
        RedissonClient redisson = Redisson.create(config);
    }
}
