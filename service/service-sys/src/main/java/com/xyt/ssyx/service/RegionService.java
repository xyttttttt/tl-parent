package com.xyt.ssyx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyt.ssyx.model.sys.Region;

import java.util.List;

/**
 * <p>
 * 地区表 服务类
 * </p>
 *
 * @author xyt
 * @since 2023-07-08
 */
public interface RegionService extends IService<Region> {

    List<Region> selectRegionByKeyWord(String keyword);
}
