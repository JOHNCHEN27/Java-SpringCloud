package com.lncanswer.clients;

import com.lncanswer.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient("userservice") //绑定服务 加上FeignClient注解spring会自动扫描此配置类
public interface UserClient {

    //编写请求方法:请求方式、参数、返回值 方法名
    @GetMapping("/user/{id}")
    User findById(@PathVariable ("id") Long id);
}
