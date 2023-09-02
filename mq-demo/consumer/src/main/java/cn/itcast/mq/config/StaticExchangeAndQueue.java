package cn.itcast.mq.config;


import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class StaticExchangeAndQueue {
    /**
     * 创建静态的交换机和队列
     */
    public static final String TEST_EXCHANGE = "test.direct";

    public static final String TEST_QUEUE = "test.queue";

}
