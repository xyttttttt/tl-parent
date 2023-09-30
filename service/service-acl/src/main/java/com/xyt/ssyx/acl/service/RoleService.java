package com.xyt.ssyx.acl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyt.ssyx.model.acl.Role;
import com.xyt.ssyx.vo.acl.RoleQueryVo;

import java.util.Map;

public interface RoleService extends IService<Role> {

    IPage<Role> selectRolePage(Page<Role> pageParam, RoleQueryVo roleQueryVo);

    Map<String, Object> getRoleByAdminId(Long adminId);

    void saveAdminRole(Long adminId, Long[] roleId);
}
