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
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.processing.Completion;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 定义一个方法filters对城市 、 星级、品牌进行聚合
     * @return
     */
    @Override
    public Map<String, List<String>> filters(RequestParams params) {
        try {
            //创建request对象
            SearchRequest request = new SearchRequest("hotel");
            //DSL
            //添加一个查询条件 query
            boolQuery(params, request);
            //将相同的内容 抽取成方法 避免太多重复数据堆积
            extracted(request);
            //发送请求
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Map<String,List<String>> map = new HashMap<>();
            //解析结果
            Aggregations aggregations = response.getAggregations();
            List<String> brandlist = getStrings(aggregations,"brandAgg");
            map.put("品牌",brandlist);
            List<String> citylist = getStrings(aggregations,"cityAgg");
            map.put("城市",citylist);
            List<String> starNamelist = getStrings(aggregations,"starNameAgg");
            map.put("星级",starNamelist);

            return map;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ES自动分词  实现搜索框根据拼音自动分词
     * @param key
     * @return
     */
    @Override
    public List<String> suggestion(String key) {
        try {
            SearchRequest request = new SearchRequest("hotel");
            //编写DSL请求
            request.source().suggest(new SuggestBuilder().addSuggestion("suggestions",
                    SuggestBuilders.completionSuggestion("suggestion")
                            .prefix(key)  //拼音前缀
                            .skipDuplicates(true)  //跳过重复元素
                            .size(10))); //展示十条数据
            //发送请求
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            List<String> list = new ArrayList<>();
            //解析结果
            Suggest suggest = response.getSuggest();
            //根据suggestion名称获得具体的分词内容列表 转换成completionSuggestion对象
            CompletionSuggestion suggestion = suggest.getSuggestion("suggestions");
            //拿到suggestion列表
            List<CompletionSuggestion.Entry.Option> options = suggestion.getOptions();
            //遍历每个元素存放list列表中
            for (CompletionSuggestion.Entry.Option option : options) {
                String string = option.getText().toString();
                list.add(string);
            }
            //返回列表
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * boolQuery共同抽取
     * @param params
     * @param request
     */
    private static void boolQuery(RequestParams params, SearchRequest request) {
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
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
            boolQuery.filter(QueryBuilders.termQuery("city", params.getCity()));
        }
        //品牌条件
        if (params.getBrand() != null && !params.getBrand().equals("")){
            boolQuery.filter(QueryBuilders.termQuery("brand", params.getBrand()));
        }
        //星级条件
        if (params.getStarName() !=null && !params.getStarName().equals("")){
            boolQuery.filter(QueryBuilders.termQuery("starName", params.getStarName()));
        }
        //价格
        if (params.getMaxPrice() != null && !params.getMaxPrice().equals("")){
            boolQuery.filter(QueryBuilders
                    .rangeQuery("price")
                    .gte(params.getMinPrice()).lte(params.getMaxPrice()));
        }
        request.source().query(boolQuery);
    }

    /**
     * 解析返回响应的结果 在aggregations列表里
     * @param aggregations
     * @param aggName
     * @return
     */
    private static List<String> getStrings(Aggregations aggregations,String aggName) {
        //获取具体的聚合结果 根据聚合名称获取 将其返回为Terms对象
        Terms brandAgg = aggregations.get(aggName);
        List<String> list = new ArrayList<>();
        //获取Buckets集合 拿到聚合里的数据
        List<? extends Terms.Bucket> buckets = brandAgg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            String key = bucket.getKeyAsString();

            //将key值封装到list中
            list.add(key);
        }
        return list;
    }

    /**
     * 抽取相应的request aggregations请求 用到AggregationBuilders对象
     * @param request
     */
    private static void extracted(SearchRequest request) {
        request.source().aggregation(AggregationBuilders
                .terms("brandAgg") //聚合名称
                .field("brand") //聚合字段
                .size(20)); //展示条数
        request.source().aggregation(AggregationBuilders
                .terms("cityAgg")
                .field("city")
                .size(10));
        request.source().aggregation(AggregationBuilders
                .terms("starNameAgg")
                .field("starName")
                .size(10));
    }
}
