package com.xyt.ssyx.acl.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyt.ssyx.acl.service.AdminRoleService;
import com.xyt.ssyx.acl.service.AdminService;
import com.xyt.ssyx.acl.service.RoleService;
import com.xyt.ssyx.common.result.Result;
import com.xyt.ssyx.common.utils.MD5;
import com.xyt.ssyx.model.acl.Admin;
import com.xyt.ssyx.vo.acl.AdminQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "用户接口")
@RestController
@RequestMapping("/admin/acl/user")
//@CrossOrigin
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    //参数有用户id  和 多个角色id
    @ApiOperation("为用户进行角色分配")
    @PostMapping("/doAssign")
    public Result doAssign(@RequestParam Long adminId,
                           @RequestParam Long[] roleId){
        roleService.saveAdminRole(adminId,roleId);
        return Result.ok(null);
    }

    @ApiOperation("获取用户的角色")
    @GetMapping("/toAssign/{adminId}")
    public Result toAssign(@PathVariable Long adminId){
        //map返回两部分数据：所有角色   和    用户分配角色列表
        Map<String,Object> map=roleService.getRoleByAdminId(adminId);
        return Result.ok(map);
    }



    //1 用户条件分页查询
    @ApiOperation("用户条件分页查询")
    @GetMapping("/{current}/{limit}")
    public Result list (@PathVariable Long current,
                        @PathVariable Long limit,
                        AdminQueryVo adminQueryVo){
        Page<Admin> page = new Page<>(current,limit);
        IPage<Admin> pageModel = adminService.selectPageUser(page,adminQueryVo);
        return Result.ok(pageModel);
    }

    //2 id查询用户
    @ApiOperation("根据id查询")
    @GetMapping("/get/{id}")
    public Result getById(@PathVariable Long id){
        Admin admin = adminService.getById(id);
        return Result.ok(admin);
    }
    //3 添加用户
    @ApiOperation("添加用户")
    @PostMapping("/save")
    public Result save(@RequestBody Admin admin){
        //获取输入密码
        String password = admin.getPassword();
        //对密码进行MD5加密
        String encrypt = MD5.encrypt(password);
        //设置密码
        admin.setPassword(encrypt);
        adminService.save(admin);
        return Result.ok(null);
    }
    //4 修改
    @ApiOperation("修改用户")
    @PutMapping("/update")
    public Result update(@RequestBody Admin admin){
        adminService.updateById(admin);
        return Result.ok(null);
    }
    //5 id删除
    @ApiOperation("根据id删除")
    @DeleteMapping("/remove/{id}")
    public Result removeById(@PathVariable Long id){
        adminService.removeById(id);
        return Result.ok(null);
    }
    //6批量删除
    @ApiOperation("批量删除")
    @DeleteMapping("/batchRemove")
    public Result batchRemove(@RequestBody List<Long> ids){
        adminService.removeByIds(ids);
        return Result.ok(null);
    }
}
