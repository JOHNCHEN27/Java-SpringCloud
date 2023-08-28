package cn.itcast.hotel.controller;

import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParams;
import cn.itcast.hotel.service.IHotelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/hotel")
public class HotelController {
    @Autowired
    private IHotelService hotelService;
    @PostMapping("/list")
    public PageResult search(@RequestBody RequestParams params) throws IOException {
       return hotelService.search(params);
    }

    @PostMapping("filters")
    public Map<String , List<String>> getFilters(@RequestBody RequestParams params){
        return hotelService.filters(params);
    }

    /**
     * 搜索框根据拼音自动分词
     */
    @GetMapping("/suggestion")
    public List<String> getSuggestion(@RequestParam("key") String key){
        return hotelService.suggestion(key);
    }





}
