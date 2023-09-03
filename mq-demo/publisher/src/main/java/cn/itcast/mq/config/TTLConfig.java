package cn.itcast.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TTLConfig {
    /**
     * 可以给消息和队列指定过期时间， 当任意一个过期时，会将消息转发到与队列绑定的死信交换机中
     * 当消息和队列都设置了过期时间之后， 以短的时间为主
     * 注意需要把MessageRecoverer设置为默认的类型 当重试之后直接拒绝 然后消息才会传送到队列
     * 绑定的死信交换机，如果设置的是重试之后转发到指定的交换机类型，那么消息重发上线之后
     * 不会发送到队列的死信交换机
     * @return
     */

    @Bean
    public DirectExchange ttlExchange(){
        return new DirectExchange("ttl.direct");
    }

    /**
     * 在队列中配置一个dead-letterExchange 指定一个交换机 那么这个队列中的死信会发送到指定的交换机中
     * 以及与这个交换机绑定routingkey的队列中
     * @return
     */
    @Bean
    public Queue ttlQueue(){
        return QueueBuilder.durable("ttl.queue")
                .ttl(5000)  //设置队列的超时时间为5秒
                .deadLetterExchange("dl.direct")  //指定死信交换机
                .deadLetterRoutingKey("dl") //指定死信交换机的RoutingKey
                .build();
    }

    /**
     * 将交换机和队列进行绑定
     * @return
     */
    @Bean
    public Binding ttlBinding(){
        return BindingBuilder.bind(ttlQueue()).to(ttlExchange()).with("ttl");
    }
}
