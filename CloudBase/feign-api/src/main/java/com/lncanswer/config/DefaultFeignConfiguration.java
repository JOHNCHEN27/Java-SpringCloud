package com.lncanswer.config;

import com.lncanswer.factory.UserClientFallbackFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 编写降级策略总结： 1、在Feign客户端类中的配置文件开启sentinel的功能（true）
 * 2、编写一个类实现FallbackFactory工厂接口 指定泛型为要实现的调用接口类：如Userclient 实现接口里的方法
 * 3、在此类中 在实现的方法里编写需要反馈的降级策略 ，然后再编写一个配置类将此类注册为Bean对象
 * 4、最后在具体的方法调用接口中 FeignClient注解里加上编写的工厂接口降级策略，Spring会自动实现
 */

@Configuration
public class DefaultFeignConfiguration {

    /**
     * 将FallbackFactory实现接口类的降级策略类 定义成一个bean交给IOC容器管理
     * @return
     */
    @Bean
    public UserClientFallbackFactory userClientFallbackFactory(){
        return new UserClientFallbackFactory();
    }
}
