package com.xyt.ssyx.acl.utils;

import com.xyt.ssyx.model.acl.Permission;

import java.util.ArrayList;
import java.util.List;

public class PermissionHelper {


    public static List<Permission> buildPermission(List<Permission> allPermissions) {

        //创建一个最终数据封装的list集合
        List<Permission> trees = new ArrayList<>();
        //遍历所有菜单list集合 ，得到第一层数据   pid=0
        for (Permission permission:allPermissions){
            //pid =0 ，就是第一层数据
            if (permission.getPid() == 0){
                permission.setLevel(1);
                //调用方法，从第一次往下找
                trees.add(findChildren(permission,allPermissions));
            }
        }
        return trees;
    }

    //递归往下去找
    //permission： 上层节点 ，从这里往下找
    //allPermissions所有菜单
    private static Permission findChildren(Permission permission,
                                           List<Permission> allPermissions) {
        permission.setChildren(new ArrayList<Permission>());

        //遍历allPermissions所有菜单数据
        //判断：当前节点id = pid 是否一样  封装，递归往下
        for (Permission it : allPermissions){
            if (it.getPid().longValue() == permission.getId().longValue()){
                int level =  permission.getLevel()+1;
                it.setLevel(level);
                if (permission.getChildren() == null){
                    permission.setChildren(new ArrayList<Permission>());
                }
                //封装下一层数据
                permission.getChildren().add(findChildren(it,allPermissions));
            }
        }
        return permission;
    }
}
