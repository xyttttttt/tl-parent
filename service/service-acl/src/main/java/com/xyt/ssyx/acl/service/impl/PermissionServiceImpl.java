package com.xyt.ssyx.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyt.ssyx.acl.mapper.PermissionMapper;
import com.xyt.ssyx.acl.service.PermissionService;
import com.xyt.ssyx.acl.service.RolePermissionService;
import com.xyt.ssyx.acl.utils.PermissionHelper;
import com.xyt.ssyx.model.acl.AdminRole;
import com.xyt.ssyx.model.acl.Permission;
import com.xyt.ssyx.model.acl.Role;
import com.xyt.ssyx.model.acl.RolePermission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Autowired
    private RolePermissionService rolePermissionService;
    //查询所有菜单
    @Override
    public List<Permission> queryAllPermission() {
        //1 查询所有菜单
        List<Permission> allPermissions = baseMapper.selectList(null);
        //2 转换要求数据格式
        List<Permission> result = PermissionHelper.buildPermission(allPermissions);
        return result;
    }

    @Override
    public void removeChildById(Long id) {
        //list中有要删除的所有id
        List<Long> list = new ArrayList<>();

        this.getAllPermissionId(id,list);
        list.add(id);
        baseMapper.deleteBatchIds(list);
    }



    //递归找当前菜单下面的所有子菜单
    //第一个参数：当前菜单id
    //第二个参数包含所有菜单id
    private void getAllPermissionId(Long id, List<Long> list) {

        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Permission::getPid,id);
        List<Permission> childList = baseMapper.selectList(queryWrapper);
        childList.stream().forEach(item -> {
            //封装id到list集合中
            list.add(item.getId());
            //递归
            this.getAllPermissionId(item.getId(),list);
        });
    }

    @Override
    public Map<String, Object> getRoleByPermissionId(Long roleId) {
        List<Permission> allPermission = baseMapper.selectList(null);

        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId,roleId);
        List<RolePermission> rolePermissionList = rolePermissionService.list(wrapper);

        List<Long> permissionIdList = rolePermissionList.stream().map(item -> item.getPermissionId()).collect(Collectors.toList());

        List<Permission> assignRoleList = new ArrayList<>();

        for (Permission p : allPermission){
            if (permissionIdList.contains(p.getId())){
                assignRoleList.add(p);
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("allPermissions",allPermission);
        //selectedRoles
        map.put("selectedRoles",assignRoleList);
        return map;
    }

    @Override
    public void saveAdminRole(Long roleId, Long[] permissionId) {
        //1.删除用户已经分配过的角色数据
        //根据用户id删除admin_role里面对应的数据
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId,roleId);
        rolePermissionService.remove(wrapper);
        //2.重新分配
        //遍历多个菜单id，得到每个菜单id，拿着每个菜单id   +   角色id添加菜单用户关系表
        List<RolePermission>  list = new ArrayList<>();
        for (Long pId:permissionId){
            RolePermission rolePermission = new RolePermission();
            rolePermission.setPermissionId(pId);
            rolePermission.setRoleId(roleId);
            list.add(rolePermission);
        }
        rolePermissionService.saveBatch(list);
    }

}
