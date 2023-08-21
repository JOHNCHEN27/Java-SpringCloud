package com.lncanswer;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(-1) //指定此过滤器执行顺序 数字越小执行优先级越高
@Component  //定义成bean加入到IOC容器管理
public class AuthorizeFilter implements GlobalFilter {
    /**
     * 判断请求头中是否携带authorization参数 判断参数值是否等于admin
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1、获取请求参数
        ServerHttpRequest request =  exchange.getRequest();
        MultiValueMap<String,String> params =request.getQueryParams();
        //2、获取参数中的authorization参数
        String auth = params.getFirst("authorization");
        System.out.println(auth);
        //3、判断参数值是否等于admin
        if("admin".equals(auth)){
            //4.是，放行
            return chain.filter(exchange);
        }
        //5、否，拦截
        //设置状态码  HttpStatus.unauthorized = 401
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        //拦截请求
        return exchange.getResponse().setComplete();
    }
}
