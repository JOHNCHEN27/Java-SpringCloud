package cn.itcast.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.swing.plaf.PanelUI;

@Configuration
public class ErrorExchangeAndQueueConfig {
    /**
     * 重写MessageRecoverer接口中的实现类， 将重试次数上限之后的消息发送到指定的交换机和队列
     * 这里重写编写一个存放错误消息的交换机和队列 以便于对错误消息的处理
     * @return
     */

    @Bean
    public DirectExchange errorMessageExchange(){
        return new DirectExchange("error.direct");
    }

    @Bean
    public Queue errorQueue(){
        return new Queue("error.queue");
    }

    @Bean
    public Binding bingExchangeAndQueue(){
        return BindingBuilder.bind(errorQueue()).to(errorMessageExchange()).with("error");
    }

    /**
     * 在开启重试模式之后 重试次数耗尽 如果消息依然失败 则需要由MessageRecoverer接口来处理
     * 有三种不同的实现
     * RejectAndDontRecoverer：重试耗尽之后，直接reject拒绝 丢弃消息 spring默认是这种机制
     * ImmediateRequeueMessageRecoverer：重试耗尽之后，返回nack 消息重新入队
     * RepublishMessageRecoverer：重试耗尽之后，将失败的消息投递到指定的交换机
     * 这里我们重写new 一个 RepublishMessageRecovervre 替代原本默认的模式
     * @param rabbitTemplate
     * @return
     */
//    @Bean
//    public MessageRecoverer republishMessageRecover(RabbitTemplate rabbitTemplate){
//        return new RepublishMessageRecoverer(rabbitTemplate,"error.direct","error");
//    }
}
