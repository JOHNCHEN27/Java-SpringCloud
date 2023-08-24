package cn.itcast.mq.listner;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration  //配置类交给spring管理
public class FanoutExchangeQueue {
    /**
     * 创建交换机和两个队列的bean
     */
    //创建交换机
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange("lnc.fanout");
    }

    //创建第一个队列
    @Bean
    public Queue fanoutQueue1(){
        return new Queue("lnc.queue1");
    }
    //将第一个队列与交换机绑定
    @Bean
    public Binding bindingQueue1(Queue fanoutQueue1,FanoutExchange fanoutExchange){
        return BindingBuilder.bind(fanoutQueue1).to(fanoutExchange);
    }

    //创建第二个队列
    @Bean
    public Queue fanoutQueue2(){
        return new Queue("lnc.queue2");
    }

    //将第二个队列进行绑定
    @Bean
    public Binding bindQueue2(Queue fanoutQueue2,FanoutExchange fanoutExchange){
        return BindingBuilder.bind(fanoutQueue2).to(fanoutExchange);
    }
}
