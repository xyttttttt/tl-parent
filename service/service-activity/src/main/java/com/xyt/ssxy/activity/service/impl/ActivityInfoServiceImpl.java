package com.xyt.ssxy.activity.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyt.ssxy.activity.mapper.ActivityInfoMapper;
import com.xyt.ssxy.activity.mapper.ActivityRuleMapper;
import com.xyt.ssxy.activity.mapper.ActivitySkuMapper;
import com.xyt.ssxy.activity.service.ActivityInfoService;
import com.xyt.ssxy.activity.service.CouponInfoService;
import com.xyt.ssyx.client.product.ProductFeignClient;
import com.xyt.ssyx.enums.ActivityType;
import com.xyt.ssyx.model.activity.ActivityInfo;
import com.xyt.ssyx.model.activity.ActivityRule;
import com.xyt.ssyx.model.activity.ActivitySku;
import com.xyt.ssyx.model.activity.CouponInfo;
import com.xyt.ssyx.model.order.CartInfo;
import com.xyt.ssyx.model.product.SkuInfo;
import com.xyt.ssyx.vo.activity.ActivityRuleVo;
import com.xyt.ssyx.vo.order.CartInfoVo;
import com.xyt.ssyx.vo.order.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 活动表 服务实现类
 * </p>
 *
 * @author xyt
 * @since 2023-07-10
 */
@Service
public class ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoMapper, ActivityInfo> implements ActivityInfoService {

    @Autowired
    private ActivityRuleMapper activityRuleMapper;
    @Autowired
    private ActivitySkuMapper activitySkuMapper;

    @Autowired
    private CouponInfoService couponInfoService;
    @Autowired
    private ProductFeignClient productFeignClient;



