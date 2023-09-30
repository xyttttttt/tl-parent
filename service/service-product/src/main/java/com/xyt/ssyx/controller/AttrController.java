package com.xyt.ssyx.controller;


import com.xyt.ssyx.common.result.Result;
import com.xyt.ssyx.model.product.Attr;
import com.xyt.ssyx.model.product.AttrGroup;
import com.xyt.ssyx.service.AttrService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品属性 前端控制器
 * </p>
 *
 * @author xyt
 * @since 2023-07-08
 */
@Api(tags = "属性")
@RestController
@RequestMapping("/admin/product/attr")
//@CrossOrigin
public class AttrController {
    @Autowired
    private AttrService attrService;

    /*url: `${api_name}/${groupId}`,
    method: 'get'*/
    @ApiOperation("根据平台属性分租id查询")
    @GetMapping("{groupId}")
    public Result getByGroupId(@PathVariable Long groupId){
        List<Attr> list = attrService.getAttrListByGroupId(groupId);
        return Result.ok(list);
    }
    /*url: `${api_name}/get/${id}`,
    method: 'get'*/
    @ApiOperation("根据平台属性id查询")
    @GetMapping("/get/{id}")
    public Result getById(@PathVariable Long id){
        Attr attr = attrService.getById(id);
        return Result.ok(attr);
    }
   /* url: `${api_name}/save`,
    method: 'post',
    data: role*/
   @ApiOperation("添加平台属性")
   @PostMapping("/save")
   public Result save(@RequestBody Attr attr){
       attrService.save(attr);
       return Result.ok(null);
   }

    /*url: `${api_name}/update`,
       method: 'put',
       data: role*/
    @ApiOperation("修改平台属性")
    @PutMapping("/update")
    public Result update(@RequestBody Attr attr){
        attrService.updateById(attr);
        return Result.ok(null);
    }

    /* url: `${api_name}/remove/${id}`,
     method: 'delete'*/
    @ApiOperation("根据id删除平台属性")
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable Long id){
        attrService.removeById(id);
        return Result.ok(null);
    }
    /*url: `${api_name}/batchRemove`,
    method: 'delete',
    data: idList*/
    @ApiOperation("批量删除平台属性")
    @DeleteMapping("/batchRemove")
    public Result update(@RequestBody List<Long> idList){
        attrService.removeByIds(idList);
        return Result.ok(null);
    }

}

