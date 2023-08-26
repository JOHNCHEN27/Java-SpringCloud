package cn.itcast.hotel.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestHighLevelClient {
    public RestHighLevelClient(RestClientBuilder builder) {
    }

    /**
     * 将RestHighLevelClient对象注入IOC容器
     */
    @Bean
    public RestHighLevelClient client(){
       return new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.12.132:9200")
        ));
    }
}
