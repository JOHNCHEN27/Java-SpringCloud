package cn.itcast.mq;

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

    /**
     * FanoutExchange交换机 广播消息给与它绑定的所有队列
     */
    @Test
    public void testFanoutExchange(){
        //交换机名称
        String exchangeName = "lnc.fanout";
        //发送的消息
        String msg = "testFanoutExchange";
        //利用rabbitTemplate对象发送广播
        rabbitTemplate.convertAndSend(exchangeName,"",msg);
    }

    /**
     * DirectExchange 路由交换机 设置routingKey值 将消息传送给与它绑定key值的队列
     */
    @Test
    public void testDirectExchange(){
        //交换机名称
        String exchangeName = "direct.exchange";
        String msg = "DireExchange__";
        rabbitTemplate.convertAndSend(exchangeName,"blue",msg);
    }

    /**
     * TopicExchange交换机 设置key值 * 表示一个 #表示多个
     */
    @Test
    public void testTopicExchange(){
        String exchangeName = "Topic.Exchange";
        String msg = "喜报！孙悟空大战哥斯拉，胜";
        rabbitTemplate.convertAndSend(exchangeName,"china.wu",msg);
    }
}
