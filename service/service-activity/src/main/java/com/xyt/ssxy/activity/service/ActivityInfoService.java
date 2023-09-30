package com.xyt.ssxy.activity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyt.ssyx.model.activity.ActivityInfo;
import com.xyt.ssyx.model.activity.ActivityRule;
import com.xyt.ssyx.model.order.CartInfo;
import com.xyt.ssyx.model.product.SkuInfo;
import com.xyt.ssyx.vo.activity.ActivityRuleVo;
import com.xyt.ssyx.vo.order.CartInfoVo;
import com.xyt.ssyx.vo.order.OrderConfirmVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 活动表 服务类
 * </p>
 *
 * @author xyt
 * @since 2023-07-10
 */
public interface ActivityInfoService extends IService<ActivityInfo> {

    IPage<ActivityInfo> selectPageActivityInfo(Page<ActivityInfo> pageParam);

    Map<String, Object> findActivityRuleList(Long id);

    void saveActivityRule(ActivityRuleVo activityRuleVo);

    List<SkuInfo> findSkuInfoByKeyword(String keyword);

    Map<Long, List<String>> findActivity(List<Long> skuIdList);

    Map<String, Object> findActivityAndCoupon(Long skuId, Long userId);

    //根据skuId获取到活动规则数据
    List<ActivityRule> findActivityRuleBySkuId(Long skuId);
    //获取购物车满足条件的优惠卷优惠活动的信息
    OrderConfirmVo findCartActivityAndCoupon(List<CartInfo> cartInfoList, Long userId);
    //获取购物项对应规则
    List<CartInfoVo> findCartActivityList(List<CartInfo> cartInfoList);
}
