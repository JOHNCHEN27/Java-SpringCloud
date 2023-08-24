package cn.itcast.mq.listner;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component  //交给spring IOC容器管理
public class DirectExchangeQueue {

    /**
     * DirectExchange交换机的使用 每一个设置Routingkey 来绑定交换机路由
     * 交换机将消息路由发送给与它绑定RoutingKey值的队列上
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name ="direct.queue1"),
            exchange = @Exchange(name = "direct.exchange",type = ExchangeTypes.DIRECT),
            key = {"red","blue"}
    ))
    public void lisenerDirectQueue1(String msg){
        System.out.println("1.。。。。。。。Direct { + " + msg + "}");
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue2"),
            exchange = @Exchange(name = "direct.exchange",type = ExchangeTypes.DIRECT),
            key = {"red","yellow"}
    ))
    public void listenerDirectQueue2(String msg){
        System.out.println("2.............Direct { " + msg + "}");
    }
}
