package cn.itcast.hotel.config;

import cn.itcast.hotel.constants.MqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {
    /**
     * 定义交换机的Bean交给IOC容器管理
     */
    @Bean
    public TopicExchange topicExchange(){
        //利用实现定义好的静态常量名  防止写错  持久化
        return new TopicExchange(MqConstants.HOTEL_EXCHANGE,true,false);
    }
    /**
     * 定义新增和修改队列的Bean
     */
    @Bean
    public Queue insertAndUpdateQueue(){
        return new Queue(MqConstants.HOTEL_INSERT_QUEUE,true);
    }
    /**
     * 定义删除的队列
     */
    @Bean
    public Queue deleteQueue(){
        return new Queue(MqConstants.HOTEL_DELET_QUEUE,true);
    }
    /**
     * 新增和修改队列绑定交换机 利用Binding 绑定交换机及RoutingKey
     */
    @Bean
    public Binding insertQueueBinding(){
        return BindingBuilder.bind(insertAndUpdateQueue()).to(topicExchange()).with(MqConstants.HOTEL_INSERT_KEY);
    }
    /**
     * 删除队列绑定交换机和RoutingKey
     */
    @Bean
    public Binding deleteQueueBinding(){
        return BindingBuilder.bind(deleteQueue()).to(topicExchange()).with(MqConstants.HOTEL_DELETE_KEY);
    }

}
