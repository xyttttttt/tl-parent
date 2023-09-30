package com.xyt.ssyx.acl.controller;

import com.xyt.ssyx.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "登录接口")
@RestController
@RequestMapping("/admin/acl/index")
//@CrossOrigin   //解决跨域
public class IndexController {

    //1.login登录
    @ApiOperation("登录")
    @PostMapping("/login")
    public Result login(){
        //返回token
        Map<String,String> map = new HashMap<>();
        map.put("token","token-admin");
        return Result.ok(map);
    }
    //2.getInfo 获取信息
    @ApiOperation("获取信息")
    @GetMapping("/info")
    public Result info(){
        //返回token
        Map<String,String> map = new HashMap<>();
        map.put("name","admin");
        map.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        return Result.ok(map);
    }
    //3.logout 退出
    @ApiOperation("退出")
    @PostMapping("/logout")
    public Result logout(){
        return Result.ok(null);
    }
}
