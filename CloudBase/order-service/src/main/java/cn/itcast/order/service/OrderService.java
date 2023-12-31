package cn.itcast.order.service;

import cn.itcast.order.mapper.OrderMapper;
import cn.itcast.order.pojo.Order;
import com.lncanswer.clients.UserClient;
import com.lncanswer.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    //改用feign来发送远程调用
    @Autowired
    private UserClient userClient;
//    @Autowired
//    private RestTemplate restTemplate;
//
//    public Order queryOrderById(Long orderId) {
//        // 1.查询订单
//        Order order = orderMapper.findById(orderId);
//        //2、利用RestTemplate对象来发起Http请求 获取用户信息
//        //获取请求路径 注意id不能写死
//        //这里userservice 是指eureka的服务名称，会从eureka去找相应的地址 不使用硬编码
//        String url = "http://userservice/user/" + order.getUserId();
//        //利用RestTemplate来发送请求，返回JSON格式的对象，可以指定返回的对象类型
//        User user = restTemplate.getForObject(url, User.class);
//        //封装到order对象中
//        order.setUser(user);
//        // 4.返回
//        return order;
//    }

       public Order queryOrderById(Long orderId){
           //1、查询订单
           Order order = orderMapper.findById(orderId);
           //2、用feign远程调用
           User user =userClient.findById(order.getUserId());
           //3、封装User和order
           order.setUser(user);
           //返回order
           return order;
       }
}
