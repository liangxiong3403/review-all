package org.liangxiong.review.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-09-19 12:40
 * @description 服务提供方
 **/
@SpringBootApplication(exclude = {ElasticsearchDataAutoConfiguration.class})
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
