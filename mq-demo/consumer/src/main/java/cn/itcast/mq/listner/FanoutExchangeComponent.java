package cn.itcast.mq.listner;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class FanoutExchangeComponent {
    /**
     * 监听两个队列并输出内容
     */
    @RabbitListener(queues = "lnc.queue1")
    public void listenerFanoutQueue1(String msg){
        System.out.println("1.......收到：{"+ msg + "}");
    }

    @RabbitListener(queues = "lnc.queue2")
    public void listenerFanoutQueue2(String msg ){
        System.out.println("2.......收到：{"+ msg + "}");
    }
}
