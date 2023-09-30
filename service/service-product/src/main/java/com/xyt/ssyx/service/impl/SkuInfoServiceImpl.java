package com.xyt.ssyx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyt.ssyx.common.constant.RedisConst;
import com.xyt.ssyx.common.exception.SsyxException;
import com.xyt.ssyx.common.result.ResultCodeEnum;
import com.xyt.ssyx.mapper.SkuInfoMapper;
import com.xyt.ssyx.model.product.SkuAttrValue;
import com.xyt.ssyx.model.product.SkuImage;
import com.xyt.ssyx.model.product.SkuInfo;
import com.xyt.ssyx.model.product.SkuPoster;
import com.xyt.ssyx.mq.constant.MqConst;
import com.xyt.ssyx.mq.service.RabbitService;
import com.xyt.ssyx.service.SkuAttrValueService;
import com.xyt.ssyx.service.SkuImageService;
import com.xyt.ssyx.service.SkuInfoService;
import com.xyt.ssyx.service.SkuPosterService;
import com.xyt.ssyx.vo.product.SkuInfoQueryVo;
import com.xyt.ssyx.vo.product.SkuInfoVo;
import com.xyt.ssyx.vo.product.SkuStockLockVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * sku信息 服务实现类
 * </p>
 *
 * @author xyt
 * @since 2023-07-08
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService {

    @Autowired
    private SkuImageService skuImageService;
    @Autowired
    private SkuAttrValueService skuAttrValueService;
    @Autowired
    private SkuPosterService skuPosterService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RabbitService rabbitService;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public IPage<SkuInfo> selectPageSkuInfo(Page<SkuInfo> pageParam, SkuInfoQueryVo skuInfoQueryVo) {
        String keyword = skuInfoQueryVo.getKeyword();
        Long categoryId = skuInfoQueryVo.getCategoryId();
        String skuType = skuInfoQueryVo.getSkuType();
        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(keyword)){
            wrapper.like(SkuInfo::getSkuName,keyword);
        }
        if (!StringUtils.isEmpty(categoryId)){
            wrapper.like(SkuInfo::getCategoryId,categoryId);
        }
        if (!StringUtils.isEmpty(skuType)){
            wrapper.like(SkuInfo::getSkuType,skuType);
        }
        Page<SkuInfo> skuInfoPage = baseMapper.selectPage(pageParam, wrapper);
        return skuInfoPage;
    }

    //添加商品sku信息
    @Override
    public void saveSkuInfo(SkuInfoVo skuInfoVo) {
        //1 添加sku基本信息
        //skuInfoVo  ---》  skuInfo
        SkuInfo skuInfo = new SkuInfo();
//        skuInfo.setSkuName(skuInfoVo.getSkuName());
        BeanUtils.copyProperties(skuInfoVo,skuInfo);
        baseMapper.insert(skuInfo);
        //2 保存sku海报
        List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();
        if (!CollectionUtils.isEmpty(skuPosterList)){
            //遍历，向每个海报对象添加商品skuId
            for (SkuPoster skuPoster: skuPosterList){
                skuPoster.setSkuId(skuInfo.getId());
            }
            skuPosterService.saveBatch(skuPosterList);
        }
        //3 保存sku图片
        List<SkuImage> skuImagesList = skuInfoVo.getSkuImagesList();
        if (!CollectionUtils.isEmpty(skuImagesList)){
            int sort = 1;
            //遍历，向每个海报对象添加商品skuId
            for (SkuImage skuImage: skuImagesList){
                skuImage.setSkuId(skuInfo.getId());
                skuImage.setSort(sort);
                sort++;
            }
            skuImageService.saveBatch(skuImagesList);
        }
        //4 保存sku平台属性
        List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)){
            //遍历，向每个海报对象添加商品skuId
            for (SkuAttrValue skuAttrValue : skuAttrValueList){
                skuAttrValue.setSkuId(skuInfo.getId());
            }
            skuAttrValueService.saveBatch(skuAttrValueList);
        }
    }

    @Override
    public SkuInfoVo getSkuInfo(Long id) {
        SkuInfoVo skuInfoVo = new SkuInfoVo();
        //根据id查询sku基本基本信息
        SkuInfo skuInfo = baseMapper.selectById(id);
        //根据id查询商品图片列表
        List<SkuImage>  skuImage = skuImageService.getImageListBySkuId(id);
        //根据id查询商品海报列表
        List<SkuPoster>  skuPoster = skuPosterService.getPosterListBySkuId(id);
        //根据id查询商品属性
        List<SkuAttrValue>  skuAttrValue = skuAttrValueService.setAttrValueListBySkuId(id);

        BeanUtils.copyProperties(skuInfo,skuInfoVo);
        skuInfoVo.setSkuImagesList(skuImage);
        skuInfoVo.setSkuPosterList(skuPoster);
        skuInfoVo.setSkuAttrValueList(skuAttrValue);
        return skuInfoVo;
    }

    @Override
    public void updateSkuInfo(SkuInfoVo skuInfoVo) {
        Long id = skuInfoVo.getId();
        //修改sku基本信息
        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuInfoVo,skuInfo);
        baseMapper.updateById(skuInfo);
        //海报信息
        LambdaQueryWrapper<SkuPoster> skuPosterLambdaQueryWrapper = new LambdaQueryWrapper<>();
        skuPosterLambdaQueryWrapper.eq(SkuPoster::getSkuId,id);
        skuPosterService.remove(skuPosterLambdaQueryWrapper);
        List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();
        if (!CollectionUtils.isEmpty(skuPosterList)){
            for (SkuPoster skuPoster : skuPosterList){
                skuPoster.setSkuId(id);
            }
            skuPosterService.saveBatch(skuPosterList);
        }
        //商品图片
        skuImageService.remove(new LambdaQueryWrapper<SkuImage>().eq(SkuImage::getSkuId,id));
        List<SkuImage> skuImagesList = skuInfoVo.getSkuImagesList();
        if (!CollectionUtils.isEmpty(skuImagesList)){
            for (SkuImage skuImage : skuImagesList){
                skuImage.setSkuId(id);
            }
            skuImageService.saveBatch(skuImagesList);
        }
        //商品属性
        skuAttrValueService.remove(new LambdaQueryWrapper<SkuAttrValue>().eq(SkuAttrValue::getSkuId,id));
        List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)){
            for (SkuAttrValue skuAttrValue : skuAttrValueList){
                skuAttrValue.setSkuId(id);
            }
            skuAttrValueService.saveBatch(skuAttrValueList);
        }
    }



    //商品审核
    @Override
    public void check(Long skuId, Integer status) {
        SkuInfo skuInfo = baseMapper.selectById(skuId);
        skuInfo.setCheckStatus(status);
        baseMapper.updateById(skuInfo);
    }
    //新人专享
    @Override
    public void isNewPerson(Long skuId, Integer status) {
        SkuInfo skuInfoUp = new SkuInfo();
        skuInfoUp.setId(skuId);
        skuInfoUp.setIsNewPerson(status);
        baseMapper.updateById(skuInfoUp);
    }

    //商品上架
    @Override
    public void publish(Long skuId, Integer status) {
        if (status == 1){ //上架
            SkuInfo skuInfo = baseMapper.selectById(skuId);
            skuInfo.setPublishStatus(status);
            baseMapper.updateById(skuInfo);
            //TODO  整合mq把数据同步到es中
            rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT,MqConst.ROUTING_GOODS_UPPER,skuId);
        }else {   //下架
            SkuInfo skuInfo = baseMapper.selectById(skuId);
            skuInfo.setPublishStatus(status);
            baseMapper.updateById(skuInfo);
            //TODO  整合mq把数据同步到es中
            rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT,MqConst.ROUTING_GOODS_LOWER,skuId);
        }
    }

    @Override
    public List<SkuInfo> findSkuInfoList(List<Long> skuIds) {
        List<SkuInfo> skuInfoList = baseMapper.selectBatchIds(skuIds);
        return skuInfoList;
    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();
        List<SkuInfo> skuInfoList = baseMapper.selectList(wrapper.like(SkuInfo::getSkuName, keyword));
        return skuInfoList;
    }

    @Override
    public List<SkuInfo> findNewPersonSkuInfoList() {
        //条件1： is_new_person=1
        //条件2： publish_status=1
        //条件3： 显示其中三个
        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuInfo::getIsNewPerson,1).eq(SkuInfo::getPublishStatus,1);
        wrapper.orderByDesc(SkuInfo::getStock); //库存排序
        //分页
        Page<SkuInfo> page = new Page<>(1,3);
        Page<SkuInfo> skuInfoPage = baseMapper.selectPage(page, wrapper);
        //获取集合
        List<SkuInfo> skuInfoList = skuInfoPage.getRecords();
        return skuInfoList;
    }
    //根据skuId获取商品详情
    @Override
    public SkuInfoVo getSkuInfoVo(Long skuId) {
        SkuInfoVo skuInfoVo = new SkuInfoVo();
        //skuId查询skuInfo信息
        SkuInfo skuInfo = baseMapper.selectById(skuId);
        //skuId查询sku图片信息
        List<SkuImage> imageListBySkuId = skuImageService.getImageListBySkuId(skuId);
        //skuId查询sku海报
        List<SkuPoster> posterListBySkuId = skuPosterService.getPosterListBySkuId(skuId);
        //skuId查询sku属性
        List<SkuAttrValue> skuAttrValueList = skuAttrValueService.setAttrValueListBySkuId(skuId);
        //封装
        BeanUtils.copyProperties(skuInfo,skuInfoVo);
        skuInfoVo.setSkuImagesList(imageListBySkuId);
        skuInfoVo.setSkuPosterList(posterListBySkuId);
        skuInfoVo.setSkuAttrValueList(skuAttrValueList);
        return skuInfoVo;
    }
    //验证和锁定库存
    @Override
    public Boolean checkAndLock(List<SkuStockLockVo> skuStockLockVoList, String orderNo) {
        //1 判断skuStockLockVoList是否为空
        if (CollectionUtils.isEmpty(skuStockLockVoList)){
            throw new SsyxException(ResultCodeEnum.DATA_ERROR);
        }
        //2 集合遍历得到每个商品，验证库存并锁定库存，具备原子性
        skuStockLockVoList.stream().forEach(skuStockLockVo -> {
            this.checkLock(skuStockLockVo);
        });
        //3 只要有一个商品锁定失败，所有锁定成功的商品都要解锁
        boolean flag = skuStockLockVoList.stream().anyMatch(skuStockLockVo -> !skuStockLockVo.getIsLock());
        if (flag){ //所有锁定成功的商品都要解锁
            skuStockLockVoList.stream().filter(SkuStockLockVo::getIsLock)
                        .forEach(skuStockLockVo -> {
                            baseMapper.unlockStock(skuStockLockVo.getSkuId(),skuStockLockVo.getSkuNum());
                        });
            //返回失败
            return false;
        }
        //4 如果所有商品锁定成功，redis里面缓存相关数据，方便后门解锁和减库存
        redisTemplate.opsForValue().set(RedisConst.SROCK_INFO+orderNo,skuStockLockVoList);
        return true;
    }


    private void checkLock(SkuStockLockVo skuStockLockVo) {
        //获取锁  公平锁
        RLock rLock = this.redissonClient.getFairLock(RedisConst.SKUKEY_PREFIX + skuStockLockVo.getSkuId());
        //加锁
        rLock.lock();
        try{
            //验证库存
            SkuInfo skuInfo= baseMapper.checkStock(skuStockLockVo.getSkuId(),skuStockLockVo.getSkuNum());
            //判断没有满足条件商品
            if (skuInfo == null){
                skuStockLockVo.setIsLock(false);
                return;
            }
            //有满足条件的商品
            //锁定库存:更新
           Integer rows = baseMapper.lockStock(skuStockLockVo.getSkuId(),skuStockLockVo.getSkuNum());
            if (rows >= 1 ){
                skuStockLockVo.setIsLock(true);
            }
        }finally {
            rLock.unlock();
        }
    }

    //减库存
    @Override
    public void minusStock(String orderNo) {
        //从redis获取锁定库存信息
        List<SkuStockLockVo> skuStockLockVoList = (List<SkuStockLockVo>) redisTemplate
                                        .opsForValue().get(RedisConst.SROCK_INFO + orderNo);
        if (CollectionUtils.isEmpty(skuStockLockVoList)){
            return;
        }
        //遍历集合得到每个对象，减库存
        skuStockLockVoList.stream().forEach(skuStockLockVo -> {
            baseMapper.minusStock(skuStockLockVo.getSkuId(),skuStockLockVo.getSkuNum());
        });
        //删除redis数据
        redisTemplate.delete(RedisConst.SROCK_INFO + orderNo);
    }
}
