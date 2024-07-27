package com.shiro.sojbackenduserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.shiro.sojbackenduserservice.mapper")
@ComponentScan("com.shiro")
@EnableFeignClients({"com.shiro.sojbackendserviceclient.service"})
@EnableDiscoveryClient
public class SojBackendUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SojBackendUserServiceApplication.class, args);
    }

}
