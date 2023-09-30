package com.xyt.ssyx.acl.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyt.ssyx.acl.service.RoleService;
import com.xyt.ssyx.common.result.Result;
import com.xyt.ssyx.model.acl.Role;
import com.xyt.ssyx.vo.acl.RoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "角色接口")
@RestController
//@CrossOrigin
@RequestMapping("/admin/acl/role")
public class RoleController {

    @Autowired
    private RoleService roleService;
    //角色列表（条件分页查询）
    @ApiOperation("角色条件分页查询")
    @GetMapping("/{current}/{limit}")
    public Result pageList(@PathVariable Long current,
                           @PathVariable Long limit,
                           RoleQueryVo roleQueryVo){
        //1.创建一个page对象，传递当前页和每页记录数
        Page<Role> pageParam = new Page<>(current,limit);
        //2.调用service方法实现条件分页查询，返回分页对象
        IPage<Role> pageModel =  roleService.selectRolePage(pageParam,roleQueryVo);
        return Result.ok(pageModel);
    }
    //根据id查询角色
    @ApiOperation("更具id查询角色")
    @GetMapping("/get/{id}")
    public Result get(@PathVariable Long id){
        Role role = roleService.getById(id);
        return Result.ok(role);
    }
    //添加角色
    @ApiOperation("添加角色")
    @PostMapping("/save")
    public Result save(@RequestBody Role role){
        boolean is_success = roleService.save(role);
        if (is_success){
            return Result.ok(null);
        }
        else {
            return Result.fail(null);
        }
    }
    //修改角色
    @ApiOperation("修改角色")
    @PutMapping("/update")
    public Result update(@RequestBody Role role){
        boolean is_success =  roleService.updateById(role);
        if (is_success){
            return Result.ok(null);
        }
        else {
            return Result.fail(null);
        }
    }
    //根据id删除角色
    @ApiOperation("根据id删除角色")
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable Long id){
        boolean is_success = roleService.removeById(id);
        if (is_success){
            return Result.ok(null);
        }
        else {
            return Result.fail(null);
        }
    }
    //批量删除角色
    @ApiOperation("批量删除角色")
    @DeleteMapping("/batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList){
        boolean is_success = roleService.removeByIds(idList);
        if (is_success){
            return Result.ok(null);
        }
        else {
            return Result.fail(null);
        }
    }
}
