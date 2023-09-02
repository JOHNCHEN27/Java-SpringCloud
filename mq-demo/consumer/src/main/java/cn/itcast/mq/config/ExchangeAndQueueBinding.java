package cn.itcast.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangeAndQueueBinding {

    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(StaticExchangeAndQueue.TEST_EXCHANGE);
    }

    @Bean
    public Queue directQueue(){
        return new Queue(StaticExchangeAndQueue.TEST_QUEUE);
    }

    @Bean
    public Binding bindingQueue1(){
        return BindingBuilder.bind(directQueue()).to(directExchange()).with("");
    }
}
