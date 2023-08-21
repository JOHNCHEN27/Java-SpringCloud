package com.lncanswer.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    /**
     * 创建RestTemplate 并注入容器 用来发送业务请求
     */
    @Bean
    @LoadBalanced //负载均衡  将restTemplate对象发起的请求拦截 到eureka中去查找 在挑选合适的地址（轮询）
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }


    /**
     * 创建一个IRule接口 bean对象，决定选取列表的方法 默认是轮询（轮流访问）
     */
//    @Bean
//    public IRule randomRule (){
//        //负载均衡内部通过IRule对象来最终选择访问哪个地址 访问的方式是什么
//        return new RandomRule(); //这里我们设置访问的方式是随机访问
//    }
}
