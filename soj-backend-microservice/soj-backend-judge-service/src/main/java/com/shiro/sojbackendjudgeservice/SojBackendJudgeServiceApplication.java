package com.shiro.sojbackendjudgeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.shiro")
@EnableFeignClients({"com.shiro.sojbackendserviceclient.service"})
@EnableDiscoveryClient
public class SojBackendJudgeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SojBackendJudgeServiceApplication.class, args);
    }

}
