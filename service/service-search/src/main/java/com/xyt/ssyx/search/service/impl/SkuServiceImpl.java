package com.xyt.ssyx.search.service.impl;

import com.xyt.ssyx.activity.ActivityFeignClient;
import com.xyt.ssyx.client.product.ProductFeignClient;
import com.xyt.ssyx.common.auth.AuthContextHolder;
import com.xyt.ssyx.enums.SkuType;
import com.xyt.ssyx.model.product.Category;
import com.xyt.ssyx.model.product.SkuInfo;
import com.xyt.ssyx.model.search.SkuEs;
import com.xyt.ssyx.search.repository.SkuRepository;
import com.xyt.ssyx.search.service.SkuService;
import com.xyt.ssyx.vo.search.SkuEsQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuRepository skuRepository;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private ActivityFeignClient activityFeignClient;

    @Override
    public void upperSku(Long skuId) {
        //通过远程调用，根据skuId获取相关信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if (skuInfo == null){
            return;
        }
        Category category = productFeignClient.getCategory(skuInfo.getCategoryId());
        //获取数据封装SkuEs对象
        SkuEs skuEs = new SkuEs();
        if (category != null){
            skuEs.setCategoryId(category.getId());
            skuEs.setCategoryName(category.getName());
        }
        skuEs.setId(skuInfo.getId());
        skuEs.setKeyword(skuInfo.getSkuName()+","+skuEs.getCategoryName());
        skuEs.setWareId(skuInfo.getWareId());
        skuEs.setIsNewPerson(skuInfo.getIsNewPerson());
        skuEs.setImgUrl(skuInfo.getImgUrl());
        skuEs.setTitle(skuInfo.getSkuName());
        if(skuInfo.getSkuType() == SkuType.COMMON.getCode()) {
            skuEs.setSkuType(0);
            skuEs.setPrice(skuInfo.getPrice().doubleValue());
            skuEs.setStock(skuInfo.getStock());
            skuEs.setSale(skuInfo.getSale());
            skuEs.setPerLimit(skuInfo.getPerLimit());
        }
        //调用方法添加ES
        skuRepository.save(skuEs);
    }

    @Override
    public void lowerSku(Long skuId) {
        skuRepository.deleteById(skuId);
    }

    //获取爆款商品
    @Override
    public List<SkuEs> findHotSkuList() {
        //以find /read /get 开头
        //关联条件 and /or
        //0 代表第一页
        Pageable pageable = PageRequest.of(0,10);
        Page<SkuEs> pageModel = skuRepository.findByOrderByHotScoreDesc(pageable);
        List<SkuEs> skuEsList = pageModel.getContent();
        return skuEsList;
    }
    //查询分类商品
    @Override
    public Page<SkuEs> search(Pageable pageable, SkuEsQueryVo skuEsQueryVo) {
        //1 向SkuEsQueryVo设置仓库id，当前登录用户的仓库id

        skuEsQueryVo.setWareId(AuthContextHolder.getWareId());
        //2 调用skuRepository方法，根据springData命名规则定义方法进行查询
        Page<SkuEs> pageModel = null;
        String keyword = skuEsQueryVo.getKeyword();
        if (StringUtils.isEmpty(keyword)){////判断keyword是否为空，如果为空，根据仓库id + 分类id 查询
            pageModel = skuRepository.findByCategoryIdAndWareId(skuEsQueryVo.getCategoryId()
                                            , skuEsQueryVo.getWareId(), pageable);
        }else {////判断keyword是否为空，如果为不为空，根据仓库id + 分类id +keyword 查询
            pageModel = skuRepository.findByKeywordAndWareId(skuEsQueryVo.getKeyword()
                                                , skuEsQueryVo.getWareId(), pageable);
        }
        //3 查询商品参加优惠活动
        List<SkuEs> skuEsList = pageModel.getContent();
        if (!CollectionUtils.isEmpty(skuEsList)){
            //遍历，的到所有skuId
            List<Long> skuIdList = skuEsList.stream().map(item -> item.getId())
                                                        .collect(Collectors.toList());
            //根据skuIdList列表远程调用 ，调用service-activity得到数据
            //返回Map<Long,List<String>>
            //map集合的key就是skuId，
            //map集合value是List集合，sku参加活动里面多个规则列表
            //一个商品参加一个活动，一个活动里面可以用多个规则
            ////比如活动：中秋节满减活动
            ////一个活动里面可以用多个规则：满20元减3元，满58元减8元
            Map<Long,List<String>> skuIdToRuleListMao = activityFeignClient.findActivity(skuIdList);
            if (skuIdToRuleListMao !=null){
                skuEsList.forEach(skuEs -> {
                    skuEs.setRuleList(skuIdToRuleListMao.get(skuEs.getId()));
                });
            }
            //封装获取数据到skuEs里卖弄的ruleList里面去
        }
        return pageModel;
    }
    //更新商品热度
    @Override
    public void incrHotScore(Long skuId) {
        String key = "hotScore";
        Double hotScore = redisTemplate.opsForZSet().incrementScore(key, "skuId:" + skuId, 1);
        if (hotScore %10 ==0){
            //更新es
            Optional<SkuEs> optional = skuRepository.findById(skuId);
            SkuEs skuEs = optional.get();
            skuEs.setHotScore(Math.round(hotScore));
            //有id进行更新，没有id添加
            skuRepository.save(skuEs);
        }
    }
}
