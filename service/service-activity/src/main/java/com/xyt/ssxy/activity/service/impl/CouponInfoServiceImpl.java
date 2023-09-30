package com.xyt.ssxy.activity.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyt.ssxy.activity.mapper.CouponInfoMapper;
import com.xyt.ssxy.activity.mapper.CouponRangeMapper;
import com.xyt.ssxy.activity.mapper.CouponUseMapper;
import com.xyt.ssxy.activity.service.CouponInfoService;
import com.xyt.ssxy.activity.service.CouponUseService;
import com.xyt.ssyx.client.product.ProductFeignClient;
import com.xyt.ssyx.enums.CouponRangeType;
import com.xyt.ssyx.enums.CouponStatus;
import com.xyt.ssyx.model.activity.CouponInfo;
import com.xyt.ssyx.model.activity.CouponRange;
import com.xyt.ssyx.model.activity.CouponUse;
import com.xyt.ssyx.model.order.CartInfo;
import com.xyt.ssyx.model.product.Category;
import com.xyt.ssyx.model.product.SkuInfo;
import com.xyt.ssyx.vo.activity.CouponRuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.sql.rowset.spi.SyncResolver;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 优惠券信息 服务实现类
 * </p>
 *
 * @author xyt
 * @since 2023-07-10
 */
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {

    @Autowired
    private CouponRangeMapper couponRangeMapper;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private CouponUseMapper couponUseMapper;

    @Override
    public IPage<CouponInfo> selectPageCouponInfo(Page<CouponInfo> pageParam) {
        Page<CouponInfo> couponInfoPage = baseMapper.selectPage(pageParam, null);
        List<CouponInfo> couponInfoList = couponInfoPage.getRecords();
        couponInfoList.stream().forEach(item -> {
            item.setCouponTypeString(item.getCouponType().getComment());
            CouponRangeType rangeType = item.getRangeType();
            if (rangeType != null){
                item.setRangeTypeString(rangeType.getComment());
            }
        });
        return couponInfoPage;
    }
    //3 根据id查询优惠卷
    @Override
    public CouponInfo getCouponInfo(Long id) {
        CouponInfo couponInfo = baseMapper.selectById(id);
        couponInfo.setCouponTypeString(couponInfo.getCouponType().getComment());
        if (couponInfo.getRangeType() != null){
            couponInfo.setRangeTypeString(couponInfo.getRangeType().getComment());
        }
        return couponInfo;
    }

    @Override
    public Map<String, Object> findCouponRuleList(Long id) {
        Map<String ,Object> map = new HashMap<>();
        //第一步  根据优惠卷id查询优惠卷信息  coupon_info
        CouponInfo couponInfo = baseMapper.selectById(id);
        //第二部  根据优惠卷id查询coupon_range 查询里面对应range_id
        List<CouponRange> couponRangeList = couponRangeMapper
                                                .selectList(new LambdaQueryWrapper<CouponRange>()
                                                .eq(CouponRange::getCouponId, id));
        //获取所有range_id
        List<Long> rangeIdList = couponRangeList.stream().map(CouponRange::getRangeId)
                                                        .collect(Collectors.toList());
        //第三步 分别判断进行封装
        ////  规则类型是SKU 得到skuId值 远程调用根据多个skuId值获取对应sku信息
        ////  如果规则是分类 得到分类id，远程调用根据多个分类值获取对应分类信息
        if (!CollectionUtils.isEmpty(rangeIdList)){
            ////  如果规则类型是SKU range_id 就是skuId值
            if (couponInfo.getRangeType() == CouponRangeType.SKU){
                List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoList(rangeIdList);
                map.put("skuInfoList",skuInfoList);
            }////  如果规则类型是CATEGORY range_id 就是分类Id值
            else if (couponInfo.getRangeType() == CouponRangeType.CATEGORY){
                List<Category> categoryList = productFeignClient.findCategoryList(rangeIdList);
                map.put("categoryList",categoryList);
            }
        }
        return map;
    }
    //5 添加优惠卷规则数据
    @Override
    public void saveCouponRule(CouponRuleVo couponRuleVo) {
        //1 根据优惠卷id删除规则数据
        couponRangeMapper.delete(new LambdaQueryWrapper<CouponRange>()
                                    .eq(CouponRange::getCouponId,couponRuleVo.getCouponId()));
        //2 更新优惠卷基本信息
        CouponInfo couponInfo = baseMapper.selectById(couponRuleVo.getCouponId());
        couponInfo.setRangeType(couponRuleVo.getRangeType());
        //使用门栏
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        //金额
        couponInfo.setAmount(couponRuleVo.getAmount());
        //使用范围描述
        couponInfo.setRangeDesc(couponRuleVo.getRangeDesc());
        baseMapper.updateById(couponInfo);
        //3 添加优惠卷新规则数据   得到优惠券参与的商品list
        List<CouponRange> couponRangeList = couponRuleVo.getCouponRangeList();
        for (CouponRange couponRange:couponRangeList) {
            //设置优惠卷id
            couponRange.setCouponId(couponRuleVo.getCouponId());
            //添加
            couponRangeMapper.insert(couponRange);
        }
    }
    //2 根据skuId+userId查询优惠卷信息
    @Override
    public List<CouponInfo> findCouponInfoList(Long skuId, Long userId) {
        // 根据skuId获取skuInfo信息  远程调用
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        // 根据条件查询，skuId  +   分类id   +  userId
        List<CouponInfo> couponInfoList = baseMapper.selectCouponInfoList(skuInfo.getId(),skuInfo.getCategoryId(),userId);
        return couponInfoList;
    }
    //3 获取购物车可用优惠卷列表
    @Override
    public List<CouponInfo> findCartCouponInfo(List<CartInfo> cartInfoList, Long userId) {
        //1 根据用户id获取用户全部优惠卷  coupon_use  coupon_info
        List<CouponInfo> userAllCouponInfoList = baseMapper.selectCartCouponInfoList(userId);
        if (CollectionUtils.isEmpty(userAllCouponInfoList)){
            return new ArrayList<CouponInfo>();
        }
        //2 从第一步返回list集合中，获取所有优惠卷id列表
        List<Long> couponIdList = userAllCouponInfoList.stream().map(CouponInfo::getId).collect(Collectors.toList());
        //3 查询优惠卷使用范围 coupon_range
        LambdaQueryWrapper<CouponRange> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CouponRange::getCouponId,couponIdList);
        List<CouponRange> couponRangeList = couponRangeMapper.selectList(wrapper);
        //4 获取优惠卷id，对应skuId列表
        //优惠卷id进行分组
        // Map<Long,List<Long>>
        Map<Long,List<Long>> couponIdToSkuIdMap = this.findCouponIdToSkuIdMap(cartInfoList,couponRangeList);
        //5 判断优惠卷类型 全程通用 sku 和 分类
        BigDecimal reduceAmount = new BigDecimal(0);
        CouponInfo optimalCouponInfo =null;
        for (CouponInfo couponInfo:userAllCouponInfoList){
            //全场通用
            if (CouponRangeType.ALL == couponInfo.getRangeType()){
                BigDecimal totalAmount = computeTotalAmount(cartInfoList);
                if (totalAmount.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0){
                    couponInfo.setIsSelect(1);
                }
            }else {
                //获取优惠卷id对应skuId列表
                List<Long> skuIdList = couponIdToSkuIdMap.get(couponInfo.getId());
                //满足使用范围的购物项
                List<CartInfo> currentCartInfoList = cartInfoList.stream()
                                            .filter(cartInfo -> skuIdList.contains(cartInfo.getSkuId()))
                                            .collect(Collectors.toList());
                BigDecimal totalAmount = computeTotalAmount(currentCartInfoList);
                if (totalAmount.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0){
                    couponInfo.setIsSelect(1);
                }
            }
            if (couponInfo.getIsSelect().intValue() == 1 && couponInfo.getAmount().subtract(reduceAmount).doubleValue() > 0) {
                reduceAmount = couponInfo.getAmount();
                optimalCouponInfo = couponInfo;
            }
        }
        //6 返回List<CouponInfo>
        if (null != optimalCouponInfo){
            optimalCouponInfo.setIsOptimal(1);
        }
        return userAllCouponInfoList;
    }


    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList){
        BigDecimal total = new BigDecimal(0);
        for (CartInfo cartInfo:cartInfoList){
            //是否选中
            if (cartInfo.getIsChecked().intValue() ==1){
                BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                total = total.add(itemTotal);
            }
        }
        return total;
    }

    //4 获取优惠卷id，对应skuId列表
    //优惠卷id进行分组
    // Map<Long,List<Long>>
    private Map<Long, List<Long>> findCouponIdToSkuIdMap(List<CartInfo> cartInfoList, List<CouponRange> couponRangeList) {
        Map<Long, List<Long>> couponIdToSkuIdMap = new HashMap<>();

        //couponRangeList数据处理
        Map<Long, List<CouponRange>> couponRangeToRangeListMap = couponRangeList.stream()
                                        .collect(Collectors.groupingBy(couponRange -> couponRange.getCouponId()));
        //遍历map集合
        Iterator<Map.Entry<Long, List<CouponRange>>> iterator = couponRangeToRangeListMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Long, List<CouponRange>> entry = iterator.next();
            Long couponId = entry.getKey();
            List<CouponRange> rangeList = entry.getValue();
            //创建集合 set  一个商品对应一个优惠卷
            Set<Long> skuIdSet = new HashSet<>();
            for (CartInfo cartInfo:cartInfoList){
                for (CouponRange couponRange: rangeList){
                    //判断
                    if (couponRange.getRangeType() == CouponRangeType.SKU
                            && couponRange.getRangeId().longValue() == cartInfo.getSkuId().intValue())
                    { // RangeType是按skuId
                        skuIdSet.add(cartInfo.getSkuId());
                    }else if (couponRange.getRangeType() == CouponRangeType.CATEGORY
                            && couponRange.getRangeId().longValue() == cartInfo.getCategoryId().intValue())
                    { // RangeType是按分类id
                        skuIdSet.add(cartInfo.getSkuId());
                    }
                    else {//全场通用

                    }
                }
            }
            couponIdToSkuIdMap.put(couponId,new ArrayList<>(skuIdSet));
        }
        return couponIdToSkuIdMap;
    }
    //获取购物车里对应优惠卷
    @Override
    public CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList, Long couponId) {
        //根据优惠卷id获取基本信息
        CouponInfo couponInfo = baseMapper.selectById(couponId);
        if (couponInfo == null){
            return null;
        }
        // 根据couponId查询对应coupon_range数据
        List<CouponRange> couponRangeList = couponRangeMapper.selectList(
                new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, couponId)
        );
        //对应sku信息
        Map<Long, List<Long>> couponIdToSkuIdMap = this.findCouponIdToSkuIdMap(cartInfoList, couponRangeList);
        //遍历map集合，得到value值，封装到couponInfo对象
        List<Long> skuIdList = couponIdToSkuIdMap.entrySet().iterator().next().getValue();
        couponInfo.setSkuIdList(skuIdList);
        return couponInfo;
    }
    //更新优惠卷使用状态
    @Override
    public void updateCouponInfoUseStatus(Long couponId, Long userId, Long orderId) {

        //更加couponId查询优惠价信息
        LambdaQueryWrapper<CouponUse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CouponUse::getCouponId,couponId);
        wrapper.eq(CouponUse::getUserId,userId);
        CouponUse couponUse = couponUseMapper.selectOne(wrapper);
        if (couponUse!=null){
            //设置修改值
            couponUse.setCouponStatus(CouponStatus.USED);
            //调用方法修改
            couponUseMapper.updateById(couponUse);
        }
    }
}
