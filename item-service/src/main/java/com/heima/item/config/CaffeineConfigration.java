package com.heima.item.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.heima.item.pojo.Item;
import com.heima.item.pojo.ItemStock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CaffeineConfigration {
    /**
     * 为了能在多个地方访问 将Cache注册为Bean交给IOC容器进行管理
     * 将本地缓存（Caffeine）设置初始大小和缓存上线
     */
    @Bean
    public Cache<Long, Item> itemCache(){
        return  Caffeine.newBuilder()
                .maximumSize(10_000)  //设置缓存上限为1000
                .initialCapacity(100) //设置初始容量为100
                .expireAfterWrite(Duration.ofSeconds(600)) //设置缓存过期时间为600秒
                .build(); //构建缓存

    }

    /**
     * 声明ItemStock的Cachebean
     */
    @Bean
    public Cache<Long, ItemStock> itemStockCache(){
        return Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(Duration.ofSeconds(600))
                .initialCapacity(100)
                .build();
    }


}
