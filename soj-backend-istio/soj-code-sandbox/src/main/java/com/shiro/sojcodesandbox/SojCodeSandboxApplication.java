package com.shiro.sojcodesandbox;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDubbo(scanBasePackages = "com.shiro.sojcodesandbox.provider")
public class SojCodeSandboxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SojCodeSandboxApplication.class, args);
    }

}
