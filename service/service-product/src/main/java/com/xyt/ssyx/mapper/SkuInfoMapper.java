package com.xyt.ssyx.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xyt.ssyx.model.product.SkuInfo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * sku信息 Mapper 接口
 * </p>
 *
 * @author xyt
 * @since 2023-07-08
 */
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {
    //所有锁定成功的商品都要解锁
    void unlockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    //验证库存
    SkuInfo checkStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);
    //锁定库存
    Integer lockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);
    //遍历集合得到每个对象，减库存
    void minusStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);
}
