package org.liangxiong.review.client.controller;

import lombok.extern.slf4j.Slf4j;
import org.liangxiong.review.api.HelloClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-09-19 20:08
 * @description
 **/
@Slf4j
@RestController
@RequestMapping("/client")
public class FeignController {

    @Autowired
    private ObjectProvider<HelloClient> provider;

    @RequestMapping("/feign")
    public String hello(@RequestParam String username) {
        if (log.isInfoEnabled()) {
            log.info("client receive request...");
        }
        AtomicReference<String> result = new AtomicReference<>("default");
        provider.ifAvailable(client -> {
            result.set(client.hello(username));
        });
        return result.get();
    }
}
