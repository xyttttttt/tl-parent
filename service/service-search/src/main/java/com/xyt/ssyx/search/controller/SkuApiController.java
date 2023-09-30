package com.xyt.ssyx.search.controller;

import com.xyt.ssyx.common.result.Result;
import com.xyt.ssyx.model.search.SkuEs;
import com.xyt.ssyx.search.service.SkuService;
import com.xyt.ssyx.vo.search.SkuEsQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search/sku")
public class SkuApiController {

    @Autowired
    private SkuService skuService;

    //获取爆款商品
    @GetMapping("/inner/findHotSkuList")
    public List<SkuEs> findHotSkuList(){
        return skuService.findHotSkuList();
    }

    //商品上架
    @GetMapping("/inner/upperSku/{skuId}")
    public Result upperSku(@PathVariable("skuId") Long skuId){
        skuService.upperSku(skuId);
        return Result.ok(null);
    }

    //商品下架
    @GetMapping("/inner/lowerSku/{skuId}")
    public Result lowerSku(@PathVariable("skuId") Long skuId){
        skuService.lowerSku(skuId);
        return Result.ok(null);
    }

    //查询分类商品
    @GetMapping("/{page}/{limit}")
    public Result listSku(@PathVariable int page
            , @PathVariable int limit
            , SkuEsQueryVo skuEsQueryVo){
        Pageable pageable = PageRequest.of(page-1,limit);
        Page<SkuEs> pageModel = skuService.search(pageable,skuEsQueryVo);
        return Result.ok(pageModel);
    }
    //更新商品热度
    @GetMapping("inner/incrHotScore/{skuId}")
    public Boolean incrHotScore(@PathVariable("skuId") Long skuId){
        skuService.incrHotScore(skuId);
        return true;
    }
}
