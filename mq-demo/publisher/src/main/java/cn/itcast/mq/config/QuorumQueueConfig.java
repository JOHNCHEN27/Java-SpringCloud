package cn.itcast.mq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class QuorumQueueConfig {

    /**
     * 声明一个仲裁队列 需要配置MQ的集群模式才可以使用 设置模式为quorum
     */
    //@Bean
    public Queue quorumQueue(){
        return QueueBuilder
                .durable("quorum.queue")
                .quorum().build();
    }
}
