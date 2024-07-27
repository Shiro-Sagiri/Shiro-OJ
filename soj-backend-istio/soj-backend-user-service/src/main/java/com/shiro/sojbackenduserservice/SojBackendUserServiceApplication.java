package com.shiro.sojbackenduserservice;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.shiro.sojbackenduserservice.mapper")
@ComponentScan("com.shiro")
@EnableDubbo(scanBasePackages = {"com.shiro.sojbackenduserservice.provider"})
public class SojBackendUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SojBackendUserServiceApplication.class, args);
    }

}
