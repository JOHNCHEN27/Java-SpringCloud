package com.lncanswer.factory;

import com.lncanswer.clients.UserClient;
import com.lncanswer.pojo.User;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 定义类实现FallbackFactory  此接口可以对远程调用的异常做处理，而FallbackClass不能
 */
@Slf4j
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {
    /**
     * 创建UserClient 接口实现类，调用其中的方法编写降级处理逻辑
     * @param throwable
     * @return
     */
    @Override
    public UserClient create(Throwable throwable) {
        return new UserClient() {
            @Override
            public User findById(Long id) {
                log.info("查询用户失败！");
                //根据业务需求返回默认的数据 这里是空用户
                return new User();
            }
        };
    }
}
