package com.xyt.ssyx.home.service.impl;

import com.xyt.ssyx.activity.ActivityFeignClient;
import com.xyt.ssyx.client.product.ProductFeignClient;
import com.xyt.ssyx.client.search.SkuFeignClient;
import com.xyt.ssyx.home.config.ThreadPoolConfig;
import com.xyt.ssyx.home.service.ItemService;
import com.xyt.ssyx.model.product.SkuInfo;
import com.xyt.ssyx.vo.product.SkuInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private ActivityFeignClient activityFeignClient;
    @Autowired
    private SkuFeignClient skuFeignClient;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Override
    public Map<String, Object> item(Long skuId, Long userId) {
        Map<String, Object> result = new HashMap<>();

        //skuId查询
        CompletableFuture<SkuInfo> skuInfoVoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            //远程调用获取sku对应数据
            SkuInfoVo skuInfoVo = productFeignClient.getSkuInfoVoById(skuId);
            result.put("skuInfoVo", skuInfoVo);
            return skuInfoVo;
        },threadPoolExecutor);

        //sku对应优惠卷信息
        CompletableFuture<Void> activityCompletableFuture = CompletableFuture.runAsync(() -> {
            //远程调用获取优惠卷信息
            Map<String,Object> activityMap = activityFeignClient.findActivityAndCoupon(skuId,userId);
            result.putAll(activityMap);
        },threadPoolExecutor);
        //更新商品热度
        CompletableFuture<Void> hotCompletableFuture = CompletableFuture.runAsync(() -> {
            //远程调用更新热度
            skuFeignClient.incrHotScore(skuId);
        },threadPoolExecutor);
        //任务组合
        CompletableFuture.allOf(skuInfoVoCompletableFuture,activityCompletableFuture,hotCompletableFuture).join();
        return result;
    }
}
