package com.shiro.sojbackendjudgeservice;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan("com.shiro")
@EnableDubbo
public class SojBackendJudgeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SojBackendJudgeServiceApplication.class, args);
    }

}
