package cn.itcast.hotel;

import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
public class HotelDocumentTest {
    private RestHighLevelClient client;

    @Autowired
    private IHotelService iHotelService;

    /**
     * 新增文档 AddDocument
     */
    @Test
    void addHotelDocument() throws IOException {
        //利用Ihotel mp对象实现增删改查
        Hotel id = iHotelService.getById(61083);
        //转换为文档类型
        HotelDoc hotelDoc = new HotelDoc(id);

        //1、创建文档连接对象
        IndexRequest request = new IndexRequest("hotel").id(hotelDoc.getId().toString());
        //2、准备Json文档
        request.source(JSON.toJSONString(hotelDoc), XContentType.JSON);
        //3、发送请求
        client.index(request, RequestOptions.DEFAULT);
    }

    /**
     * 查询文档信息
     */
    @Test
    void testGetDoucument() throws IOException {
        //1、创建request对象
        GetRequest hotel = new GetRequest("hotel", "61083");
        //2、发送请求
        GetResponse response = client.get(hotel,RequestOptions.DEFAULT);
        //3、解析结果
        String json =response.getSourceAsString();

        HotelDoc hotelDoc =JSON.parseObject(json,HotelDoc.class);
        System.out.println(hotelDoc);
    }

    /**
     * 根据id修改酒店数据 ES中分为全部更新和局部更新
     * 全部更新方式跟新增方式几乎没有区别
     * 这里使用局部更新
     */
    @Test
    void testupdateDocumentById() throws IOException {
        //1、创建request对象
        UpdateRequest request = new UpdateRequest("hotel","61083");
        //2、准备参数 每两个参数为一对key value
        request.doc(
                "name","上海滴水湖皇冠假日酒店"
        );
        //3、更新文档
        client.update(request,RequestOptions.DEFAULT);
    }

    /**
     * 删除文档信息
     */
    @Test
    void testDeleteDocumentById() throws IOException {
        //1、创建request对象
        DeleteRequest request = new DeleteRequest("hotel","61083");
        //2、发送请求
        client.delete(request,RequestOptions.DEFAULT);
    }

    /**
     * 批量添加文档   bulk
     */
    @Test
    void testBulkRequest() throws IOException {
        //  批量查询酒店数据
        List<Hotel> hotels = iHotelService.list();
        
        //1、创建Request对象
        BulkRequest request = new BulkRequest();
        //2、准备参数 将mysql查询到的数据封装到文档中
        for (Hotel hotel : hotels) {
            //转换为文档类型的HotelDoc
            HotelDoc hotelDoc = new HotelDoc(hotel);
            //创建新增文档的request对象
            request.add(new IndexRequest("hotel")
                    .id(hotelDoc.getId().toString())
                    .source(JSON.toJSONString(hotelDoc),XContentType.JSON));
        }
        //bulk批量发送请求
        client.bulk(request,RequestOptions.DEFAULT);

    }
    @BeforeEach
    void setUp(){
        this.client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.12.132:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        this.client.close();
    }
}
