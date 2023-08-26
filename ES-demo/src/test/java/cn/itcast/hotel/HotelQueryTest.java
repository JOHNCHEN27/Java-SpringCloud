package cn.itcast.hotel;

import cn.itcast.hotel.pojo.HotelDoc;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

public class HotelQueryTest {
    /**
     * 练习ES搜索
     */

    private RestHighLevelClient client;
    @BeforeEach
    void setUp(){
        this.client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.12.132:9200")
        ));
    }
    @AfterEach
    void tearDown() throws IOException {
        this.client.close();;
    }

    /**
     * 将共同的结果抽取为一个方法  快捷键 Ctrl+alt+m
     * @param response
     */
    private static void handleResponse(SearchResponse response) {
        //4、解析结果
        SearchHits hits = response.getHits();
        //4.1查询总条数
        long total = hits.getTotalHits().value;
        System.out.println("查询总条数："+total);

        //4.2查询结果数组
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit searchHit :hits1 ){
            //4.3 得到source资源
            String json = searchHit.getSourceAsString();
            //反序列化 将string转换成 JSON对象
            HotelDoc hotelDoc = JSON.parseObject(json,HotelDoc.class);
            //拿到高亮结果
            Map<String, HighlightField> highlight = searchHit.getHighlightFields();

            HighlightField name = highlight.get("city");
            //获取高亮值
            if(name != null) {
                String highlightName = name.getFragments()[0].string();

                hotelDoc.setName(highlightName);
            }

            System.out.println(hotelDoc);
        }
    }



    /**
     * 查询hotel 所有文档 并解析结果  Ctrl+alt+m 抽取部分内容
     * @throws IOException
     */
    @Test
    void testMatchAll() throws IOException {
        //1、创建request对象
        SearchRequest request = new SearchRequest("hotel");
        //2、编写DSL
        request.source().query(QueryBuilders.matchAllQuery());
        //3、发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

       // System.out.println(response)
        handleResponse(response);
    }

    /**
     *查询 match方法
     */
    @Test
    void testMatch() throws IOException {
        //1、创建request对象
        SearchRequest request = new SearchRequest("hotel");
        //2、DSL编写  match查询 ： 字段名 加具体数值
        request.source().query(QueryBuilders.matchQuery("all","如家"));
        //3、发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //4、解析结果
        SearchHits hits = response.getHits();
        TotalHits total = hits.getTotalHits();
        System.out.println("获取的记录数："+ total);

        SearchHit[] hits1 = hits.getHits();
        for (SearchHit hit : hits1){
            String Json = hit.getSourceAsString();
            System.out.println(Json);
        }
    }

    /**
     * booleanSeach
     */
    @Test
    void testBooleanSeach() throws IOException {
        //1、创建request对象
        SearchRequest request = new SearchRequest("hotel");
        //2、创建BoolRequest对象 利用request加载DSL
        BoolQueryBuilder boolQuery =QueryBuilders.boolQuery();
        // term
        boolQuery.must(QueryBuilders.termQuery("city","上海"));
        //range
        boolQuery.filter(QueryBuilders.rangeQuery("price").lte(300));
        //request发送请求
        request.source().query(boolQuery);
        //3、client发送请求
        SearchResponse response = client.search(request,RequestOptions.DEFAULT);
        //4、解析结果
        handleResponse(response);
    }

    /**
     * 查询分页 query from size
     */
    @Test
    void testPageSearch() throws IOException {
        //1、创建request对象
        SearchRequest request = new SearchRequest("hotel");
        //2.1 构建查询条件 查询如家酒店 价格小于等于三百
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        boolQuery.must(QueryBuilders.termQuery("brand","如家"));
        boolQuery.filter(QueryBuilders.rangeQuery("price").lte(300));

        request.source().query(boolQuery);
        //2.2 设置起始查询页 from
        request.source().from(0);
        //2.3设置一页查多少条文档（数据）
        request.source().size(15);

        //3 RestHighLevelClient 对象发送请求给ES服务
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //4、解析结果
        handleResponse(response);
    }

    /**
     * highlight高亮展示 利用HighlightBuilder对象
     */
    @Test
    void testHighlight() throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        //match查询的语法 字段名：字段数值
        request.source().query(QueryBuilders.matchQuery("city","上海"));
        //注意需要高亮展示的名字必须与查询的字段名一致 否则不能进行高亮展示 可以设置取消
        request.source().highlighter(new HighlightBuilder()
                .field("city")
                .requireFieldMatch(false));

       SearchResponse response = client.search(request,RequestOptions.DEFAULT);
       handleResponse(response);

    }



}
