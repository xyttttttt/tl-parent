package com.xyt.ssyx.order.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyt.ssyx.model.order.OrderInfo;
import com.xyt.ssyx.vo.order.OrderConfirmVo;
import com.xyt.ssyx.vo.order.OrderSubmitVo;
import com.xyt.ssyx.vo.order.OrderUserQueryVo;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author xyt
 * @since 2023-07-18
 */
public interface OrderInfoService extends IService<OrderInfo> {

    //确认订单
    OrderConfirmVo confirmOrder();
    //生成订单
    public Long submitOrder(OrderSubmitVo orderSubmitVo);
    //获取订单详情
    OrderInfo getOrderInfoById(Long orderId);
    //根据orderNo查询订单信息
    OrderInfo getOrderInfoByOrderNo(String orderNo);

    //订单支付成功，更新订单状态，扣减库存
    void orderPay(String orderNo);
    //订单查询
    IPage<OrderInfo> getOrderInfoByUserIdPage(Page<OrderInfo> pageParam, OrderUserQueryVo orderUserQueryVo);
}
