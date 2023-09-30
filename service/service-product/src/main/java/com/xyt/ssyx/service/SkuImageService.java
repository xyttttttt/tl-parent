package com.xyt.ssyx.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xyt.ssyx.model.product.SkuImage;

import java.util.List;

/**
 * <p>
 * 商品图片 服务类
 * </p>
 *
 * @author xyt
 * @since 2023-07-08
 */
public interface SkuImageService extends IService<SkuImage> {

    List<SkuImage> getImageListBySkuId(Long id);
}
