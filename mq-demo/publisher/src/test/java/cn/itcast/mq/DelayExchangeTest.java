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

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class DelayExchangeTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testDelayExchange(){
      //创建消息
        Message message = MessageBuilder
                .withBody("delayExchange".getBytes(StandardCharsets.UTF_8))
                .setHeader("x-delay",5000) //必须指定x-delay属性 指定延迟时长
                .build();

        //消息ID 需要封装消息到correlationData
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        correlationData.getFuture().addCallback( result -> {
            if (result.isAck())
            {
                log.info("消息发送成功,ID:{}",correlationData.getId());
            }else
            {
                log.info("消息发送失败,ID{}，失败原因：{}",correlationData.getId(),result.getReason());
            }
        },ex -> log.info("消息发送异常,id:{}，异常消息：{}",correlationData.getId(),ex.getMessage()));
        //发送消息
        rabbitTemplate.convertAndSend("delay.direct","delay",message.getMessageProperties().toString(),correlationData);
    }


}
