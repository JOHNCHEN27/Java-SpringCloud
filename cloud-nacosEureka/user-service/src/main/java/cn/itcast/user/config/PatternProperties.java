package cn.itcast.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;


@Data
@ConfigurationProperties(prefix = "pattern")  //此注解可以实现配置属性的热部署（实时更新）
@Component
public class PatternProperties {
    //注入配置文件中的属性 利用注解ConfigurationProperties 指定前缀 ，属性名为其后缀名
    private String dateForMate;
}
