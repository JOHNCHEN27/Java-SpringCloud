package cn.itcast.hotel.mq;

import cn.itcast.hotel.constants.MqConstants;
import cn.itcast.hotel.service.IHotelService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HotelListener {

    @Autowired
    private IHotelService iHotelService;
    /**
     * 监听消息 根据id新增或修改
     * @param id
     */
    @RabbitListener(queues = MqConstants.HOTEL_INSERT_QUEUE)
    public void listenHotelInsertOrUpdate(Long id){
        iHotelService.insertById(id);
    }

    /**
     * 监听消息根据id删除
     * @param id
     */
    @RabbitListener(queues = MqConstants.HOTEL_DELET_QUEUE)
    public void listenHotelDelete(Long id){
           iHotelService.deleteById(id);
    }
}
