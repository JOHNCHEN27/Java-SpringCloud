package cn.itcast.mq.listner;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class WorkRabbitQueue {

    /**
     * WorkRabbitQueue类型 一个队列有多个消费者 每个消费者依次拿去消息
     * 通过配置设置每次只取一个消息
     * @param msg
     * @throws InterruptedException
     */
    @RabbitListener(queues = "work.queue")
    public void WorkRabbitQueueSumer1(String msg) throws InterruptedException {
        System.out.println("消费者1接受消息：{ " + msg + " }" + LocalTime.now());
        Thread.sleep(25);
    }

    @RabbitListener(queues = "work.queue")
    public void WorkRabbitQueueSumer2(String msg) throws InterruptedException {
        System.out.println("消费者2接受消息: { " + msg
                +" }" + LocalTime.now());
        Thread.sleep(100);
    }

}
