package com.xyt.ssyx.acl.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyt.ssyx.acl.mapper.RolePermissionMapper;
import com.xyt.ssyx.acl.service.RolePermissionService;
import com.xyt.ssyx.model.acl.RolePermission;
import org.springframework.stereotype.Service;

@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {
}
