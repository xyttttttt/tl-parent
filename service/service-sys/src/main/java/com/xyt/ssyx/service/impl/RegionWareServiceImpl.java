package com.xyt.ssyx.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyt.ssyx.common.exception.SsyxException;
import com.xyt.ssyx.common.result.ResultCodeEnum;
import com.xyt.ssyx.mapper.RegionWareMapper;
import com.xyt.ssyx.model.sys.RegionWare;
import com.xyt.ssyx.service.RegionWareService;
import com.xyt.ssyx.vo.sys.RegionWareQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 城市仓库关联表 服务实现类
 * </p>
 *
 * @author xyt
 * @since 2023-07-08
 */
@Service
public class RegionWareServiceImpl extends ServiceImpl<RegionWareMapper, RegionWare> implements RegionWareService {

    @Autowired
    private RegionWareMapper regionWareMapper;


    @Override
    public IPage<RegionWare> selectPageRegionWare(Page<RegionWare> pageParam, RegionWareQueryVo regionWareQueryVo) {
        //1 获取查询条件
        String keyword = regionWareQueryVo.getKeyword();
        //2 判断条件值是否为空，不为空封装条件
        LambdaQueryWrapper<RegionWare> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(keyword)){
            //根据区域名称或者仓库名称进行查询
            wrapper.like(RegionWare::getRegionName,keyword).or().like(RegionWare::getWareName,keyword);
        }
        //3 调用方法实现条件分页
        Page<RegionWare> regionWarePage = baseMapper.selectPage(pageParam, wrapper);
        //4 数据返回
        return regionWarePage;
    }

    @Override
    public void saveRegionWare(RegionWare regionWare) {
        LambdaQueryWrapper<RegionWare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RegionWare::getRegionId,regionWare.getRegionId());
        Integer integer = regionWareMapper.selectCount(wrapper);
        if (integer > 0) {
            //已经存在
            throw new SsyxException(ResultCodeEnum.REGION_OPEN);
        }
        baseMapper.insert(regionWare);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        RegionWare regionWare = baseMapper.selectById(id);
        regionWare.setStatus(status);
        baseMapper.updateById(regionWare);
    }
}
