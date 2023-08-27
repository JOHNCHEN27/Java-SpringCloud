package cn.itcast.hotel.service.impl;

import cn.itcast.hotel.config.RestHighLevelClientConfig;
import cn.itcast.hotel.mapper.HotelMapper;
import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParams;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {

    @Autowired
    private RestHighLevelClient client;
    @Override
    public PageResult search(RequestParams params) throws IOException {
        //拿到分页大小
        Integer page = params.getPage();
        Integer pageSize = params.getSize();
        //1、创建request请求对象
        SearchRequest request = new SearchRequest("hotel");
        //2、准备DSL语句
        //构造BooleanQuery
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //拿到前端参数提供的key
        String key = params.getKey();
        //健壮性判断
        if(key == null ||"".equals(key))
        {
            boolQuery.must(QueryBuilders.matchAllQuery());
        }else {
            boolQuery.must(QueryBuilders.matchQuery("all",key));
        }

        //filter过滤 城市条件
        if(params.getCity() != null && !params.getCity().equals("")){
            boolQuery.filter(QueryBuilders.termQuery("city",params.getCity()));
        }
        //品牌条件
        if (params.getBrand() != null && !params.getBrand().equals("")){
            boolQuery.filter(QueryBuilders.termQuery("brand",params.getBrand()));
        }
        //星级条件
        if (params.getStarName() !=null && !params.getStarName().equals("")){
            boolQuery.filter(QueryBuilders.termQuery("starName",params.getStarName()));
        }
        //价格
        if (params.getMaxPrice() != null && !params.getMaxPrice().equals("")){
            boolQuery.filter(QueryBuilders
                    .rangeQuery("price")
                    .gte(params.getMinPrice()).lte(params.getMaxPrice()));
        }
        //算分控制 function相关性查询 第一个参数为查询条件，第二个参数为funcation数组
        FunctionScoreQueryBuilder functionScoreQueryBuilder =
                QueryBuilders.functionScoreQuery(boolQuery,
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                                //在funcation数组里计算相关性
                                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                //isAD属性为true则可以算分
                                QueryBuilders.termQuery("isAD",true),
                                ScoreFunctionBuilders.weightFactorFunction(10)
                                )
                        });


        request.source().query(functionScoreQueryBuilder);
        //2.2分页请求 起始位置等于 page -1 乘以 pageSize 链式编程
        request.source().from((page-1)*pageSize).size(pageSize);
        //2.3 排序
        String location = params.getLocation();
        if(location != null && !location.equals("")){
            request.source().sort(SortBuilders
                    .geoDistanceSort("location",new GeoPoint(location))
                    .order(SortOrder.ASC)
                    .unit(DistanceUnit.KILOMETERS));
        }

        //3、发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //解析结果
        return getPageResult(response);
    }

    private static PageResult getPageResult(SearchResponse response) {
        SearchHits hits = response.getHits();
        //拿到总记录条数
        Long total = hits.getTotalHits().value;
        System.out.println("记录总条数：" +total);
        //拿到内容
        SearchHit[] hits1 = hits.getHits();
        List<HotelDoc> list = new ArrayList<>();
        for(SearchHit hit : hits1){
            String sourceAsString = hit.getSourceAsString();
            //利用JSON转换对象 将每个文档存入实体类中
            HotelDoc hotelDoc = JSON.parseObject(sourceAsString, HotelDoc.class);
            //拿到hits数组中的距离
            Object[] sortValues = hit.getSortValues();
            if (sortValues.length > 0)
            {
                Object sortValue = sortValues[0];
                //封装到hotelDoc中
                hotelDoc.setDistance(sortValue);
            }

            //将每个对象封装到响应结果对象中
            list.add(hotelDoc);
        }
        PageResult pageResult = new PageResult(total,list);
        return pageResult;
    }
}
