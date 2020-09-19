package org.liangxiong.review.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-09-19 20:08
 * @description
 **/
@RestController
@RequestMapping("/server")
public class FeignController {

    @Autowired
    private DiscoveryClient client;

    @RequestMapping("/hello")
    public String hello() {
        List<ServiceInstance> instances = client.getInstances("review-server");
        ServiceInstance selectedInstance = instances
                .get(new Random().nextInt(instances.size()));
        return "Hello World: " + selectedInstance.getServiceId() + ":" + selectedInstance
                .getHost() + ":" + selectedInstance.getPort();
    }
}
