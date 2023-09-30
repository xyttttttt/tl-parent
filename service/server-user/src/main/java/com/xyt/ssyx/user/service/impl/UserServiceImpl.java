package com.xyt.ssyx.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyt.ssyx.model.user.Leader;
import com.xyt.ssyx.model.user.User;
import com.xyt.ssyx.model.user.UserDelivery;
import com.xyt.ssyx.user.mapper.LeaderMapper;
import com.xyt.ssyx.user.mapper.UserDeliveryMapper;
import com.xyt.ssyx.user.mapper.UserMapper;
import com.xyt.ssyx.user.service.UserService;
import com.xyt.ssyx.vo.user.LeaderAddressVo;
import com.xyt.ssyx.vo.user.UserLoginVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private LeaderMapper leaderMapper;
    @Autowired
    private UserDeliveryMapper userDeliveryMapper;

    @Override
    public User getUserByOpenId(String openid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenId,openid);
        User user = baseMapper.selectOne(wrapper);
        return user;
    }
    //5 根据userId查询提货点和团长信息
    @Override
    public LeaderAddressVo getLeaderAddressByUserId(Long id) {
        //根据用户id查询用户默认的团长id
        UserDelivery userDelivery = userDeliveryMapper.selectOne(new LambdaQueryWrapper<UserDelivery>()
                                                        .eq(UserDelivery::getUserId, id)
                                                        .eq(UserDelivery::getIsDefault, 1));
        if (userDelivery==null){
            return null;
        }
        //拿着团长id查找leader其他信息
        Leader leader = leaderMapper.selectById(userDelivery.getLeaderId());
        //封装数据
        LeaderAddressVo leaderAddressVo = new LeaderAddressVo();
        BeanUtils.copyProperties(leader, leaderAddressVo);
        leaderAddressVo.setUserId(id);
        leaderAddressVo.setLeaderId(leader.getId());
        leaderAddressVo.setLeaderName(leader.getName());
        leaderAddressVo.setLeaderPhone(leader.getPhone());
        leaderAddressVo.setWareId(userDelivery.getWareId());
        leaderAddressVo.setStorePath(leader.getStorePath());
        return leaderAddressVo;
    }

    //7 获取当前登录用户信息 ，放到redis里面，设置有效时间
    @Override
    public UserLoginVo getUserLoginVo(Long id) {
        User user = baseMapper.selectById(id);
        UserLoginVo userLoginVo = new UserLoginVo();
        userLoginVo.setNickName(user.getNickName());
        userLoginVo.setPhotoUrl(user.getPhotoUrl());
        userLoginVo.setIsNew(user.getIsNew());
        userLoginVo.setOpenId(user.getOpenId());
        UserDelivery userDelivery = userDeliveryMapper.selectOne(new LambdaQueryWrapper<UserDelivery>()
                                    .eq(UserDelivery::getUserId, id).eq(UserDelivery::getIsDefault, 1));
        if (userDelivery!=null){
            userLoginVo.setLeaderId(userDelivery.getLeaderId());
            userLoginVo.setWareId(userDelivery.getWareId());
        }
     /*   userLoginVo.setLeaderId(1L);
        userLoginVo.setWareId(1L);*/
        return userLoginVo;
    }
}
