package cn.itcast.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TTLMessage {
    /**
     * 编写一个死信交换机 和队列 用来接受过期或者没人消费的消息
     * 实现消息的延迟
     * @param msg
     */

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "dl.queue",durable = "true"),
            exchange = @Exchange(name = "dl.direct"),
            key = "dl"
    ))
    public void listenerDLQueue(String msg){
        log.info("接受到dl.queue的延迟消息：{}",msg);
    }

    /**
     * 监听延时交换机绑定的队列发送的消息
     */
    @RabbitListener(queues = "delay.queue")
    public void listenerDelayQueue(String msg){
        log.info("接受到delayQueue的消息：{}",msg);
    }
}
