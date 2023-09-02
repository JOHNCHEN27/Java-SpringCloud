package cn.itcast.mq.config;

import org.junit.platform.commons.util.PackageUtils;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchageQueueConfig {
    /**
     * 持久化交换机、队列、消息
     */
    @Bean
    public DirectExchange simpleExchange(){
        //三个参数：交换机名称、是否持久化、当没有队列绑定是否自动删除
        return new DirectExchange("simple.direct",true,false);
    }
    @Bean
    public Queue simpleQueue(){
        //利用QueueBuilder调用durable实现队列持久化
        return QueueBuilder.durable("simpledirect.queue").build();
    }

    /**
     * 实现交换机和队列的绑定
     * @return
     */
    @Bean
    public Binding bindingExchangeAndQueue(){
        return BindingBuilder.bind(simpleQueue()).to(simpleExchange()).with("direct");
    }
}