    //获取购物车满足条件的优惠卷优惠活动的信息
    @Override
    public OrderConfirmVo findCartActivityAndCoupon(List<CartInfo> cartInfoList, Long userId) {
        //1 获取购物车，每个购物项参与活动，根据获得规则进行分组
        //一个获得规则对应多个商品
        //CartInfoVo
        List<CartInfoVo> cartInfoVoList = this.findCartActivityList(cartInfoList);
        //2 促销活动优惠的总金额
        BigDecimal activityReduceAmount = cartInfoVoList.stream().filter(cartInfoVo -> cartInfoVo.getActivityRule() != null)
                .map(cartInfoVo -> cartInfoVo.getActivityRule().getReduceAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //3 获取购物车可用优惠卷列表
        List<CouponInfo> couponInfoList = couponInfoService.findCartCouponInfo(cartInfoList,userId);
        //4 优惠券可优惠的总金额，一次购物只能使用一张优惠券
        BigDecimal couponReduceAmount = new BigDecimal(0);
        if (!CollectionUtils.isEmpty(couponInfoList)){
            couponReduceAmount = couponInfoList.stream().filter(couponInfo -> couponInfo.getIsOptimal().intValue() ==1)
                                    .map(couponInfo -> couponInfo.getAmount())
                                    .reduce(BigDecimal.ZERO,BigDecimal::add);
        }
        //5 计算没有参与活动，没有使用优惠卷原始金额
        BigDecimal originalTotalAmount = cartInfoList.stream().filter(cartInfo -> cartInfo.getIsChecked() == 1)
                                .map(cartInfo -> cartInfo.getCartPrice()
                                                        .multiply(new BigDecimal(cartInfo.getSkuNum())))
                                .reduce(BigDecimal.ZERO,BigDecimal::add);
        //6 参与活动，使用优惠卷总金额
        BigDecimal totalAmount = originalTotalAmount.subtract(activityReduceAmount).subtract(couponReduceAmount);
        //封装数据到OrderConfirmVo，返回

        OrderConfirmVo orderTradeVo = new OrderConfirmVo();
        orderTradeVo.setCarInfoVoList(cartInfoVoList);
        orderTradeVo.setActivityReduceAmount(activityReduceAmount);
        orderTradeVo.setCouponInfoList(couponInfoList);
        orderTradeVo.setCouponReduceAmount(couponReduceAmount);
        orderTradeVo.setOriginalTotalAmount(originalTotalAmount);
        orderTradeVo.setTotalAmount(totalAmount);
        return orderTradeVo;
    }

    @Override
    public List<CartInfoVo> findCartActivityList(List<CartInfo> cartInfoList) {
        List<CartInfoVo> cartInfoVoList = new ArrayList<>();
        //获取所有skuId
        List<Long> skuIdList = cartInfoList.stream().map(CartInfo::getSkuId).collect(Collectors.toList());
        //根据skuId获取参与活动
        //TODO  问题：购物车里的购物项没有参加活动的商品 activitySkuList 是否为null  (解决，做了非空判断)
        List<ActivitySku> activitySkuList = baseMapper.selectCartActivity(skuIdList);
        //根据活动进行分组，每个活动里面有哪些skuId信息
        //map里面的key是分组字段 活动id
        //value 是每组里面sku列表数据，set集合
        Map<Long, Set<Long>> activityIdToSkuIdListMap = activitySkuList.stream()
                                    .collect(Collectors.groupingBy(ActivitySku::getActivityId
                                    , Collectors.mapping(ActivitySku::getSkuId, Collectors.toSet())));
        //获取活动规则数据
        //key是活动id，value是活动里面规则列表数据
        Map<Long ,List<ActivityRule>> activityIdToActivityRuleListMap = new HashMap<>();
        //获取活动id
        Set<Long> activityIdSet = activitySkuList.stream().map(ActivitySku::getActivityId)
                                                            .collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(activityIdSet)){
            //activity_rule表
            LambdaQueryWrapper<ActivityRule> wrapper = new LambdaQueryWrapper<>();
            wrapper.orderByDesc(ActivityRule::getConditionAmount,ActivityRule::getConditionNum);
            wrapper.in(ActivityRule::getActivityId,activityIdSet);
            List<ActivityRule> activityRuleList = activityRuleMapper.selectList(wrapper);
            //封装到activityIdToActivityRuleListMap
            activityIdToActivityRuleListMap = activityRuleList.stream()
                                                    .collect(Collectors.groupingBy(ActivityRule::getActivityId));
        }
        //有活动的购物项skuId
        Set<Long> activitySkuIdSet = new HashSet<>();
        if (!CollectionUtils.isEmpty(activityIdToSkuIdListMap)){
            //遍历activityIdToSkuIdListMap集合
            Iterator<Map.Entry<Long, Set<Long>>> iterator = activityIdToSkuIdListMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<Long, Set<Long>> entry = iterator.next();
                //活动id
                Long activityId = entry.getKey();
                //每个活动里面对应skuId列表
                Set<Long> currentActivitySkuIdSet = entry.getValue();
                //获取当前活动对应购物项列表
                List<CartInfo> currentActivityCartInfoList = cartInfoList.stream().filter(
                        cartInfo -> currentActivitySkuIdSet.contains(cartInfo.getSkuId()))
                        .collect(Collectors.toList());
                //计算有活动的购物项总金额和总数量
                BigDecimal totalAmount = this.computeTotalAmount(currentActivityCartInfoList);
                int computeCartNum = this.computeCartNum(currentActivityCartInfoList);
                //计算活动对应规则
                //根据活动id获取活动对应规则
                List<ActivityRule> currentActivityRuleList = activityIdToActivityRuleListMap.get(activityId);
                ActivityType activityType = currentActivityRuleList.get(0).getActivityType();
                //判断活动类型
                ActivityRule activityRule = null;
                if (activityType == ActivityType.FULL_REDUCTION){ //满减
                    activityRule = this.computeFullReduction(totalAmount, currentActivityRuleList);
                }else {//满量
                    activityRule = this.computeFullDiscount(computeCartNum, totalAmount, currentActivityRuleList);
                }
                //cartInfoVo封装
                CartInfoVo cartInfoVo = new CartInfoVo();
                cartInfoVo.setActivityRule(activityRule);
                cartInfoVo.setCartInfoList(currentActivityCartInfoList);
                cartInfoVoList.add(cartInfoVo);
                //记录哪线购物项参与了活动
                activitySkuIdSet.addAll(currentActivitySkuIdSet);
            }
        }
        //没有活动的购物项skuId
        //获取哪些skuId没有参加活动
        skuIdList.removeAll(activityIdSet);
        if (!CollectionUtils.isEmpty(skuIdList)){
            //skuId对应购物项
            Map<Long, CartInfo> skuIdCartInfoMap = cartInfoList.stream().collect(
                                            Collectors.toMap(CartInfo::getSkuId, CartInfo -> CartInfo));
            for (Long skuId : skuIdList){
                CartInfoVo cartInfoVo = new CartInfoVo();
                cartInfoVo.setActivityRule(null);
                List<CartInfo> cartInfos = new ArrayList<>();
                CartInfo cartInfo = skuIdCartInfoMap.get(skuId);
                cartInfos.add(cartInfo);
                cartInfoVo.setCartInfoList(cartInfos);
                cartInfoVoList.add(cartInfoVo);
            }
        }
        return cartInfoVoList;
    }



    /**
     * 计算满量打折最优规则
     * @param totalNum
     * @param activityRuleList //该活动规则skuActivityRuleList数据，已经按照优惠折扣从大到小排序了
     */
    private ActivityRule computeFullDiscount(Integer totalNum, BigDecimal totalAmount, List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
        for (ActivityRule activityRule : activityRuleList) {
            //如果订单项购买个数大于等于满减件数，则优化打折
            if (totalNum.intValue() >= activityRule.getConditionNum()) {
                BigDecimal skuDiscountTotalAmount = totalAmount.multiply(activityRule.getBenefitDiscount().divide(new BigDecimal("10")));
                BigDecimal reduceAmount = totalAmount.subtract(skuDiscountTotalAmount);
                activityRule.setReduceAmount(reduceAmount);
                optimalActivityRule = activityRule;
                break;
            }
        }
        if(null == optimalActivityRule) {
            //如果没有满足条件的取最小满足条件的一项
            optimalActivityRule = activityRuleList.get(activityRuleList.size()-1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0"));
            optimalActivityRule.setSelectType(1);

            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionNum())
                    .append("元打")
                    .append(optimalActivityRule.getBenefitDiscount())
                    .append("折，还差")
                    .append(totalNum-optimalActivityRule.getConditionNum())
                    .append("件");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
        } else {
            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionNum())
                    .append("元打")
                    .append(optimalActivityRule.getBenefitDiscount())
                    .append("折，已减")
                    .append(optimalActivityRule.getReduceAmount())
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
            optimalActivityRule.setSelectType(2);
        }
        return optimalActivityRule;
    }

    /**
     * 计算满减最优规则
     * @param totalAmount
     * @param activityRuleList //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
     */
    private ActivityRule computeFullReduction(BigDecimal totalAmount, List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
        for (ActivityRule activityRule : activityRuleList) {
            //如果订单项金额大于等于满减金额，则优惠金额
            if (totalAmount.compareTo(activityRule.getConditionAmount()) > -1) {
                //优惠后减少金额
                activityRule.setReduceAmount(activityRule.getBenefitAmount());
                optimalActivityRule = activityRule;
                break;
            }
        }
        if(null == optimalActivityRule) {
            //如果没有满足条件的取最小满足条件的一项
            optimalActivityRule = activityRuleList.get(activityRuleList.size()-1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0"));
            optimalActivityRule.setSelectType(1);

            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionAmount())
                    .append("元减")
                    .append(optimalActivityRule.getBenefitAmount())
                    .append("元，还差")
                    .append(totalAmount.subtract(optimalActivityRule.getConditionAmount()))
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
        } else {
            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionAmount())
                    .append("元减")
                    .append(optimalActivityRule.getBenefitAmount())
                    .append("元，已减")
                    .append(optimalActivityRule.getReduceAmount())
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
            optimalActivityRule.setSelectType(2);
        }
        return optimalActivityRule;
    }

    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal("0");
        for (CartInfo cartInfo : cartInfoList) {
            //是否选中
            if(cartInfo.getIsChecked().intValue() == 1) {
                BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                total = total.add(itemTotal);
            }
        }
        return total;
    }

    private int computeCartNum(List<CartInfo> cartInfoList) {
        int total = 0;
        for (CartInfo cartInfo : cartInfoList) {
            //是否选中
            if(cartInfo.getIsChecked().intValue() == 1) {
                total += cartInfo.getSkuNum();
            }
        }
        return total;
    }

    @Override
    public IPage<ActivityInfo> selectPageActivityInfo(Page<ActivityInfo> pageParam) {
        QueryWrapper<ActivityInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        Page<ActivityInfo> activityInfoPage = baseMapper.selectPage(pageParam, queryWrapper);
        //分页查询对象里面获取列表数据
        List<ActivityInfo> activityInfoList = activityInfoPage.getRecords();
        //遍历集合，得到每个ActivityInfo对象，向ActivityInfo对象封装活动类型
        activityInfoList.stream().forEach(item -> {
            item.setActivityTypeString(item.getActivityType().getComment());
        });
        return activityInfoPage;
    }

    @Override
    public Map<String, Object> findActivityRuleList(Long id) {

        Map<String, Object> map = new HashMap<>();
        //根据活动id进行查询，查询规则列表
        LambdaQueryWrapper<ActivityRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityRule::getActivityId,id);
        List<ActivityRule> activityRules = activityRuleMapper.selectList(wrapper);
        //根据活动id查询， 查询使用规则商品
        List<ActivitySku> activitySkus = activitySkuMapper
                                        .selectList(new LambdaQueryWrapper<ActivitySku>()
                                                    .eq(ActivitySku::getActivityId, id));
        //获取所有skuId
        List<Long> skuIds = activitySkus.stream().map(item -> item.getActivityId()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(skuIds)){
            List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoList(skuIds);
            map.put("skuInfoList",skuInfoList);
        }
        map.put("activityRuleList",activityRules);
        return map;
    }

    @Override
    public void saveActivityRule(ActivityRuleVo activityRuleVo) {
        //1 根据活动id删除之前规则数据
        //ActivityRule数据删除
//        ActivitySku删除
        activityRuleMapper.delete(new LambdaQueryWrapper<ActivityRule>()
                                .eq(ActivityRule::getActivityId,activityRuleVo.getActivityId()));
        activitySkuMapper.delete(new LambdaQueryWrapper<ActivitySku>()
                                .eq(ActivitySku::getActivityId,activityRuleVo.getActivityId()));
        //2 获取规则列表数据
        ActivityInfo activityInfo = baseMapper.selectById(activityRuleVo.getActivityId());
        List<ActivityRule> activityRuleList = activityRuleVo.getActivityRuleList();
        for (ActivityRule activityRule:activityRuleList){
            activityRule.setActivityId(activityRuleVo.getActivityId());
            activityRule.setActivityType(activityInfo.getActivityType());
            activityRuleMapper.insert(activityRule);
        }
        //3 获取活动商品列表数据
        List<ActivitySku> activitySkuList = activityRuleVo.getActivitySkuList();
        for (ActivitySku activitySku:activitySkuList){
            activitySku.setActivityId(activityRuleVo.getActivityId());
            activitySkuMapper.insert(activitySku);
        }
    }

    //根据关键字匹配sku列表
    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        List<SkuInfo> skuInfoList = new ArrayList<>();
        //1 根据输入关键字查询sku匹配内容列表
        ////   service-product模块创建接口，根据关键字查询sku匹配内容列表
        ////   service-activity远程调用的到sku内容列表
        List<SkuInfo> skuInfoByKeyword = productFeignClient.findSkuInfoByKeyword(keyword);
        //如果根据关键字查不到匹配内容直接返回空集合
        if (skuInfoByKeyword.size() ==0){
            return skuInfoList;
        }
        //2 判断要添加的商品是否添加过优惠活动，如果之前参加过，活动正在进行中，排除商品
        ////   查询两张表 activity-info  activity-sku,编写sql语句实现
