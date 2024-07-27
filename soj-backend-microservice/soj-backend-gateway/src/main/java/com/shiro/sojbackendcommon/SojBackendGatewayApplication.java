package com.shiro.sojbackendcommon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@ComponentScan("com.shiro")
public class SojBackendGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SojBackendGatewayApplication.class, args);
    }

}
