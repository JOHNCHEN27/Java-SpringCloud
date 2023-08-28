package cn.itcast.hotel.constans;

public class MqConstants {
    /**
     * 定义交换机
     */
    public final static String HOTEL_EXCHANGE ="hotel.topic";
    /**
     * 监听新增和修改的队列 ES中索引库的新增和修改几乎一样只需定义一个
     */
    public final static String HOTEL_INSERT_QUEUE ="hotel.insert.queue";
    /**
     * 监听删除的队列
     */
    public final static String HOTEL_DELET_QUEUE = "hotel.delete.queue";
    /**
     * 新增或修改的RoutingKey
     */
    public final static String HOTEL_INSERT_KEY = "hotel.insert";
    /**
     * 删除的RoytingKey
     */
    public final static String HOTEL_DELETE_KEY = "hotel.delete";
}
