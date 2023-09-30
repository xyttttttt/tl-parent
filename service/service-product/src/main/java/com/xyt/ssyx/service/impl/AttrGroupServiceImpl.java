package com.xyt.ssyx.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyt.ssyx.mapper.AttrGroupMapper;
import com.xyt.ssyx.model.product.AttrGroup;
import com.xyt.ssyx.service.AttrGroupService;
import com.xyt.ssyx.vo.product.AttrGroupQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 属性分组 服务实现类
 * </p>
 *
 * @author xyt
 * @since 2023-07-08
 */
@Service
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroup> implements AttrGroupService {

    @Override
    public IPage<AttrGroup> selectPageAttrGroup(Page<AttrGroup> pageParam, AttrGroupQueryVo attrGroupQueryVo) {
        String name = attrGroupQueryVo.getName();
        LambdaQueryWrapper<AttrGroup> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(name)){
            wrapper.like(AttrGroup::getName,name);
        }
        Page<AttrGroup> attrGroupPage = baseMapper.selectPage(pageParam, wrapper);
        return attrGroupPage;
    }

    @Override
    public List<AttrGroup> findAllListAttrGroup() {
//        LambdaQueryWrapper<AttrGroup> wrapper = new LambdaQueryWrapper<>();
//        wrapper.orderByDesc(AttrGroup::getId);
        QueryWrapper<AttrGroup> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        List<AttrGroup> attrGroups = baseMapper.selectList(wrapper);
        return attrGroups;
    }


}
