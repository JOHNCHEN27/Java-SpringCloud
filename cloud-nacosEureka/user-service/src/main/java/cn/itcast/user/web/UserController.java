package cn.itcast.user.web;

import cn.itcast.user.config.PatternProperties;
import cn.itcast.user.pojo.User;
import cn.itcast.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/user")
//@RefreshScope //RefreshScope 此注解完成属性刷新 ，可以实现nacos配置管理的热部署（实时更新）
public class UserController {

    @Autowired
    private UserService userService;

    //测试是否成功配置了nacos客户端的配置管理
    //@Value("${pattern.dateForMate}")
    //private String patter ;

    @Autowired
    private PatternProperties patter;

    /**
     * 路径： /user/110
     *
     * @param id 用户id
     * @return 用户
     */
    @GetMapping("/{id}")
    public User queryById(@PathVariable("id") Long id) {
        return userService.queryById(id);
    }

    @GetMapping("now")
    public String now(){
        //第一种方式 value注解注入属性 需要RefreshScope注解
        //return LocalDateTime.now().format(DateTimeFormatter.ofPattern(patter));

        // 第二种方式注入属性，通过ConfigurationProperties 指定配置文件中的前缀 不需要加RefreshScope注解
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(patter.getDateForMate()));
    }
}
