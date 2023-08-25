package cn.itcast.hotel;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static cn.itcast.hotel.constants.HotelConstants.HOTEL_DSL;

public class HotelIndexTest {
    //定义restHighLevelClient客户端对象
    private RestHighLevelClient restHighLevelClient;

    @Test
    void testInit(){
        System.out.println(restHighLevelClient);
    }

    /**
     * 创建索引库  利用indices对象 说明需要创建索引库的名称 和DSL语句
     * @throws IOException
     */
    @Test
    void createHotelIndex() throws IOException {
        //1、创建Request对象 对应需要创建索引库的名称 hotel
        CreateIndexRequest request = new CreateIndexRequest("hotel");
        //2、准备请求的语句 也就是我们在客户端写的DSL语句
        request.source(HOTEL_DSL, XContentType.JSON);
        //3、发送请求 创建索引库
        restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
    }

    /**
     * 查询索引库  根据索引库名称进行查询
     */
    @Test
    void getHotelIndex() throws IOException {
        //1、创建Request 查询对象 查询索引库
        GetIndexRequest request = new GetIndexRequest("hotel");
        //2、发送请求 查询索引库不需要编写内容
        GetIndexResponse exists = restHighLevelClient.indices().get(request,RequestOptions.DEFAULT);
        //3、输出 查看是否存在
        System.err.println( exists != null ? "索引库已经存在" : "索引库不存在");

    }

    /**
     * 删除索引库  delete
     */
    @Test
    void deleteHotelIndex() throws IOException {
        //1、创建request delete对象
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("hotel");
        //2、发送请求删除索引库
        restHighLevelClient.indices().delete(deleteIndexRequest,RequestOptions.DEFAULT);
    }

    /**
     * 之后所有的单元测试在加载方法的时候 会先运行Before 在运行After
     */
    @BeforeEach
    void setUp(){
        this.restHighLevelClient = new RestHighLevelClient(RestClient.builder(
                //绑定es的客户端
                HttpHost.create("http://192.168.12.132:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        //使用完成之后关闭
        this.restHighLevelClient.close();
    }
}
