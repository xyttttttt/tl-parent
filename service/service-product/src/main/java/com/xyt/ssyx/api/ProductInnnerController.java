package com.xyt.ssyx.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xyt.ssyx.common.result.Result;
import com.xyt.ssyx.model.product.Category;
import com.xyt.ssyx.model.product.SkuInfo;
import com.xyt.ssyx.service.CategoryService;
import com.xyt.ssyx.service.SkuInfoService;
import com.xyt.ssyx.vo.product.SkuInfoVo;
import com.xyt.ssyx.vo.product.SkuStockLockVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductInnnerController {

    @Autowired
    private CategoryService  categoryService;
    @Autowired
    private SkuInfoService skuInfoService;

    //根据分类获取分类信息
    @GetMapping("/inner/getCategory/{categoryId}")
    public Category getCategory(@PathVariable Long categoryId){
        Category category = categoryService.getById(categoryId);
        return category;
    }
    //根据skuid获取sku信息
    @GetMapping("/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId){
        SkuInfo skuInfo = skuInfoService.getById(skuId);
        return skuInfo;
    }

    //根据skuId列表得到sku信息列表
    @PostMapping("/inner/findSkuInfoList")
    public List<SkuInfo> findSkuInfoList(@RequestBody List<Long> skuIds){
        return skuInfoService.findSkuInfoList(skuIds);
    }
    //根据关键字匹配sku列表
    @GetMapping("/inner/findSkuInfoByKeyword/{keyword}")
    public List<SkuInfo> findSkuInfoByKeyword(@PathVariable String keyword){
        List<SkuInfo> list = skuInfoService.findSkuInfoByKeyword(keyword);
        return list;
    }

    //根据分类Id列表得到分类信息列表
    @PostMapping("/inner/findCategoryList")
    public List<Category> findCategoryList(@RequestBody List<Long> categoryIds){
        return categoryService.listByIds(categoryIds);
    }

    //获取所有分类
    @GetMapping("/inner/findAllCategoryList")
    public List<Category> findAllCategoryList(){
        List<Category> list = categoryService.list();
        return list;
    }

    //获取新人专享商品
    @GetMapping("/inner/findNewPersonSkuInfoList")
    public List<SkuInfo> findNewPersonSkuInfoList(){
       List<SkuInfo> list = skuInfoService.findNewPersonSkuInfoList();
       return list;
    }
    //根据skuId获取商品详情
    @GetMapping("/inner/getSkuInfoVoById/{skuId}")
    public SkuInfoVo getSkuInfoVoById(@PathVariable Long skuId){
        SkuInfoVo skuInfoVo = skuInfoService.getSkuInfoVo(skuId);
        return skuInfoVo;
    }
    //验证和锁定库存
    @PostMapping("inner/checkAndLock/{orderNo}")
    public Boolean checkAndLock(@RequestBody List<SkuStockLockVo> skuStockLockVoList,
                                @PathVariable("orderNo") String orderNo){
        return skuInfoService.checkAndLock(skuStockLockVoList,orderNo);
    }
}
