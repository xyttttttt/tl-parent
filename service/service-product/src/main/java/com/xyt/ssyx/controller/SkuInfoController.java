package com.xyt.ssyx.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyt.ssyx.common.result.Result;
import com.xyt.ssyx.model.product.SkuInfo;
import com.xyt.ssyx.service.SkuInfoService;
import com.xyt.ssyx.vo.product.SkuInfoQueryVo;
import com.xyt.ssyx.vo.product.SkuInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * sku信息 前端控制器
 * </p>
 *
 * @author xyt
 * @since 2023-07-08
 */
@Api(tags = "SKU")
@RestController
@RequestMapping("/admin/product/skuInfo")
//@CrossOrigin
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    /*url: `${api_name}/${page}/${limit}`,
    method: 'get',
    params: searchObj*/
    @ApiOperation("sku列表")
    @GetMapping("/{page}/{limit}")
    public Result list(@PathVariable Long page
                        , @PathVariable Long limit
                        , SkuInfoQueryVo skuInfoQueryVo){
        Page<SkuInfo> pageParam = new Page<>(page,limit);
        IPage<SkuInfo> pageModel = skuInfoService.selectPageSkuInfo(pageParam,skuInfoQueryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation("添加商品sku信息")
    @PostMapping("/save")
    public Result save(@RequestBody SkuInfoVo skuInfoVo){
        skuInfoService.saveSkuInfo(skuInfoVo);
        return Result.ok(null);
    }

    @ApiOperation("根据id获取商品sku信息")
    @GetMapping("/get/{id}")
    public Result get (@PathVariable Long id){
        SkuInfoVo skuInfoVo = skuInfoService.getSkuInfo(id);
        return Result.ok(skuInfoVo);
    }

    @ApiOperation("修改sku信息")
    @PutMapping("/update")
    public Result update(@RequestBody SkuInfoVo skuInfoVo){
        skuInfoService.updateSkuInfo(skuInfoVo);
        return Result.ok(null);
    }
    @ApiOperation("删除sku信息")
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable Long id){
        skuInfoService.removeById(id);
        return Result.ok(null);
    }
    @ApiOperation("批量删除sku信息")
    @DeleteMapping("/batchRemove")
    public Result batchRemove(@RequestBody List<Long>  idList){
        skuInfoService.removeByIds(idList);
        return Result.ok(null);
    }
    @ApiOperation("商品审核")
    @GetMapping("/check/{skuId}/{status}")
    public Result check(@PathVariable Long skuId
                        ,@PathVariable Integer status){
        skuInfoService.check(skuId,status);
        return Result.ok(null);
    }
    @ApiOperation("商品上架")
    @GetMapping("/publish/{skuId}/{status}")
    public Result publish(@PathVariable Long skuId
                            ,@PathVariable Integer status){
        skuInfoService.publish(skuId,status);
        return Result.ok(null);
    }
    //新人专享
    @ApiOperation("新人专享")
    @GetMapping("isNewPerson/{skuId}/{status}")
    public Result isNewPerson(@PathVariable("skuId") Long skuId,
                              @PathVariable("status") Integer status) {
        skuInfoService.isNewPerson(skuId, status);
        return Result.ok(null);
    }
}

