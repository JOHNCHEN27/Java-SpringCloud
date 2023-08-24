package cn.itcast.mq.listner;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class SpringRabbitListener {
    @RabbitListener(queues = "simple.queue")
    public void listenSimpleQueue(String msg) throws InterruptedException{
        System.out.println("接受到消息: [ " +msg + " ] ");
    }
}
