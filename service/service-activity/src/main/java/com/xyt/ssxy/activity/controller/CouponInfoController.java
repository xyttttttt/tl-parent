package com.xyt.ssxy.activity.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyt.ssxy.activity.service.CouponInfoService;
import com.xyt.ssyx.common.result.Result;
import com.xyt.ssyx.model.activity.CouponInfo;
import com.xyt.ssyx.vo.activity.CouponRuleVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠券信息 前端控制器
 * </p>
 *
 * @author xyt
 * @since 2023-07-10
 */
@RestController
@RequestMapping("/admin/activity/couponInfo")
public class CouponInfoController {

    @Autowired
    private CouponInfoService couponInfoService;

   /* url: `${api_name}/${page}/${limit}`,
    method: 'get'*/
    //1 优惠卷分页查询
    @ApiOperation("优惠卷分页查询")
    @GetMapping("/{page}/{limit}")
    public Result list(@PathVariable Long page
                       , @PathVariable Long limit){
        Page<CouponInfo> pageParam = new Page<>(page,limit);
        IPage<CouponInfo> pageModel = couponInfoService.selectPageCouponInfo(pageParam);
        return Result.ok(pageModel);
    }


    //2 添加优惠卷

  /*  url: `${api_name}/save`,
    method: 'post',
    data: role*/
    @ApiOperation("添加优惠卷")
    @PostMapping("/save")
    public Result save(@RequestBody CouponInfo couponInfo){
        couponInfoService.save(couponInfo);
        return Result.ok(null);
    }
    //3 根据id查询优惠卷
   /* url: `${api_name}/get/${id}`,
    method: 'get'*/
    @ApiOperation("根据id查询优惠卷")
    @GetMapping("/get/{id}")
    public Result getById(@PathVariable Long id){
        CouponInfo couponInfo = couponInfoService.getCouponInfo(id);
        return Result.ok(couponInfo);
    }
    //4 根据优惠卷id查询规则数据
//    url: `${api_name}/findCouponRuleList/${id}`,
//    method: 'get'
    @ApiOperation("根据优惠卷id查询规则数据")
    @GetMapping("/findCouponRuleList/{id}")
    public Result findCouponRuleList(@PathVariable Long id){
        Map<String,Object> map = couponInfoService.findCouponRuleList(id);
        return Result.ok(map);
    }
    //5 添加优惠卷规则数据
       /*url: `${api_name}/saveCouponRule`,
    method: 'post',
    data: rule*/
    @ApiOperation("添加优惠卷规则数据")
    @PostMapping("saveCouponRule")
    public Result saveCouponRule(@RequestBody CouponRuleVo couponRuleVo){
        couponInfoService.saveCouponRule(couponRuleVo);
        return Result.ok(null);
    }

  /*  url: `${api_name}/update`,
    method: 'put',
    data: role*/
    @ApiOperation("修改优惠价规则")
    @PutMapping("/update")
    public Result update(@RequestBody CouponInfo couponInfo){
        couponInfoService.updateById(couponInfo);
        return Result.ok(null);
    }

//    url: `${api_name}/remove/${id}`,
//    method: 'delete'
    @ApiOperation("删除优惠价规则")
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable Long id){
        couponInfoService.removeById(id);
        return Result.ok(null);
    }
   /* url: `${api_name}/batchRemove`,
    method: 'delete',
    data: idList*/
   @ApiOperation("删除优惠价规则")
   @DeleteMapping("/batchRemove")
   public Result batchRemove(@RequestBody List<Long> idList){
        couponInfoService.removeByIds(idList);
        return Result.ok(null);
   }
}

