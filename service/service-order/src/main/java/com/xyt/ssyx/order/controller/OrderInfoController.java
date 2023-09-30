package com.xyt.ssyx.order.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyt.ssyx.common.auth.AuthContextHolder;
import com.xyt.ssyx.common.result.Result;
import com.xyt.ssyx.model.order.OrderInfo;
import com.xyt.ssyx.order.service.OrderInfoService;
import com.xyt.ssyx.vo.order.OrderConfirmVo;
import com.xyt.ssyx.vo.order.OrderSubmitVo;
import com.xyt.ssyx.vo.order.OrderUserQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author xyt
 * @since 2023-07-18
 */
@RestController
@RequestMapping("/api/order")
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;

    @ApiOperation("确认订单")
    @GetMapping("auth/confirmOrder")
    public Result confirm(){
        OrderConfirmVo orderConfirmVo = orderInfoService.confirmOrder();
        return Result.ok(orderConfirmVo);
    }

    @ApiOperation("生成订单")
    @PostMapping("auth/submitOrder")
    public Result submitOrder(@RequestBody OrderSubmitVo orderSubmitVo){
        Long orderId = orderInfoService.submitOrder(orderSubmitVo);
        return Result.ok(orderId);
    }

    @ApiOperation("获取订单详情")
    @GetMapping("auth/getOrderInfoById/{orderId}")
    public Result getOrderInfoById(@PathVariable("orderId") Long orderId){
        OrderInfo orderInfo = orderInfoService.getOrderInfoById(orderId);
        return Result.ok(orderInfo);
    }
    //根据orderNo查询订单信息
    @GetMapping("inner/getOrderInfo/{orderNo}")
    public OrderInfo getOrderInfo(@PathVariable("orderNo") String orderNo){
        OrderInfo orderInfo= orderInfoService.getOrderInfoByOrderNo(orderNo);
        return orderInfo;
    }

    //订单查询
    @GetMapping("auth/findUserOrderPage/{page}/{limit}")
    public Result findUserOrderPage(@PathVariable Long page, @PathVariable Long limit, OrderUserQueryVo orderUserQueryVo){
        //获取userId
        Long userId = AuthContextHolder.getUserId();
        orderUserQueryVo.setUserId(userId);
        Page<OrderInfo> pageParam = new Page<>(page,limit);
        IPage<OrderInfo> pageModel= orderInfoService.getOrderInfoByUserIdPage(pageParam,orderUserQueryVo);
        return Result.ok(pageModel);
    }
}

