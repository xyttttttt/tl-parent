package com.xyt.ssyx.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xyt.ssyx.model.product.SkuPoster;

import java.util.List;

/**
 * <p>
 * 商品海报表 服务类
 * </p>
 *
 * @author xyt
 * @since 2023-07-08
 */
public interface SkuPosterService extends IService<SkuPoster> {

    List<SkuPoster> getPosterListBySkuId(Long id);
}
