package com.xyt.ssyx.acl.controller;


import com.xyt.ssyx.acl.service.PermissionService;
import com.xyt.ssyx.common.result.Result;
import com.xyt.ssyx.model.acl.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "菜单管理")
@RestController
@RequestMapping("/admin/acl/permission")
//@CrossOrigin
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

  /*
 查看某个角色的权限列表
 *//*
    toAssign(roleId) {
        return request({
                url: `${api_name}/toAssign/${roleId}`,
        method: 'get'
    })
    },
*/
    @ApiOperation("查看角色权限列表")
    @GetMapping("/toAssign/{roleId}")
    public Result toAssign(@PathVariable Long roleId){
        Map<String, Object> map = permissionService.getRoleByPermissionId(roleId);
        return Result.ok(map);
    }
 /*
    给某个角色授权
    *//*
    doAssign(roleId, permissionId) {
        return request({
                url: `${api_name}/doAssign`,
        method: "post",
                params: {roleId, permissionId}
    })
    }*/
     @ApiOperation("授予角色")
     @PostMapping("/doAssign")
     public Result doAssign(@RequestParam Long roleId,
                            @RequestParam Long[] permissionId) {
         permissionService.saveAdminRole(roleId, permissionId);
         return Result.ok(null);
     }


    //查询所有菜单
    @ApiOperation("查询所有菜单")
    @GetMapping
    public Result list(){
         List<Permission> list= permissionService.queryAllPermission();
         return Result.ok(list);
    }
    //添加菜单
    @ApiOperation("添加菜单")
    @PostMapping("/save")
    public Result save(@RequestBody Permission permission){
        permissionService.save(permission);
        return Result.ok(null);
    }
    //修改菜单
    @ApiOperation("修改菜单")
    @PutMapping("/update")
    public Result update(@RequestBody Permission permission){
        permissionService.updateById(permission);
        return Result.ok(null);
    }

    //递归删除菜单
    @ApiOperation("递归删除")
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable Long id){
        permissionService.removeChildById(id);
        return Result.ok(null);
    }


}
