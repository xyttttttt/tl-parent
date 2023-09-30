package com.xyt.ssyx.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyt.ssyx.mapper.RegionMapper;
import com.xyt.ssyx.model.sys.Region;
import com.xyt.ssyx.service.RegionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 地区表 服务实现类
 * </p>
 *
 * @author xyt
 * @since 2023-07-08
 */
@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements RegionService {

    @Override
    public List<Region> selectRegionByKeyWord(String keyword) {
        LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Region::getName,keyword);
        List<Region> regionList = baseMapper.selectList(wrapper);
        return regionList;
    }
}
