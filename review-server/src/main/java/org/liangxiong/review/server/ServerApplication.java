package org.liangxiong.review.server;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-09-19 12:40
 * @description 服务端;spring-boot-admin的序列化不能使用fastjson全局配置,会报错400
 **/
@EnableAdminServer
@EnableOpenApi
@SpringBootApplication(exclude = {ElasticsearchDataAutoConfiguration.class})
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
