package cn.itcast.mq.listner;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TopicExchangeQueue {
    /**
     * TopicExchange交换机 key.*huo# *表示 一个任何  #表示多个任意
     */
    @RabbitListener(bindings = @QueueBinding(
            value =@Queue("Topic.queue1"),
            exchange = @Exchange(value = "Topic.Exchange",type = ExchangeTypes.TOPIC),
            key = "china.#"
    ))
    public void TopicQueue1(String msg){
        System.out.println("1.....接到Topic消息： + { " + msg + "}");
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("Topic.queue2"),
            exchange = @Exchange(value = "Topic.Exchange",type = ExchangeTypes.TOPIC),
            key = "#.news"
    ))
    public void TopicQueue2(String msg){
        System.out.println("2.....接到Topic消息： + { " + msg + "}");
    }
}
