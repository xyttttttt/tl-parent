package com.xyt.ssyx.user.api;

import com.xyt.ssyx.user.service.UserService;
import com.xyt.ssyx.vo.user.LeaderAddressVo;
import feign.Param;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "团长接口")
@RestController
@RequestMapping("/api/user/leader")
public class LeaderAddressApiController {

    @Resource
    private UserService userService;



    @ApiOperation("提货点地址信息")
    @GetMapping("/inner/getLeaderAddressVoByUserId/{userId}")
    public LeaderAddressVo getLeaderAddressVoByUserId(@PathVariable("userId") Long userId) {
        return userService.getLeaderAddressByUserId(userId);
    }
}
