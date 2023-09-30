package com.xyt.ssyx.home.controller;

import com.xyt.ssyx.client.product.ProductFeignClient;
import com.xyt.ssyx.common.auth.AuthContextHolder;
import com.xyt.ssyx.common.result.Result;
import com.xyt.ssyx.home.service.HomeService;
import com.xyt.ssyx.vo.search.SkuEsQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "首页接口")
@RestController
@RequestMapping("/api/home")
public class HomeApiController {

    @Autowired
    private HomeService homeService;


    @ApiOperation("首页数据显示")
    @GetMapping("/index")
    public Result index(){
      //  Long userId = AuthContextHolder.getUserId();

        Map<String,Object> map = homeService.homeData();
        return Result.ok(map);
    }

}
