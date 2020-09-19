package org.liangxiong.review.client;

import org.liangxiong.review.client.mapper.UserMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-09-19 12:38
 * @description 客户端
 **/
@MapperScan(basePackageClasses = UserMapper.class)
@EnableOpenApi
@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
}
