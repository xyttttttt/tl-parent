package com.xyt.ssyx.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyt.ssyx.common.result.Result;
import com.xyt.ssyx.model.product.AttrGroup;
import com.xyt.ssyx.service.AttrGroupService;
import com.xyt.ssyx.vo.product.AttrGroupQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 属性分组 前端控制器
 * </p>
 *
 * @author xyt
 * @since 2023-07-08
 */
@Api(tags = "属性组")
@RestController
@RequestMapping("/admin/product/attrGroup")
//@CrossOrigin
public class AttrGroupController {

    @Autowired
    private AttrGroupService attrGroupService;

    /*url: `${api_name}/${page}/${limit}`,
    method: 'get',
    params: searchObj*/
    @ApiOperation("平台属性分组列表")
    @GetMapping("/{page}/{limit}")
    public Result list(@PathVariable Long page
                        , @PathVariable Long limit
                        , AttrGroupQueryVo attrGroupQueryVo){
        Page<AttrGroup> pageParam = new Page<>(page,limit);
        IPage<AttrGroup> pageModel =  attrGroupService.selectPageAttrGroup(pageParam,attrGroupQueryVo);
        return Result.ok(pageModel);
    }

    //查询所有平台属性分组列表功能
    /*url: `${api_name}/findAllList`,
    method: 'get'*/
    @ApiOperation("查询所有平台属性分组列表功能")
    @GetMapping("/findAllList")
    public Result findAllList(){
        List<AttrGroup> attrGroups = attrGroupService.findAllListAttrGroup();
        return Result.ok(attrGroups);
    }

    /*url: `${api_name}/get/${id}`,
    method: 'get'*/
    @ApiOperation("根据id获取平台属性")
    @GetMapping("/get/{id}")
    public Result getById(@PathVariable Long id){
        AttrGroup attrGroup = attrGroupService.getById(id);
        return Result.ok(attrGroup);
    }

   /* url: `${api_name}/save`,
    method: 'post',
    data: role*/
   @ApiOperation("添加平台属性")
   @PostMapping("/save")
   public Result save(@RequestBody AttrGroup attrGroup){
       attrGroupService.save(attrGroup);
       return Result.ok(null);
   }

    /*url: `${api_name}/update`,
    method: 'put',
    data: role*/
    @ApiOperation("修改平台属性")
    @PutMapping("/update")
    public Result update(@RequestBody AttrGroup attrGroup){
        attrGroupService.updateById(attrGroup);
        return Result.ok(null);
    }

   /* url: `${api_name}/remove/${id}`,
    method: 'delete'*/
   @ApiOperation("根据id删除平台属性")
   @DeleteMapping("/remove/{id}")
   public Result remove(@PathVariable Long id){
       attrGroupService.removeById(id);
       return Result.ok(null);
   }
    /*url: `${api_name}/batchRemove`,
    method: 'delete',
    data: idList*/
    @ApiOperation("批量删除平台属性")
    @DeleteMapping("/batchRemove")
    public Result update(@RequestBody List<Long> idList){
        attrGroupService.removeByIds(idList);
        return Result.ok(null);
    }
}



