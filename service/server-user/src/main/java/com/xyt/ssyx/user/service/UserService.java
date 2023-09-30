package com.xyt.ssyx.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyt.ssyx.model.user.User;
import com.xyt.ssyx.vo.user.LeaderAddressVo;
import com.xyt.ssyx.vo.user.UserLoginVo;

public interface UserService extends IService<User> {
    /**
     * 根据微信openid获取用户信息
     * @param openid
     * @return
     */
    User getUserByOpenId(String openid);

    LeaderAddressVo getLeaderAddressByUserId(Long id);

    /**
     * 获取当前登录用户信息
     * @param id
     * @return
     */
    UserLoginVo getUserLoginVo(Long id);
}