//        获取所有skuId
        List<Long> skuIdList = skuInfoByKeyword.stream().map(SkuInfo::getId).collect(Collectors.toList());
        List<Long> existSkuIdList = baseMapper.selectSkuIdListExist(skuIdList);
        ////   判断逻辑处理
        for (SkuInfo skuInfo:skuInfoByKeyword){
            if (!existSkuIdList.contains(skuInfo.getId())){
                skuInfoList.add(skuInfo);
            }
        }
        return skuInfoList;
    }

    @Override
    public Map<Long, List<String>> findActivity(List<Long> skuIdList) {
        Map<Long, List<String>> result = new HashMap<>();
        //skuIdList遍历，得到每个skuId
        skuIdList.forEach(skuId ->{
            //根据skuId查询，查询sku对应活动里面的规则列表
            List<ActivityRule> activityRuleList =  baseMapper.findActivityRule(skuId);
            //封装数据，规则名称
            if (!CollectionUtils.isEmpty(activityRuleList)){
                List<String> ruleList = new ArrayList<>();
                //吧规则名称处理
                for (ActivityRule activityRule:activityRuleList){
                    //activityRule.setRuleDesc(this.getRuleDesc(activityRule));
                    ruleList.add(this.getRuleDesc(activityRule));
                }
               //activityRuleList.stream().map(activityRule -> activityRule.getRuleDesc()).collect(Collectors.toList());
                result.put(skuId,ruleList);
            }
        });
        return result;
    }

    //构造规则名称的方法
    private String getRuleDesc(ActivityRule activityRule) {
        ActivityType activityType = activityRule.getActivityType();
        StringBuffer ruleDesc = new StringBuffer();
        if (activityType == ActivityType.FULL_REDUCTION) {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionAmount())
                    .append("元减")
                    .append(activityRule.getBenefitAmount())
                    .append("元");
        } else {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionNum())
                    .append("元打")
                    .append(activityRule.getBenefitDiscount())
                    .append("折");
        }
        return ruleDesc.toString();
    }
    //根据skuId获取营销数据和优惠卷
    @Override
    public Map<String, Object> findActivityAndCoupon(Long skuId, Long userId) {
        //1 根据skuId获取sku营销活动，一个活动有多个规则
        List<ActivityRule> activityRuleList = this.findActivityRuleBySkuId(skuId);
        //2 根据skuId+userId查询优惠卷信息
        List<CouponInfo> couponInfoList = couponInfoService.findCouponInfoList(skuId,userId);
        //封装，返回
        Map<String, Object> map = new HashMap<>();
        map.put("couponInfoList",couponInfoList);
        map.put("activityRuleList",activityRuleList);
        return map;
    }

    //根据skuID获得规则信息
    @Override
    public List<ActivityRule> findActivityRuleBySkuId(Long skuId) {
        List<ActivityRule> activityRuleList = baseMapper.findActivityRule(skuId);
        for (ActivityRule activityRule: activityRuleList){
            String ruleDesc = this.getRuleDesc(activityRule);
            activityRule.setRuleDesc(ruleDesc);
        }
        return activityRuleList;
    }

}
