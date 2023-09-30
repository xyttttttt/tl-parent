package com.xyt.ssyx.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xyt.ssyx.common.auth.AuthContextHolder;
import com.xyt.ssyx.common.constant.RedisConst;
import com.xyt.ssyx.common.exception.SsyxException;
import com.xyt.ssyx.common.result.Result;
import com.xyt.ssyx.common.result.ResultCodeEnum;
import com.xyt.ssyx.common.utils.JwtHelper;
import com.xyt.ssyx.enums.UserType;
import com.xyt.ssyx.model.user.User;
import com.xyt.ssyx.model.user.UserDelivery;
import com.xyt.ssyx.user.mapper.UserDeliveryMapper;
import com.xyt.ssyx.user.service.UserService;
import com.xyt.ssyx.user.utils.ConstantPropertiesUtil;
import com.xyt.ssyx.user.utils.HttpClientUtils;
import com.xyt.ssyx.vo.user.LeaderAddressVo;
import com.xyt.ssyx.vo.user.UserLoginVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/user/weixin")
public class weixinApiController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserDeliveryMapper userDeliveryMapper;

    //用户微信授权登录
    @ApiOperation(value = "微信登录获取openid(小程序)")
    @GetMapping("/wxLogin/{code}")
    public Result loginWx(@PathVariable String code){
        if (StringUtils.isEmpty(code)) {
            throw new SsyxException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }
        //1 得到微信返回code临时票据值
        //2 拿着code + 小程序id  + 小程序密钥  请求微信接口服务
        ////使用HttpClient工具请求
        //小程序id
        String wxOpenAppId = ConstantPropertiesUtil.WX_OPEN_APP_ID;
        //小程序密钥
        String wxOpenAppSecret = ConstantPropertiesUtil.WX_OPEN_APP_SECRET;
        //get请求  拼接请求地址+参数
        //地址?name=value1&name2=value2
        StringBuffer url = new StringBuffer()
                                .append("https://api.weixin.qq.com/sns/jscode2session")
                                .append("?appid=%s")
                                .append("&secret=%s")
                                .append("&js_code=%s")
                                .append("&grant_type=authorization_code");
        //format(String format, Object… args)：新字符串使用本地语言环境，制定字符串格式和参数生成格式化的新字符串。
        //  %s：占位符   占位字符串
        String tokenUrl = String.format(url.toString(), wxOpenAppId, wxOpenAppSecret, code);
        //HttpClient发送get请求
        String result =null;
        try {
            result = HttpClientUtils.get(tokenUrl);
        } catch (Exception e) {
            throw new SsyxException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }
        //3 请求微信接口服务，返回两个值， session_key 和 openid
        ////openId 是你微信得到唯一标识
        //将str转化为相应的JSONObject对象，其中str是“键值对”形式的json字符串，
        JSONObject jsonObject = JSONObject.parseObject(result);
        String session_key = jsonObject.getString("session_key");
        String openid = jsonObject.getString("openid");
        //4 添加微信用户信息到数据库里面
        //// 操作user表
        //// 判断是否是第一次使用微信授权登录：    如何判断？  openId
        User user = userService.getUserByOpenId(openid);
        if (user ==null){
            user = new User();
            user.setOpenId(openid);
            user.setNickName(openid);
            user.setPhotoUrl("");
            user.setUserType(UserType.USER);
            user.setIsNew(0);
            userService.save(user);
            User one = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getOpenId, openid));
            Long id = one.getId();
            UserDelivery userDelivery = new UserDelivery();
            userDelivery.setUserId(id);
            userDelivery.setLeaderId(3L);
            userDelivery.setWareId(1L);
            userDelivery.setIsDefault(1);
            userDelivery.setCreateTime(new Date());
            userDelivery.setUpdateTime(new Date());
            userDeliveryMapper.insert(userDelivery);
        }
        //5 根据userId查询提货点和团长信息
        //// 提货点 到什么地方取货   user_delivery表
        //// 团长  管理提货点的     leader表
        LeaderAddressVo leaderAddressVo = userService.getLeaderAddressByUserId(user.getId());
        //6 使用jwt工具 根据userId和userName 生成token字符串
        String token = JwtHelper.createToken(user.getId(), user.getNickName());
        //7 获取当前登录用户信息 ，放到redis里面，设置有效时间
        UserLoginVo userLoginVo = this.userService.getUserLoginVo(user.getId());
        userLoginVo.setUserId(user.getId());
        redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_KEY_PREFIX+user.getId(),userLoginVo,RedisConst.USERKEY_TIMEOUT, TimeUnit.DAYS);
        //8 需要数据封装到map进行返回
        Map<String,Object> map = new HashMap<>();
        map.put("user",user);
        map.put("token",token);
        map.put("leaderAddressVo",leaderAddressVo);
        return Result.ok(map);
    }

    @PostMapping("/auth/updateUser")
    @ApiOperation(value = "更新用户昵称与头像")
    public Result updateUser(@RequestBody User user) {
        //获取当前登录用户id
        User user1 = userService.getById(AuthContextHolder.getUserId());
        if (user1!=null){
            //把昵称更新为微信用户
            user1.setNickName(user.getNickName().replaceAll("[ue000-uefff]", "*"));
            user1.setPhotoUrl(user.getPhotoUrl());
            userService.updateById(user1);
        }
        return Result.ok(null);
    }
}
