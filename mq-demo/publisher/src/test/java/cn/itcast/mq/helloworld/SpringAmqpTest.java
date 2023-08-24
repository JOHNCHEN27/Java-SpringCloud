package cn.itcast.mq.helloworld;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringAmqpTest {
    /**
     * 导入amqp依赖后 spring ioc里注入了RabbitTemplate消息队列模板对象 利用它发送消息
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Test
    public void testRabbitTemplate(){
        String queueName ="simple.queue";
        String msg = "hello simple.queque";
        rabbitTemplate.convertAndSend(queueName,msg);

    }


    /**
     * WorkRabbitQueue队列 多个消费者来绑定一个队列  同一条消息只能被一个消费者处理
     */
    @Test
    public void testWorkRabbitQueue() throws InterruptedException {
        String queueName ="work.queue";
        String msg = "WorkRabbitQueue__";

        for (int i = 1; i<= 50 ; i++){
            //发送消息
            rabbitTemplate.convertAndSend(queueName,msg+i);
            //避免发送太快
            Thread.sleep(25);
        }

    }
}
