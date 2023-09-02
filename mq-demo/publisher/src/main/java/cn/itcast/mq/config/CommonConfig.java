package cn.itcast.mq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class CommonConfig implements ApplicationContextAware {
    /**
     * 每一个RabbitTemplate只能配置一个ReturnCallBack  实现接口 在项目启动过程中配置
     * @param applicationContext the ApplicationContext object to be used by this object
     * @throws BeansException
     */

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //获取RabbitTemplate
        RabbitTemplate rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        //设置ReturnCallBack
        rabbitTemplate.setReturnCallback((message, replycode, replyText, exchange, routingKey) ->{
            //如果消息发送到队列失败 直接记录失败信息
          log.info("消息发送失败，应答码：{},原因：{},交换机:{},路由键:{},消息：{}",
                  replycode,replyText,exchange,routingKey,message.toString());
          //也可以进一步进行消息的重发
        });
    }
}
