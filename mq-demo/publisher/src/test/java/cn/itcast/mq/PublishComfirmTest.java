package cn.itcast.mq;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class PublishComfirmTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * correlationData是publish confirm机制，消息确认机制 确认消息是否发送到交换机
     * 当消息发送带上这个参数时，会返回callback信息，可以查看是否成功发送到交换机
     * 每一条消息都应该有一个全局唯一的id 用来区分不同消息 在correlationData封装一个UUID随机生成
     * 给correlationData添加一个CallBack回馈 用来处理如果消息发送失败的逻辑
     * 如果成功发送到交换机 会返回一个ACK 如果没有发送成功则会返回一个 nACK
     * 
     */
    @Test
    public void testSendMessage(){
         //封装消息
        String msg = "Spring Map!!!";
        //消息id 需要封装到CorrelationData中
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        //添加callback
        correlationData.getFuture().addCallback( result -> {
            if (result.isAck()){
                //ack 消息发送成功
                log.info("消息发送成功，Id:{}",correlationData.getId());
            }else {
                //nack 消息发送失败
                log.error("消息发送失败,Id:{},原因：{}",correlationData.getId(),result.getReason());
            }

        }, ex -> log.info("消息发送异常, ID :{},原因：{}",correlationData.getId(),ex.getMessage()));

        //发送消息
        rabbitTemplate.convertAndSend("simple","simple1",msg,correlationData);


    }

    /**
     * 测试 消息和队列设置过期时间 将消息发送到与消息目标队列指定的死信交换机中
     * 实现延迟发送 --延迟队列
     */
    @Test
    public void testTTLMessage() {
        Message msg = MessageBuilder
                .withBody("TTLExchange".getBytes(StandardCharsets.UTF_8))
                .build();
        //消息ID 需要封装到CorrelationData中  每条死信消息指定一个唯一ID用于区分
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        //给correlationData添加一个Confirm确认
        correlationData.getFuture().addCallback(result -> {
                    if (result.isAck()) {
                        log.info("发送消息成功 id : {}", correlationData.getId());
                    } else {
                        log.info("消息发送失败, id:{},原因：{}", correlationData.getId(), result.getReason());
                    }
                },
                ex -> log.info("发送消息异常,ID:{},原因：{}", correlationData.getId(), ex.getMessage()));
        //发送消息
        rabbitTemplate.convertAndSend("ttl.direct","ttl",msg.getBody().toString(),correlationData);
    }

}
