package com.xyt.ssyx.payment.controller;

import com.xyt.ssyx.common.result.Result;
import com.xyt.ssyx.common.result.ResultCodeEnum;
import com.xyt.ssyx.payment.service.PaymentInfoService;
import com.xyt.ssyx.payment.service.WeixinService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(tags = "微信支付接口")
@RestController
@RequestMapping("/api/payment/weixin")
@Slf4j
public class WeixinController {

    @Autowired
    private WeixinService weixinService;
    @Autowired
    private PaymentInfoService paymentInfoService;

    //调用微信支付系统生成预付单
    @GetMapping("/createJsapi/{orderNo}")
    public Result createJsapi (@PathVariable("orderNo") String orderNo){
        Map<String,String> map = weixinService.createJsapi(orderNo);
        return Result.ok(map);
    }

    //查询订单支付状态
    @GetMapping("/queryPayStatus/{orderNo}")
    public Result queryPayStatus(@PathVariable("orderNo") Long orderNo){
        //1 调用微信支付系统接口查询订单支付状态
        Map<String,String> resultMap =  weixinService.queryPayStatus(orderNo);
        //2 微信支付系统返回值为空，支付失败
        if (resultMap == null){
            return Result.build(null, ResultCodeEnum.PAYMENT_FAIL);
        }
        //3 如果微信支付系统返回值，判断支付成功
        if (resultMap.get("trade_state").equals("SUCCESS")){
            //3.1 支付成功，修改支付记录表状态：已经支付
            //3.2 支付成功，修改订单记录已经支付，库存扣减
            String  out_trade_no= resultMap.get("out_trade_no");
            paymentInfoService.paySuccess(out_trade_no,resultMap);
            return Result.ok(null);
        }
        //4 支付中，等待
        return Result.build(null,ResultCodeEnum.PAYMENT_WAITING);
    }
}
