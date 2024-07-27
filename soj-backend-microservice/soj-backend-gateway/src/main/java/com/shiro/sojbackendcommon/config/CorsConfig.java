package com.shiro.sojbackendcommon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Collections;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*"); // 允许提交请求的方法，*表示全部允许
        config.setAllowCredentials(true); // 允许cookies跨域
        config.setAllowedOriginPatterns(Collections.singletonList("*")); // 允许跨域的域名，可以用*表示允许任何域名使用
        config.addAllowedHeader("*"); // 允许任何头
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config); // 对接口配置跨域设置
        return new CorsWebFilter(source);
    }
}
