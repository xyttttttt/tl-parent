package com.xyt.ssyx.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyt.ssyx.acl.mapper.AdminMapper;
import com.xyt.ssyx.acl.service.AdminService;
import com.xyt.ssyx.model.acl.Admin;
import com.xyt.ssyx.vo.acl.AdminQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {



    @Override
    public IPage<Admin> selectPageUser(Page<Admin> page, AdminQueryVo adminQueryVo) {
        String username = adminQueryVo.getUsername(); //用户名
        String name = adminQueryVo.getName();   //昵称
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(username)){
            wrapper.eq(Admin::getUsername,username);
        }
        if (!StringUtils.isEmpty(name)){
            wrapper.like(Admin::getName,name);
        }
        Page<Admin> adminPage = baseMapper.selectPage(page, wrapper);
        return adminPage;
    }
}
