package com.xyt.ssyx.cart.service;

import com.xyt.ssyx.model.order.CartInfo;

import java.util.List;

public interface CartInfoService {
    //添加商品到购物车
    void addToCart(Long userId, Long skuId, Integer skuNum);
    //根据skuId删除购物车
    void deleteCart(Long skuId, Long userId);
    //清空购物车
    void deleteAllCart(Long userId);
    //批量删除购物车
    void batchDeleteCart(List<Long> skuIdList, Long userId);
    //购物车列表接口
    List<CartInfo> getCartList(Long userId);
    //1 根据skuId选中
    void checkCart(Long userId, Long skuId, Integer isChecked);
    //2 全选
    void checkAllCart(Long userId, Integer isChecked);
    //3 批量选中
    void batchCheckCart(List<Long> skuIdList, Long userId, Integer isChecked);
    //获取当前用户购物车里面选中购物项
    List<CartInfo> getCartCheckedList(Long userId);
    //根据用户id删除选中购物车中的记录
    void batchDeleteCartChecked(Long userId);
}
