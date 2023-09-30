package com.xyt.ssyx.cart;

import com.xyt.ssyx.model.order.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("service-cart")
public interface CartFeignClient {


    //获取当前用户购物车里面选中购物项
    @GetMapping("/api/cart/inner/getCartCheckedList/{userId}")
    public List<CartInfo> getCartCheckedList(@PathVariable("userId") Long userId);
}
