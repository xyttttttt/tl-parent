package com.xyt.ssyx.client.product;

import com.xyt.ssyx.model.product.Category;
import com.xyt.ssyx.model.product.SkuInfo;
import com.xyt.ssyx.vo.product.SkuInfoVo;
import com.xyt.ssyx.vo.product.SkuStockLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "service-product")
public interface ProductFeignClient {


    //根据分类获取分类信息
    @GetMapping("/api/product/inner/getCategory/{categoryId}")
    public Category getCategory(@PathVariable("categoryId") Long categoryId);


    //根据skuid获取sku信息
    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId);

    //根据skuId列表得到sku信息列表
    @PostMapping("/api/product/inner/findSkuInfoList")
    public List<SkuInfo> findSkuInfoList(@RequestBody List<Long> skuIds);

    //根据关键字匹配sku列表
    @GetMapping("/api/product/inner/findSkuInfoByKeyword/{keyword}")
    public List<SkuInfo> findSkuInfoByKeyword(@PathVariable String keyword);

    @PostMapping("/api/product/inner/findCategoryList")
    public List<Category> findCategoryList(@RequestBody List<Long> categoryIds);


    //获取所有分类
    @GetMapping("/api/product/inner/findAllCategoryList")
    public List<Category> findAllCategoryList();

    //获取新人专享商品
    @GetMapping("/api/product/inner/findNewPersonSkuInfoList")
    public List<SkuInfo> findNewPersonSkuInfoList();

    //根据skuId获取商品详情
    @GetMapping("/api/product/inner/getSkuInfoVoById/{skuId}")
    public SkuInfoVo getSkuInfoVoById(@PathVariable Long skuId);

    //验证和锁定库存
    @PostMapping("/api/product/inner/checkAndLock/{orderNo}")
    public Boolean checkAndLock(@RequestBody List<SkuStockLockVo> skuStockLockVoList,
                                @PathVariable("orderNo") String orderNo);
}
