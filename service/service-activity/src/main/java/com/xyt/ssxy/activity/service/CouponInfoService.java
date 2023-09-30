package com.xyt.ssxy.activity.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyt.ssyx.model.activity.CouponInfo;
import com.xyt.ssyx.model.order.CartInfo;
import com.xyt.ssyx.vo.activity.CouponRuleVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠券信息 服务类
 * </p>
 *
 * @author xyt
 * @since 2023-07-10
 */
public interface CouponInfoService extends IService<CouponInfo> {

    IPage<CouponInfo> selectPageCouponInfo(Page<CouponInfo> pageParam);

    CouponInfo getCouponInfo(Long id);

    Map<String, Object> findCouponRuleList(Long id);

    void saveCouponRule(CouponRuleVo couponRuleVo);

    List<CouponInfo> findCouponInfoList(Long skuId, Long userId);
    //3 获取购物车可用优惠卷列表
    List<CouponInfo> findCartCouponInfo(List<CartInfo> cartInfoList, Long userId);
    //获取购物车里对应优惠卷
    CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList, Long couponId);
    //更新优惠卷使用状态
    void updateCouponInfoUseStatus(Long couponId, Long userId, Long orderId);
}
