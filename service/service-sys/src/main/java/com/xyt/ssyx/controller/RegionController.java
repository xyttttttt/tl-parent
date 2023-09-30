package com.xyt.ssyx.controller;


import com.xyt.ssyx.common.result.Result;
import com.xyt.ssyx.model.sys.Region;
import com.xyt.ssyx.service.RegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 地区表 前端控制器
 * </p>
 *
 * @author xyt
 * @since 2023-07-08
 */
@Api(tags = "区域")
@RestController
@RequestMapping("/admin/sys/region")
//@CrossOrigin
public class RegionController {
 /*
                url: `${api_name}/findRegionByKeyword/${keyword}`,
        method: 'get'
    })
    },
*/
    @Autowired
    private RegionService regionService;

    @ApiOperation("根据关键字查询区域列表信息")
    @GetMapping("/findRegionByKeyword/{keyword}")
    public Result findRegionByKeyWord(@PathVariable String keyword){
        List<Region> regionList =  regionService.selectRegionByKeyWord(keyword);
        return Result.ok(regionList);
    }
  /*
                url: `${api_name}/findByParentId/${parentId}`,
        method: 'get'
   */
}

