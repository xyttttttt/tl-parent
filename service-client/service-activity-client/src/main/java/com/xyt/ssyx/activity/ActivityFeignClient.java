package com.xyt.ssyx.activity;

import com.xyt.ssyx.model.activity.CouponInfo;
import com.xyt.ssyx.model.order.CartInfo;
import com.xyt.ssyx.vo.order.CartInfoVo;
import com.xyt.ssyx.vo.order.OrderConfirmVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient("service-activity")
public interface ActivityFeignClient {


    @PostMapping("/api/activity/inner/findActivity")
    public Map<Long, List<String>> findActivity(@RequestBody List<Long> skuIdList);

    //根据skuId获取营销数据和优惠卷
    @ApiOperation("根据skuId获取营销数据和优惠卷")
    @GetMapping("/api/activity/inner/findActivityAndCoupon/{skuId}/{userId}")
    public Map<String,Object> findActivityAndCoupon(@PathVariable("skuId") Long skuId, @PathVariable("userId") Long userId);

    //获取购物车满足条件的优惠卷优惠活动的信息
    @PostMapping("/api/activity/inner/findCartActivityAndCoupon/{userId}")
    public OrderConfirmVo findCartActivityAndCoupon(@PathVariable("userId") Long userId
            , @RequestBody List<CartInfo> cartInfoList);

    //获取购物项对应规则
    @PostMapping("/api/activity/inner/findCartActivityList")
    public List<CartInfoVo> findCartActivityList(@RequestBody List<CartInfo> cartInfoList);

    //获取购物车里对应优惠卷
    @PostMapping("/api/activity/inner/findRangeSkuIdList/{couponId}")
    public CouponInfo findRangeSkuIdList(@RequestBody List<CartInfo> cartInfoList, @PathVariable("couponId") Long couponId);

    //更新优惠卷使用状态
    @GetMapping("/api/activity/inner/updateCouponInfoUseStatus/{couponId}/{userId}/{orderId}")
    public Boolean updateCouponInfoUseStatus(@PathVariable("couponId") Long couponId
                                            ,@PathVariable("userId") Long userId
                                            ,@PathVariable("orderId") Long orderId);
}
