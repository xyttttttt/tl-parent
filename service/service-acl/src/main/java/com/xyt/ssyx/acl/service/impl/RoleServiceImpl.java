package com.xyt.ssyx.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyt.ssyx.acl.mapper.RoleMapper;
import com.xyt.ssyx.acl.service.AdminRoleService;
import com.xyt.ssyx.acl.service.RoleService;
import com.xyt.ssyx.model.acl.AdminRole;
import com.xyt.ssyx.model.acl.Role;
import com.xyt.ssyx.vo.acl.RoleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private AdminRoleService adminRoleService;


    @Override
    public IPage<Role> selectRolePage(Page<Role> pageParam,
                                      RoleQueryVo roleQueryVo) {
        //获取条件值
        String roleName = roleQueryVo.getRoleName();
        //创建mp条件对象
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        //判断条件值是否为空，不为空封装查询条件
        if(!StringUtils.isEmpty(roleName)){
            wrapper.like(Role::getRoleName,roleName);
        }
        //调用方法实现条件分页查询
        Page<Role> rolePage = baseMapper.selectPage(pageParam, wrapper);
        //返回分页对象
        return rolePage;
    }

    @Override
    public Map<String, Object> getRoleByAdminId(Long adminId) {
        //1 查询所有角色
        List<Role> allRoles = baseMapper.selectList(null);

        //2 根据用户id查询用户分配角色列表
        //2.1根据用户id查询用户角色关系表，查询用户已经分配的角色id列表
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRole::getAdminId,adminId);
        List<AdminRole> adminRoleList = adminRoleService.list(wrapper);
        //List<AdminRole>
        //2.2 通过第一步返回的集合，获取所有角色id的列表 List<Long>
        List<Long> roleIdList = adminRoleList.stream().map(item -> item.getRoleId()).collect(Collectors.toList());
        //2.3 创建一个新LIST集合，用于存储用户分配角色
        List<Role> assignRoleList = new ArrayList<>();
        //2.4遍历所有角色列表 allRoles，得到每个角色
        //判断所有角色里面是否包含已经分配角色id，分装到LIST集合中去
        for (Role role : allRoles){
            if (roleIdList.contains(role.getId())){
                assignRoleList.add(role);
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("allRolesList",allRoles);
        map.put("assignRoles",adminRoleList);
        return map;
    }

    @Override
    public void saveAdminRole(Long adminId, Long[] roleIds) {
        //1.删除用户已经分配过的角色数据
        //根据用户id删除admin_role里面对应的数据
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRole::getAdminId,adminId);
        adminRoleService.remove(wrapper);
        //2.重新分配
        //遍历多个角色id，得到每个角色id，拿着每个角色id   +   用户id添加用户角色关系表
        /*for (Long roleId:roleIds){
            AdminRole adminRole = new AdminRole();
            adminRole.setAdminId(adminId);
            adminRole.setRoleId(roleId);
            adminRoleService.save(adminRole);
        }*/
        List<AdminRole>  list = new ArrayList<>();
        for (Long roleId:roleIds){
            AdminRole adminRole = new AdminRole();
            adminRole.setAdminId(adminId);
            adminRole.setRoleId(roleId);
            list.add(adminRole);
        }
        adminRoleService.saveBatch(list);
    }
}
