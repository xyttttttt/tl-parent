package com.xyt.ssyx.acl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyt.ssyx.model.acl.Permission;

import java.util.List;
import java.util.Map;

public interface PermissionService extends IService<Permission> {
    List<Permission> queryAllPermission();

    void removeChildById(Long id);

    Map<String, Object> getRoleByPermissionId(Long roleId);

    void saveAdminRole(Long roleId, Long[] permissionId);
}
