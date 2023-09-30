package com.xyt.ssyx.payment.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyt.ssyx.common.exception.SsyxException;
import com.xyt.ssyx.common.result.ResultCodeEnum;
import com.xyt.ssyx.enums.PaymentStatus;
import com.xyt.ssyx.enums.PaymentType;
import com.xyt.ssyx.model.order.OrderInfo;
import com.xyt.ssyx.model.order.PaymentInfo;

import com.xyt.ssyx.mq.constant.MqConst;
import com.xyt.ssyx.mq.service.RabbitService;
import com.xyt.ssyx.order.OrderFeignClient;
import com.xyt.ssyx.payment.mapper.PaymentInfoMapper;
import com.xyt.ssyx.payment.service.PaymentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * <p>
 * 支付信息表 服务实现类
 * </p>
 *
 * @author xyt
 * @since 2023-07-18
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {

    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private RabbitService rabbitService;
    //根据orderNo查询支付记录
    @Override
    public PaymentInfo getPaymentInfoByOrderNo(String orderNo) {
        PaymentInfo paymentInfo = baseMapper.selectOne
                                    (new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderNo, orderNo));
        return paymentInfo;
    }

    //添加支付记录
    @Override
    public PaymentInfo savePaymentInfo(String orderNo) {
        //远程调用根据orderNo查询订单信息
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderNo);
        if (orderInfo == null){
            throw new SsyxException(ResultCodeEnum.DATA_ERROR);
        }
        //封装到paymentInfo中
        PaymentInfo paymentInfo = new PaymentInfo();
        // 保存交易记录
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(PaymentType.WEIXIN);
        paymentInfo.setUserId(orderInfo.getUserId());
        paymentInfo.setOrderNo(orderInfo.getOrderNo());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID);
        String subject = "userId:"+orderInfo.getUserId();
        paymentInfo.setSubject(subject);
        //paymentInfo.setTotalAmount(order.getTotalAmount());
        //TODO 为了测试支付0.01
        paymentInfo.setTotalAmount(new BigDecimal("0.01"));
        //调用方法进行添加
        baseMapper.insert(paymentInfo);
        return paymentInfo;
    }

    //3.1 支付成功，修改支付记录表状态：已经支付
    //3.2 支付成功，修改订单记录已经支付，库存扣减
    @Override
    public void paySuccess(String out_trade_no, Map<String, String> resultMap) {
        //1 查询当前订单记录支付状态  是否是已经支付
        PaymentInfo paymentInfo = baseMapper.selectOne
                                    (new LambdaQueryWrapper<PaymentInfo>()
                                                .eq(PaymentInfo::getOrderNo, out_trade_no));
        if (paymentInfo.getPaymentStatus() != PaymentStatus.UNPAID){
            return;
        }
        //2 如果支付记录表支付状态没有支付，更新
        paymentInfo.setPaymentStatus(PaymentStatus.PAID);
        paymentInfo.setTradeNo(resultMap.get("transaction_id"));
        paymentInfo.setCallbackContent(resultMap.toString());
        paymentInfo.setCallbackTime(new Date());
        baseMapper.updateById(paymentInfo);
        //TODO 3 整合RabbitMQ  修改订单记录已经支付，库存扣减
        rabbitService.sendMessage(MqConst.EXCHANGE_PAY_DIRECT
                               , MqConst.ROUTING_PAY_SUCCESS
                               , out_trade_no);
    }
}
