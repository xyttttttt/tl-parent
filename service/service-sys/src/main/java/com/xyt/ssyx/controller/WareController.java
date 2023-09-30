package com.xyt.ssyx.controller;


import com.xyt.ssyx.common.result.Result;
import com.xyt.ssyx.model.sys.Ware;
import com.xyt.ssyx.service.WareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 仓库表 前端控制器
 * </p>
 *
 * @author xyt
 * @since 2023-07-08
 */
@Api(tags = "仓库")
@RestController
@RequestMapping("/admin/sys/ware")
//@CrossOrigin
public class WareController {

    @Autowired
    private WareService wareService;

   /* url: `${api_name}/findAllList`,
    method: 'get'*/
    @ApiOperation("查询所有仓库")
    @GetMapping("/findAllList")
    public Result list(){
        List<Ware> list = wareService.list();
        return Result.ok(list);
    }
}

