package com.xyt.ssyx.order.service.impl;


import com.xyt.ssyx.model.order.OrderItem;
import com.xyt.ssyx.order.mapper.OrderItemMapper;
import com.xyt.ssyx.order.service.OrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单项信息 服务实现类
 * </p>
 *
 * @author xyt
 * @since 2023-07-18
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

}
