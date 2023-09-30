package com.xyt.ssxy.activity.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xyt.ssyx.model.activity.ActivityInfo;
import com.xyt.ssyx.model.activity.ActivityRule;
import com.xyt.ssyx.model.activity.ActivitySku;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 活动表 Mapper 接口
 * </p>
 *
 * @author xyt
 * @since 2023-07-10
 */
public interface ActivityInfoMapper extends BaseMapper<ActivityInfo> {

    List<Long> selectSkuIdListExist(@Param("skuIdList") List<Long> skuIdList);

    List<ActivityRule> findActivityRule(@Param("skuId") Long skuId);

    List<ActivitySku> selectCartActivity(@Param("skuIdList") List<Long> skuIdList);
}
