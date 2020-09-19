package org.liangxiong.review.api.fallback;

import org.liangxiong.review.api.HelloClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-08-15 09:27
 * @description
 **/
@RequestMapping("/fallback")
@Component
public class HelloClientFallback implements HelloClient {

    @Override
    public String hello(String username) {
        return "hello hystrix";
    }
}
