package cn.itcast.hotel.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestHighLevelClientConfig {
    public RestHighLevelClientConfig(RestClientBuilder builder) {
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
