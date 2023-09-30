package com.xyt.ssxy.activity.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xyt.ssxy.activity.service.ActivityInfoService;
import com.xyt.ssxy.activity.service.CouponInfoService;
import com.xyt.ssxy.activity.service.CouponUseService;
import com.xyt.ssyx.model.activity.CouponInfo;
import com.xyt.ssyx.model.activity.CouponUse;
import com.xyt.ssyx.model.order.CartInfo;
import com.xyt.ssyx.vo.order.CartInfoVo;
import com.xyt.ssyx.vo.order.OrderConfirmVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "促销与优惠卷接口")
@RestController
@RequestMapping("/api/activity")
public class ActivityInfoApiController {


    @Autowired
    private ActivityInfoService activityInfoService;
    @Autowired
    private CouponInfoService couponInfoService;

    @ApiOperation("根据skuId俩表获取促销信息")
    @PostMapping("inner/findActivity")
    public Map<Long, List<String>> findActivity(@RequestBody List<Long> skuIdList){
        return   activityInfoService.findActivity(skuIdList);
    }
    //根据skuId获取营销数据和优惠卷
    @ApiOperation("根据skuId获取营销数据和优惠卷")
    @GetMapping("inner/findActivityAndCoupon/{skuId}/{userId}")
    public Map<String,Object> findActivityAndCoupon(@PathVariable Long skuId,@PathVariable Long userId){
        Map<String,Object> map = new HashMap<>();
        map = activityInfoService.findActivityAndCoupon(skuId,userId);
        return map;
    }

    //获取购物车满足条件的优惠卷优惠活动的信息
    @PostMapping("inner/findCartActivityAndCoupon/{userId}")
    public OrderConfirmVo findCartActivityAndCoupon(@PathVariable("userId") Long userId
                                                    , @RequestBody List<CartInfo> cartInfoList){
        return activityInfoService.findCartActivityAndCoupon(cartInfoList,userId);
    }

    //获取购物项对应规则
    @PostMapping("inner/findCartActivityList")
    public List<CartInfoVo> findCartActivityList(@RequestBody List<CartInfo> cartInfoList){
        return activityInfoService.findCartActivityList(cartInfoList);
    }

    //获取购物车里对应优惠卷
    @PostMapping("inner/findRangeSkuIdList/{couponId}")
    public CouponInfo findRangeSkuIdList(@RequestBody List<CartInfo> cartInfoList,@PathVariable("couponId") Long couponId){
        return couponInfoService.findRangeSkuIdList(cartInfoList,couponId);
    }

    //更新优惠卷使用状态
    @GetMapping("/inner/updateCouponInfoUseStatus/{couponId}/{userId}/{orderId}")
    public Boolean updateCouponInfoUseStatus(@PathVariable("couponId") Long couponId
                                                ,@PathVariable("userId") Long userId
                                             ,@PathVariable("orderId") Long orderId){
        couponInfoService.updateCouponInfoUseStatus(couponId,userId,orderId);
        return true;
    }
}
