package com.xyt.ssyx.client.user;

import com.xyt.ssyx.vo.user.LeaderAddressVo;
import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-user")
public interface UserFeignClient {


    @GetMapping("/api/user/leader/inner/getLeaderAddressVoByUserId/{userId}")
    public LeaderAddressVo getLeaderAddressVoByUserId(@PathVariable("userId") Long userId);
}
