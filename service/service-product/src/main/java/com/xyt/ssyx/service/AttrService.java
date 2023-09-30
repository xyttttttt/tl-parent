package com.xyt.ssyx.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xyt.ssyx.model.product.Attr;

import java.util.List;

/**
 * <p>
 * 商品属性 服务类
 * </p>
 *
 * @author xyt
 * @since 2023-07-08
 */
public interface AttrService extends IService<Attr> {

    List<Attr> getAttrListByGroupId(Long groupId);
}
