package com.xyt.ssyx.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyt.ssyx.mapper.AttrMapper;
import com.xyt.ssyx.model.product.Attr;
import com.xyt.ssyx.service.AttrService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品属性 服务实现类
 * </p>
 *
 * @author xyt
 * @since 2023-07-08
 */
@Service
public class AttrServiceImpl extends ServiceImpl<AttrMapper, Attr> implements AttrService {

    @Override
    public List<Attr> getAttrListByGroupId(Long groupId) {

        LambdaQueryWrapper<Attr> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attr::getAttrGroupId,groupId);
        List<Attr> attrList = baseMapper.selectList(wrapper);
        return attrList;
    }
}
