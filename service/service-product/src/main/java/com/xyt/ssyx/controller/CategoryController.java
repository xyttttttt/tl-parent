package com.xyt.ssyx.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyt.ssyx.common.result.Result;
import com.xyt.ssyx.model.product.Category;
import com.xyt.ssyx.service.CategoryService;
import com.xyt.ssyx.vo.product.CategoryQueryVo;
import com.xyt.ssyx.vo.product.CategoryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品三级分类 前端控制器
 * </p>
 *
 * @author xyt
 * @since 2023-07-08
 */
@Api(tags = "商品种类")
@RestController
@RequestMapping("/admin/product/category")
//@CrossOrigin
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    //商品分类列表
    @ApiOperation("商品分类列表")
    @GetMapping("/{page}/{limit}")
    public Result list (@PathVariable Long page
                        , @PathVariable Long limit
                        , CategoryQueryVo categoryQueryVo){
        Page<Category> pageParam = new Page<>(page,limit);
        IPage<Category> pageModel =  categoryService.selectPageCategory(pageParam,categoryQueryVo);
        return Result.ok(pageModel);
    }

   /* url: `${api_name}/get/${id}`,
    method: 'get'*/
    @ApiOperation("按照ID获取商品分类信息")
    @GetMapping("/get/{id}")
    public Result getById(@PathVariable Long id){
        Category category = categoryService.getById(id);
        return Result.ok(category);
    }
  /*  url: `${api_name}/save`,
    method: 'post',*/
    @ApiOperation("新增商品分类")
    @PostMapping("/save")
    public Result save(@RequestBody Category category){
        categoryService.save(category);
        return Result.ok(null);
    }
   /* url: `${api_name}/update`,
    method: 'put',*/
    @ApiOperation("更新商品分类")
    @PutMapping("/update")
    public Result update(@RequestBody Category category){
        categoryService.updateById(category);
        return Result.ok(null);
    }

    /*url: `${api_name}/remove/${id}`,
    method: 'delete'*/
    @ApiOperation("删除商品分类")
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable Long id){
        categoryService.removeById(id);
        return Result.ok(null);
    }

  /*  url: `${api_name}/batchRemove`,
    method: 'delete',*/
    @ApiOperation("批量删除商品分类")
    @DeleteMapping("/batchRemove")
    public Result batchRemove(@RequestBody List<Long> ids){
        categoryService.removeByIds(ids);
        return Result.ok(null);
    }
   /* url: `${api_name}/findAllList`,
    method: 'get'*/
    @ApiOperation("查找所有商品分类")
    @GetMapping("/findAllList")
    public Result findAllList(){
        List<Category> categoryList = categoryService.list();
        return Result.ok(categoryList);
    }
}

