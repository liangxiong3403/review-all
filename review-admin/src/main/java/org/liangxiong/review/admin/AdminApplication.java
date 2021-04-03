package org.liangxiong.review.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2021-04-03 13:16
 * @description 监控管理服务最好单独部署
 * spring-boot-admin的序列化不能使用fastjson全局配置,会报错400
 * spring-boot-admin会导致knife4j的主页doc.html访问404
 **/
@EnableAdminServer
@SpringBootApplication
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
