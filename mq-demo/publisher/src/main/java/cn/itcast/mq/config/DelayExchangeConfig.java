package cn.itcast.mq.config;

import cn.itcast.mq.PublisherApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DelayExchangeConfig {

    /**
     * 基于DelayExchange插件 来实现一个延迟交换机 需要在mq中部署插件才可以使用此功能
     * 本质上还是三种类型的交换机声明 只不过是 加上delayed属性 设置为一个延时交换机
     * @return
     */
    @Bean
    public DirectExchange delayExchange(){
        return ExchangeBuilder.directExchange("delay.direct") //指定延迟交换机名字
                .delayed() //delayed开启延迟 默认属性为tRUE
                .durable(true)
                .build();
    }

    @Bean
    public Queue delayQueue(){
        return new Queue("delay.queue");
    }

    @Bean
    public Binding BindDelayExchangeAndQueue(){
        return BindingBuilder.bind(delayQueue()).to(delayExchange()).with("delay");
    }
}
