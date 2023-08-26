package cn.itcast.hotel.pojo;

import lombok.Data;

import java.util.List;

/**
 * 返回类型
 */
@Data
public class PageResult {
    private Long total;
    private List<HotelDoc> hotels;
}
