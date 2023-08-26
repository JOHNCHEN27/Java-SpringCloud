package cn.itcast.hotel.pojo;

import lombok.Data;

/**
 * 接受前端参数实体类
 */
@Data
public class RequestParams {
    private String key;
    private Integer page;
    private Integer size;
    private String sortBy;
}
