package com.xyt.ssyx.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xyt.ssyx.model.product.SkuAttrValue;

import java.util.List;

/**
 * <p>
 * spu属性值 服务类
 * </p>
 *
 * @author xyt
 * @since 2023-07-08
 */
public interface SkuAttrValueService extends IService<SkuAttrValue> {

    List<SkuAttrValue> setAttrValueListBySkuId(Long id);
}
