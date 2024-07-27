package com.shiro.sojbackendquestionservice;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.shiro.sojbackendquestionservice.mapper")
@ComponentScan("com.shiro")
@EnableDubbo(scanBasePackages = "com.shiro.sojbackendquestionservice.provider")
public class SojBackendQuestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SojBackendQuestionServiceApplication.class, args);
    }

}
