package com.xyt.ssxy.activity.mapper;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xyt.ssyx.model.activity.CouponInfo;
import com.xyt.ssyx.model.activity.CouponUse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 优惠券信息 Mapper 接口
 * </p>
 *
 * @author xyt
 * @since 2023-07-10
 */
public interface  CouponInfoMapper extends BaseMapper<CouponInfo> {

    List<CouponInfo> selectCouponInfoList(@Param("skuId") Long id, @Param("categoryId") Long categoryId, @Param("userId") Long userId);

    /*1 根据用户id获取用户全部优惠卷*/
    List<CouponInfo> selectCartCouponInfoList(@Param("userId") Long userId);


}
