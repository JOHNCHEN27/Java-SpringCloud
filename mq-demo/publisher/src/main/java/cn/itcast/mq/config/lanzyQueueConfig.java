package cn.itcast.mq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class lanzyQueueConfig {
    /**
     * 声明一个惰性队列
     * 有如下特征：
     * 惰性队列接受到消息之后直接存入磁盘而非内存
     * 消费者需要消费消息的时候，才会从磁盘中读取并加载到内存
     * 支持数百万条消息存储
     */

    @Bean
    public Queue lazyQueue(){
        return QueueBuilder.durable("lanzy.queue")
                .lazy() //指定lazy属性 开启x-queue-mode为lazy 声明为惰性队列
                .build();
    }
}
