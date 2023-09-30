package com.xyt.ssyx.home.controller;

import com.xyt.ssyx.client.product.ProductFeignClient;
import com.xyt.ssyx.common.result.Result;
import com.xyt.ssyx.model.product.Category;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "商品分类")
@RestController
@RequestMapping("/api/home")
public class CategoryApiController {

    @Autowired
    private ProductFeignClient productFeignClient;
    //查询所有分类
    @GetMapping("/category")
    public Result categoryList(){
        List<Category> allCategoryList = productFeignClient.findAllCategoryList();
        return Result.ok(allCategoryList);
    }
}
