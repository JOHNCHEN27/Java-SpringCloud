package com.lncanswer.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class FeignClientConfiguration {
    /**
     * 基于配置类实现feign 日志级别配置  如果想要其生效需要加到注解中
     * 如果是全局配置，要把此类放到EnableFeignClients注解中
     * 如果是局部配置，要把此类放到FeignClient注解中
     * @return
     */
    @Bean
    public Logger.Level feignLogLevel(){
        return Logger.Level.BASIC;
    }
}
